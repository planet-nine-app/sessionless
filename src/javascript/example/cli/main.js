import { program } from 'commander';
import colorTest from './src/commands/color-test.js';
import lotsTest from './src/commands/lots-of-users-test.js';
import config from './config/local.js';

program
  .name('sessionless-cli-example')
  .description('A node cli for using and testing the Sessionless protocol')
  .version('0.0.1');

program
  .command('test')
  .description('Runs a single test against a single language/color')
  .option('-c, --color <color>')
  .option('-l, --language <language>')
  .action((options) => {
    const color = options.color || config.languages[options.language];
    if(!color) {
      throw new Error('Must provide a color or language');
    }
    colorTest(color, options.language);
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
  .command('rainbow')
  .description('Test all available colors, and associate keys to Voltron together the rainbow cli')
  .action((options) => {
    console.log(chalk.red('Unimplemented'));
  });

program.parse();

const options = program.opts();
const limit = options.first ? 1 : undefined;


