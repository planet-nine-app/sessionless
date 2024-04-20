namespace server.Models
{
    public class UserModel
    {
        public string uuid { get; set; } = "default";

        public string pubKey { get; set; } = "default";
        public UserModel(string uuid, string pubKey)
        {
            this.uuid = uuid;
            this.pubKey = pubKey;
        }
    }
}