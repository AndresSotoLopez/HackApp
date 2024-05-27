package com.app.hackapp;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageButton;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home extends Fragment {

    private RecyclerView recyclerView;
    private ImageButton btnNotis;
    private EditText etBuscador;

    private String sToken = "";
    private ExploitAdapter adapter = new ExploitAdapter();

    //Creacion de la lista para guardar los datos de la peticion
    List<Exploits> aExploits = new ArrayList<>();
    private List<Exploits> aExploitsFiltrados = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) throws IllegalStateException{
        // Cargar el fragmento
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        // Setear las variables
        recyclerView = view.findViewById(R.id.activity_home_exploits);
        btnNotis = view.findViewById(R.id.activity_home_notis);
        etBuscador = view.findViewById(R.id.activity_home_search);

        // Obtener el SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        // Obtener el valor de una clave
        sToken = sharedPreferences.getString("token", null);

        if (sToken != null) {
            onSendRequest();
        }

        btnNotis.setOnClickListener(v -> {
            Context cContext = v.getContext();
            Intent intent = new Intent(cContext, NotificacionesActivity.class);
            cContext.startActivity(intent);
        });

        onSetArrayFiltrado();

        return view;
    }

    private void onSendRequest(){

        // Cada vez que se lance la peticion se borra la lista para que los items no se dupliquen
        aExploits.clear();
        //Creamos una peticion para obtener los datos del JSON
        JsonArrayRequest request = new JsonArrayRequest
                (Request.Method.GET,
                        Server.getServer() + "v1/publicacion/0?tipoNoticia=2",
                        null,
                        new Response.Listener<JSONArray>(){
                            @Override
                            public void onResponse(JSONArray response) {
                                try {
                                    //Recorremos el array de datos de la peticion
                                    for (int nIndex = 0; nIndex < response.length(); nIndex++) {
                                        JSONObject oObjeto = response.getJSONObject(nIndex);
                                        Exploits exploits = new Exploits(oObjeto);
                                        aExploits.add(exploits);
                                    }

                                    //Mostramos el recyclerview a traves de nuestro adapter
                                    adapter = new ExploitAdapter(aExploits, getActivity());
                                    recyclerView.setAdapter(adapter);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

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

    private void onSetArrayFiltrado () {
        etBuscador.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                aExploitsFiltrados.clear();
                if (s.toString().isEmpty()) {
                    aExploitsFiltrados.addAll(aExploits);
                } else {
                    String sTexto = s.toString().toLowerCase();
                    for (Exploits exploit : aExploits) {
                        if (exploit.getsNombre().toLowerCase().contains(sTexto)) {
                            aExploitsFiltrados.add(exploit);
                        }
                    }
                }
                adapter = new ExploitAdapter(aExploitsFiltrados, getActivity());
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}