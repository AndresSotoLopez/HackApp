package com.app.hackapp;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class LoginActivity extends AppCompatActivity {

    AppCompatButton btBottomSheet, btLogin, btCreaAcc, btForgotpass;
    ImageButton btCheckPass;
    private EditText etUser, etPass;
    private String sUser, sPass;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        // Obtener todas las referencias de los botones.
        btBottomSheet = findViewById(R.id.activity_login_button_termsAndConditions);
        btLogin = findViewById(R.id.activity_login_button_login);
        btCreaAcc = findViewById(R.id.activity_login_button_crearCuenta);
        btForgotpass = findViewById(R.id.activity_login_button_forgotPassword);
        btCheckPass = findViewById(R.id.activity_login_imagButton_password);
        etUser = findViewById(R.id.activity_login_editeText_email);
        etPass = findViewById(R.id.activity_login_editeText_password);

        // Obetener todos los datos de los campos edittext
        btBottomSheet.setOnClickListener(v -> onShowBottomDialog());
        btLogin.setOnClickListener(v -> onSendLoginRequest());
        btCreaAcc.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateAccActivity.class); // Falta por implementar la actividad
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finishAffinity();
        });
        btCheckPass.setOnClickListener(v -> onShowPass());
        btForgotpass.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class); // Falta por implementar la actividad
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_animation, R.anim.slide_out_animation);
            finishAffinity();
        });
    }

    private void onShowBottomDialog () {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);

        // Inflar el layout del Bottom Sheet Dialog
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);

        // Establecer el contenido del BottomSheetDialog
        bottomSheetDialog.setContentView(bottomSheetView);

        // Mostrar el BottomSheetDialog
        bottomSheetDialog.show();

    }

    private void onSendLoginRequest () {

        sUser = etUser.getText().toString();
        sPass = etPass.getText().toString();

        JSONObject oBodyRequest = new JSONObject();

        try {
            oBodyRequest.put("user", sUser);
            oBodyRequest.put("password", sPass);

        } catch (JSONException e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                Server.getServer() + "v1/auth",
                oBodyRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            setToken(response.getString("token"));
                            startActivity(new Intent(LoginActivity.this, LoginActivity.class)); // Cambiar por actividad main
                        } catch (JSONException e) {
                            Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        checkErrors(error);
                    }
                });

        // 3. Añadir la solicitud a la cola de solicitudes
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);

    }

    private void setToken (String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.apply();
    }

    private void onShowPass () {

        int cursorPosition = etPass.getSelectionStart();

        if (etPass.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
            // Mostrar la contraseña
            etPass.setTransformationMethod(null);
            btCheckPass.setImageResource(R.drawable.check_pass_blocked);
        } else {
            // Ocultar la contraseña
            etPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
            btCheckPass.setImageResource(R.drawable.check_pass_open);
        }

        // Restaurar la posición del cursor
        etPass.setSelection(cursorPosition);
    }

    private void showError (EditText input, String s) {
         input.setError(s);
         input.requestFocus();
    }

    private void checkErrors (VolleyError error) {
        if (error != null) {
            try {
                int nStatusCode = error.networkResponse.statusCode;
                // Convertir el cuerpo de la respuesta a una cadena JSON
                String errorResponse = new String(error.networkResponse.data, "utf-8");

                // Parsear la cadena JSON a un objeto JSONObject
                JSONObject jsonObject = new JSONObject(errorResponse);

                // Obtener el valor del error del objeto JSON
                String errorMessage = jsonObject.getString("Error");

                if (nStatusCode == 404) {
                    showError(etUser, errorMessage);
                }
                else if (nStatusCode == 401) {
                    showError(etPass, errorMessage);
                }
                else if (nStatusCode == 400) {
                    if (etPass.getText().toString().equals("") && etUser.getText().toString().equals("")) {
                        showError(etPass, "Contraseña vacía");
                        showError(etUser, "Email vacío");

                    }
                    else if (etPass.getText().toString().equals("") ) {
                        showError(etPass, "Contraseña vacía");

                    }
                    else {
                        showError(etUser, "Email vacío");
                    }
                }
                else {
                    Toast.makeText(LoginActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

            } catch (UnsupportedEncodingException | JSONException e) {
                e.printStackTrace();
            }
        }
    }
}