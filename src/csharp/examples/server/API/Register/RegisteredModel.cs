namespace SessionlessExample.Server.Models

{
    public record RegisteredModel
    {
        public string uuid { get; set; };
        public string welcomeMessage { get; set; };

        public RegisteredModel(string uuid, string welcomeMessage)
        {
            this.uuid = uuid;
            this.welcomeMessage = welcomeMessage;
        }
    }
}
