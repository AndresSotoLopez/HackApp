package com.app.hackapp;

import org.json.JSONException;
import org.json.JSONObject;

public class NoticiasClase {
    private String sNombre = "", sDescrip = "", sLink_expdb = "", sLinkExt = "", sLinkImagen = "", sUsuario = "";
    private int nId = 0;

    public NoticiasClase() {}

    public NoticiasClase(JSONObject oObject) throws JSONException {
        this.nId = oObject.getInt("id");
        this.sNombre = oObject.getString("nombre");
        this.sDescrip = oObject.getString("descripcion");
        this.sLinkExt = oObject.getString("link_externo");
        this.sLinkImagen = oObject.getString("imagen");
        this.sUsuario = oObject.getString("usuario");
    }

    public String getsNombre() {
        return sNombre;
    }

    public void setsNombre(String sNombre) {
        this.sNombre = sNombre;
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

    public String getsLinkImagen() {
        return sLinkImagen;
    }

    public void setsLinkImagen(String sLinkImagen) {
        this.sLinkImagen = sLinkImagen;
    }

    public int getnId() {
        return nId;
    }

    public void setnId(int nId) {
        this.nId = nId;
    }
}
