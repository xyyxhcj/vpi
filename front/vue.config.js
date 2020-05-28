const CompressionPlugin = require('compression-webpack-plugin');
const HOST_URL = {
    dev: 'http://120.132.18.250:11111',
    // dev: 'http://127.0.0.1:11111',
    prod: '$prodApiUrl',
};
const debug = process.env.NODE_ENV !== 'production';
const profilesActive = debug ? 'dev' : 'prod';

// https://github.com/vuejs/vue-cli/tree/dev/docs/zh/config
module.exports = {
    pages: {
        index: {
            entry: 'src/main.js',
            template: 'public/index.html',
            filename: 'index.html',
        },
        exportDoc: {
            entry: 'src/pages/exportDoc.js',
            template: 'public/exportDoc.html',
            filename: 'exportDoc.html',
        },
    },
    publicPath: '/',
    outputDir: 'dist',
    // js, css, img, fonts
    assetsDir: 'assets',
    // eslint save test：ture | false | 'error'
    lintOnSave: false,
    runtimeCompiler: true,
    transpileDependencies: [],
    productionSourceMap: true,
    // productionSourceMap: debug,
    css: {
        sourceMap: debug,
    },
    configureWebpack: config => {
        if (debug) {
            config.devtool = 'cheap-module-eval-source-map'
        } else {
            return {
                plugins:[new CompressionPlugin({
                    test: /\.js$|\.html$|\.css/,
                    threshold: 10240,
                    deleteOriginalAssets: false,
                })]
            }
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
