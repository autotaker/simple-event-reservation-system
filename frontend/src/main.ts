import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import './styles/base.css';
import './styles/layout.css';
import './styles/components.css';
import './styles/state.css';
import './styles/tokens.css';

createApp(App).use(router).mount('#app');
