import Vue from 'vue';
import Router from 'vue-router';
import {CONSTANT} from "@/common/js/constant";

Vue.use(Router);

let router = new Router({
    base: process.env.BASE_URL,
    routes: CONSTANT.MENUS
});
router.beforeEach((to, from, next) => {
    if (to.meta && to.meta.title) {
        document.title = to.meta.title;
    }
    next()
})
export default router;
