package com.app.hackapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class noti_activity extends AppCompatActivity {

    private TextView tvUsername;
    private RecyclerView rRecycler;
    private String sToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noti);

        // Boton para volver atras
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // seteamos los ids de la vista
        setIds();

        //Obtenemos el token del usuario
        getUsername();

        // Obtenemos las notificaciones del usuario
        peticion();
    }

    public void setIds() {
        rRecycler = findViewById(R.id.activity_notifications_recycler);
        tvUsername = findViewById(R.id.activity_notifications_username);
    }

    private void peticion () {

        //Creacion de la lista para guardar los datos de la peticion
        List<Notificaciones> aNotis = new ArrayList<>();

        //Creamos una peticion para obtener los datos del JSON
        JsonArrayRequest request = new JsonArrayRequest
                (Request.Method.GET,
                        Server.getServer() + "v1/manejarPeticiones",
                        null,
                        new Response.Listener<JSONArray>(){
                            @Override
                            public void onResponse(JSONArray response) {
                                try {
                                    //Recorremos el array de datos de la peticion
                                    for (int nIndex = 0; nIndex < response.length(); nIndex++) {
                                        JSONObject jsonObject = response.getJSONObject(nIndex);
                                        Notificaciones oNoti = new Notificaciones(jsonObject);
                                        aNotis.add(oNoti);
                                    }

                                    //Mostramos el recyclerview a traves de nuestro adapter
                                    notifications_adapter adapter = new notifications_adapter(aNotis, noti_activity.this);
                                    rRecycler.setAdapter(adapter);
                                    rRecycler.setLayoutManager(new LinearLayoutManager(noti_activity.this));

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

        Volley.newRequestQueue(noti_activity.this).add(request);

    }

    private void getUsername () {
        SharedPreferences sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        sToken = sharedPreferences.getString("token", null);
    }
}