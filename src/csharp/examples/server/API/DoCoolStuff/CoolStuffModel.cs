namespace server.Models
{
    public class CoolStuffModel
    {
        public string doubleCool { get; set; } = "default";
        public CoolStuffModel(string doubleCoolString)
        {
            doubleCool = doubleCoolString;
        }
    }
}