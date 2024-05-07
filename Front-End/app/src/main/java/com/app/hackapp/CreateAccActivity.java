package com.app.hackapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
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


public class CreateAccActivity extends AppCompatActivity {

    private String sAdvise = "Antes de crear la cuenta, asegurate de poner bien el telefono.\n Te mandaremos un SMS para confirmar que este es tu número.";
    private AppCompatButton btCreateAcc, btBottomSheet, btLogin, btForgotpass;
    ImageButton btCheckPass;
    private EditText etUser, etPass, etNombre, etApellidos, etMail, etTelefono;
    private String sUser, sPass;
    private EditText etNTelefono;
    private Spinner spinner;
    private AlertDialog dialog;
    private int nCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_acc);

        // Obtener todos los objetos por id
        etNTelefono = findViewById(R.id.activity_creatAcc_editeText_telefono);
        etNombre = findViewById(R.id.activity_login_editeText_name);
        etApellidos = findViewById(R.id.activity_login_editeText_secondName);
        etMail = findViewById(R.id.activity_creatAcc_editeText_email);
        etPass = findViewById(R.id.activity_creatAcc_editeText_password);
        etUser = findViewById(R.id.activity_creatAcc_editeText_username);
        etTelefono = findViewById(R.id.activity_creatAcc_editeText_telefono);
        btCreateAcc = findViewById(R.id.activity_creatAcc_button_crearCuanta);
        btCheckPass = findViewById(R.id.activity_creatAcc_imagButton_password);
        btBottomSheet = findViewById(R.id.activity_creatAcc_button_termsAndConditions);
        btLogin = findViewById(R.id.activity_creatAcc_button_login);

        // Settear valores del spinner del CC (Datos y el color)
        spinner = findViewById(R.id.activity_creatAcc_spinner_cc);
        List<CharSequence> options = Arrays.asList(getResources().getStringArray(R.array.opciones));
        int textColor = ContextCompat.getColor(CreateAccActivity.this, R.color.gris_texto);

        // Necesario crear un nuevo adapter
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this, android.R.layout.simple_spinner_item, options, textColor);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Acciones de los botones
        btCreateAcc.setOnClickListener(v -> onShowWarningDialog());
        btCheckPass.setOnClickListener(v -> onShowPass());
        btLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finishAffinity();
        });
        btBottomSheet.setOnClickListener(v -> onShowBottomDialog());
    }

    private void showError (EditText input, String s) {
        input.setError(s);
        input.requestFocus();
    }

    private void onShowWarningDialog () {
        ConstraintLayout warning = findViewById(R.id.layout_warning_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.warning_dialog, warning);
        Button btCont = view.findViewById(R.id.layout_warning_btn_continue);
        Button btBack = view.findViewById(R.id.layout_warning_btn_back);

        AlertDialog.Builder builder = new AlertDialog.Builder(CreateAccActivity.this);
        builder.setView(view);
        dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        dialog.show();

        btCont.setOnClickListener(v -> {
            try {
                onSendCreateAccRequest();
            } catch (Exception e) {
                e.printStackTrace();
                dialog.hide();
            }
        });

        btBack.setOnClickListener(v -> dialog.hide());
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

    private void onSendCreateAccRequest () {

        String sNombre, sApellidos, sEmail, sPass, sUser, sCT;
        int nCC, nTelefono;

        sNombre = etNombre.getText().toString();
        if (sNombre.equals("")) {
            showError(etNombre, "No puede ser un campo vacío");
        }
        sApellidos = etApellidos.getText().toString();
        if (sApellidos.equals("")) {
            showError(etNombre, "No puede ser un campo vacío");
        }
        sEmail = etMail.getText().toString();
        if (sEmail.equals("")) {
            showError(etNombre, "No puede ser un campo vacío");
        }
        sPass = etPass.getText().toString();
        if (sPass.equals("")) {
            showError(etNombre, "No puede ser un campo vacío");
        }
        sUser = etUser.getText().toString();
        if (sUser.equals("")) {
            showError(etNombre, "No puede ser un campo vacío");
        }
        nTelefono = Integer.parseInt(etNTelefono.getText().toString());
        sCT = spinner.getSelectedItem().toString();
        nCC = Integer.parseInt(sCT.substring(sCT.indexOf("+") + 1));

        JSONObject oBodyRequest = new JSONObject();

        try {
            oBodyRequest.put("nombre", sNombre);
            oBodyRequest.put("apellidos", sApellidos);
            oBodyRequest.put("password", sPass);
            oBodyRequest.put("email", sEmail);
            oBodyRequest.put("username", sUser);
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
                            setToken(response.getString("token"));
                            onSendSms();
                            Intent intent = new Intent(CreateAccActivity.this, Code.class);
                            intent.putExtra("telefono", nTelefono);
                            intent.putExtra("code", nCode);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        } catch (JSONException e) {
                            Toast.makeText(CreateAccActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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
        editor.putString("username", sUser);
        editor.apply();
    }

    private void checkErrors (VolleyError error) {

        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
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
                    if (errorMessage.equals("Nombre de usuario en uso")) {
                        showError(etUser, errorMessage);
                    }
                    else if (errorMessage.equals("Email en uso")) {
                        showError(etMail, errorMessage);
                    }
                    else {
                        showError(etTelefono, errorMessage);
                    }
                }
                else {
                    Toast.makeText(CreateAccActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }

            } catch (UnsupportedEncodingException | JSONException | NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void setSMSPermisions () {
        if (!(ContextCompat.checkSelfPermission(CreateAccActivity.this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(CreateAccActivity.this, new String[]{Manifest.permission.SEND_SMS}, 100);
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
            smsManager.sendTextMessage(etNTelefono.getText().toString(),null,sMessage, null, null);
        } catch (Exception e) {}


    }
}