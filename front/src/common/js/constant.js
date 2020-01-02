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
    },
    LOCAL_STORAGE_KEY: {
        LOGIN_AUTH: 'auth',
        USER: 'user',
    },
    REQUEST_URL: {
        DETAIL: '/system/user/detail',
        LOGIN: '/user/login',
        LOGIN_OUT: '/system/user/loginOut',
        USER_UPDATE: '/system/user/update',

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
        0: 'Enable',
        1: 'Disable'
    },
    READ: {
        READ: 1,
        UNREAD: 0
    },
};

