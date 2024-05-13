from src.requests.register import register
from src.requests.do_cool_stuff import do_cool_stuff

async def color_test(color, language):
    print(f'{color} and {language}')
    res = await register(color, language)
    cool_res = await do_cool_stuff(res)
    coolness = cool_res.json()
    print(f'got cool_res of {coolness}')
