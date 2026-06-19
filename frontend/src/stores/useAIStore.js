import { create } from 'zustand'
import { classifyIntent } from '../components/compass-chat/ai-assistant/intentRouter'
import { api } from '../lib/api'
import subProjectData from '../lib/resources.json'
import { AI_SUBPROJECT_CHANNELS } from './useChatStore'

function welcomeMessage() {
    return {
        id: 'ai-welcome',
        from: 'ai',
        type: 'text',
        text: "Hi, I'm here if you need help finding resources",
        createdAt: new Date().toISOString(),
    }
}

function subProjectWelcomeMessage(channelId) {
    const channel = AI_SUBPROJECT_CHANNELS.find((c) => c.id === channelId)
    const project = subProjectData.subProjects.find((p) => p.slug === channel?.slug)
    return {
        id: `ai-${channelId}-welcome`,
        from: 'ai',
        type: 'text',
        text: project
            ? `Hi! I can help you with ${project.name} — ${project.tagline} What would you like to know?`
            : "Hi, I'm here if you need help finding resources.",
        createdAt: new Date().toISOString(),
    }
}

function findSubProject(slug) {
    return subProjectData.subProjects.find((p) => p.slug === slug)
}

function extractAiPayload(response) {
    return {
        text: response?.data?.response || response?.data?.message || 'I received your message.',
        intent: response?.data?.intent || null,
        liveAgentSuggested: response?.data?.liveAgentSuggested === true,
    }
}

const BACKEND_TO_SUBPROJECT = {
    HOUSING: 'homematch',
    YOUTH: 'futurepath',
    CIVIC: 'firststep',
    RESOURCES: 'kindconnect',
}

function mapBackendIntent(intent) {
    if (!intent) return null
    if (intent === 'ESCALATE' || intent === 'CASEWORKER') return { intent: 'URGENT' }
    if (intent === 'MOOD') return { intent: 'MOOD' }
    if (BACKEND_TO_SUBPROJECT[intent]) return { intent: 'RESOURCE', subProject: BACKEND_TO_SUBPROJECT[intent] }
    return { intent: 'GENERAL' }
}

function makeAiTextMessage(text) {
    return {
        id: `ai-${Date.now()}`,
        from: 'ai',
        type: 'text',
        text,
        createdAt: new Date().toISOString(),
    }
}

function persistUnread(channelKey, count) {
    localStorage.setItem(`cc_ai_unread_${channelKey}`, String(count))
}

function loadUnread(channelKey) {
    return Number(localStorage.getItem(`cc_ai_unread_${channelKey}`) ?? (channelKey === 'main' ? 1 : 0))
}

const LISTING_INTENTS = new Set(['RESOURCES', 'HOUSING'])

function parseServiceListings(text) {
    // Normalize: split on any newline (single or double) before a bullet
    const normalized = text.replace(/\n(?=•)/g, '\n\n')
    const blocks = normalized.split(/\n\n(?=•)/)
    return blocks
        .map((block) => {
            const lines = block.split('\n')
            // Find the bullet line — skip any non-bullet header lines in the block
            const bulletIdx = lines.findIndex((l) => l.trim().startsWith('•'))
            if (bulletIdx === -1) return null
            const name = lines[bulletIdx].replace(/^•\s*/, '').trim()
            if (!name) return null
            const remaining = lines.slice(bulletIdx + 1)
            const phoneLineIdx = remaining.findIndex((l) => l.trim().startsWith('Phone:'))
            const descLines = remaining
                .slice(0, phoneLineIdx === -1 ? undefined : phoneLineIdx)
                .map((l) => l.trim())
                .filter(Boolean)
            const description = descLines.join(' ')
            let phone = '', website = ''
            if (phoneLineIdx !== -1) {
                const parts = remaining[phoneLineIdx].replace(/^\s*Phone:\s*/, '').split(' | ')
                phone = parts[0]?.trim() ?? ''
                website = parts[1]?.trim() ?? ''
            }
            return { name, description, phone, website }
        })
        .filter((s) => s !== null && s.name)
}

function buildHistory(messages) {
    return messages
        .filter((m) => m.type === 'text' && m.text)
        .slice(-6)
        .map((m) => m.text)
}

function channelKey(id) {
    return id ?? 'main'
}

const MOOD_PHRASES = {
    VERY_LOW: "I hear you — things sound really hard right now. You don't have to face this alone.",
    LOW: "I'm sorry you're going through this. You don't have to face it alone.",
    NEUTRAL: "Thanks for sharing. I want to make sure you have the right support.",
    GOOD: "It's great to hear things are going okay. Here are some resources that might still help.",
    GREAT: "Glad things are going well! Here are some resources if you ever need them.",
}

export const useAIStore = create((set, get) => ({
    messagesByChannel: {
        null: [welcomeMessage()],
    },
    unreadByChannel: {
        null: loadUnread('main'),
    },
    isThinking: false,
    activeAiChannelId: null,

    getMessages: () => {
        const id = get().activeAiChannelId
        return get().messagesByChannel[id] ?? []
    },
    getTotalUnread: () => {
        return Object.values(get().unreadByChannel).reduce((sum, n) => sum + n, 0)
    },

    setActiveAiChannel: (id) => {
        set((state) => {
            const key = channelKey(id)
            const alreadyHasMessages = id in state.messagesByChannel
            const alreadyHasUnread = id in state.unreadByChannel
            return {
                activeAiChannelId: id,
                messagesByChannel: alreadyHasMessages
                    ? state.messagesByChannel
                    : { ...state.messagesByChannel, [id]: [subProjectWelcomeMessage(id)] },
                unreadByChannel: alreadyHasUnread
                    ? state.unreadByChannel
                    : { ...state.unreadByChannel, [id]: loadUnread(key) },
            }
        })
    },

    markRead: () => {
        const id = get().activeAiChannelId
        const key = channelKey(id)
        persistUnread(key, 0)
        set((state) => ({
            unreadByChannel: { ...state.unreadByChannel, [id]: 0 },
        }))
    },

    addUserMessage: async (text) => {
        if (!text || !text.trim()) return

        const userText = text.trim()
        const activeId = get().activeAiChannelId

        const userMsg = {
            id: `user-${Date.now()}`,
            from: 'user',
            type: 'text',
            text: userText,
            createdAt: new Date().toISOString(),
        }

        set((state) => ({
            messagesByChannel: {
                ...state.messagesByChannel,
                [activeId]: [...(state.messagesByChannel[activeId] ?? []), userMsg],
            },
            isThinking: true,
        }))

        try {
            const history = buildHistory(get().messagesByChannel[activeId] ?? [])

            // Prepend sub-project context so the backend knows which channel the user is in
            const activeChannel = AI_SUBPROJECT_CHANNELS.find((c) => c.id === activeId)
            const contextualHistory = activeChannel
                ? [
                    `Context: The user is asking in the #${activeChannel.name} channel. This channel is specifically for ${activeChannel.slug} resources. Answer questions about ${activeChannel.slug} directly — do not redirect to other sub-projects.`,
                    ...history,
                  ]
                : history

            const backendResponse = await api.aiChat(userText, contextualHistory)

            const { text: aiText, intent: backendIntent, liveAgentSuggested } = extractAiPayload(backendResponse)

            const newMessages = []
            let renderedListings = false
            if (LISTING_INTENTS.has(backendIntent) && aiText.includes('•')) {
                const listings = parseServiceListings(aiText)
                if (listings.length > 0) {
                    const introLine = aiText.split('\n')[0].replace(/^•.*/, '').trim()
                    const intro = introLine || 'Here are some resources that may help:'
                    newMessages.push(makeAiTextMessage(intro))
                    listings.forEach((s) =>
                        newMessages.push({
                            id: `ai-${Date.now()}-${s.name}`,
                            from: 'ai',
                            type: 'service-listing',
                            ...s,
                            createdAt: new Date().toISOString(),
                        })
                    )
                    renderedListings = true
                }
            }
            if (!renderedListings) {
                newMessages.push(makeAiTextMessage(aiText))
            }

            // Item F: prepend crisis block when frontend detects distressed mood
            const frontendClassify = classifyIntent(userText)
            if (frontendClassify.distressed) {
                newMessages.unshift({
                    id: `ai-${Date.now()}-crisis`,
                    from: 'ai',
                    type: 'crisis-block',
                    createdAt: new Date().toISOString(),
                })
            }

            if (liveAgentSuggested) {
                newMessages.push({
                    id: `ai-${Date.now()}-esc`,
                    from: 'ai',
                    type: 'escalate-cta',
                    label: 'Talk to a human',
                    contextMessage: `Escalated from AI Assistant: "${userText}"`,
                    createdAt: new Date().toISOString(),
                })
            }

            // Item C3: handoff-cta — skip if we already showed real service listing cards
            const mapped = mapBackendIntent(backendIntent)
            if (!renderedListings && mapped?.intent === 'RESOURCE') {
                const project = findSubProject(mapped.subProject)
                if (project) {
                    newMessages.push({
                        id: `ai-${Date.now()}-handoff`,
                        from: 'ai',
                        type: 'handoff-cta',
                        projectName: project.name,
                        projectSlug: project.slug,
                        contextMessage: `User asked about ${project.name}: "${userText}"`,
                        createdAt: new Date().toISOString(),
                    })
                }
            }

            if (mapped?.intent === 'MOOD') {
                const frontendMood = classifyIntent(userText)
                get()._appendMoodResources(frontendMood.moodType || 'NEUTRAL', userText)
            }

            // Secondary fallback: if backend gave no recognized resource intent, use frontend classifier
            if (!mapped || mapped.intent === 'GENERAL') {
                const secondary = activeChannel
                    ? { intent: 'RESOURCE', subProject: activeChannel.slug }
                    : classifyIntent(userText)
                if (secondary.intent === 'RESOURCE') {
                    const project = findSubProject(secondary.subProject)
                    if (project) {
                        newMessages.push({
                            id: `ai-${Date.now()}-handoff-fallback`,
                            from: 'ai',
                            type: 'handoff-cta',
                            projectName: project.name,
                            projectSlug: project.slug,
                            contextMessage: `User asked about ${project.name}: "${userText}"`,
                            createdAt: new Date().toISOString(),
                        })
                    }
                }
            }

            set((state) => {
                const key = channelKey(activeId)
                const newCount = (state.unreadByChannel[activeId] ?? 0) + 1
                persistUnread(key, newCount)
                return {
                    messagesByChannel: {
                        ...state.messagesByChannel,
                        [activeId]: [...(state.messagesByChannel[activeId] ?? []), ...newMessages],
                    },
                    isThinking: false,
                    unreadByChannel: { ...state.unreadByChannel, [activeId]: newCount },
                }
            })
        } catch (error) {
            if (error.status === 401 || error.status === 403) {
                const errorMsg = makeAiTextMessage('Please login first. The AI Assistant needs your login token.')
                set((state) => {
                    const key = channelKey(activeId)
                    const newCount = (state.unreadByChannel[activeId] ?? 0) + 1
                    persistUnread(key, newCount)
                    return {
                        messagesByChannel: {
                            ...state.messagesByChannel,
                            [activeId]: [...(state.messagesByChannel[activeId] ?? []), errorMsg],
                        },
                        isThinking: false,
                        unreadByChannel: { ...state.unreadByChannel, [activeId]: newCount },
                    }
                })
                return
            }

            const activeChannel = AI_SUBPROJECT_CHANNELS.find((c) => c.id === activeId)
            const result = activeChannel
                ? { intent: 'RESOURCE', subProject: activeChannel.slug }
                : classifyIntent(userText)
            await get()._respond(result, userText)
        }
    },

    _respond: async (result, originalText) => {
        const activeId = get().activeAiChannelId
        const reply = []

        if (result.intent === 'URGENT') {
            reply.push({
                type: 'text',
                text: "That sounds important, and you don't have to navigate it alone. I'm connecting you with our team right now.",
            })
            reply.push({
                type: 'escalate-cta',
                label: 'Talk to a human',
                contextMessage: `Escalated from AI Assistant: "${originalText}"`,
            })
        } else if (result.intent === 'MOOD') {
            if (result.distressed) {
                reply.push({ type: 'crisis-block' })
            }
            reply.push({
                type: 'text',
                text: MOOD_PHRASES[result.moodType] ?? "Thanks for sharing. I found some support that might help.",
            })

            try {
                const moodRes = await api.postMood({ moodType: result.moodType, note: originalText })
                const resources = moodRes?.data?.resources || moodRes?.resources

                if (resources?.length) {
                    resources.forEach((r) =>
                        reply.push({
                            type: 'resource-card',
                            title: r.title || r.name,
                            description: r.summary || r.description,
                            link: r.url || r.link,
                        })
                    )
                } else {
                    const kc = findSubProject('kindconnect')
                    reply.push({ type: 'resource-card', title: kc.name, description: kc.tagline, link: `/${kc.slug}` })
                }
            } catch {
                const kc = findSubProject('kindconnect')
                reply.push({ type: 'resource-card', title: kc.name, description: kc.tagline, link: `/${kc.slug}` })
            }
        } else if (result.intent === 'RESOURCE') {
            const project = findSubProject(result.subProject)
            reply.push({
                type: 'text',
                text: `It sounds like ${project.name} could help with that.`,
            })
            reply.push({
                type: 'handoff-cta',
                projectName: project.name,
                projectSlug: project.slug,
                contextMessage: `User asked about ${project.name}: "${originalText}"`,
            })
        } else {
            reply.push({
                type: 'text',
                text: "I want to make sure you get the right help. Could you tell me a little more — or I can connect you with someone on our team.",
            })
        }

        const aiMessages = reply.map((r, i) => ({
            id: `ai-${Date.now()}-${i}`,
            from: 'ai',
            createdAt: new Date().toISOString(),
            ...r,
        }))

        set((state) => {
            const key = channelKey(activeId)
            const newCount = (state.unreadByChannel[activeId] ?? 0) + 1
            persistUnread(key, newCount)
            return {
                messagesByChannel: {
                    ...state.messagesByChannel,
                    [activeId]: [...(state.messagesByChannel[activeId] ?? []), ...aiMessages],
                },
                isThinking: false,
                unreadByChannel: { ...state.unreadByChannel, [activeId]: newCount },
            }
        })
    },

    _appendMoodResources: async (moodType, originalText) => {
        const activeId = get().activeAiChannelId
        try {
            const res = await api.postMood({ moodType, note: originalText })
            const resources = res?.data?.resources || []
            if (!resources.length) return

            const cards = resources.map((r, i) => ({
                id: `ai-${Date.now()}-mood-${i}`,
                from: 'ai',
                type: 'resource-card',
                title: r.title || r.name,
                description: r.summary || r.description,
                link: r.url || r.link,
                createdAt: new Date().toISOString(),
            }))

            set((state) => ({
                messagesByChannel: {
                    ...state.messagesByChannel,
                    [activeId]: [...(state.messagesByChannel[activeId] ?? []), ...cards],
                },
                unreadByChannel: {
                    ...state.unreadByChannel,
                    [activeId]: (state.unreadByChannel[activeId] ?? 0) + 1,
                },
            }))
        } catch {
            // silent — text response was already displayed
        }
    },

    clearMessages: () => {
        const id = get().activeAiChannelId
        const reset = id === null ? welcomeMessage() : subProjectWelcomeMessage(id)
        set((state) => ({
            messagesByChannel: { ...state.messagesByChannel, [id]: [reset] },
        }))
    },
}))
