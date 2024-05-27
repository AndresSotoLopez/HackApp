package com.app.hackapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ContraseñaOlvidada extends AppCompatActivity {

    private AppCompatButton btnBtmSheet, btnReenviarCodigo, btnEnviarCodigo;
    private CountDownTimer nCuentaAtras;
    private EditText et1, et2, et3, et4, nTelefono;
    private TextView nTextoContador;
    private String sTelefono = "", sCodigoUsuario = "";
    private int nCodigo = 0;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_pass_activity);

        // Seteamos los items de la vista
        btnBtmSheet = findViewById(R.id.activity_fp_button_termsAndConditions);
        btnReenviarCodigo = findViewById(R.id.activity_fp_button_resendCode);
        nTextoContador = findViewById(R.id.activity_fp_text_countTimer);
        btnEnviarCodigo = findViewById(R.id.activity_fp_button_send);
        et1 = findViewById(R.id.activity_fp_sms_et);
        et2 = findViewById(R.id.activity_fp_sms_et2);
        et3 = findViewById(R.id.activity_fp_sms_et3);
        et4 = findViewById(R.id.activity_fp_sms_et4);
        nTelefono = findViewById(R.id.activity_fp_editeText_telefono);


        btnBtmSheet.setOnClickListener(v -> onShowBottomDialog());
        timmer();
        setUpInputs();
        setSpinner();

        btnEnviarCodigo.setOnClickListener(v -> {
            //Comprobar que el numero de telefono no está vacio
            if (nTelefono.getText().toString().isEmpty()) {
                showError(nTelefono, "Introduce tu Nº de teléfono");
            }else {
                // Crear string para comparar los codigos. El codigo generado hay que pasarlo a valor String
                sCodigoUsuario = et1.getText().toString() + et2.getText().toString() + et3.getText().toString() + et4.getText().toString();
                if (!(sCodigoUsuario.equals(String.valueOf(nCodigo)))) {
                    String sMensajeError = "Código erróneo";
                    showError(et4, sMensajeError);
                } else {
                    Intent oProxAct = new Intent(ContraseñaOlvidada.this, ChangePass.class); // cambiar a actividad para cambiar la contraseña
                    oProxAct.putExtra("telefono", nTelefono.getText().toString());
                    startActivity(oProxAct);
                    finishAffinity();
                }
            }
        });
    }

    private void setSpinner () {
        // Settear valores del spinner del CC (Datos y el color)
        spinner = findViewById(R.id.activity_fp_spinner_cc);
        List<CharSequence> opciones = Arrays.asList(getResources().getStringArray(R.array.opciones));
        int nColorTexto = ContextCompat.getColor(ContraseñaOlvidada.this, R.color.gris_texto);

        // Necesario crear un nuevo adapter
        AdaptadorSpinnerPersonalizado adapter = new AdaptadorSpinnerPersonalizado(ContraseñaOlvidada.this, android.R.layout.simple_spinner_item, opciones, nColorTexto);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
    private void onShowBottomDialog () {
        BottomSheetDialog oDialogoBtmSheet = new BottomSheetDialog(this);

        // Inflar el layout del Bottom Sheet Dialog
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);

        // Establecer el contenido del BottomSheetDialog
        oDialogoBtmSheet.setContentView(bottomSheetView);

        // Mostrar el BottomSheetDialog
        oDialogoBtmSheet.show();

    }
    private void timmer () {

        // Configurar el temporizador
        nCuentaAtras = new CountDownTimer(20000, 1000) { // 20 segundos, actualización cada segundo
            // Cada segundo el texto cambia. Una vez llega a cero, el boton se desbloquea y se puede mandar otro sms
            public void onTick(long millisUntilFinished) {
                int nSegundos = (int) (millisUntilFinished / 1000);
                nTextoContador.setText(String.valueOf(nSegundos));
            }

            public void onFinish() {
                btnReenviarCodigo.setEnabled(true);
            }
        }.start();

        nCuentaAtras.start();

        // Configurar el OnClickListener del botón
        btnReenviarCodigo.setOnClickListener(v -> {
            nCuentaAtras.cancel();
            nCuentaAtras.start();

            if (!nTelefono.getText().toString().isEmpty()) {
                // Despues de enviar el codigo, el boton vuelve a estar desactivado para evitar el envio masivo de sms
                onSendSms();
            }
            else {
                showError(nTelefono, "Introduce tu Nº de teléfono");
            }

            btnReenviarCodigo.setEnabled(false);
        });
    }

    // Funcion para pasar al siguiente input text tras escribir un valor en el campo.
    private void setUpInputs () {

        // Hasta que el usuario no rellene el campo de telefono no se le mandará el sms
        nTelefono.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !nTelefono.getText().toString().isEmpty()) {
                    onSendSms();
                }
            }
        });

        et1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    et2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    et3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    et4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }
    private void onSendSms () {

        // Generar codigo sms
        Random random = new Random();
        nCodigo = random.nextInt(9000) + 1000;
        String sMensaje = "From HackAPP\n\nHemos recibido una petición para cambiar tu contraseña, si no has sido tu por favor cambia urgentemente tu calve de acceso.\n\nTu código para cambiar la contraseña es: " + nCodigo;

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(sTelefono,null,sMensaje, null, null);
        } catch (Exception e) {
            // Toast existente para ejecutar la app desde Android Studio
            Toast.makeText(this, String.valueOf(nCodigo), Toast.LENGTH_LONG).show();
        }
    }

    private void showError (EditText input, String s) {
        input.setError(s);
        input.requestFocus();
    }
}