public class Program
{
    public static void Main(string[] args)
    {
        CreateHostBuilder(args).Build().Run();
    }
 
    public static IHostBuilder CreateHostBuilder(string[] args) =>
        Host.CreateDefaultBuilder(args)
            .ConfigureWebHostDefaults(webBuilder =>
            {
                webBuilder.UseStartup<Startup>();
            });
}

/*using server.Components;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddServerSideBlazor();


/*builder.UseEndpoints(endpoints =>
{
    endpoints.MapControllers();
    endpoints.MapBlazorHub();
    endpoints.MapFallbackToPage("/_Host");
});*/

// Add services to the container.
/*builder.Services.AddRazorComponents()
    .AddInteractiveServerComponents();

var app = builder.Build();

app.UseRouting();

app.MapBlazorHub();

//app.MapRazorComponents<App>()
//    .AddInteractiveServerRenderMode(); 

app.Run();*/
