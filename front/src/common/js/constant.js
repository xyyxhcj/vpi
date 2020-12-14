export const CONSTANT = {
    BASE_URL: '/vpi',
    MENUS: [
        {
            path: '/',
            redirect: '/index',
            meta: { title: 'vpi!'},
            noMenu: true
        },
        {
            path: '/index',
            name: 'index',
            meta: { title: 'vpi!'},
            component: () => import('@/views/index'),
            noMenu: true
        },
        {
            path: '/help',
            name: 'help',
            component: resolve => require(['@/views/help/index.vue'], resolve),
            // component: () => import('@/views/help/index'),
            noMenu: true
        },
        {
            path: '/apiDoc',
            name: 'apiDoc',
            component: resolve => require(['@/views/apiDoc/index.vue'], resolve),
            meta: {title: 'API Doc', icon: 'el-icon-grape'}
        },
        {
            path: '/environmentConfig',
            name: 'environmentConfig',
            component: resolve => require(['@/views/environmentConfig/index.vue'], resolve),
            meta: {title: 'Environment Config', icon: 'el-icon-moon'}
        },
        {
            path: '/dataStructure',
            name: 'dataStructure',
            component: resolve => require(['@/views/dataStructure/index.vue'], resolve),
            meta: {title: 'Data Structure', icon: 'el-icon-coin'}
        },
        {
            path: '/api',
            name: 'api',
            component: resolve => require(['@/views/api/index.vue'], resolve),
            notMenu: true,
            children: [
                {
                    path: '/api/edit',
                    name: 'api/edit',
                    component: resolve => require(['@/views/api/edit.vue'], resolve),
                },
                {
                    path: '/api/detail',
                    name: 'api/detail',
                    component: resolve => require(['@/views/api/detail.vue'], resolve),
                },
                {
                    path: '/api/test',
                    name: 'api/test',
                    component: resolve => require(['@/views/api/test.vue'], resolve),
                },
            ],
        },
        {
            path: '/exportDoc',
            name: 'exportDoc',
            beforeEnter: () => window.location = '/exportDoc.html' + window.location.search,
            noMenu: true
        },
    ],
    CONFIG: {
        PAGE_SIZE_DEFAULT: 20,
        PAGE_SIZES: [20, 50, 100, 1000],
        DATE_FORMAT: 'yyyy-MM-dd hh:mm:ss',
        DATE_FORMAT_QUERY: 'yyyy-MM-dd',
        DEFAULT_PROJECT_VERSION: '1.0',
        DEFAULT_PROJECT_TYPE: 0,
        USER_SHOW_STYLE: '{userName} ({loginName})',
        ADMIN_LOGIN_NAME: 'admin',
        DEFAULT_DATA_LIST_SIZE: 3,
        DEBUG: process.env.NODE_ENV !== 'production',
        AES_KEY: 'QuBabnE0dR1pb29h',
        ARRAY_SPLIT_STR: '|',
        getProfilesActive: (debug) => debug ? 'dev' : 'prod'
    },
    HOST_URL: {
        dev: 'http://120.132.18.250:11111',
        // dev: 'http://127.0.0.1:11111',
        prod: '$prodApiUrl',
    },
    LOCAL_STORAGE_KEY: {
        LOGIN_AUTH: 'auth',
        USER: 'user',
        REMEMBER_INFO: 'rememberInfo',
    },
    SESSION_STORAGE_KEY: {
        SELECTED_PROJECT_ID: 'selectedProjectId',
        SELECTED_PROJECT_NAME: 'selectedProjectName',
        SELECTED_PROJECT_USER_TYPE: 'selectedProjectUserType',
        SELECTED_ENV: 'selectedEnv',
        LEFT_MENU_IS_COLLAPSE: 'leftMenuIsCollapse',
    },
    REQUEST_URL: {
        CHROME_PLUGIN_DOWNLOAD : '$chromePluginDownloadUrl',

        LOGIN: '/user/login',
        LOGIN_OUT: '/user/loginOut',
        USER_ADD: '/user/add',
        USER_UPDATE: '/user/update',
        USER_REMOVE: '/user/remove',
        USER_FIND_PAGE: '/user/findPage',

        PROJECT_ADD: '/project/add',
        PROJECT_EDIT: '/project/edit',
        PROJECT_REMOVE: '/project/remove',
        PROJECT_FIND_LIST_BY_GROUP_FOR_OWNER: '/project/findListByGroupForOwner',
        PROJECT_FIND_LIST_BY_GROUP_FOR_OTHER: '/project/findListByGroupForOther',
        PROJECT_FIND_PROJECT_USER: '/project/findProjectUser',
        PROJECT_EDIT_PROJECT_USER: '/project/editProjectUser',
        PROJECT_ASSIGN: '/project/assign',

        PROJECT_GROUP_ADD: '/projectGroup/add',
        PROJECT_GROUP_EDIT: '/projectGroup/edit',
        PROJECT_GROUP_MOVE_GROUP: '/projectGroup/moveGroup',
        PROJECT_GROUP_DELETE: '/projectGroup/delete',
        PROJECT_GROUP_FIND_DETAIL: '/projectGroup/findDetail',
        PROJECT_GROUP_FIND_LIST: '/projectGroup/findList',

        API_ENV_ADD: '/apiEnv/add',
        API_ENV_EDIT: '/apiEnv/edit',
        API_ENV_DELETE: '/apiEnv/delete',
        API_ENV_FIND_LIST: '/apiEnv/findList',

        STRUCTURE_ADD: '/structure/add',
        STRUCTURE_EDIT: '/structure/edit',
        STRUCTURE_REMOVE: '/structure/remove',
        STRUCTURE_FIND_DETAIL: '/structure/findDetail',
        STRUCTURE_FIND_PAGE: '/structure/findPage',

        API_GROUP_ADD: '/apiGroup/add',
        API_GROUP_EDIT: '/apiGroup/edit',
        API_GROUP_DELETE: '/apiGroup/delete',
        API_GROUP_FIND_LIST: '/apiGroup/vpi/findList',

        API_ADD: '/api/add',
        API_EDIT: '/api/edit',
        API_SWITCH_STATUS: '/api/switchStatus',
        API_MOVE_GROUP: '/api/moveGroup',
        API_SAVE_MOCK: '/api/saveMock',
        API_REMOVE: '/api/remove',
        API_FIND_DETAIL: '/api/findDetail',
        API_FIND_ALL_DETAIL: '/api/vpi/findAllDetail',
        API_FIND_PAGE: '/api/findPage',
        API_FIND_REFERENCE_API: '/api/findReferenceApi',

        API_TEST_HISTORY_ADD: '/apiTestHistory/add',
        API_TEST_HISTORY_DELETE: '/apiTestHistory/delete',
        API_TEST_HISTORY_FIND_PAGE: '/apiTestHistory/findPage',
    },
    CLOSE_LOADING_URL: [],
    RESULT_CODE: {
        SUCCESS: 200,
        USER_INVALID: 10004,
        LOGIN_NOT: 10005,
        STRUCTURE_USED: 10008,
    },
    STATUS: {
        0: 'Enable', 1: 'Disable'
    },
    PROJECT_TYPE: {
        0: 'web', 1: 'app', 2: 'pc', 3: 'others'
    },
    AUTH_ROLE: {
        ALL: '0',
        ADMIN: '1',
        READ_WRITE: '2',
        READ: '3',
    },
    PROJECT_USER_TYPE: {
        1: 'Admin',
        2: 'Read-Write',
        3: 'Read-Only',
    },
    PARAM_TYPE: {
        STRING: 0,
        FILE: 1,
        JSON: 2,
        NUMBER: 3,
        DOUBLE: 4,
        TIME: 5,
        BOOLEAN: 6,
        ARRAY: 7,
        OBJECT: 8,
        NULL: 9,
    },
    PARAM_TYPE_STR: [
        'string',
        'file',
        'json',
        'number',
        'double',
        'time',
        'boolean',
        'array',
        'object',
        'null',
    ],
    REQUIRED_TYPE: {
        REQUIRED: 0,
        OPTIONAL: 1,
        SPECIAL: 2,
    },
    REQUIRED_TYPE_STR: {
        0: 'required',
        1: 'optional',
        2: 'special',
    },
    REQUEST_TYPE: {
        0: 'POST', 1: 'GET',
    },
    API_STATUS: {
        0: 'Enable',
        1: 'Maintaining',
        2: 'Deprecated',
        3: 'Pending',
        4: 'Plan',
        5: 'Develop',
        6: 'Test',
        7: 'Docking',
        8: 'Bug',
    },
    CONTENT_TYPE: [
        ['Content-Type', 'application/json;charset=UTF-8'],
        ['Content-Type', 'application/x-www-form-urlencoded;charset=UTF-8'],
    ],
    REQUEST_PARAM_TYPE: {
        0: 'json', 1: 'form',
    },
    RESPONSE_PARAM_TYPE: {
        0: 'json', 1: 'binary',
    },
    ITEM_TEMPLATE: JSON.stringify({
        paramKey: '',
        paramType: 0,
        requireType: 0,
        paramDesc: '',
        value: '',
        level: 0,
        subList: [],
        paramKeyIsEmpty: false,
        show: true,
    }),
    IS: {
        NO: 0,
        YES: 1,
    },
};

