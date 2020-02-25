import 'default-passive-events';
import Vue from 'vue';
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import router from './router';
import App from './App.vue';
import '../public/static/css/reset.styl';
import store from './store';
import Axios from './common/js/http';
import {CONSTANT} from "./common/js/constant";

Vue.config.productionTip = false;
Vue.use(ElementUI, {locale: 'en'});
Axios.defaults.withCredentials = true;
Axios.defaults.baseURL = CONSTANT.BASE_URL;
Vue.prototype.$axios = Axios;
new Vue({
    router,
    store,
    render: h => h(App),
}).$mount('#app');
