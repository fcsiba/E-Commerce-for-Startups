package com.material.components.model;

import android.graphics.drawable.Drawable;
import android.widget.TextView;

public class People {

    public int image;
    public Drawable imageDrw;
    public String name;
    public String email;
    public boolean section = false;
    public boolean isAbsent = false;
    public String status;
    String from;
    String to;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public People() {
    }

    public People(String name, boolean section) {
        this.name = name;
        this.section = section;
        this.isAbsent = false;
        this.status = "P";

    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public Drawable getImageDrw() {
        return imageDrw;
    }

    public void setImageDrw(Drawable imageDrw) {
        this.imageDrw = imageDrw;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isSection() {
        return section;
    }

    public void setSection(boolean section) {
        this.section = section;
    }

    public boolean isAbsent() {
        return isAbsent;
    }

    public void setAbsent(boolean absent) {
        isAbsent = absent;
       if (absent)
           status = "A";
       else
           status = "P";
    }

    public void markOnLeave(String _from, String _to)
    {
        status = "L";
        from = _from;
        to = _to;
    }
}
