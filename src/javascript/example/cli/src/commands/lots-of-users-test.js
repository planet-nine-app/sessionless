import chalk from 'chalk';
import colorTest from './color-test.js';

const lotsTest = async (color, language, iterations = 100) => {
  await runColorTest(color, language, iterations);
};

const runColorTest = (color, language, iterations) => {
  if(iterations < 1) {
    process.exit(chalk.green('You made it!'));
  }
  colorTest(color, language)
    .then(res => {
      return runColorTest(color, language, iterations - 1)
    })
    .catch(process.exit);
};

export default lotsTest;
