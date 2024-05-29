package com.app.hackapp;

public class Comentario {

    private String sUsuario, sComentario, sAvatar;
    private Double fVal;
    public Comentario() {}

    public Comentario(String sUsuario, Double fVal, String sComment, String sAvatar) {
        this.fVal = fVal;
        this.sUsuario = sUsuario;
        this.sComentario = sComment;
        this.sAvatar = sAvatar;
    }

    public String getsUsuario() {
        return sUsuario;
    }

    public void setsUsuario(String sUsuario) {
        this.sUsuario = sUsuario;
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
