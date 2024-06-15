import chalk from 'chalk';
import register from '../requests/register.js';
import doCoolStuff from '../requests/do-cool-stuff.js';

const colorTest = async (color, language) => {
  try {
    const registerResponse = await register(color);
    const doCoolStuffResponse = await doCoolStuff(registerResponse.body);
    if(!doCoolStuffResponse.doubleCool) {
      throw(chalk.red(`The ${color} server failed`));
    }
    console.log(chalk[color](`Aww yeah! The ${language ? language : ''} server thinks you're ${doCoolStuffResponse.doubleCool}.`));
  } catch(err) {
    console.log(`There is an error: ${err}`);
    throw(err);
  } 
};

export default colorTest;
