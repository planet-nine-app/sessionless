namespace SessionlessExample.Server.Models

{
    public record CoolStuffModel
    {
       public class CoolStuffModel(string doubleCool) {
           public string DoubleCool { get; set; } = doubleCool;
       }  
    }
}
