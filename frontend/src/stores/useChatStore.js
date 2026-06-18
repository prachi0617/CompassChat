import { create } from 'zustand'
import { api } from '../lib/api'
import {
    subscribeToChannel,
    unsubscribeFromChannel,
    publishMessage,
    isSocketConnected,
} from '../lib/websocket'

export const AI_SUBPROJECT_CHANNELS = [
    { id: 'ai-homematch-help', name: 'homematch-help', slug: 'homematch', type: 'AI_CHANNEL' },
    { id: 'ai-futurepath-help', name: 'futurepath-help', slug: 'futurepath', type: 'AI_CHANNEL' },
    { id: 'ai-kindconnect-help', name: 'kindconnect-help', slug: 'kindconnect', type: 'AI_CHANNEL' },
    { id: 'ai-firststep-help', name: 'firststep-help', slug: 'firststep', type: 'AI_CHANNEL' },
]

const AI_CHANNEL_IDS = new Set(AI_SUBPROJECT_CHANNELS.map((c) => c.id))

const MOCK_CHANNELS = [
    { id: 'mock-general', name: 'general', type: 'CHANNEL', memberCount: 12 },
    { id: 'mock-case-workers', name: 'case-workers', type: 'CHANNEL', memberCount: 5 },
    { id: 'mock-housing-team', name: 'housing-team', type: 'CHANNEL', memberCount: 7 },
    { id: 'mock-homematch-help', name: 'homematch-help', type: 'CHANNEL', memberCount: 3 },
    { id: 'mock-futurepath-help', name: 'futurepath-help', type: 'CHANNEL', memberCount: 2 },
    { id: 'mock-kindconnect-help', name: 'kindconnect-help', type: 'CHANNEL', memberCount: 4 },
    { id: 'mock-firststep-help', name: 'firststep-help', type: 'CHANNEL', memberCount: 2 },
]

const MOCK_DMS = [
    { id: 'mock-dm-admin', name: 'Admin Team', type: 'DM' },
    { id: 'mock-dm-caseworker', name: 'Case Worker — Jordan', type: 'DM' },
]

function buildMockMessages() {
    const now = Date.now()
    const lines = [
        ['Jordan Reyes', 'Morning! Reminder that the housing voucher clinic is at 10am today.'],
        ['Guest User', 'Thanks for the heads up — I will be there.'],
        ['Sam Okafor', 'Does anyone have the updated intake form?'],
        ['Jordan Reyes', "Yep, I'll drop it in here in a sec."],
        ['Jordan Reyes', 'intake-form-v3.pdf'],
        ['Guest User', 'Got it, thank you!'],
        ['Sam Okafor', "I'll review with the client this afternoon."],
        ['Priya Nair', 'Quick one — is the wellness check-in still on for Friday?'],
        ['Jordan Reyes', 'Yes, confirmed for Friday at 2pm.'],
        ['Guest User', 'Perfect, see everyone there 👋'],
    ]
    return lines.map(([sender, body], i) => ({
        id: `mock-msg-${i}`,
        body,
        sender: { displayName: sender },
        createdAt: new Date(now - (lines.length - i) * 60000 * 7).toISOString(),
        isMock: true,
    }))
}

export const useChatStore = create((set, get) => ({
    channels: [],
    dms: [],
    messagesByConversation: {},
    activeConversationId: null,
    mode: 'mock', // 'mock' | 'live'
    loading: false,

    loadConversations: async () => {
        set({ loading: true })
        if (isSocketConnected()) {
            try {
                const channels = await api.myChannels()
                set({
                    channels: channels.filter((c) => c.type !== 'DIRECT'),
                    dms: channels.filter((c) => c.type === 'DIRECT'),
                    mode: 'live',
                    loading: false,
                })
                return
            } catch (err) {
                console.warn('Falling back to mock chat data:', err)
            }
        }

        // Mock fallback (M2 demo state, or backend unreachable)
        set({
            channels: MOCK_CHANNELS,
            dms: MOCK_DMS,
            mode: 'mock',
            loading: false,
            messagesByConversation: { [MOCK_CHANNELS[0].id]: buildMockMessages() },
        })
    },

    isAiChannel: (id) => AI_CHANNEL_IDS.has(id),
    getAiChannelSlug: (id) => AI_SUBPROJECT_CHANNELS.find((c) => c.id === id)?.slug ?? null,

    selectConversation: async (conversationId) => {
        const prev = get().activeConversationId
        if (prev && get().mode === 'live' && !AI_CHANNEL_IDS.has(prev)) unsubscribeFromChannel(prev)

        set({ activeConversationId: conversationId })

        if (conversationId === null) return // AI Assistant — no fetch needed
        if (AI_CHANNEL_IDS.has(conversationId)) return // AI sub-project channel — handled by useAIStore

        if (get().mode === 'live') {
            try {
                const page = await api.channelMessages(conversationId, 0, 50)
                const messages = [...(page.content || page)].reverse()
                set((state) => ({
                    messagesByConversation: { ...state.messagesByConversation, [conversationId]: messages },
                }))
                subscribeToChannel(conversationId, (incoming) => {
                    set((state) => ({
                        messagesByConversation: {
                            ...state.messagesByConversation,
                            [conversationId]: [...(state.messagesByConversation[conversationId] || []), incoming],
                        },
                    }))
                })
            } catch (err) {
                console.warn('Could not load channel history:', err)
            }
            return
        }

        // Mock mode: generate messages for this conversation on first visit
        if (!get().messagesByConversation[conversationId]) {
            set((state) => ({
                messagesByConversation: {
                    ...state.messagesByConversation,
                    [conversationId]: conversationId === MOCK_CHANNELS[0].id ? buildMockMessages() : [],
                },
            }))
        }
    },

    sendMessage: (conversationId, body) => {
        if (get().mode === 'live' && isSocketConnected()) {
            publishMessage(conversationId, body)
            return
        }

        // Mock mode: append locally
        const message = {
            id: `local-${Date.now()}`,
            body,
            sender: { displayName: 'Guest User' },
            createdAt: new Date().toISOString(),
            isMock: true,
        }
        set((state) => ({
            messagesByConversation: {
                ...state.messagesByConversation,
                [conversationId]: [...(state.messagesByConversation[conversationId] || []), message],
            },
        }))
    },

    switchToSubProjectChannel: (slug, contextMessage) => {
        const channelName = `${slug}-help`
        const existing = get().channels.find((c) => c.name === channelName)
        const targetId = existing?.id ?? `mock-${slug}-help`

        if (!get().messagesByConversation[targetId]) {
            set((state) => ({
                messagesByConversation: { ...state.messagesByConversation, [targetId]: [] },
            }))
        }

        set({ activeConversationId: targetId })

        if (contextMessage) {
            const systemMsg = {
                id: `sys-${Date.now()}`,
                body: `📋 Context from AI Assistant: ${contextMessage}`,
                sender: { displayName: 'Community Compass AI' },
                createdAt: new Date().toISOString(),
                isMock: true,
                isSystem: true,
            }
            set((state) => ({
                messagesByConversation: {
                    ...state.messagesByConversation,
                    [targetId]: [...(state.messagesByConversation[targetId] ?? []), systemMsg],
                },
            }))
        }
        return targetId
    },

    clearConversation: (conversationId) => {
        set((state) => ({
            messagesByConversation: { ...state.messagesByConversation, [conversationId]: [] },
        }))
    },

    switchToAdminDm: (contextMessage) => {
        const adminDm = get().dms.find((d) => /admin/i.test(d.name)) || MOCK_DMS[0]
        set({ activeConversationId: adminDm.id })
        if (contextMessage) {
            get().sendMessage(adminDm.id, contextMessage)
        }
        return adminDm
    },
}))