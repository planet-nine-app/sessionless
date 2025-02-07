const NodePolyfillPlugin = require('node-polyfill-webpack-plugin');
const path = require('path');

module.exports = {
  entry: './sessionless.cjs',
  output: {
    path: path.resolve(__dirname, 'dist'),
    filename: 'bundle.js',
    library: 'sessionless',
    libraryTarget: 'umd'
  },
  resolve: {
    extensions: ['.js']
  },
  module: {
    rules: [{
      test: /\.js$/,
      exclude: /node_modules/,
      use: {
        loader: 'babel-loader'
      }
    }]
  },
  plugins: [
    new NodePolyfillPlugin()
  ]
};

