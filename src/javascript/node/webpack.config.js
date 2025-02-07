import NodePolyfillPlugin from 'node-polyfill-webpack-plugin';
import path from 'path';

module.exports = {
  entry: './sessionless.cjs',
  output: {
    path: path.resolve(__dirname, 'dist'),
    filename: 'bundle.js',
    library: 'bundle',
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

