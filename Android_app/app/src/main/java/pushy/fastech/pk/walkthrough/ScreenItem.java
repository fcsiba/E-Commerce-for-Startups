package pushy.fastech.pk.walkthrough;

public class ScreenItem {

    String Title, Description;
    int ScreenImg, Logo, Tint;

    public ScreenItem(String title, String description, int screenImg, int logo, int tint) {
        Title = title;
        Description = description;
        ScreenImg = screenImg;
        Logo = logo;
        Tint = tint;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setScreenImg(int screenImg) {
        ScreenImg = screenImg;
    }

    public void setLogo(int logo) {
        Logo = logo;
    }

    public void setTint(int tint) {
        Logo = tint;
    }


    public String getTitle() {
        return Title;
    }

    public String getDescription() {
        return Description;
    }

    public int getScreenImg() {
        return ScreenImg;
    }

    public int getLogo() {
        return Logo;
    }

    public int getTint(){ return Tint; }

}
