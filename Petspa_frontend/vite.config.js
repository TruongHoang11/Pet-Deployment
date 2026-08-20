import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173, // Hoặc cổng mặc định của bạn
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // Trỏ tới backend Java Spring Boot chạy local
        changeOrigin: true,
        secure: false,
      }
    }
  }
})