// vite.config.js
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { viteStaticCopy } from 'vite-plugin-static-copy'

export default defineConfig({
  plugins: [
    vue(),
    viteStaticCopy({
      targets: [
        {
          // copy nguyên thư mục và mọi file con
          src: 'src/i18n/**/*',
          // đích là /dist/i18n
          dest: 'i18n'
        }
      ]
    })
  ],

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
