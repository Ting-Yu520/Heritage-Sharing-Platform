import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import axios from 'axios'

// ✨ Global request interceptor: automatically attach the user role on every API request
axios.interceptors.request.use(config => {
  const role = localStorage.getItem('userRole')
  if (role) {
    config.headers['X-User-Role'] = role
  }
  const username = localStorage.getItem('currentUser')
  if (username) {
    config.headers['X-User-Username'] = username
  }
  return config
})

const app = createApp(App)
app.use(router)
app.use(ElementPlus)
app.mount('#app')
