import { create } from 'zustand'
import { classifyIntent } from '../components/compass-chat/ai-assistant/intentRouter'
import { api } from '../lib/api'
import subProjectData from '../lib/resources.json'

function welcomeMessage() {
    return {
        id: 'ai-welcome',
        from: 'ai',
        type: 'text',
        text: "Hi, I'm here if you need help finding resources",
        createdAt: new Date().toISOString(),
    }
}

function findSubProject(slug) {
    return subProjectData.subProjects.find((p) => p.slug === slug)
}

// Item 2: returns all three fields from the backend ChatResponse wrapper
function extractAiPayload(response) {
    return {
        text: response?.data?.response || response?.data?.message || 'I received your message.',
        intent: response?.data?.intent || null,
        liveAgentSuggested: response?.data?.liveAgentSuggested === true,
    }
}

// Item 2: maps backend IntentType enum values to frontend intent/subProject shape
const BACKEND_TO_SUBPROJECT = {
    HOUSING: 'homematch',
    YOUTH: 'futurepath',
    CIVIC: 'firststep',
    RESOURCES: 'kindconnect',
}

function mapBackendIntent(intent) {
    if (!intent) return null
    if (intent === 'ESCALATE') return { intent: 'URGENT' }
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

// Item 5: persist unread count to localStorage so it survives page reloads
function persistUnread(count) {
    localStorage.setItem('cc_ai_unread', String(count))
}

// Item 7: build last-N text messages as history for the backend
function buildHistory(messages) {
    return messages
        .filter((m) => m.type === 'text' && m.text)
        .slice(-6)
        .map((m) => m.text)
}

export const useAIStore = create((set, get) => ({
    messages: [welcomeMessage()],
    // Item 5: initialize from localStorage; default to 1 for the welcome message
    unreadCount: Number(localStorage.getItem('cc_ai_unread') ?? 1),
    isThinking: false,

    // Item 5: clear persisted count when user opens the AI panel
    markRead: () => {
        persistUnread(0)
        set({ unreadCount: 0 })
    },

    addUserMessage: async (text) => {
        if (!text || !text.trim()) return

        const userText = text.trim()

        const userMsg = {
            id: `user-${Date.now()}`,
            from: 'user',
            type: 'text',
            text: userText,
            createdAt: new Date().toISOString(),
        }

        set((state) => ({
            messages: [...state.messages, userMsg],
            isThinking: true,
        }))

        try {
            // Item 7: include recent message history for multi-turn context
            const history = buildHistory(get().messages)
            const backendResponse = await api.aiChat(userText, history)

            // Item 2: extract all three fields instead of just the text string
            const { text: aiText, intent: backendIntent, liveAgentSuggested } = extractAiPayload(backendResponse)

            const newMessages = [makeAiTextMessage(aiText)]

            // Item 2: backend signals escalation → append EscalateCTA
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

            // Item 2: backend intent drives card rendering on the happy path
            const mapped = mapBackendIntent(backendIntent)
            if (mapped?.intent === 'RESOURCE') {
                const project = findSubProject(mapped.subProject)
                if (project) {
                    newMessages.push({
                        id: `ai-${Date.now()}-res`,
                        from: 'ai',
                        type: 'resource-card',
                        title: project.name,
                        description: project.tagline,
                        link: `/${project.slug}`,
                        createdAt: new Date().toISOString(),
                    })
                }
            }

            // Item 3b: MOOD intent → POST to /api/moods to log + get real resource cards
            if (mapped?.intent === 'MOOD') {
                const frontendMood = classifyIntent(userText)
                get()._appendMoodResources(frontendMood.moodType || 'NEUTRAL', userText)
            }

            set((state) => {
                const newCount = state.unreadCount + 1
                persistUnread(newCount)
                return { messages: [...state.messages, ...newMessages], isThinking: false, unreadCount: newCount }
            })
        } catch (error) {
            if (error.status === 401 || error.status === 403) {
                const errorMsg = makeAiTextMessage(
                    'Please login first. The AI Assistant needs your login token.'
                )
                set((state) => {
                    const newCount = state.unreadCount + 1
                    persistUnread(newCount)
                    return { messages: [...state.messages, errorMsg], isThinking: false, unreadCount: newCount }
                })
                return
            }

            const result = classifyIntent(userText)
            await get()._respond(result, userText)
        }
    },

    _respond: async (result, originalText) => {
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
                text: `Thanks for telling me — it sounds like things feel ${result.moodType
                    .toLowerCase()
                    .replace('_', ' ')} right now. I found some support that might help.`,
            })

            try {
                const moodRes = await api.postMood({
                    moodType: result.moodType,
                    note: originalText,
                })

                const resources = moodRes?.data?.resources || moodRes?.resources

                if (resources?.length) {
                    // Item 3a: backend sends `summary` and `url`, not `description` and `link`
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
                    reply.push({
                        type: 'resource-card',
                        title: kc.name,
                        description: kc.tagline,
                        link: `/${kc.slug}`,
                    })
                }
            } catch {
                const kc = findSubProject('kindconnect')
                reply.push({
                    type: 'resource-card',
                    title: kc.name,
                    description: kc.tagline,
                    link: `/${kc.slug}`,
                })
            }
        } else if (result.intent === 'RESOURCE') {
            const project = findSubProject(result.subProject)
            reply.push({
                type: 'text',
                text: `It sounds like ${project.name} could help with that.`,
            })
            reply.push({
                type: 'resource-card',
                title: project.name,
                description: project.tagline,
                link: `/${project.slug}`,
            })
        } else {
            reply.push({
                type: 'text',
                text: "I want to make sure you get the right help. Could you tell me a little more — or I can connect you with someone on our team.",
            })
            reply.push({
                type: 'smart-suggestion',
                title: 'Not sure where to start?',
                body: "Try telling me how you're feeling, or what kind of support you need: housing, youth services, wellness, or news.",
            })
        }

        const aiMessages = reply.map((r, i) => ({
            id: `ai-${Date.now()}-${i}`,
            from: 'ai',
            createdAt: new Date().toISOString(),
            ...r,
        }))

        set((state) => {
            const newCount = state.unreadCount + 1
            persistUnread(newCount)
            return { messages: [...state.messages, ...aiMessages], isThinking: false, unreadCount: newCount }
        })
    },

    // Item 3b: called from the happy path when backend returns MOOD intent;
    // POSTs to /api/moods to log the mood and get real resource cards from the service directory
    _appendMoodResources: async (moodType, originalText) => {
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
                messages: [...state.messages, ...cards],
                unreadCount: state.unreadCount + 1,
            }))
        } catch {
            // silent — text response was already displayed
        }
    },
}))
