package com.app.hackapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VistaForo extends AppCompatActivity {

    private TextView tvNombreUsuario, tvNombre, tvDesc, tvProbado;
    private ImageButton imgBtnUsuario;
    private String sToken, sID, sNombrUsuario;
    private RecyclerView recyclerView;
    private Intent intent, oProxAct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_view);

        //Control de intents
        intent = getIntent();
        oProxAct = new Intent(this, OtroUsuarioActivity.class);

        // Boton para volver atras
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //Seteamos los ids de la vista
        setIds();

        // Obtenemos el token de sesion de usuario
        getData();

        peticion();

        // Vista del usuario que ha publicado
        imgBtnUsuario.setOnClickListener(v -> {
            if (!(tvNombreUsuario.getText().toString().equalsIgnoreCase(sNombrUsuario))) {
                startActivity(oProxAct);
            }
        });
    }

    private void getData () {
        SharedPreferences sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        sToken = sharedPreferences.getString("token", null);
        sNombrUsuario = sharedPreferences.getString("Usuario", null);
        sID = intent.getStringExtra("id_pub");
    }

    private void setIds () {

        tvNombreUsuario = findViewById(R.id.fragment_forum_view_text_Usuario);
        tvDesc = findViewById(R.id.fragment_forum_view_et_desc);
        tvProbado = findViewById(R.id.fragment_forum_view_et_poc);
        tvNombre = findViewById(R.id.fragment_forum_view_et_newName);

        imgBtnUsuario = findViewById(R.id.fragment_forum_view_et_userImg);

        recyclerView = findViewById(R.id.fragment_forum_view_recycler);
    }

    private void peticion () {
        //Creamos una peticion para obtener los datos del JSON
        JsonArrayRequest request = new JsonArrayRequest
                (Request.Method.GET,
                        Server.getServer() + "v1/publicacion/" + sID ,
                        null,
                        new Response.Listener<JSONArray>(){
                            @Override
                            public void onResponse(JSONArray response) {
                                try {
                                    JSONObject oObjeto = response.getJSONObject(0);
                                    Publicacion oPost = new Publicacion(oObjeto);
                                    oProxAct.putExtra("seguido", oPost.getsUsuario());
                                    setData(oPost);
                                    getComments();
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
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

        // 3. Añadir la solicitud a la cola de solicitudes
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);

    }

    private void getComments () {

        List<Comentario> oComentarios = new ArrayList<>();

        //Creamos una peticion para obtener los datos del JSON
        JsonArrayRequest request = new JsonArrayRequest
                (Request.Method.GET,
                        Server.getServer() + "v1/comentario/" + sID,
                        null,
                        new Response.Listener<JSONArray>(){
                            @Override
                            public void onResponse(JSONArray response) {
                                try {
                                    //Recorremos el array de datos de la peticion
                                    for (int nIndex = 0; nIndex < response.length(); nIndex++) {
                                        JSONObject jsonObject = response.getJSONObject(nIndex);
                                        Comentario oComentario = new Comentario(jsonObject.getString("usuario"), jsonObject.getDouble("valoracion"), jsonObject.getString("comentario"), jsonObject.getString("avatar"));
                                        oComentarios.add(oComentario);
                                    }

                                    //Mostramos el recyclerview a traves de nuestro adapter
                                    ComentarioAdapter adapter = new ComentarioAdapter(oComentarios, VistaForo.this);
                                    recyclerView.setAdapter(adapter);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(VistaForo.this));

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

        Volley.newRequestQueue(this).add(request);

    }

    private void setData(Publicacion oPost) {
        tvNombreUsuario.setText(oPost.getsUsuario());
        tvDesc.setText(oPost.getsDesc());
        tvProbado.setText(oPost.getStexto());
        tvNombre.setText(oPost.getsNombre());

        Glide.with(VistaForo.this).load(oPost.getsAvatar()).apply(RequestOptions.circleCropTransform()).into(imgBtnUsuario);

    }
}