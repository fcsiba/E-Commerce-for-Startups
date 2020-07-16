using Microsoft.Owin;
using Owin;

[assembly: OwinStartupAttribute(typeof(ims_mat_pro.Startup))]
namespace ims_mat_pro
{
    public partial class Startup
    {
        public void Configuration(IAppBuilder app)
        {
            ConfigureAuth(app);
        }

    }
}
