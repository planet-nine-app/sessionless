import chalk from 'chalk';
import register from '../requests/register.js';
import doCoolStuff from '../requests/do-cool-stuff.js';

const colorTest = async (color, language) => {
  await register(color)
  .then((res) => doCoolStuff(res.body))
  .then((res) => {
    if(!res.doubleCool) {
      throw(chalk.red('OH NO YOUR SHIT BLEW UP AGAIN!!!!'));
    }
    console.log(chalk[color](`Aww yeah! The ${language ? language : ''} server thinks you're ${res.doubleCool}.`));
  })
  .catch(console.error);
};

export default colorTest;
