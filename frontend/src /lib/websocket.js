import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import { getStoredToken } from './auth'

let stompClient = null
const subscriptions = new Map()

export function connectSocket({ onConnect, onDisconnect, onError } = {}) {
    if (stompClient?.active) return stompClient

    const token = getStoredToken()

    stompClient = new Client({
        webSocketFactory: () => new SockJS('/ws'),
        connectHeaders: token ? { Authorization: `Bearer ${token}` } : {},
        reconnectDelay: 4000,
        heartbeatIncoming: 10000,
        heartbeatOutgoing: 10000,
        onConnect: () => onConnect?.(),
        onDisconnect: () => onDisconnect?.(),
        onStompError: (frame) => {
            console.error('STOMP error', frame)
            onError?.(frame)
        },
    })

    stompClient.activate()
    return stompClient
}

export function disconnectSocket() {
    if (stompClient) {
        subscriptions.forEach((sub) => sub.unsubscribe())
        subscriptions.clear()
        stompClient.deactivate()
        stompClient = null
    }
}

export function subscribeToChannel(channelId, callback) {
    if (!stompClient?.active) return null
    const dest = `/topic/channels/${channelId}`
    subscriptions.get(dest)?.unsubscribe()
    const sub = stompClient.subscribe(dest, (message) => callback(JSON.parse(message.body)))
    subscriptions.set(dest, sub)
    return sub
}

export function unsubscribeFromChannel(channelId) {
    const dest = `/topic/channels/${channelId}`
    subscriptions.get(dest)?.unsubscribe()
    subscriptions.delete(dest)
}

export function subscribeToErrors(callback) {
    if (!stompClient?.active) return null
    const dest = '/user/queue/errors'
    subscriptions.get(dest)?.unsubscribe()
    const sub = stompClient.subscribe(dest, (message) => callback(JSON.parse(message.body)))
    subscriptions.set(dest, sub)
    return sub
}

export function publishMessage(channelId, body) {
    if (!stompClient?.active) return false
    stompClient.publish({
        destination: `/app/channels/${channelId}/messages`,
        body: JSON.stringify({ body }),
    })
    return true
}

export function isSocketConnected() {
    return !!stompClient?.active
}