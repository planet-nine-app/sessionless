namespace SessionlessExample.Server.Models

{
    public record UserModel
    {
        public class UserModel(string uuid, string pubKey)
        {
            public string uuid { get; set; } = uuid;
            public string pubKey { get; set; } = pubKey;
        }
    }
}
