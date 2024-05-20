package com.app.hackapp;

import org.json.JSONException;
import org.json.JSONObject;

public class Notificaciones {

    private String sSeguido, sAvatar;
    private int nId;

    private Notificaciones () {}

    public Notificaciones(JSONObject jsonObject) throws JSONException {
        this.sSeguido = jsonObject.getString("seguido");
        this.sAvatar = jsonObject.getString("avatar");
        this.nId = jsonObject.getInt("id");
    }

    public String getsSeguido() {
        return sSeguido;
    }

    public void setsSeguido(String sSeguido) {
        this.sSeguido = sSeguido;
    }

    public String getsAvatar() {
        return sAvatar;
    }

    public void setsAvatar(String sAvatar) {
        this.sAvatar = sAvatar;
    }

    public int getnId() {
        return nId;
    }

    public void setnId(int nId) {
        this.nId = nId;
    }
}
