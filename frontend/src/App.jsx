import { useEffect, useState } from 'react'
import { Routes, Route } from 'react-router-dom'
import Layout from './components/community-compass/Layout'
import DashboardPage from './pages/DashboardPage'
import HomeMatchPage from './pages/HomeMatchPage'
import FuturePathPage from './pages/FuturePathPage'
import KindConnectPage from './pages/KindConnectPage'
import FirstStepPage from './pages/FirstStepPage'
import Toast from './components/ui/Toast'
import { useAuthStore } from './stores/useAuthStore'
import { subscribeToErrors } from './lib/websocket'

export default function App() {
    const boot = useAuthStore((s) => s.boot)
    const status = useAuthStore((s) => s.status)
    const [toastMessage, setToastMessage] = useState(null)

    useEffect(() => {
        boot()
    }, [boot])

    useEffect(() => {
        if (status !== 'ready') return
        const sub = subscribeToErrors((err) => {
            setToastMessage(err?.message || 'Something went wrong. Please try again.')
        })
        return () => sub?.unsubscribe()
    }, [status])

    return (
        <>
            <Routes>
                <Route element={<Layout />}>
                    <Route path="/" element={<DashboardPage />} />
                    <Route path="/homematch" element={<HomeMatchPage />} />
                    <Route path="/futurepath" element={<FuturePathPage />} />
                    <Route path="/kindconnect" element={<KindConnectPage />} />
                    <Route path="/firststep" element={<FirstStepPage />} />
                </Route>
            </Routes>
            <Toast message={toastMessage} onDismiss={() => setToastMessage(null)} />
        </>
    )
}
