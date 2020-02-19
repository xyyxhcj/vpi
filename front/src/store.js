import Vue from 'vue';
import Vuex from 'vuex';
import {CONSTANT} from "./common/js/constant";

Vue.use(Vuex);

export default new Vuex.Store({
    state: {
        user: undefined,
        menuTree: undefined,
        selectedProjectId: undefined,
        selectedProjectName: undefined,
    },
    getters: {
        user(state) {
            if (state.user) {
                return state.user;
            } else {
                return JSON.parse(localStorage.getItem(CONSTANT.LOCAL_STORAGE_KEY.USER));
            }
        },
        loginAuth(state) {
            if (state.loginAuth) {
                return state.loginAuth;
            } else {
                return localStorage.getItem(CONSTANT.LOCAL_STORAGE_KEY.LOGIN_AUTH);
            }
        },
        selectedProjectId(state) {
            if (state.selectedProjectId) {
                return state.selectedProjectId;
            } else {
                return sessionStorage.getItem(CONSTANT.SESSION_STORAGE_KEY.SELECTED_PROJECT_ID);
            }
        },
        selectedProjectName(state) {
            if (state.selectedProjectName) {
                return state.selectedProjectName;
            } else {
                return sessionStorage.getItem(CONSTANT.SESSION_STORAGE_KEY.SELECTED_PROJECT_NAME);
            }
        },
        selectedProjectUserType(state) {
            if (state.selectedProjectUserType) {
                return state.selectedProjectUserType;
            } else {
                return sessionStorage.getItem(CONSTANT.SESSION_STORAGE_KEY.SELECTED_PROJECT_USER_TYPE);
            }
        },
        leftMenuIsCollapse(state) {
            if (state.leftMenuIsCollapse !== undefined) {
                return state.leftMenuIsCollapse;
            } else {
                return Boolean(sessionStorage.getItem(CONSTANT.SESSION_STORAGE_KEY.LEFT_MENU_IS_COLLAPSE));
            }
        }
    },
    // change global variables
    // mutations only use sync method
    mutations: {
        login(state, user) {
            if (user.auth) {
                localStorage.setItem(CONSTANT.LOCAL_STORAGE_KEY.LOGIN_AUTH, user.auth);
            }
            state.user = user;
            localStorage.setItem(CONSTANT.LOCAL_STORAGE_KEY.USER, JSON.stringify(user));
        },
        loginOut(state) {
            localStorage.removeItem(CONSTANT.LOCAL_STORAGE_KEY.LOGIN_AUTH);
            state.user = undefined;
            localStorage.removeItem(CONSTANT.LOCAL_STORAGE_KEY.USER);
        },
        selectProject(state, project) {
            state.selectedProjectId = project.id;
            state.selectedProjectName = project.name;
            state.selectedProjectUserType = project.userType.toString();
            sessionStorage.setItem(CONSTANT.SESSION_STORAGE_KEY.SELECTED_PROJECT_ID, project.id);
            sessionStorage.setItem(CONSTANT.SESSION_STORAGE_KEY.SELECTED_PROJECT_NAME, project.name);
            sessionStorage.setItem(CONSTANT.SESSION_STORAGE_KEY.SELECTED_PROJECT_USER_TYPE, state.selectedProjectUserType);
        },
        setLeftMenuIsCollapse(state, isCollapse) {
            state.leftMenuIsCollapse = isCollapse;
            sessionStorage.setItem(CONSTANT.SESSION_STORAGE_KEY.LEFT_MENU_IS_COLLAPSE, isCollapse.toString());
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
        selectProject(context, project) {
            context.commit('selectProject', project);
        },
        setLeftMenuIsCollapse(context, isCollapse) {
            context.commit('setLeftMenuIsCollapse', isCollapse);
        }
    }
});
