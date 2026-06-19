import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import path from 'path'

export default defineConfig({
    plugins: [react()],

    resolve: {
        alias: {
            // Force all cookie imports (including react-router's nested v1.x) to use cookie@0.6.0
            'cookie': path.resolve('./node_modules/cookie/index.js'),
        },
    },

    server: {
        port: 5173,
        strictPort: true,
        proxy: {
            '/api': {
                target: 'http://localhost:8081',
                changeOrigin: true,
                secure: false,
            },
        },
    },

    define: {
        global: 'window',
    },

    optimizeDeps: {
        include: ['cookie', 'set-cookie-parser'],
    },
})