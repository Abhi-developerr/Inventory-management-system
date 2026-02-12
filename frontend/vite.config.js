import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  // Ensure Node globals used by sockjs-client are available in the browser bundle
  define: {
    global: 'window',
  },
})
