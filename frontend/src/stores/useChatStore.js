import { create } from 'zustand'
import { api } from '../lib/api'
import { getStoredToken } from '../lib/auth'
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

// Fixed display-only topic labels shown under "Channels". These do not
// map to backend channels — clicking them opens an empty thread.
const TOPIC_CHANNELS = [
    { id: 'topic-notices', name: 'Notices', type: 'CHANNEL', memberCount: 12 },
    { id: 'topic-housing-search', name: 'Housing Search', type: 'CHANNEL', memberCount: 8 },
    { id: 'topic-moods-gifs', name: 'Moods/Gifs', type: 'CHANNEL', memberCount: 15 },
    { id: 'topic-community', name: 'Community', type: 'CHANNEL', memberCount: 20 },
]

// The seeded backend DIRECT channel that the demo script posts into.
const ADMIN_DM_BACKEND_NAME = 'dm-resident-zip'

// Case Worker DM is always a pure mock thread.
const MOCK_DM_CASEWORKER = { id: 'mock-dm-caseworker', name: 'Case Worker — Jordan', type: 'DM' }
// Fallback Admin Team DM id when no live backend channel is available.
const MOCK_DM_ADMIN = { id: 'mock-dm-admin', name: 'Admin Team', type: 'DM' }

// Scripted Demo Resident ↔ Zip Carter conversation for the Admin Team DM.
// Fired one line per Enter press (on an empty message box) during the demo.
// Demo Resident lines use 'Guest User' so MessageThread right-aligns them
// (its currentUserName defaults to 'Guest User'); Zip lines align left.
const ADMIN_TEAM_SCRIPT = [
    { displayName: 'Guest User', body: 'Hi - I need a place to stay.' },
    { displayName: 'Zip Carter', body: 'Hi! I just saw your message - If you are between 18 and 25 you may qualify for services through FuturePath.' },
    { displayName: 'Guest User', body: 'Actually, I have a housing voucher.' },
    { displayName: 'Zip Carter', body: 'Good news, HomeMatch offers housing search for people using a housing voucher. If you would like to learn more about either one of these or other resources, let me know and I can connect you.' },
    { displayName: 'Guest User', body: "Ok, I saw that on your website. Let me read about it and I'll get back to you." },
    { displayName: 'Zip Carter', body: 'Sounds great - take your time and reach out whenever you\'re ready.' },
]

function buildMockMessages() {
    const now = Date.now()
    const lines = [
        ['Jordan Reyes', 'Morning! Reminder that the housing voucher clinic is at 10am today.'],
        ['Guest User', 'Thanks for the heads up — I will be there.'],
        ['Jordan Reyes', "Yep, I'll drop the intake form in here in a sec."],
        ['Jordan Reyes', 'intake-form-v3.pdf'],
        ['Guest User', 'Got it, thank you!'],
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

// username → display name overrides for demo personas
const DISPLAY_NAMES = {
    'demo-resident': 'Demo Resident',
    'zip': 'Zip Carter',
    'holson-prymer': 'Holson Prymer',
    'erik-stevens': 'Erik Stevens',
    'compass-bot': 'CompassBot',
}

function normalizeMessage(msg, userMap) {
    const senderId = msg.senderId
    const username = userMap[senderId] || senderId
    const displayName = DISPLAY_NAMES[username] || username
    return {
        id: msg.id || msg.messageId,
        body: msg.body || msg.content,
        sender: { displayName },
        createdAt: msg.createdAt || msg.sentAt,
    }
}

export const useChatStore = create((set, get) => ({
    channels: [],
    dms: [],
    userMap: {}, // userId → username
    messagesByConversation: {},
    activeConversationId: null,
    mode: 'mock', // 'mock' | 'live'
    loading: false,
    adminScriptCursor: 0, // index of the next Admin Team scripted line to fire

    loadConversations: async () => {
        set({ loading: true })
        // Channels/users come from plain REST and only need a valid token —
        // they do NOT depend on the WebSocket handshake (the socket is only
        // used later for live message push).
        if (getStoredToken()) {
            try {
                const [channelsRes, usersRes] = await Promise.all([
                    api.myChannels(),
                    api.listUsers(),
                ])
                const allChannels = channelsRes?.data ?? []
                const userList = usersRes?.data ?? []
                const userMap = Object.fromEntries(userList.map((u) => [u.id, u.username]))

                // Admin Team DM points at the live backend DIRECT channel so
                // the demo script's messages appear in real time.
                const backendAdminDm = allChannels.find((c) => c.name === ADMIN_DM_BACKEND_NAME)
                const adminDm = backendAdminDm
                    ? { id: backendAdminDm.id, name: 'Admin Team', type: 'DM' }
                    : MOCK_DM_ADMIN

                set({
                    channels: TOPIC_CHANNELS,
                    dms: [adminDm, MOCK_DM_CASEWORKER],
                    userMap,
                    mode: 'live',
                    loading: false,
                    // Case Worker is always a mock thread.
                    messagesByConversation: { [MOCK_DM_CASEWORKER.id]: buildMockMessages() },
                })
                return
            } catch (err) {
                console.warn('Falling back to mock chat data:', err)
            }
        }

        // Mock fallback (offline, or backend unreachable)
        set({
            channels: TOPIC_CHANNELS,
            dms: [MOCK_DM_ADMIN, MOCK_DM_CASEWORKER],
            mode: 'mock',
            loading: false,
            messagesByConversation: { [MOCK_DM_CASEWORKER.id]: buildMockMessages() },
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

        // Display-only topic labels and mock threads never hit the backend.
        const isLocalConversation =
            conversationId.startsWith('topic-') || conversationId.startsWith('mock-')

        if (get().mode === 'live' && !isLocalConversation) {
            try {
                const page = await api.channelMessages(conversationId, 0, 50)
                const raw = page?.data?.content || page?.content || page?.data || page || []
                const userMap = get().userMap
                const messages = [...raw].reverse().map((m) => normalizeMessage(m, userMap))
                set((state) => ({
                    messagesByConversation: { ...state.messagesByConversation, [conversationId]: messages },
                }))
                subscribeToChannel(conversationId, (incoming) => {
                    const norm = normalizeMessage(incoming, get().userMap)
                    set((state) => ({
                        messagesByConversation: {
                            ...state.messagesByConversation,
                            [conversationId]: [...(state.messagesByConversation[conversationId] || []), norm],
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
                    [conversationId]: conversationId === 'mock-dm-caseworker' ? buildMockMessages() : [],
                },
            }))
        }
    },

    sendMessage: (conversationId, body, attachment = null) => {
        // Text-only live messages go over the socket. Images are demo-only
        // (data URLs aren't sent over STOMP), so any attachment appends locally.
        if (!attachment && get().mode === 'live' && isSocketConnected()) {
            publishMessage(conversationId, body)
            return
        }

        // Mock / local append
        const message = {
            id: `local-${Date.now()}`,
            body,
            attachment: attachment || null,
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
                body: contextMessage,
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
        const adminDm = get().dms[0] || MOCK_DM_ADMIN
        set({ activeConversationId: adminDm.id })
        if (contextMessage) {
            get().sendMessage(adminDm.id, contextMessage)
        }
        return adminDm
    },

    // The Admin Team DM id (first DM, set in loadConversations; falls back to mock).
    getAdminDmId: () => get().dms.find((d) => d.name === 'Admin Team')?.id ?? MOCK_DM_ADMIN.id,

    // Fire the next line of the scripted Admin Team conversation. Each call
    // appends one message (alternating Demo Resident / Zip) until exhausted.
    fireNextAdminScriptLine: () => {
        const cursor = get().adminScriptCursor
        if (cursor >= ADMIN_TEAM_SCRIPT.length) return // script exhausted

        const adminDmId = get().getAdminDmId()
        const line = ADMIN_TEAM_SCRIPT[cursor]
        const message = {
            id: `admin-script-${cursor}`,
            body: line.body,
            sender: { displayName: line.displayName },
            createdAt: new Date().toISOString(),
            isMock: true,
        }
        set((state) => ({
            adminScriptCursor: cursor + 1,
            messagesByConversation: {
                ...state.messagesByConversation,
                [adminDmId]: [...(state.messagesByConversation[adminDmId] || []), message],
            },
        }))
    },

    // Clear the Admin Team thread and reset the script so it can be re-run.
    resetAdminScript: () => {
        const adminDmId = get().getAdminDmId()
        set((state) => ({
            adminScriptCursor: 0,
            messagesByConversation: { ...state.messagesByConversation, [adminDmId]: [] },
        }))
    },
}))