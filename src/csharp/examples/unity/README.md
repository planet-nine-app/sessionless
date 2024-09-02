# Sessionless in Unity

To add Sessionless to your Unity project, copy these .dlls (these can be built from source too from the Sessionless dir using `dotnet build SessionlessNET.csproj` to the /Assets/Plugins folder of your Unity project.
You can then do stuff like:

```csharp
        Debug.Log("foo bar");
        Vault = new Vault(VaultGetter, VaultSaver);
        Sessionless Sessionless = new(Vault);

        var generatedKeys = Sessionless.GenerateKeys();
        Debug.Log("Got keys");
        Debug.Log(generatedKeys);
        var message = "FOO BAR";
        MessageSignatureHex signature = Sessionless.Sign(message);
        Debug.Log("Got signature");
        Debug.Log(signature);
        SignedMessage signedMessage = new SignedMessage(message, signature);
        Debug.Log("Got Signed message");
        Debug.Log(signedMessage.Message);
        Debug.Log(signedMessage.Signature);
        bool Verified = Sessionless.VerifySignature(signedMessage);
        Debug.Log(Verified);
        

        //StartCoroutine(Register());
```

In general, you'll probably want to use something that wraps Sessionless in a nicer interface, but that doesn't exist yet so here we are.

