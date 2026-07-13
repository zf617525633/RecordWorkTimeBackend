import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import 'vant/lib/index.css';
import { Toast, Button, Form, Field, CellGroup, NavBar } from 'vant';

const app = createApp(App)

app.use(router)
app.use(createPinia())
app.use(Toast)
app.use(Button)
app.use(Form)
app.use(Field)
app.use(CellGroup)
app.use(NavBar)

app.mount('#app')
