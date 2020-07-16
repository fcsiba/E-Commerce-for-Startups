package pushy.fastech.pk.Helper;

import java.util.List;

public class Attendance {
    Integer id;
    Integer sID;
    String gr;
    String name;
    String fName;
    String status;
    String details; //For leave from - to details
    Boolean isAbsent = false;
    String from;
    String to;


    public  Attendance()
    {}


    public String getGr() {
        return gr;
    }

    public void setGr(String gr) {
        this.gr = gr;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {

        this.status = status;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
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



    public Boolean getAbsent() {
        return isAbsent;
    }

    public void setAbsent(Boolean absent) {
        isAbsent = absent;
        if (absent) {
            status = "A";
        }
        else {
            status = "P";

        }

    }

    public void markOnLeave(String _from, String _to)
    {
        status = "L";
        from = _from;
        to = _to;


    }

    public  String getTotal(List<Attendance> list, String status)
    {
        Integer i = 0;
        for (Attendance c: list) {
            if (c.getStatus().equals(status))
                i++;
        }
        return i + "";
    }


}
