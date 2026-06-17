/** @type {import('tailwindcss').Config} */
export default {
    content: ['./index.html', './src/**/*.{js,jsx}'],
    theme: {
        extend: {
            colors: {
                mint: {
                    50: '#F0FBF7',
                    100: '#D7F4E7',
                    300: '#7DD9B0',
                    500: '#3DBE8A',
                    700: '#1F8C61',
                },
                pink: {
                    50: '#FFF1F5',
                    300: '#FBA5C0',
                    500: '#EC4899',
                    700: '#BE185D',
                },
                yellow: {
                    50: '#FFFBEB',
                    300: '#FCD34D',
                    500: '#F59E0B',
                },
                ink: {
                    DEFAULT: '#0F172A',
                    70: '#475569',
                    50: '#94A3B8',
                },
            },
            fontSize: {
                meta: '0.75rem',
                body: '0.9375rem',
            },
            fontFamily: {
                display: ['"Sora"', '"Inter"', 'system-ui', 'sans-serif'],
                body: ['"Inter"', 'system-ui', 'sans-serif'],
            },
            transitionTimingFunction: {
                'panel-out': 'cubic-bezier(0.16, 1, 0.3, 1)',
            },
            keyframes: {
                'slide-in': {
                    '0%': { transform: 'translateX(100%)' },
                    '100%': { transform: 'translateX(0)' },
                },
                'fade-in': {
                    '0%': { opacity: '0' },
                    '100%': { opacity: '1' },
                },
                'badge-pulse': {
                    '0%, 100%': { transform: 'scale(1)' },
                    '50%': { transform: 'scale(1.12)' },
                },
            },
            animation: {
                'slide-in': 'slide-in 250ms cubic-bezier(0.16, 1, 0.3, 1)',
                'fade-in': 'fade-in 200ms ease-out',
                'badge-pulse': 'badge-pulse 1.8s ease-in-out 3',
            },
        },
    },
    plugins: [],
}