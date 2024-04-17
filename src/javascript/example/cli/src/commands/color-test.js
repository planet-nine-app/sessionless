import chalk from 'chalk';
import register from '../requests/register.js';
import doCoolStuff from '../requests/do-cool-stuff.js';

const colorTest = async (color) => {
  register(color)
  .then((res) => doCoolStuff(res.body))
  .then((res) => {
console.log('ressed');
console.log(res);
    console.log(chalk[color](`Aww yeah! The server thinks you're ${res.doubleCool}.`));
  })
  .catch(console.error);
};

export default colorTest;
