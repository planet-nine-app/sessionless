namespace SessionlessExample.Server.Models

{
    public class RegisteredModel(string uuid, string welcomeMessage)
    {
	public string uuid { get; set; } = uuid;
	public string welcomeMessage { get; set; } = welcomeMessage;
    }
}
