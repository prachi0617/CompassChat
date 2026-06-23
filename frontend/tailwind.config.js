/** @type {import('tailwindcss').Config} */
export default {
    content: ['./index.html', './src/**/*.{js,jsx}'],
    theme: {
        extend: {
            colors: {
                mint: {
                    50: '#EAF8F2',
                    100: '#CDEFE0',
                    500: '#2FBF86',
                    700: '#159267',
                },
                pink: {
                    50: '#FCEAF1',
                    100: '#F8C9DA',
                    700: '#C23A70',
                    950: '#4A1026',
                },
                yellow: {
                    50: '#FFF7E8',
                    100: '#FDEFC8',
                    700: '#C47A00',
                    900: '#6B4200',
                },
                ink: {
                    DEFAULT: '#111827',
                    70: '#374151',
                    50: '#6B7280',
                },
                blue: {
                    50: '#EFF6FF',
                    100: '#DBEAFE',
                    600: '#2563EB',
                    700: '#1D4ED8',
                },
                orange: {
                    50: '#FFF7ED',
                    100: '#FFEDD5',
                    600: '#EA580C',
                    700: '#C2410C',
                },
            },
            fontFamily: {
                display: ['Georgia', 'serif'],
            },
            keyframes: {
                'slide-in': {
                    '0%': { transform: 'translateX(100%)' },
                    '100%': { transform: 'translateX(0)' },
                },
                'slide-out': {
                    '0%': { transform: 'translateX(0)' },
                    '100%': { transform: 'translateX(100%)' },
                },
                'fade-in': {
                    '0%': { opacity: '0' },
                    '100%': { opacity: '1' },
                },
                'fade-out': {
                    '0%': { opacity: '1' },
                    '100%': { opacity: '0' },
                },
            },
            animation: {
                'slide-in': 'slide-in 250ms cubic-bezier(0.16, 1, 0.3, 1) both',
                'slide-out': 'slide-out 200ms cubic-bezier(0.16, 1, 0.3, 1) both',
                'fade-in': 'fade-in 200ms ease both',
                'fade-out': 'fade-out 150ms ease both',
            },
        },
    },
    plugins: [],
}