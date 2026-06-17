import { create } from 'zustand'
import { classifyIntent } from '../components/compass-chat/ai-assistant/intentRouter'
import { api } from '../lib/api'
import subProjectData from '../lib/resources.json'

function welcomeMessage() {
    return {
        id: 'ai-welcome',
        from: 'ai',
        type: 'text',
        text: "Hi, I'm your Community Compass guide. Tell me what's on your mind — housing, support, or just where to start — and I'll point you the right way.",
        createdAt: new Date().toISOString(),
    }
}

function findSubProject(slug) {
    return subProjectData.subProjects.find((p) => p.slug === slug)
}

export const useAIStore = create((set, get) => ({
    messages: [welcomeMessage()],
    unreadCount: 1,
    isThinking: false,

    markRead: () => set({ unreadCount: 0 }),

    addUserMessage: async (text) => {
        const userMsg = {
            id: `user-${Date.now()}`,
            from: 'user',
            type: 'text',
            text,
            createdAt: new Date().toISOString(),
        }
        set((state) => ({ messages: [...state.messages, userMsg], isThinking: true }))

        const result = classifyIntent(text)
        await get()._respond(result, text)
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
                text: `Thanks for telling me — it sounds like things feel ${result.moodType.toLowerCase().replace('_', ' ')} right now. I found some support that might help.`,
            })

            try {
                const moodRes = await api.postMood({ moodType: result.moodType, note: originalText })
                const resources = moodRes?.data?.resources || moodRes?.resources
                if (resources?.length) {
                    resources.forEach((r) =>
                        reply.push({ type: 'resource-card', title: r.title || r.name, description: r.description, link: r.link })
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
            reply.push({ type: 'resource-card', title: project.name, description: project.tagline, link: `/${project.slug}` })
        } else {
            reply.push({
                type: 'text',
                text: "I want to make sure you get the right help. Could you tell me a little more — or I can connect you with someone on our team.",
            })
            reply.push({ type: 'smart-suggestion', title: 'Not sure where to start?', body: 'Try telling me how you\'re feeling, or what kind of support you need (housing, youth services, wellness, or news).' })
        }

        const aiMessages = reply.map((r, i) => ({
            id: `ai-${Date.now()}-${i}`,
            from: 'ai',
            createdAt: new Date().toISOString(),
            ...r,
        }))

        set((state) => ({ messages: [...state.messages, ...aiMessages], isThinking: false }))
    },
}))