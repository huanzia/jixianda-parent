const path = require('path')
const name = 'Vue Typescript Admin'
const IS_PROD = ['production', 'development'].includes(process.env.NODE_ENV)
const API_PROXY_TARGET = process.env.VUE_APP_PROXY_TARGET || 'http://localhost:81'

module.exports = {
  publicPath: process.env.NODE_ENV === 'production' ? './' : '/',
  lintOnSave: false,
  pwa: {
    name: name
  },
  pluginOptions: {
    'style-resources-loader': {
      preProcessor: 'scss',
      patterns: [
        path.resolve(__dirname, 'src/styles/_variables.scss'),
        path.resolve(__dirname, 'src/styles/_mixins.scss')
      ]
    }
  },

  devServer: {
    port: 8888,
    open: true,
    disableHostCheck: true,
    hot: true,
    overlay: {
      warnings: false,
      errors: true
    },
    proxy: {
      '/api': {
        target: API_PROXY_TARGET,
        ws: false,
        secure: false,
        changeOrigin: true,
        pathRewrite: {
          '^/api': '/admin'
        }
      }
    }
  },

  chainWebpack: config => {
    config.resolve.symlinks(true)
  },
  configureWebpack: {
    devtool: 'source-map'
  },
  css: {
    extract: IS_PROD,
    sourceMap: false,
    loaderOptions: {},
    modules: false
  }
}
