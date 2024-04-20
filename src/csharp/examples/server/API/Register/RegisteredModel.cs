namespace server.Models
{
    public class RegisteredModel
    {
        public string uuid { get; set; } = "default";
        public string welcomeMessage { get; set; } = "default";

        public RegisteredModel(string uuid, string welcomeMessage)
        {
            this.uuid = uuid;
            this.welcomeMessage = welcomeMessage;
        }
    }
}