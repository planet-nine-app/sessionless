from cli_command_parser import Command, Option, main

class Hello(Command, description='Simple greeting example'):
    name = Option('-n', default='World', help='The person to say hello to')
    count: int = Option('-c', default=1, help='Number of times to repeat the message')

    def main(self):
        for _ in range(self.count):
            print(f'Hello {self.name}!')

if __name__ == '__main__':
    main()
