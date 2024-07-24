import { program } from 'commander';
import colorTest from './src/commands/color-test.js';
import rainbowTest from './src/commands/rainbow-test.js';
import lotsTest from './src/commands/lots-of-users-test.js';
import config from './config/local.js';
import chalk from 'chalk';

program
  .name('sessionless-cli-example')
  .description('A node cli for using and testing the Sessionless protocol')
  .version('0.0.1');

program
  .command('test')
  .description('Runs a single test against a single language/color')
  .option('-c, --color <color>')
  .option('-l, --language <language>')
  .action(async (options) => {
    const color = options.color || config.languages[options.language];
    if(!color) {
      throw new Error('Must provide a color or language');
    }
    await colorTest(color, options.language);
  });

program
  .command('lots')
  .description('Runs lots of color tests')
  .option('-c --color <color>')
  .option('-l --language <language>')
  .option('-i --iterations <iterations>')
  .action(async (options) => {
    const color = options.color || config.languages[options.language];
    if(!color) {
      throw new Error('Must provide a color or language');
    }
    await lotsTest(color, options.language, options.iterations);
  });

program
  .command('rainbow-test')
  .description('Test all available colors')
  .action(async (options) => {
    console.log(chalk.green('Starting test...'));
    await rainbowTest();
  });

program.parse();

const options = program.opts();
const limit = options.first ? 1 : undefined;


