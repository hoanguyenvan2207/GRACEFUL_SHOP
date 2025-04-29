// vite.config.js
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],

  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (p) => p.replace(/^\/api/, '')
      },
      '/ws-chat': {
        target: 'http://localhost:8080',
        ws: true,
        changeOrigin: true
      },
      '/ghtk': {
        target: 'https://services.giaohangtietkiem.vn',
        changeOrigin: true,
        rewrite: (p) => p.replace(/^\/ghtk/, ''),
      },
      '/provinces': {
        target: 'https://provinces.open-api.vn',
        changeOrigin: true,
        rewrite: (p) => p.replace(/^\/provinces/, '')
      }
    }
  },

  define: {
    global: 'window'
  }
})
