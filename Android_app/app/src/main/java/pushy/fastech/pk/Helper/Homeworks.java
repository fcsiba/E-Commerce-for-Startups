package pushy.fastech.pk.Helper;

public class Homeworks {
    Integer id;
    String cls;
    String sec;
    String fac;
    String sub;
    String date;
    String dateStr;
    String homework;
    public boolean sectionSeparate = false;

    public Homeworks(String date, boolean sectionSeparate) {
        this.date = date;
        this.sectionSeparate = sectionSeparate;
    }

    public Homeworks(Integer id, String myClass, String section, String fac, String subject, String date, String homework) {
        this.id = id;
        this.cls = myClass;
        this.sec = section;
        this.fac = fac;
        this.sub = subject;
        this.dateStr = date;
        this.homework = homework;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCls() {
        return cls;
    }

    public void setCls(String cls) {
        this.cls = cls;
    }

    public String getSec() {
        return sec;
    }

    public void setSec(String sec) {
        this.sec = sec;
    }

    public String getFac() {
        return fac;
    }

    public void setFac(String fac) {
        this.fac = fac;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getDate() {
        return date;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHomework() {
        return homework;
    }

    public void setHomework(String homework) {
        this.homework = homework;
    }
}
