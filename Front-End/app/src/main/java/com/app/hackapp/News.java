package com.app.hackapp;

import org.json.JSONException;
import org.json.JSONObject;

public class News {
    private String sName = "", sDescrip = "", sLink_expdb = "", sLinkExt = "", sLinkImage = "", sUsername = "";
    private int nId = 0;

    public News () {}

    public News (JSONObject oObject) throws JSONException {
        this.nId = oObject.getInt("id");
        this.sName = oObject.getString("nombre");
        this.sDescrip = oObject.getString("descripcion");
        this.sLinkExt = oObject.getString("link_externo");
        this.sLinkImage = oObject.getString("imagen");
        this.sUsername = oObject.getString("usuario");
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getsDescrip() {
        return sDescrip;
    }

    public void setsDescrip(String sDescrip) {
        this.sDescrip = sDescrip;
    }

    public String getsLink_expdb() {
        return sLink_expdb;
    }

    public void setsLink_expdb(String sLink_expdb) {
        this.sLink_expdb = sLink_expdb;
    }

    public String getsLinkExt() {
        return sLinkExt;
    }

    public void setsLinkExt(String sLinkExt) {
        this.sLinkExt = sLinkExt;
    }

    public String getsLinkImage() {
        return sLinkImage;
    }

    public void setsLinkImage(String sLinkImage) {
        this.sLinkImage = sLinkImage;
    }

    public int getnId() {
        return nId;
    }

    public void setnId(int nId) {
        this.nId = nId;
    }
}
