package pushy.fastech.pk.Helper;

public class Marks {

    public Integer id;
    public Integer sID;
    public String gr;
    public String name;
    public String fName;
    public String obtainedMarks = "-";
    public float TotalMarks = 100f;
    public float numericMarks;

    public Marks(){
        // Leave empty constructor
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getsID() {
        return sID;
    }

    public void setsID(Integer sID) {
        this.sID = sID;
    }

    public String getGr() {
        return gr;
    }

    public void setGr(String gr) {
        this.gr = gr;
    }

    public String getObtainedMarks() {
        return obtainedMarks;
    }

    public void setObtainedMarks(String obtainedMarks) {
        this.obtainedMarks = obtainedMarks;
    }

    public float getTotalMarks() {
        return TotalMarks;
    }

    public void setTotalMarks(float totalMarks) {
        TotalMarks = totalMarks;
    }

    public float getNumericMarks() {
        return numericMarks;
    }

    public void setNumericMarks(float numericMarks) {
        this.numericMarks = numericMarks;
    }
}
