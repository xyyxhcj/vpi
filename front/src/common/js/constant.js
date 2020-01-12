export const CONSTANT = {
    BASE_URL: '/api',
    MENUS: [
        {
            path: '/',
            name: '',
            component: () => import('@/views/index'),
            noMenu: true
        },
        {
            path: '/apiDoc',
            name: 'apiDoc',
            component: () => import('@/views/apiDoc/index'),
            meta: {title: 'API Doc', icon: 'el-icon-grape'}
        },
        {
            path: '/environmentConfig',
            name: 'environmentConfig',
            component: () => import('@/views/environmentConfig/index'),
            meta: {title: 'Environment Config', icon: 'el-icon-moon'}
        },
        {
            path: '/dataStructure',
            name: 'dataStructure',
            component: () => import('@/views/dataStructure/index'),
            meta: {title: 'Data Structure', icon: 'el-icon-coin'}
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
    },
    LOCAL_STORAGE_KEY: {
        LOGIN_AUTH: 'auth',
        USER: 'user',
    },
    SESSION_STORAGE_KEY: {
        SELECTED_PROJECT_ID: 'selectedProjectId',
        SELECTED_PROJECT_NAME: 'selectedProjectName',
    },
    REQUEST_URL: {
        LOGIN: '/user/login',
        LOGIN_OUT: '/user/loginOut',
        USER_ADD: '/user/add',

        PROJECT_ADD: '/project/add',
        PROJECT_EDIT: '/project/edit',
        PROJECT_FIND_LIST_BY_GROUP: '/project/findListByGroup',
        PROJECT_FIND_PROJECT_USER: '/project/findProjectUser',
        PROJECT_EDIT_PROJECT_USER: '/project/editProjectUser',

        PROJECT_GROUP_ADD: '/projectGroup/add',
        PROJECT_GROUP_EDIT: '/projectGroup/edit',
        PROJECT_GROUP_FIND_DETAIL: '/projectGroup/findDetail',

    },
    CLOSE_LOADING_URL: [
        '/system/sysUserWechat/loginAsync',
        '/system/rsa/getPublicKey',
        '/system/qyWechatUser/binding',
    ],
    ROUTER_URL: {
        DICT_ITEM: '/system/dictItem',
    },
    RESULT_CODE: {
        SUCCESS: 200,
        USER_INVALID: 10004,
        LOGIN_NOT: 10005,
    },
    STATUS: {
        0: 'Enable', 1: 'Disable'
    },
    READ: {
        READ: 1, UNREAD: 0
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
    }
};

