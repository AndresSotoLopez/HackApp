package com.app.hackapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Random;

public class Codigo extends AppCompatActivity {

    private AppCompatButton btnBottonSheet, btnReenviarCodigo, btnEnviarCodigo;
    private CountDownTimer nCuentaAtras;
    private EditText et1, et2, et3, et4;
    private TextView tvCuentaAtrasTexto;
    private String sTelefono = "", sCodigo = "";
    private int nCodigo = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.code_activity);

        // En el método onCreate() de ActivityB, recupera el dato
        Intent intent = getIntent();
        sTelefono = intent.getStringExtra("telefono");
        nCodigo = intent.getIntExtra("code", 0);

        // Toast existente para ejecutar la app desde Android Studio
        Toast.makeText(this, String.valueOf(nCodigo), Toast.LENGTH_LONG).show();

        // Seteamos los items de la vista
        btnBottonSheet = findViewById(R.id.activity_code_button_termsAndConditions);
        btnReenviarCodigo = findViewById(R.id.activity_code_button_resendCode);
        tvCuentaAtrasTexto = findViewById(R.id.activity_code_text_countTimer);
        btnEnviarCodigo = findViewById(R.id.activity_code_button_send);
        et1 = findViewById(R.id.activity_code_sms_et);
        et2 = findViewById(R.id.activity_code_sms_et2);
        et3 = findViewById(R.id.activity_code_sms_et3);
        et4 = findViewById(R.id.activity_code_sms_et4);


        btnBottonSheet.setOnClickListener(v -> onShowBottomSheet());
        timmer();
        setUpInputs();

        btnEnviarCodigo.setOnClickListener(v -> {
            // Crear string para comparar los codigos. El codigo generado hay que pasarlo a valor String
            sCodigo = et1.getText().toString() + et2.getText().toString() + et3.getText().toString() + et4.getText().toString();
            if (!(sCodigo.equals(String.valueOf(nCodigo)))) {
                String sErrorMessage = "Código erróneo";
                showError(et4, sErrorMessage);
            }
            else {
                Intent oNextAct = new Intent(Codigo.this, MainActivity.class);
                startActivity(oNextAct);
                finishAffinity();
            }
        });

    }

    private void onShowBottomSheet() {
        BottomSheetDialog dialogoBtmSheet = new BottomSheetDialog(this);

        // Inflar el layout del Bottom Sheet Dialog
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);

        // Establecer el contenido del BottomSheetDialog
        dialogoBtmSheet.setContentView(bottomSheetView);

        // Mostrar el BottomSheetDialog
        dialogoBtmSheet.show();

    }
    private void timmer () {

        // Configurar el temporizador
        nCuentaAtras = new CountDownTimer(20000, 1000) { // 20 segundos, actualización cada segundo
            // Cada segundo el texto cambia. Una vez llega a cero, el boton se desbloquea y se puede mandar otro sms
            public void onTick(long millisUntilFinished) {
                int nSegundos = (int) (millisUntilFinished / 1000);
                tvCuentaAtrasTexto.setText(String.valueOf(nSegundos));
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

                // Despues de enviar el codigo, el boton vuelve a estar desactivado para evitar el envio masivo de sms
                onSendSms();

                btnReenviarCodigo.setEnabled(false);
        });
    }

    // Funcion para pasar al siguiente input text tras escribir un valor en el campo.
    private void setUpInputs () {
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
        String sMensaje = "From HackAPP\n\nDe parte de toda la comindad de ciberseguridad te damos las gracias por unirte a nosotros.\n\nTu código para verificarte es: " + nCodigo;

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