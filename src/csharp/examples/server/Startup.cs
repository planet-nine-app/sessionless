public class Startup {

    public IConfiguration Configuration { get; }

    public Startup(IConfiguration configuration)
    {
        Configuration = configuration;
    }
    public void ConfigureServices(IServiceCollection services)
    {
        services.AddRazorPages();
        services.AddServerSideBlazor();
    }

  
    public void Configure(IApplicationBuilder app, IWebHostEnvironment env)
    {
        //app.UseCors(x => x.AllowAnyHeader().AllowAnyMethod().AllowAnyOrigin());

        app.UseRouting();

        app.UseEndpoints(endpoints =>
        {
            endpoints.MapControllers();
            endpoints.MapBlazorHub();
            //endpoints.MapFallbackToPage("/_Host");
        });
    }
}

