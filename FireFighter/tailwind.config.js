/** @type {import('tailwindcss').Config} */
module.exports = {
  darkMode: false,

  content: [
    "./src/**/*.{html,ts,scss}",   // scan all templates, components & styles
    './node_modules/flowbite/**/*.js',
  ],
  theme: {
    extend: {
      // Custom color palette
      colors: {
        'matisse': {
          '50': '#f5faff',    // New lightest color
          '100': '#f0f8ff',   // Was 50
          '200': '#e0f0fe',   // Was 100
          '300': '#b9e1fe',   // Was 200
          '400': '#7bcbfe',   // Was 300
          '500': '#35b0fb',   // Was 400
          '600': '#0b96ec',   // Was 500
          '700': '#0077ca',   // Was 600
          '800': '#0166b1',   // Was 700
          '900': '#055087',   // Was 800
          '950': '#032842',   // New darkest color (darker than 900/old 800)
        },
        'darkblue': '#080a1c', // Very dark blue, almost black
      },
    },
  },
  plugins: [
    // e.g. require('@tailwindcss/forms')
    require('flowbite/plugin')
  ],
};