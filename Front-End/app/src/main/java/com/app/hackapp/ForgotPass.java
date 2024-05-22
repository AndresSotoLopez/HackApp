package com.app.hackapp;

import android.content.Intent;
import android.content.pm.FeatureGroupInfo;
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

public class ForgotPass extends AppCompatActivity {

    private AppCompatButton btBottomSheet, btResendCode, btSendCode;
    private CountDownTimer nCountDownTimer;
    private EditText et1, et2, et3, et4, nTelefono;
    private TextView nCountTimerText;
    private String sTelefono = "", sUserCode = "";
    private int nCode = 0;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_pass_activity);

        // Seteamos los items de la vista
        btBottomSheet = findViewById(R.id.activity_fp_button_termsAndConditions);
        btResendCode = findViewById(R.id.activity_fp_button_resendCode);
        nCountTimerText = findViewById(R.id.activity_fp_text_countTimer);
        btSendCode = findViewById(R.id.activity_fp_button_send);
        et1 = findViewById(R.id.activity_fp_sms_et);
        et2 = findViewById(R.id.activity_fp_sms_et2);
        et3 = findViewById(R.id.activity_fp_sms_et3);
        et4 = findViewById(R.id.activity_fp_sms_et4);
        nTelefono = findViewById(R.id.activity_fp_editeText_telefono);


        btBottomSheet.setOnClickListener(v -> onShowBottomDialog());
        timmer();
        setUpInputs();
        setSpinner();

        btSendCode.setOnClickListener(v -> {
            //Comprobar que el numero de telefono no está vacio
            if (nTelefono.getText().toString().isEmpty()) {
                showError(nTelefono, "Introduce tu Nº de teléfono");
            }else {
                // Crear string para comparar los codigos. El codigo generado hay que pasarlo a valor String
                sUserCode = et1.getText().toString() + et2.getText().toString() + et3.getText().toString() + et4.getText().toString();
                if (!(sUserCode.equals(String.valueOf(nCode)))) {
                    String sErrorMessage = "Código erróneo";
                    showError(et4, sErrorMessage);
                } else {
                    Intent oNextAct = new Intent(ForgotPass.this, ChangePass.class); // cambiar a actividad para cambiar la contraseña
                    oNextAct.putExtra("telefono", nTelefono.getText().toString());
                    startActivity(oNextAct);
                    finishAffinity();
                }
            }
        });
    }

    private void setSpinner () {
        // Settear valores del spinner del CC (Datos y el color)
        spinner = findViewById(R.id.activity_fp_spinner_cc);
        List<CharSequence> options = Arrays.asList(getResources().getStringArray(R.array.opciones));
        int textColor = ContextCompat.getColor(ForgotPass.this, R.color.gris_texto);

        // Necesario crear un nuevo adapter
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(ForgotPass.this, android.R.layout.simple_spinner_item, options, textColor);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
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
    private void timmer () {

        // Configurar el temporizador
        nCountDownTimer = new CountDownTimer(20000, 1000) { // 20 segundos, actualización cada segundo
            // Cada segundo el texto cambia. Una vez llega a cero, el boton se desbloquea y se puede mandar otro sms
            public void onTick(long millisUntilFinished) {
                int nSegundos = (int) (millisUntilFinished / 1000);
                nCountTimerText.setText(String.valueOf(nSegundos));
            }

            public void onFinish() {
                btResendCode.setEnabled(true);
            }
        }.start();

        nCountDownTimer.start();

        // Configurar el OnClickListener del botón
        btResendCode.setOnClickListener(v -> {
            nCountDownTimer.cancel();
            nCountDownTimer.start();

            if (!nTelefono.getText().toString().isEmpty()) {
                // Despues de enviar el codigo, el boton vuelve a estar desactivado para evitar el envio masivo de sms
                onSendSms();
            }
            else {
                showError(nTelefono, "Introduce tu Nº de teléfono");
            }

            btResendCode.setEnabled(false);
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
        nCode = random.nextInt(9000) + 1000;
        String sMessage = "From HackAPP\n\nHemos recibido una petición para cambiar tu contraseña, si no has sido tu por favor cambia urgentemente tu calve de acceso.\n\nTu código para cambiar la contraseña es: " + nCode;

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(sTelefono,null,sMessage, null, null);
        } catch (Exception e) {
            // Toast existente para ejecutar la app desde Android Studio
            Toast.makeText(this, String.valueOf(nCode), Toast.LENGTH_LONG).show();
        }
    }

    private void showError (EditText input, String s) {
        input.setError(s);
        input.requestFocus();
    }
}