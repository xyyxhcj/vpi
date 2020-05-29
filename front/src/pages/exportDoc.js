import 'default-passive-events';
import Vue from 'vue';
import ElementUI from 'element-ui';
import locale from 'element-ui/lib/locale/lang/en'
import 'element-ui/lib/theme-chalk/index.css';
import Axios from '../common/js/http';
import {CONSTANT} from "../common/js/constant";
import ExportDoc from "./exportDoc.vue";

Vue.use(ElementUI, {locale});
Axios.defaults.withCredentials = true;
Axios.defaults.baseURL = CONSTANT.BASE_URL;
Vue.prototype.$axios = Axios;
Vue.config.productionTip = false;
new Vue({
    el: '#exportDoc',
    render: h => h(ExportDoc),
});
