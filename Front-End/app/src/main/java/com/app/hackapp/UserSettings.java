package com.app.hackapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class UserSettings extends AppCompatActivity {

    private String sToken, sUser, sTelefono, sUri = "";
    private EditText etName, etApe, etUsername, etBio, etEmail;
    private ImageButton imgPass, imgDelete;
    private ImageView imgUser;
    private Button btnAvatar, btnGuardar;
    private int nCode = 0;
    private RequestQueue requestQueue;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_settings_activity);

        // Boton para volver atras
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //creamos la cola de peticiones
        requestQueue = Volley.newRequestQueue(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }


        //seteamos los ides
        setIds();

        // Obtenemos los datos del sharedpreferences y los datos del usuario
        getUserpref();
        getUserInfo();

        // Definimos las acciones de los botones
        btnGuardar.setOnClickListener(v -> checkErrors());
        imgPass.setOnClickListener(v -> {
            onSendSms();
            Intent intent = new Intent(UserSettings.this, ForgotPass.class);
            startActivity(intent);
        });

        btnAvatar.setOnClickListener(v -> openGallery());

        imgDelete.setOnClickListener(v -> onSendDeleteAcc());
    }

    private void setIds () {
        etName = findViewById(R.id.activity_user_edittext_user);
        etApe = findViewById(R.id.activity_user_edittext_ape);
        etUsername = findViewById(R.id.activity_user_edittext_username);
        etBio = findViewById(R.id.activity_user_edittext_bio);
        etEmail = findViewById(R.id.activity_user_edittext_email);

        imgPass = findViewById(R.id.activity_options_changepass);
        imgDelete = findViewById(R.id.activity_options_closseSes);
        imgUser = findViewById(R.id.activity_user_settings_img);

        btnAvatar = findViewById(R.id.activity_user_text_avatar);
        btnGuardar = findViewById(R.id.activity_user_settings_save);
    }

    private void getUserpref () {
        SharedPreferences sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        sToken = sharedPreferences.getString("token", null);
        sUser = sharedPreferences.getString("username", null);
    }

    private void getUserInfo () {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                Server.getServer() + "v1/datosUsuario/" + sUser,
                null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            etUsername.setText(sUser);
                            String sBio = response.getString("biografia");
                            if (!(sBio != null)) {
                                etBio.setText(sBio);
                            }
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                        }
                        try {
                            Glide.with(UserSettings.this).load(
                                            response.getString("avatar"))
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(imgUser);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                        try {
                            etName.setText(response.getString("nombre"));
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                        }
                        try {
                            sTelefono = response.getString("telefono");
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                        }
                        try {
                            etApe.setText(response.getString("apellidos"));
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                        }
                        try {
                            etEmail.setText(response.getString("email"));
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UserSettings.this, "Error al cargar los datos del usuario", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                // Adjuntar el token en la cabecera de la solicitud
                Map<String, String> headers = new HashMap<>();
                headers.put("token", sToken);
                return headers;
            }
        };
        request.setTag("request");
        requestQueue.add(request);
    }

    private void onSendUserData () {
        JSONObject oBodyRequest = new JSONObject();

        try {
            oBodyRequest.put("username", etUsername.getText().toString());
            oBodyRequest.put("nombre", etName.getText().toString());
            oBodyRequest.put("apellidos", etApe.getText().toString());
            oBodyRequest.put("biografia", etBio.getText().toString());
            oBodyRequest.put("email", etEmail.getText().toString());
            oBodyRequest.put("avatar", sUri);

        } catch (JSONException e) {
            Toast.makeText(UserSettings.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                Server.getServer() + "v1/cambioDatos",
                oBodyRequest,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(UserSettings.this, "Datos Grabados Correctamente", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UserSettings.this, getErrorMessage(error), Toast.LENGTH_SHORT).show();
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

        requestQueue.add(request);
    }

    private void checkErrors () {
        if (etUsername.getText().toString().equalsIgnoreCase("")){
            showError(etUsername, "Username vacío");
        }
        else {
            onSendUserData();
        }
    }

    private static String getErrorMessage(VolleyError error) {
        String message = "Error al subir los datos";
        if (error.networkResponse != null) {
            String data = new String(error.networkResponse.data);
            message = "Error: " + data;
        } else if (error.getCause() != null) {
            message = error.getCause().getMessage();
        } else if (error.getMessage() != null) {
            message = error.getMessage();
        }
        return message;
    }

    private void showError (EditText input, String s) {
        input.setError(s);
        input.requestFocus();
    }

    private void setSMSPermisions () {
        if (!(ContextCompat.checkSelfPermission(UserSettings.this, android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(UserSettings.this, new String[]{Manifest.permission.SEND_SMS}, 100);
        }

    }
    private void onSendSms () {

        setSMSPermisions();

        // Generar codigo sms
        Random random = new Random();
        nCode = random.nextInt(9000) + 1000;
        String sMessage = "From HackAPP\n\nDe parte de toda la comindad de ciberseguridad te damos las gracias por unirte a nosotros.\n\nTu código para verificarte es: " + nCode;

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(sTelefono,null,sMessage, null, null);
        } catch (Exception e) {}
    }

    private void onSendDeleteAcc () {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.DELETE,
                Server.getServer() + "v1/cuenta",
                null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null){
                            if (error.networkResponse.statusCode == 204) {
                                SharedPreferences sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.remove("token");
                                editor.remove("username");
                                editor.apply();

                                startActivity(new Intent(UserSettings.this, LoginActivity.class));
                            }
                        }
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
        requestQueue.add(request);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            sUri = imageUri.toString();

            Glide.with(UserSettings.this).load(sUri)
                    .apply(RequestOptions.circleCropTransform())
                    .into(imgUser);
        }
    }

}