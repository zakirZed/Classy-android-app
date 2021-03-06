package model;

import com.google.firebase.Timestamp;


public class Routine {
    private String subjectName, subjectCode,instructor1,instructor2,link1,link2,className;
    private String day,cardImage,docId,date,time;
    private String status, classType,shiftedDate, classTypeShiftedDate;
    private String classId,todayText;
    private boolean isInList,isIamRoutine;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    private  Timestamp queryTime;
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isIamRoutine() {
        return isIamRoutine;
    }

    public void setIamRoutine(boolean iamRoutine) {
        isIamRoutine = iamRoutine;
    }

    public String getTodayText() {
        return todayText;
    }

    public void setTodayText(String todayText) {
        this.todayText = todayText;
    }

    public boolean isInList() {
        return isInList;
    }

    public void setInList(boolean inList) {
        isInList = inList;
    }


    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }



    public String getCardImage() {
        return cardImage;
    }

    public void setCardImage(String cardImage) {
        this.cardImage = cardImage;
    }



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public String getShiftedDate() {
        return shiftedDate;
    }

    public void setShiftedDate(String shiftedDate) {
        this.shiftedDate = shiftedDate;
    }

    public String getClassTypeShiftedDate() {
        return classTypeShiftedDate;
    }

    public void setClassTypeShiftedDate(String classTypeShiftedDate) {
        this.classTypeShiftedDate = classTypeShiftedDate;
    }



    public Timestamp getQueryTime() {
        return queryTime;
    }

    public void setQueryTime(Timestamp queryTime) {
        this.queryTime = queryTime;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public Routine() {
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getInstructor1() {
        return instructor1;
    }

    public void setInstructor1(String instructor1) {
        this.instructor1 = instructor1;
    }

    public String getInstructor2() {
        return instructor2;
    }

    public void setInstructor2(String instructor2) {
        this.instructor2 = instructor2;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }


    public String getLink1() {
        return link1;
    }

    public void setLink1(String link1) {
        this.link1 = link1;
    }

    public String getLink2() {
        return link2;
    }

    public void setLink2(String link2) {
        this.link2 = link2;
    }


}
