package com.app.hackapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class CrearCuentaActivity extends AppCompatActivity {

    private String sAviso = "Antes de crear la cuenta, asegurate de poner bien el telefono.\n Te mandaremos un SMS para confirmar que este es tu número.";
    private AppCompatButton btnCrearCuenta, btnBottomSheet, btLogin, btnContraseñaOlvidada;
    ImageButton btnComprobarContraseña;
    private EditText etUsuario, etContraseña, etNombre, etApellidos, etCorreo, etTelefono;
    private EditText etNTelefono;
    private Spinner spinner;
    private AlertDialog oDialogo;
    private int nCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_acc);

        // Obtener todos los objetos por id
        etNTelefono = findViewById(R.id.activity_creatAcc_editeText_telefono);
        etNombre = findViewById(R.id.activity_login_editeText_name);
        etApellidos = findViewById(R.id.activity_login_editeText_secondName);
        etCorreo = findViewById(R.id.activity_creatAcc_editeText_email);
        etContraseña = findViewById(R.id.activity_creatAcc_editeText_clave);
        etUsuario = findViewById(R.id.activity_creatAcc_editeText_Usuario);
        etTelefono = findViewById(R.id.activity_creatAcc_editeText_telefono);
        btnCrearCuenta = findViewById(R.id.activity_creatAcc_button_crearCuanta);
        btnComprobarContraseña = findViewById(R.id.activity_creatAcc_imagButton_clave);
        btnBottomSheet = findViewById(R.id.activity_creatAcc_button_termsAndConditions);
        btLogin = findViewById(R.id.activity_creatAcc_button_login);

        // Settear valores del spinner del CC (Datos y el color)
        spinner = findViewById(R.id.activity_creatAcc_spinner_cc);
        List<CharSequence> oOpciones = Arrays.asList(getResources().getStringArray(R.array.opciones));
        int nCodigoColor = ContextCompat.getColor(CrearCuentaActivity.this, R.color.gris_texto);

        // Necesario crear un nuevo adapter
        AdaptadorSpinnerPersonalizado adapter = new AdaptadorSpinnerPersonalizado(this, android.R.layout.simple_spinner_item, oOpciones, nCodigoColor);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Acciones de los botones
        btnCrearCuenta.setOnClickListener(v -> onShowWarningDialog());
        btnComprobarContraseña.setOnClickListener(v -> onShowPass());
        btLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finishAffinity();
        });
        btnBottomSheet.setOnClickListener(v -> onShowBottomDialog());
    }

    private void showError (EditText input, String s) {
        input.setError(s);
        input.requestFocus();
    }

    private void onShowWarningDialog () {
        ConstraintLayout oAviso = findViewById(R.id.layout_warning_dialog);
        View oVista = LayoutInflater.from(this).inflate(R.layout.warning_dialog, oAviso);
        Button btnContinuar = oVista.findViewById(R.id.layout_warning_btn_continue);
        Button btnAtras = oVista.findViewById(R.id.layout_warning_btn_back);

        AlertDialog.Builder builder = new AlertDialog.Builder(CrearCuentaActivity.this);
        builder.setView(oVista);
        oDialogo = builder.create();

        if (oDialogo.getWindow() != null) {
            oDialogo.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        oDialogo.show();

        btnContinuar.setOnClickListener(v -> {
            try {
                onSendCreateAccRequest();
            } catch (Exception e) {
                e.printStackTrace();
                oDialogo.hide();
            }
        });

        btnAtras.setOnClickListener(v -> oDialogo.hide());
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

    private void onShowPass () {

        int cursorPosition = etContraseña.getSelectionStart();

        if (etContraseña.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
            // Mostrar la contraseña
            etContraseña.setTransformationMethod(null);
            btnComprobarContraseña.setImageResource(R.drawable.check_pass_blocked);
        } else {
            // Ocultar la contraseña
            etContraseña.setTransformationMethod(PasswordTransformationMethod.getInstance());
            btnComprobarContraseña.setImageResource(R.drawable.check_pass_open);
        }

        // Restaurar la posición del cursor
        etContraseña.setSelection(cursorPosition);
    }

    private void onSendCreateAccRequest () {

        String sNombre, sApellidos, sEmail, sContraseña, sUsuario, sCT;
        int nCC, nTelefono;

        sNombre = etNombre.getText().toString();
        if (sNombre.equals("")) {
            showError(etNombre, "No puede ser un campo vacío");
        }
        sApellidos = etApellidos.getText().toString();
        if (sApellidos.equals("")) {
            showError(etNombre, "No puede ser un campo vacío");
        }
        sEmail = etCorreo.getText().toString();
        if (sEmail.equals("")) {
            showError(etNombre, "No puede ser un campo vacío");
        }
        sContraseña = etContraseña.getText().toString();
        if (sContraseña.equals("")) {
            showError(etNombre, "No puede ser un campo vacío");
        }
        sUsuario = etUsuario.getText().toString();
        if (sUsuario.equals("")) {
            showError(etNombre, "No puede ser un campo vacío");
        }
        nTelefono = Integer.parseInt(etNTelefono.getText().toString());
        sCT = spinner.getSelectedItem().toString();
        nCC = Integer.parseInt(sCT.substring(sCT.indexOf("+") + 1));

        JSONObject oBodyRequest = new JSONObject();

        try {
            oBodyRequest.put("nombre", sNombre);
            oBodyRequest.put("apellidos", sApellidos);
            oBodyRequest.put("clave", sContraseña);
            oBodyRequest.put("email", sEmail);
            oBodyRequest.put("Usuario", sUsuario);
            oBodyRequest.put("ct", nCC);
            oBodyRequest.put("telefono", nTelefono);
            oBodyRequest.put("posts", 0);
            oBodyRequest.put("seguidores", 0);
            oBodyRequest.put("seguidos", 0);
            oBodyRequest.put("cuenta_privada", false);
            oBodyRequest.put("notificaciones", true);


        } catch (JSONException e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                Server.getServer() + "v1/cuenta",
                oBodyRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            setToken(response.getString("token"), etUsuario.getText().toString());
                            onSendSms();
                            Intent intent = new Intent(CrearCuentaActivity.this, Codigo.class);
                            intent.putExtra("telefono", nTelefono);
                            intent.putExtra("code", nCode);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        } catch (JSONException e) {
                            Toast.makeText(CrearCuentaActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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

    private void setToken (String token, String sUsuario) {
        SharedPreferences sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.putString("Usuario", sUsuario);
        editor.apply();
    }

    private void checkErrors (VolleyError error) {

        if (oDialogo != null && oDialogo.isShowing()) {
            oDialogo.dismiss();
        }
        if (error != null) {
            try {
                int nStatusCode = error.networkResponse.statusCode;
                // Convertir el cuerpo de la respuesta a una cadena JSON
                String sError = new String(error.networkResponse.data, "utf-8");

                // Parsear la cadena JSON a un objeto JSONObject
                JSONObject oObjeto = new JSONObject(sError);

                // Obtener el valor del error del objeto JSON
                String sMensajeError = oObjeto.getString("Error");

                if (nStatusCode == 404) {
                    showError(etUsuario, sMensajeError);
                }
                else if (nStatusCode == 401) {
                    if (sMensajeError.equals("Nombre de usuario en uso")) {
                        showError(etUsuario, sMensajeError);
                    }
                    else if (sMensajeError.equals("Email en uso")) {
                        showError(etCorreo, sMensajeError);
                    }
                    else {
                        showError(etTelefono, sMensajeError);
                    }
                }
                else {
                    Toast.makeText(CrearCuentaActivity.this, sMensajeError, Toast.LENGTH_SHORT).show();
                }

            } catch (UnsupportedEncodingException | JSONException | NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (oDialogo != null && oDialogo.isShowing()) {
            oDialogo.dismiss();
        }
    }

    private void setSMSPermisions () {
        if (!(ContextCompat.checkSelfPermission(CrearCuentaActivity.this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(CrearCuentaActivity.this, new String[]{Manifest.permission.SEND_SMS}, 100);
        }

    }
    private void onSendSms () {

        setSMSPermisions();

        // Generar codigo sms
        Random random = new Random();
        nCode = random.nextInt(9000) + 1000;
        String sMensaje = "From HackAPP\n\nDe parte de toda la comindad de ciberseguridad te damos las gracias por unirte a nosotros.\n\nTu código para verificarte es: " + nCode;

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(etNTelefono.getText().toString(),null,sMensaje, null, null);
        } catch (Exception e) {}


    }
}