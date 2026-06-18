const BASE_URL = '/api'

function getToken() {
    return localStorage.getItem('cc_demo_token')
}

async function request(path, { method = 'GET', body, auth = true } = {}) {
    const headers = { 'Content-Type': 'application/json' }

    if (auth) {
        const token = getToken()
        if (token) headers['Authorization'] = `Bearer ${token}`
    }

    const res = await fetch(`${BASE_URL}${path}`, {
        method,
        headers,
        body: body !== undefined ? JSON.stringify(body) : undefined,
    })

    if (res.status === 204) return null

    const isJson = res.headers.get('content-type')?.includes('application/json')
    const data = isJson ? await res.json() : await res.text()

    if (!res.ok) {
        const message =
            (isJson && (data?.error || data?.message)) ||
            `Request failed (${res.status})`

        const error = new Error(message)
        error.status = res.status
        error.body = data
        throw error
    }

    return data
}

export const api = {
    login: (payload) =>
        request('/auth/login', {
            method: 'POST',
            body: payload,
            auth: false,
        }),

    myChannels: () => request('/channels/mine'),

    channelMessages: (channelId, page = 0, size = 50) =>
        request(`/channels/${channelId}/messages?page=${page}&size=${size}`),

    postMood: (payload) =>
        request('/moods', {
            method: 'POST',
            body: payload,
        }),

    aiChat: (message) =>
        request('/ai/chat', {
            method: 'POST',
            body: { message },
        }),
}

export { getToken, BASE_URL }