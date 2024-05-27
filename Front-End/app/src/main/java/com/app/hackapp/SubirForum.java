package com.app.hackapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SubirForum extends Fragment {

    private Button btnNoticias, btnExploit, btnForo, btnPost;
    private EditText etNombre, etText, etDesc;
    private String sToken;
    private Fragment fNoticias, fExpl, fForo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.upload__forum_fragment, container, false);

        fNoticias = new SubirNoticias();
        fExpl = new SubirExploit();
        fForo = new SubirForum();

        // Obtenemos el token de sesion de usuario
        getData();

        //Seteamos los ids de la vista
        setIds(view);

        // Manejamos los clicks de los botones
        btnNoticias.setOnClickListener(v -> replace(fNoticias));
        btnExploit.setOnClickListener(v -> replace(fExpl));
        btnForo.setOnClickListener(v -> replace(fForo));

        btnPost.setOnClickListener(v -> peticion());

        return view;
    }
    private void getData () {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        sToken = sharedPreferences.getString("token", null);
    }

    private void setIds(View view) {
        btnNoticias = view.findViewById(R.id.fragment_upload_forum_button_news);
        btnExploit = view.findViewById(R.id.fragment_upload_forum_button_exploits);
        btnForo = view.findViewById(R.id.fragment_upload_forum_button_forum);

        etNombre = view.findViewById(R.id.fragment_upload_forum_et_newName);
        etDesc = view.findViewById(R.id.fragment_upload_forum_et_desc);
        etText = view.findViewById(R.id.fragment_upload_forum_et_poc);
        btnPost = view.findViewById(R.id.fragment_upload_forum_button_post);
    }

    // Funcion para mostrar marcar el edittext donde esta el error
    private void showError (EditText input, String s) {
        input.setError(s);
        input.requestFocus();
    }

    private boolean checkData () {
        String sNombre = etNombre.getText().toString(),
                sTest = etText.getText().toString(),
                sDesc = etDesc.getText().toString();

        if (sNombre.isEmpty()) {
            showError(etNombre, "El nombre no puede estar vacío");
            return false;
        }
        if (sDesc.isEmpty()) {
            showError(etDesc, "La descripción no puede estar vacía");
            return false;
        }

        if (sTest.isEmpty()) {
            showError(etText, "Lo que has probado no puede estar vacío");
            return false;
        }

        return true;
    }

    private void peticion () {

        if (checkData()) {
            String sNombre = etNombre.getText().toString(),
                    sTest = etText.getText().toString(),
                    sDesc = etDesc.getText().toString();

            JSONObject oJsonBody = new JSONObject();
            try {
                oJsonBody.put("nombre", sNombre);
                oJsonBody.put("tipo_publicacion", 3);
                oJsonBody.put("descripcion", sDesc);
                oJsonBody.put("probado", sTest);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    Server.getServer() + "v1/nuevaPublicacion",
                    oJsonBody,
                    new Response.Listener<JSONObject>(){
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(requireContext(), "Post subido correctament", Toast.LENGTH_SHORT).show();
                            replace(fForo);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(requireContext(), "Error al subir el post", Toast.LENGTH_SHORT).show();
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

            Volley.newRequestQueue(requireContext()).add(request);
        }
    }


    private void replace (Fragment fragment) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, fragment);
        transaction.commit();
    }
}