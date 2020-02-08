import {CONSTANT} from "./src/common/js/constant";

const HOST_URL = CONSTANT.HOST_URL;
const debug = CONSTANT.CONFIG.DEBUG;
const profilesActive = CONSTANT.CONFIG.getProfilesActive(debug);

module.exports = {
    publicPath: '/',
    outputDir: 'dist',
    // js, css, img, fonts
    assetsDir: 'assets',
    // eslint save test：ture | false | 'error'
    lintOnSave: false,
    runtimeCompiler: true,
    transpileDependencies: [],
    productionSourceMap: debug,
    configureWebpack: config => {
        if (debug) {
            config.devtool = 'cheap-module-eval-source-map'
            // eslint-disable-next-line no-empty
        } else {
        }
    },
    // https://github.com/vuejs/vue-cli/blob/dev/docs/webpack.md
    // eslint-disable-next-line no-unused-vars
    chainWebpack: config => {
        if (debug) {
            // dev
        } else {
            // prod
        }
    },
    parallel: require('os').cpus().length > 1,
    pluginOptions: {},
    // https://github.com/vuejs/vue-cli/tree/dev/packages/%40vue/cli-plugin-pwa
    pwa: {},
    devServer: {
        open: true,
        // host: debug ? 'localhost' : '0.0.0.0',
        port: debug ? 8081 : 80,
        https: false,
        hotOnly: false,
        proxy: {
            /*'/api/system': {
                target: HOST_URL[profilesActive] + ':10008/system',
                ws: true,
                changOrigin: true,
                pathRewrite: {
                    '^/api/system': ''
                }
            },*/
            '/vpi/': {
                target: HOST_URL[profilesActive],
                ws: true,
                changOrigin: true,
                pathRewrite: {
                    '^/vpi/': '/'
                }
            }
        },
        // eslint-disable-next-line no-unused-vars
        before: app => {
        }
    }
};
