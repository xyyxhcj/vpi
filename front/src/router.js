import Vue from 'vue';
import Router from 'vue-router';
import {CONSTANT} from "@/common/js/constant";
// import {CONSTANT} from "./common/js/constant";
// import {utils} from "./common/js/utils";


Vue.use(Router);

let router = new Router({
    mode: 'history',
    base: process.env.BASE_URL,
    routes: CONSTANT.MENUS
});
// routing guard
/*router.beforeEach((to, from, next) => {
    // allows uri
    let allows = ['/login*', '/notFound'];
    let isLogin = !!localStorage[CONSTANT.LOCAL_STORAGE_KEY.LOGIN_AUTH];
    if (utils.contains(allows, to.path, function (allow, path) {
        return allow === path || path.match(allow);
    })) {
        next();
    } else {
        isLogin ? next() : next('/login');
    }
});*/
export default router;
