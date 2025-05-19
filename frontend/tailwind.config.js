/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          DEFAULT: '#3f51b5',
          light: '#757de8',
          dark: '#002984',
        },
        secondary: {
          DEFAULT: '#f50057',
          light: '#ff5983',
          dark: '#bb002f',
        },
        accent: {
          DEFAULT: '#ff4081',
          light: '#ff79b0',
          dark: '#c60055',
        },
      },
      fontFamily: {
        sans: ['Roboto', 'Helvetica', 'Arial', 'sans-serif'],
      },
    },
  },
  plugins: [
    require('@tailwindcss/forms'),
  ],
}
