using Microsoft.AspNetCore.Mvc;
using System;
using SessionlessExample.Server.Models;
using System.Text.Json;
using SessionlessNET.Impl;
using SessionlessNET.Models;
using System.IO;
using System.Text;
using System.Threading.Tasks;

namespace SessionlessExample.Server.Controllers

{
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
                        
            var postedSignature = Request.Headers["Signature"];

            MessageSignatureHex signature = new MessageSignatureHex(postedSignature);

            var message = JsonSerializer.Serialize<RegisterModel>(input);

            SignedMessage signedMessage = new SessionlessNET.Models.SignedMessage(message, signature);

            if(!Sessionless.VerifySignature(signedMessage, input.pubKey)) {
                return Problem("Auth error");
            }

            var uuid = Sessionless.GenerateUUID();

            UserModel user = new UserModel(uuid, input.pubKey);

            DemoPersistenceController.saveUser(user);

            var resp = new Models.RegisteredModel(uuid, "Welcome to C#");

            return Ok(resp);
        }
    }
}
