const UglifyJsPlugin = require('uglifyjs-webpack-plugin');
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
    productionSourceMap: debug,
    css: {
        extract: true,
        sourceMap: debug,
    },
    // devtool: 'cheap-module-eval-source-map',
    configureWebpack: debug ? {} :
        {
            optimization: {
                minimizer: [
                    new UglifyJsPlugin({
                        test: /\.js($|\?)/i,
                        parallel: true,
                        uglifyOptions: {
                            warnings: false,
                            sourceMap: false,
                            output: {
                                comments: false,
                                beautify: false,
                            },
                            compress: {
                                drop_console: true,
                                drop_debugger: false,
                                pure_funcs: ['console.log']
                            }
                        }
                    })
                ]
            },
            plugins: [
                new CompressionPlugin({
                    test: /\.js$|\.html$|\.css/,
                    threshold: 10240,
                    deleteOriginalAssets: false,
                }),
            ]
        },
    // eslint-disable-next-line no-unused-vars
    chainWebpack: config => {
        config.optimization.splitChunks({
            cacheGroups: {}
        });
        config.plugins.delete('prefetch-index');
        config.plugins.delete('preload-index');
        config.plugins.delete('prefetch-exportDoc');
        config.plugins.delete('preload-exportDoc');
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
        port: debug ? 8081 : 80,
        https: false,
        hotOnly: false,
        progress: true,
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
