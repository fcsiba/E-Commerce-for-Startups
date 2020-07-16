package pushy.fastech.pk.Helper;

public class Homework {

    public String myClass;
    public String section;
    public String subject;
    public String date;
    public String dueDate;
    public String dueTime;
    public String homework;

    public boolean sectionSeparate = false;
    public boolean folder = true;


    public Homework(String date, boolean sectionSeparate) {
        this.date = date;
        this.sectionSeparate = sectionSeparate;
    }

    public Homework(String myClass, String section, String subject, String dueDate, String dueTime, String homework) {
        this.myClass = myClass;
        this.section = section;
        this.subject = subject;
        this.dueDate = dueDate;
        this.dueTime = dueTime;
        this.homework = homework;
    }

    public String getMyClass() {
        return myClass;
    }

    public void setMyClass(String myClass) {
        this.myClass = myClass;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getDueTime() {
        return dueTime;
    }

    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }

    public String getHomework() {
        return homework;
    }

    public void setHomework(String homework) {
        this.homework = homework;
    }

    public boolean isSectionSeparate() {
        return sectionSeparate;
    }

    public void setSectionSeparate(boolean sectionSeparate) {
        this.sectionSeparate = sectionSeparate;
    }

    public boolean isFolder() {
        return folder;
    }

    public void setFolder(boolean folder) {
        this.folder = folder;
    }

}
