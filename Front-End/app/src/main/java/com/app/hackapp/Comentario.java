package com.app.hackapp;

public class Comentario {

    private String sUsername, sComentario, sAvatar;
    private Double fVal;
    public Comentario() {}

    public Comentario(String sUserName, Double fVal, String sComment, String sAvatar) {
        this.fVal = fVal;
        this.sUsername = sUserName;
        this.sComentario = sComment;
        this.sAvatar = sAvatar;
    }

    public String getsUsername() {
        return sUsername;
    }

    public void setsUsername(String sUsername) {
        this.sUsername = sUsername;
    }

    public String getsComentario() {
        return sComentario;
    }

    public void setsComentario(String sComentario) {
        this.sComentario = sComentario;
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
