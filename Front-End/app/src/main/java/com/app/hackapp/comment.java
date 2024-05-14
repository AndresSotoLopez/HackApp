package com.app.hackapp;

public class comment {

    private String sUsername, sComment, sAvatar;
    private Double fVal;
    public comment () {}

    public comment (String sUserName, Double fVal, String sComment, String sAvatar) {
        this.fVal = fVal;
        this.sUsername = sUserName;
        this.sComment = sComment;
        this.sAvatar = sAvatar;
    }

    public String getsUsername() {
        return sUsername;
    }

    public void setsUsername(String sUsername) {
        this.sUsername = sUsername;
    }

    public String getsComment() {
        return sComment;
    }

    public void setsComment(String sComment) {
        this.sComment = sComment;
    }

    public Double getfVal() {
        return fVal;
    }

    public void setfVal(Double fVal) {
        this.fVal = fVal;
    }

    public String getsAvatar() {
        return sAvatar;
    }

    public void setsAvatar(String sAvatar) {
        this.sAvatar = sAvatar;
    }
}
