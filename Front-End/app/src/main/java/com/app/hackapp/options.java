package com.app.hackapp;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class options extends AppCompatActivity {

    private String sUser, sToken;
    private ImageView imgvUser, imgvEditProfile, imgvAboutUs, imgvFAQS, imgvSecPriv, imgvCloseSession;
    private TextView tvUsername;
    private Switch swDarkMode, swNotis, swPrivAcc;
    private Drawable drawable;
    private int nScaleDP = 10;
    private SharedPreferences sharedPreferences;

    Boolean bNotis, bAcc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options_activity);

        // Obtener el token y el nombre de usuario
        getData();

        //Set ID
        setIds();

        // Obtener los datos de nuestro usuario y manejar los clicks de los img button
        setDataCLicks();

        // Cambiar tamaño de los iconos
        seticonScale();
    }

    private void getData () {
        SharedPreferences sharedPreferences = this.getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        sUser = sharedPreferences.getString("username", null);
        sToken = sharedPreferences.getString("token", null);
    }

    private void setIds() {
        tvUsername = findViewById(R.id.activity_options_username);
        tvUsername.setText(sUser);

        swDarkMode = findViewById(R.id.activity_options_darkMode);
        swNotis = findViewById(R.id.activity_options_notis);
        swPrivAcc = findViewById(R.id.activity_options_accmode);
        imgvUser = findViewById(R.id.activity_options_image);

        imgvEditProfile = findViewById(R.id.activity_options_editProfile);
        imgvAboutUs = findViewById(R.id.activity_options_info);
        imgvFAQS = findViewById(R.id.activity_options_faqs);
        imgvSecPriv = findViewById(R.id.activity_options_secpriv);
        imgvCloseSession = findViewById(R.id.activity_options_closseSes);
    }

    private void setDataCLicks() {
        setUserData();

        imgvEditProfile.setOnClickListener(v -> startActivity(new Intent(options.this, options.class))); // Cambiar por la actividad de modificar el perfil
        imgvFAQS.setOnClickListener(v -> Toast.makeText(this, "Esta funcionalidad no esta disponible todavía.", Toast.LENGTH_SHORT).show());
        imgvAboutUs.setOnClickListener(v -> Toast.makeText(this, "Esta funcionalidad no esta disponible todavía.", Toast.LENGTH_SHORT).show());
        imgvSecPriv.setOnClickListener(v -> Toast.makeText(this, "Esta funcionalidad no esta disponible todavía.", Toast.LENGTH_SHORT).show());
        imgvCloseSession.setOnClickListener(v -> onSendCloseSSRequest());

        swDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("modoOscuro", isChecked);
            editor.apply();
        });

        swNotis.setOnClickListener(v -> {
            bNotis = !bNotis;
            swNotis.setChecked(bNotis);
            onSendChengeSwitches("notificaciones", bNotis);
        });

        swPrivAcc.setOnClickListener(v -> {
            bAcc = !bAcc;
            swPrivAcc.setChecked(bAcc);
            onSendChengeSwitches("cuenta_privada", bAcc);
        });

    }

    private void setUserData () {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                Server.getServer() + "v1/datosUsuario/" + sUser,
                null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {

                        // Necesario para que cuando se setean los switch, no se manden las peticiones al servidor
                        swNotis.setOnCheckedChangeListener(null);
                        swPrivAcc.setOnCheckedChangeListener(null);

                        // Seteamos los swatiches segun el user lo tenga en la BBDD y cargamos su imagen de usuario
                        try {
                            bNotis = response.getBoolean("notificaciones");
                            swNotis.setChecked(bNotis);
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                        }
                        try {
                            bAcc = response.getBoolean("cuenta_privada");
                            swPrivAcc.setChecked(bAcc);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                        try {
                            Glide.with(options.this).load(
                                            response.getString("avatar"))
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(imgvUser);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(options.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {

            // Headers de la peticion
            @Override
            public Map<String, String> getHeaders() {
                // Adjuntar el token en la cabecera de la solicitud
                Map<String, String> headers = new HashMap<>();
                headers.put("token", sToken);
                return headers;
            }
        };

        Volley.newRequestQueue(options.this).add(request);
    }

    private void onSendCloseSSRequest () {

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.DELETE,
                Server.getServer() + "v1/auth",
                null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        sharedPreferences = getSharedPreferences("NombreDeTuSharedPreferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("token");
                        editor.remove("username");
                        editor.apply();

                        startActivity(new Intent(options.this, LoginActivity.class));
                        finishAffinity();
                    }
                }
        ) {

            // Headers de la peticion
            @Override
            public Map<String, String> getHeaders() {
                // Adjuntar el token en la cabecera de la solicitud
                Map<String, String> headers = new HashMap<>();
                headers.put("token", sToken);
                return headers;
            }
        };

        Volley.newRequestQueue(options.this).add(request);
    }

    private void onSendChengeSwitches (String sClave, boolean bChecked) {
        JSONObject oBodyRequest = new JSONObject();

        try {
            oBodyRequest.put(sClave, bChecked);

        } catch (JSONException e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                Server.getServer() + "v1/cambioDatos",
                oBodyRequest,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(options.this, "Datos Grabados Correctamente", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        ) {

            // Headers de la peticion
            @Override
            public Map<String, String> getHeaders() {
                // Adjuntar el token en la cabecera de la solicitud
                Map<String, String> headers = new HashMap<>();
                headers.put("token", sToken);
                return headers;
            }
        };

        Volley.newRequestQueue(options.this).add(request);
    }
}