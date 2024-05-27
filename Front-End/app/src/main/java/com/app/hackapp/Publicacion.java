package com.app.hackapp;

import org.json.JSONException;
import org.json.JSONObject;

public class Publicacion {

    private int nID, nTipoPost, nGravedad;
    private String sNombre, sDesc, sLinkExt, sImagen, sCVE, sText, sUsername, sAvatar;

    public Publicacion () {}

    public Publicacion(JSONObject object) throws JSONException {
        try {
            this.nID = object.getInt("id");
        } catch (JSONException e) {
            this.nID = -1;
        }

        try {
            this.sDesc = object.getString("descripcion");
        } catch (JSONException e) {
            this.sDesc = "";
        }

        try {
            this.nTipoPost = object.getInt("tipo_publicacion");
        } catch (JSONException e) {
            this.nTipoPost = -1;
        }

        try {
            this.nGravedad = object.getInt("gravedad");
        } catch (JSONException e) {
            this.nGravedad = -1;
        }

        try {
            this.sNombre = object.getString("nombre");
        } catch (JSONException e) {
            this.sNombre = "";
        }

        try {
            this.sLinkExt = object.getString("link_externo");
        } catch (JSONException e) {
            this.sLinkExt = "";
        }

        try {
            this.sImagen = object.getString("imagen");
        } catch (JSONException e) {
            this.sImagen = "";
        }

        try {
            this.sCVE = object.getString("cve");
        } catch (JSONException e) {
            this.sCVE = "";
        }

        try {
            this.sText = object.getString("probado");
        } catch (JSONException e) {
            this.sText = "";
        }

        try {
            this.sUsername = object.getString("usuario");
        } catch (JSONException e) {
            this.sUsername = "";
        }

        try {
            this.sAvatar = object.getString("avatar");
        } catch (JSONException e) {
            this.sAvatar = "";
        }

    }

    public int getnID() {
        return nID;
    }

    public void setnID(int nID) {
        this.nID = nID;
    }

    public int getnTipoPost() {
        return nTipoPost;
    }

    public void setnTipoPost(int nTipoPost) {
        this.nTipoPost = nTipoPost;
    }

    public int getnGravedad() {
        return nGravedad;
    }

    public void setnGravedad(int nGravedad) {
        this.nGravedad = nGravedad;
    }

    public String getsNombre() {
        return sNombre;
    }

    public void setsNombre(String sNombre) {
        this.sNombre = sNombre;
    }

    public String getsLinkExt() {
        return sLinkExt;
    }

    public void setsLinkExt(String sLinkExt) {
        this.sLinkExt = sLinkExt;
    }

    public String getsImagen() {
        return sImagen;
    }

    public void setsImagen(String sImagen) {
        this.sImagen = sImagen;
    }

    public String getsCVE() {
        return sCVE;
    }

    public void setsCVE(String sCVE) {
        this.sCVE = sCVE;
    }

    public String getsText() {
        return sText;
    }

    public void setsText(String sText) {
        this.sText = sText;
    }

    public String getsUsername() {
        return sUsername;
    }

    public void setsUsername(String sUsername) {
        this.sUsername = sUsername;
    }

    public String getsAvatar() {
        return sAvatar;
    }

    public void setsAvatar(String sAvatar) {
        this.sAvatar = sAvatar;
    }

    public String getsDesc() {
        return sDesc;
    }

    public void setsDesc(String sDesc) {
        this.sDesc = sDesc;
    }
}
