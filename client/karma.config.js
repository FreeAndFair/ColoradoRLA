const path = require('path');
const webpack = require('webpack');

module.exports = config => {
    config.set({
        plugins: [
            require('karma-webpack'),
            require('karma-tap'),
            require('karma-tap-pretty-reporter'),
            require('karma-chrome-launcher'),
        ],

        basePath: '',
        frameworks: ['tap'],
        files: ['test.index.js'],

        mime: {
            'text/x-typescript': ['ts', 'tsx'],
        },

        preprocessors: {
            'test.index.js': ['webpack'],
        },

        webpack: {
            plugins: [
                new webpack.DefinePlugin({
                    DEBUG: true
                }),
            ],

            resolve: {
                extensions: ['.ts', '.tsx', '.js', '.json'],

                alias: {
                    // Add an import alias for the project root.
                    corla: path.resolve(__dirname, 'src'),
                },
            },

            module: {
                rules: [
                    {
                        test: /\.tsx?$/,
                        loaders: [
                            'awesome-typescript-loader'
                        ],
                        exclude: path.join(__dirname, 'node_modules'),
                        include: path.join(__dirname, 'src'),
                    },
                ],
            },

            node: { fs: 'empty' },

            externals: {
                'react/addons': true,
                'react/lib/ExecutionEnvironment': true,
                'react/lib/ReactContext': 'window',
            },
        },

        webpackMiddleware: {
            noInfo: true
        },

        browsers: ['ChromeHeadlessNoSandbox'],
        customLaunchers: {
            ChromeHeadlessNoSandbox: {
                base: 'ChromeHeadless',
                flags: ['--no-sandbox']
            }
        },
        port: 9876,

        reporters: ['tap-pretty'],
        tapReporter: {
            prettify: require('tap-spec'),
        },

        colors: true,
        logLevel: config.LOG_INFO,
    })
};
