import Vue from 'vue';
import Vuex from 'vuex';
import {CONSTANT} from "./common/js/constant";

Vue.use(Vuex);

export default new Vuex.Store({
    state: {
        user: undefined,
        menuTree: undefined,
    },
    getters: {
        user(state) {
            if (state.user) {
                return state.user;
            } else {
                return JSON.parse(localStorage.getItem(CONSTANT.LOCAL_STORAGE_KEY.USER));
            }
        },
        menuTree(state) {
            if (state.menuTree) {
                return state.menuTree;
            } else {
                return JSON.parse(localStorage.getItem(CONSTANT.LOCAL_STORAGE_KEY.MENU_TREE));
            }
        },
    },
    // change global variables
    // mutations only use sync method
    mutations: {
        login(state, user) {
            if (user.authorization) {
                localStorage.setItem(CONSTANT.LOCAL_STORAGE_KEY.LOGIN_TOKEN, user.authorization);
            }
            state.user = user;
            localStorage.setItem(CONSTANT.LOCAL_STORAGE_KEY.USER, JSON.stringify(user));
        },
        loginOut(state) {
            localStorage.removeItem(CONSTANT.LOCAL_STORAGE_KEY.LOGIN_TOKEN);
            state.user = undefined;
            localStorage.setItem(CONSTANT.LOCAL_STORAGE_KEY.USER, {});
        },
        syncMenuTree(state, data) {
            state.menuTree = data;
            localStorage.setItem(CONSTANT.LOCAL_STORAGE_KEY.MENU_TREE, JSON.stringify(data));
        }
    },
    // async actions
    // actions can't directly modify global variables, need use commit() to trigger mutations method
    actions: {
        login(context, user) {
            context.commit('login', user);
        },
        loginOut(context) {
            context.commit('loginOut');
        },
        syncMenuTree(context, data) {
            context.commit('syncMenuTree', data);
        }
    }
});
