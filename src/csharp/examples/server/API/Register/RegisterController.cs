using Microsoft.AspNetCore.Mvc;
using System;
using server.Models;
using System.Text.Json;

namespace server.Controllers

{
    [ApiController]
    public class RegisterController
    {
        [HttpPost]
        [Route("/register")]
        public void Register(RegisterModel input)
        {
            Console.WriteLine("foo");
            Console.WriteLine(JsonSerializer.Serialize<RegisterModel>(input));
        }
    }
}