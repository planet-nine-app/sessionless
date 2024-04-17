import { program } from 'commander';
import colorTest from './src/commands/color-test.js';

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
console.log(options);
console.log('do test here');
    colorTest(options.color);
  });

program
  .command('rainbow')
  .description('Test all available colors, and associate keys to Voltron together the rainbow cli')
  .action((options) => {
console.log(options);
console.log('do test here');
  });

program.parse();

const options = program.opts();
console.log(options);
const limit = options.first ? 1 : undefined;
console.log(program.args[0].split(options.separator, limit));


