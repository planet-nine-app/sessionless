namespace SessionlessExample.Server.Models

{
    public record RegisterModel
    {
        public string pubKey { get; set; } = "default";
        public string enteredText { get; set; } = "default";
        public string timestamp { get; set; } = "default";

    }
}
