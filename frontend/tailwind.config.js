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
                    700: '#C47A00',
                    900: '#6B4200',
                },
                ink: {
                    DEFAULT: '#111827',
                    70: '#374151',
                    50: '#9CA3AF',
                },
            },
            fontFamily: {
                display: ['Georgia', 'serif'],
            },
        },
    },
    plugins: [],
}