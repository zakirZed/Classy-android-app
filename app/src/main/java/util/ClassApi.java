package util;

import android.app.Application;

import com.google.type.DayOfWeek;

import java.sql.Time;
import java.util.Date;

import static com.squareup.okhttp.internal.Internal.instance;

public class ClassApi extends Application {
    private String className, instructor,subject,subCode,classId;
    private Date date;
    private DayOfWeek day;

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    private Time time;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubCode() {
        return subCode;
    }

    public void setSubCode(String subCode) {
        this.subCode = subCode;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public void setDay(DayOfWeek day) {
        this.day = day;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    private static ClassApi instance;
    public  static ClassApi getInstance(){
        if(instance==null)
            instance = new ClassApi();
        return instance;
    }




    public ClassApi() {
    }
}
