package com.app.hackapp;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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

    AppCompatButton btnSheetDialog, btnLogin, btnCrearCuenta, btnCambiarContraseña;
    ImageButton btnMostrarContraseña;
    private EditText etNombreUsuario, etContraseña;
    private String sUsuario, sContraseña;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        // Obtener todas las referencias de los botones.
        btnSheetDialog = findViewById(R.id.activity_login_button_termsAndConditions);
        btnLogin = findViewById(R.id.activity_login_button_login);
        btnCrearCuenta = findViewById(R.id.activity_login_button_crearCuenta);
        btnCambiarContraseña = findViewById(R.id.activity_login_button_forgotclave);
        btnMostrarContraseña = findViewById(R.id.activity_login_imagButton_clave);
        etNombreUsuario = findViewById(R.id.activity_login_editeText_email);
        etContraseña = findViewById(R.id.activity_login_editeText_clave);

        // Obetener todos los datos de los campos edittext
        btnSheetDialog.setOnClickListener(v -> onShowBottomDialog());
        btnLogin.setOnClickListener(v -> {
            onSendLoginRequest();
        });
        btnCrearCuenta.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, CrearCuentaActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finishAffinity();
        });
        btnMostrarContraseña.setOnClickListener(v -> onShowPass());
        btnCambiarContraseña.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ContraseñaOlvidada.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finishAffinity();
        });
    }

    private void onShowBottomDialog () {
        BottomSheetDialog oBtmSheetDialog = new BottomSheetDialog(this);

        // Inflar el layout del Bottom Sheet Dialog
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);

        // Establecer el contenido del BottomSheetDialog
        oBtmSheetDialog.setContentView(bottomSheetView);

        // Mostrar el BottomSheetDialog
        oBtmSheetDialog.show();

    }

    private void onSendLoginRequest () {

        sUsuario = etNombreUsuario.getText().toString();
        sContraseña = etContraseña.getText().toString();

        JSONObject oCuerpo = new JSONObject();

        try {
            oCuerpo.put("user", sUsuario);
            oCuerpo.put("clave", sContraseña);

        } catch (JSONException e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                Server.getServer() + "v1/auth",
                oCuerpo,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            setToken(response.getString("token"));
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
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
        editor.putString("Usuario", sUsuario);
        editor.apply();
    }

    private void onShowPass () {

        int cursorPosition = etContraseña.getSelectionStart();

        if (etContraseña.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
            // Mostrar la contraseña
            etContraseña.setTransformationMethod(null);
            btnMostrarContraseña.setImageResource(R.drawable.check_pass_blocked);
        } else {
            // Ocultar la contraseña
            etContraseña.setTransformationMethod(PasswordTransformationMethod.getInstance());
            btnMostrarContraseña.setImageResource(R.drawable.check_pass_open);
        }

        // Restaurar la posición del cursor
        etContraseña.setSelection(cursorPosition);
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
                    showError(etNombreUsuario, errorMessage);
                }
                else if (nStatusCode == 401) {
                    showError(etContraseña, errorMessage);
                }
                else if (nStatusCode == 400) {
                    if (etContraseña.getText().toString().isEmpty() && etNombreUsuario.getText().toString().isEmpty()) {
                        showError(etContraseña, "Contraseña vacía");
                        showError(etNombreUsuario, "Campo vacío");

                    }
                    else if (etContraseña.getText().toString().equals("") ) {
                        showError(etContraseña, "Contraseña vacía");

                    }
                    else  if (etNombreUsuario.getText().toString().isEmpty()) {
                        showError(etNombreUsuario, "Campo vacío");
                    }
                }
                else {
                    Toast.makeText(LoginActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

            } catch (UnsupportedEncodingException | JSONException | NullPointerException e) {
                e.printStackTrace();
                if (etNombreUsuario.getText().toString().isEmpty()) {
                    showError(etNombreUsuario, "Campo vacío");
                }
                else if (etContraseña.getText().toString().isEmpty()) {
                    showError(etContraseña, "Contraseña vacía");
                }
            }
        }
    }

}