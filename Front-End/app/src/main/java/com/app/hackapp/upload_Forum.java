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

public class upload_Forum extends Fragment {

    private Button btnNews, btnExploit, btnForo, btnPost;
    private EditText etName, etTest, etDesc;
    private String sToken;
    private Fragment fNews, fExpl, fForum;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.upload__forum_fragment, container, false);

        fNews = new upload_noticias();
        fExpl = new upload_exploit();
        fForum = new upload_Forum();

        // Obtenemos el token de sesion de usuario
        getData();

        //Seteamos los ids de la vista
        setIds(view);

        // Manejamos los clicks de los botones
        btnNews.setOnClickListener(v -> replace(fNews));
        btnExploit.setOnClickListener(v -> replace(fExpl));
        btnForo.setOnClickListener(v -> replace(fForum));

        btnPost.setOnClickListener(v -> peticion());

        return view;
    }
    private void getData () {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        sToken = sharedPreferences.getString("token", null);
    }

    private void setIds(View view) {
        btnNews = view.findViewById(R.id.fragment_upload_forum_button_news);
        btnExploit = view.findViewById(R.id.fragment_upload_forum_button_exploits);
        btnForo = view.findViewById(R.id.fragment_upload_forum_button_forum);

        etName = view.findViewById(R.id.fragment_upload_forum_et_newName);
        etDesc = view.findViewById(R.id.fragment_upload_forum_et_desc);
        etTest = view.findViewById(R.id.fragment_upload_forum_et_poc);
        btnPost = view.findViewById(R.id.fragment_upload_forum_button_post);
    }

    // Funcion para mostrar marcar el edittext donde esta el error
    private void showError (EditText input, String s) {
        input.setError(s);
        input.requestFocus();
    }

    private boolean checkData () {
        String sName = etName.getText().toString(),
                sTest = etTest.getText().toString(),
                sDesc = etDesc.getText().toString();

        if (sName.isEmpty()) {
            showError(etName, "El nombre no puede estar vacío");
            return false;
        }
        if (sDesc.isEmpty()) {
            showError(etDesc, "La descripción no puede estar vacía");
            return false;
        }

        if (sTest.isEmpty()) {
            showError(etTest, "Lo que has probado no puede estar vacío");
            return false;
        }

        return true;
    }

    private void peticion () {

        if (checkData()) {
            String sName = etName.getText().toString(),
                    sTest = etTest.getText().toString(),
                    sDesc = etDesc.getText().toString();

            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("nombre", sName);
                jsonBody.put("tipo_publicacion", 3);
                jsonBody.put("descripcion", sDesc);
                jsonBody.put("probado", sTest);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    Server.getServer() + "v1/nuevaPublicacion",
                    jsonBody,
                    new Response.Listener<JSONObject>(){
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(requireContext(), "Post subido correctament", Toast.LENGTH_SHORT).show();
                            replace(fForum);
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