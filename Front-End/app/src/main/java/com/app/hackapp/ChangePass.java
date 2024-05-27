package com.app.hackapp;

import android.content.Intent;
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

public class ChangePass extends AppCompatActivity {

    private AppCompatButton btnBottomSheet, btnCambiarContraseña;
    ImageButton btnComprobarContraseña, btnComprobarContraseña2;

    private EditText etContraseña1, etContraseña2;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changepass_activity);

        // Obetner los datos de la actividad anterior
        intent = getIntent();

        // Setear los botones
        btnBottomSheet = findViewById(R.id.activity_chpass_button_termsAndConditions);
        btnComprobarContraseña = findViewById(R.id.activity_chpass_imagButton_password);
        btnComprobarContraseña2 = findViewById(R.id.activity_chpass_imagButton_password2);
        btnCambiarContraseña = findViewById(R.id.activity_chpass_button_send);
        etContraseña1 = findViewById(R.id.activity_chpass_editeText_password);
        etContraseña2 = findViewById(R.id.activity_chpass_editeText_password2);

        btnBottomSheet.setOnClickListener(v -> onShowBottomDialog());
        btnComprobarContraseña.setOnClickListener(v -> onShowPass(etContraseña1));
        btnComprobarContraseña2.setOnClickListener(v -> onShowPass(etContraseña2));
        btnCambiarContraseña.setOnClickListener(v -> onSendChangeRequest());

    }

    private void onShowBottomDialog() {
        BottomSheetDialog dialogoBottomSheet = new BottomSheetDialog(this);

        // Inflar el layout del Bottom Sheet Dialog
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);

        // Establecer el contenido del BottomSheetDialog
        dialogoBottomSheet.setContentView(bottomSheetView);

        // Mostrar el BottomSheetDialog
        dialogoBottomSheet.show();

    }

    private void showError (EditText input, String s) {
        input.setError(s);
        input.requestFocus();
    }

    private void onShowPass(EditText etContraseña) {

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

    private void onSendChangeRequest () {
        if (!etContraseña1.getText().toString().equals(etContraseña2.getText().toString())) {
            showError(etContraseña2, "Las contraseñas no coinciden");
        }
        else {
            setRequest();
        }
    }

    private void setRequest () {

        JSONObject oCuerpoPeticion = new JSONObject();

        try {
            oCuerpoPeticion.put("telefono", intent.getStringExtra("telefono"));
            oCuerpoPeticion.put("password", etContraseña2.getText().toString());
        } catch (JSONException e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                Server.getServer() + "v1/cambioPass",
                oCuerpoPeticion,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(ChangePass.this, "La contraseña se ha cambiado correctamente", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ChangePass.this, LoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

        // 3. Añadir la solicitud a la cola de solicitudes
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);

    }
}