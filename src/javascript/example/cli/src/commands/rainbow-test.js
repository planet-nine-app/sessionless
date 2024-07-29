import chalk from 'chalk';
import register from '../requests/register.js';
import doCoolStuff from '../requests/do-cool-stuff.js';
import config from '../../config/local.js';

chalk.orange = chalk.rgb(255, 136, 0);
chalk.indigo = chalk.rgb(100, 0, 100);
chalk.violet = chalk.rgb(205, 0, 205);

const colorTest = async (color, language) => {
  try {
    const registerResponse = await register(color);
    if(!registerResponse || !registerResponse.body) {
      throw(chalk.red('Could not register'));
    }
    const doCoolStuffResponse = await doCoolStuff(registerResponse.body);
    if(!doCoolStuffResponse.doubleCool) {
      throw(chalk.red(`The ${color} server failed`));
    }
    console.log(chalk[color](`Aww yeah! The ${language ? language : ''} server thinks you're ${doCoolStuffResponse.doubleCool}.`));
    return () => chalk[color]('Success');
  } catch(err) {
    console.log(`There is an error: ${err}`);
    throw(err);
  } 
};

const rainbowTest = async () => {
  Object.keys(config.colors).reduce((promiseChain, color) => {
    return promiseChain.then(chainResults => colorTest(color, config.colors[color].language).then(result => [...chainResults, result])); 
  }, Promise.resolve([])).then(results => {
    results.map(result => console.log(result()));
  }).catch(console.warn);
};

export default rainbowTest;
