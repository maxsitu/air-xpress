const path = require('path');
const webpack = require('webpack');
const ExtractTextPlugin = require('extract-text-webpack-plugin');
const autoprefixer = require('autoprefixer');

const HtmlWebpackPlugin = require('html-webpack-plugin');
const htmlWebpackPlugin = new HtmlWebpackPlugin();

const ROOT_PATH = path.resolve(__dirname);
const SRC_PATH  = path.resolve(ROOT_PATH, 'src');

var config = {
  devtool: 'cheap-module-eval-source-map',
  context: SRC_PATH,
  entry: {
    app: './App.jsx',
    vendor: './vendor.js'
  },
  output: {
    publicPath: '/dist/',
    path: __dirname + '/dist',
    filename: '[name].js',
    chunkFilename: '[name].[chunkhash:5].min.js'
  },
  plugins: [
    htmlWebpackPlugin,
    new ExtractTextPlugin('styles.css'),
    new webpack.optimize.CommonsChunkPlugin({name: 'vendor', filename: 'vendor.bundle.js'})
  ],
  module: {
    rules: [{
      test: /\.(js|jsx)$/,
      exclude: /(node_modules)/,
      include: [SRC_PATH],
      use: {
        loader: 'babel-loader',
        options: {
          presets: [['es2015', {modules: false}], 'react', 'stage-0'],
          plugins: ['syntax-dynamic-import']
        }
      }
    }, {
      test: /.html$/,
      exclude: /(node_modules)/,
      include: [SRC_PATH],
      use: [{
        loader: 'file?name=[name].[ext]'
      }]
    }, {
      test: /.css$/,
      exclude: /(node_modules)/,
      include: [SRC_PATH],
      use: ['style-loader', 'css-loader']
    }, {
      test: /\.(gif|svg|woff|woff2|ttf|eot)$/,
      exclude: /(node_modules)/,
      include: [SRC_PATH],
      use: [{
        loader: 'file-loader?name=[name].[ext]'
      }]
    }, {
      test: /\.(png|jpg|jpeg)$/,
      exclude: /(node_modules)/,
      include: [SRC_PATH],
      use: [{
        loader: 'url-loader?limit=8192&name=images/[hash:8].[name].[ext]'
      }]
    }]
  },
  resolve: {
    extensions: ['.js', '.jsx', '.less', '.scss', '.css'], //后缀名自动补全
  }
  // postcss: function () {
  //   return [autoprefixer({browsers: ['last 2 versions']})];
  // }
};

if (process.env.NODE_ENV === 'production') {
  config.output.path = __dirname + '/dist/webapp';
  config.plugins.push(new webpack.optimize.UglifyJsPlugin());
} else {
  config.devtool = 'eval'
}

module.exports = config;
