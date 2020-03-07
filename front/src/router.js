import Vue from 'vue';
import Router from 'vue-router';
import {CONSTANT} from "@/common/js/constant";

Vue.use(Router);

let router = new Router({
    mode: 'history',
    base: process.env.BASE_URL,
    routes: CONSTANT.MENUS
});
export default router;
