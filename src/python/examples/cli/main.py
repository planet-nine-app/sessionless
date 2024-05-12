import asyncio
from cli_command_parser import AsyncCommand, Command, SubCommand, Option, main
from src.commands.color_test import color_test

class Slapper(
    AsyncCommand, 
    description='Runs a single test against a single language/color'
):
    sub_command = SubCommand()
    color = Option('-c', help='The color for the test')
    language = Option('-l', help='The language for the test')

class ColorTest(
    Slapper,
    help='Run a basic test against a single server based on color or language'
):
    async def main(self):
        print(f'run the color test on {self.color}.')
        await color_test(self.color, 'js')
        

if __name__ == '__main__':
#    asyncio.run(Slapper.parse_and_run())
#    Slapper.parse_and_run()
    main()
