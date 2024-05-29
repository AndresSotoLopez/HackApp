package com.app.hackapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class Buscar extends Fragment {

    private String sToken = "";
    private EditText etBuscador;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.search_fragment, container, false);

        //seteamos los ids
        setIds(view);

        // obtenemos el token de usuario
        getUsuario();

        // obtenemos todos los post
        getPosts("");

        // comprobacion en tiempo para la busqueda
        setRecyclerFiltrado();

        return view;
    }

    private void setIds (View view) {
        etBuscador = view.findViewById(R.id.fragment_search_search);
        recyclerView = view.findViewById(R.id.fragment_search_recycler);
    }

    private void getUsuario () {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        sToken = sharedPreferences.getString("token", null);
    }

    private void getPosts (String sBusqueda) {
        JsonArrayRequest request = new JsonArrayRequest
                (Request.Method.GET,
                        Server.getServer() + "v1/publicacion/0",
                        null,
                        new Response.Listener<JSONArray>(){
                            @Override
                            public void onResponse(JSONArray response) {
                                //Mostramos el recyclerview a traves de nuestro adapter
                                UsuarioAdapter adapter = null;
                                try {
                                    adapter = new UsuarioAdapter(response, requireActivity(), sBusqueda);
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                                recyclerView.setAdapter(adapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
                            }
                        },
                        new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        }
                ) {
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

    private void setRecyclerFiltrado () {
        etBuscador.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getPosts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}