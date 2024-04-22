namespace SessionlessExample.Server.Models

{
    public record DoCoolStuffModel
    {
        public string uuid { get; set; } = "default";
        public string coolness { get; set; } = "default";
        public string timestamp { get; set; } = "default";

    }
}
