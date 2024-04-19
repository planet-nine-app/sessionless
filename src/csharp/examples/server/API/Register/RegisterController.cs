using Microsoft.AspNetCore.Mvc;
using System;
using server.Models;
using System.Text.Json;
using SessionlessNET.Impl;
using SessionlessNET.Models;
using System.IO;
using System.Text;
using System.Threading.Tasks;

namespace server.Controllers

{
    /*[ApiController]
    public class RegisterController
    {
        [HttpPost]
        [Route("/register")]
        public void Register(Request request)
        {
            Console.WriteLine("foo");
            Console.WriteLine(request);
            //Console.WriteLine(context.Request);
            //Console.WriteLine(JsonSerializer.DeserializeObject<RegisterModel>(requestBody));
            //Console.WriteLine(JsonSerializer.Serialize<RegisterModel>(input));
            Results.Ok();
        }
    }*/

    [ApiController]
    [Route("/register")]
    public class RegisterController: ControllerBase
    {
        Vault Vault;
        Sessionless Sessionless;
        const string privPath = "./priv.key";
        const string pubPath = "./pub.key";
        static void VaultSaver(KeyPairHex pair) {
            
        }
        static KeyPairHex? VaultGetter() {
            
            return new("", "");
        }

        [HttpPost]
        [Consumes("application/json")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        public IActionResult PostJson(Models.RegisterModel input) {
            Vault = new Vault(VaultGetter, VaultSaver);
            Sessionless = new(Vault);

            Console.WriteLine("foo");
            Console.WriteLine(input);
            //Console.WriteLine(context.Request);
            //Console.WriteLine(JsonSerializer.DeserializeObject<RegisterModel>(requestBody));
            Console.WriteLine(JsonSerializer.Serialize<RegisterModel>(input));
                        
            var postedSignature = Request.Headers["Signature"];

            MessageSignatureHex signature = new MessageSignatureHex(postedSignature);

            var message = JsonSerializer.Serialize<RegisterModel>(input);

            SignedMessage signedMessage = new SessionlessNET.Models.SignedMessage(message, signature, input.pubKey);

            if(!Sessionless.VerifySignature(signedMessage)) {
                return Problem("Auth error");
            }

            var uuid = Sessionless.GenerateUUID();

            var resp = new Models.RegisteredModel(uuid, "Welcome to C#");

            return Ok(JsonSerializer.Serialize<RegisteredModel>(resp));
        }
    }
}