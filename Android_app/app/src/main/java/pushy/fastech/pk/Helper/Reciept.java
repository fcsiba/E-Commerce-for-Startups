package pushy.fastech.pk.Helper;

public class Reciept {

    Integer id;
    String title;
    String desc;
    String cls;
    String sec;
    Integer type; // 1. All Classes, 2. Selected Department, 3. Selected Class-Sec, 4. Specific Student, 5. All Employees, 6. Employees Departments, 7. Selected Employees


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
