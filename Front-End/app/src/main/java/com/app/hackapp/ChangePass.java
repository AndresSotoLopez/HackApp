package com.app.hackapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangePass extends AppCompatActivity {

    private AppCompatButton btBottomSheet, btChangePass;
    ImageButton btCheckPass, btCheckPass2;

    private EditText etPass1, etPass2;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changepass_activity);

        // Obetner los datos de la actividad anterior
        intent = getIntent();

        // Setear los botones
        btBottomSheet = findViewById(R.id.activity_chpass_button_termsAndConditions);
        btCheckPass = findViewById(R.id.activity_chpass_imagButton_password);
        btCheckPass2 = findViewById(R.id.activity_chpass_imagButton_password2);
        btChangePass = findViewById(R.id.activity_chpass_button_send);
        etPass1 = findViewById(R.id.activity_chpass_editeText_password);
        etPass2 = findViewById(R.id.activity_chpass_editeText_password2);

        btBottomSheet.setOnClickListener(v -> onShowBottomDialog());
        btCheckPass.setOnClickListener(v -> onShowPass(etPass1));
        btCheckPass2.setOnClickListener(v -> onShowPass(etPass2));
        btChangePass.setOnClickListener(v -> onSendChangeRequest());

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

    private void showError (EditText input, String s) {
        input.setError(s);
        input.requestFocus();
    }

    private void onShowPass (EditText etPass) {

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

    private void onSendChangeRequest () {
        if (!etPass1.getText().toString().equals(etPass2.getText().toString())) {
            showError(etPass2, "Las contraseñas no coinciden");
        }
        else {
            setRequest();
        }
    }

    private void setRequest () {

        JSONObject oBodyRequest = new JSONObject();

        try {
            oBodyRequest.put("telefono", intent.getStringExtra("telefono"));
            oBodyRequest.put("password", etPass2.getText().toString());
        } catch (JSONException e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                Server.getServer() + "v1/cambioPass",
                oBodyRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(ChangePass.this, "La contraseña se ha cambiado correctamente", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ChangePass.this, LoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finishAffinity();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ChangePass.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // 3. Añadir la solicitud a la cola de solicitudes
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);

    }
}