namespace SessionlessExample.Server.Models

{
    public record UserModel(string uuid, string pubKey)
    {
	public string uuid { get; set; } = uuid;
	public string pubKey { get; set; } = pubKey;
    }
}
