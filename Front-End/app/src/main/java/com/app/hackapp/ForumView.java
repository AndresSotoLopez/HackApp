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

public class ForumView extends AppCompatActivity {

    private TextView tvUsername, tvName, tvDesc, tvTest;
    private ImageButton imgbtnUser;
    private String sToken, sID, sUsername;
    private RecyclerView recyclerView;
    private Intent intent, oNextAct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_view);

        //Control de intents
        intent = getIntent();
        oNextAct = new Intent(this, OtherUserActivity.class);

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
        imgbtnUser.setOnClickListener(v -> {
            if (!(tvUsername.getText().toString().equalsIgnoreCase(sUsername))) {
                startActivity(oNextAct);
            }
        });
    }

    private void getData () {
        SharedPreferences sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        sToken = sharedPreferences.getString("token", null);
        sUsername = sharedPreferences.getString("username", null);
        sID = intent.getStringExtra("id_pub");
    }

    private void setIds () {

        tvUsername = findViewById(R.id.fragment_forum_view_text_Username);
        tvDesc = findViewById(R.id.fragment_forum_view_et_desc);
        tvTest = findViewById(R.id.fragment_forum_view_et_poc);
        tvName = findViewById(R.id.fragment_forum_view_et_newName);

        imgbtnUser = findViewById(R.id.fragment_forum_view_et_userImg);

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
                                    JSONObject oObject = response.getJSONObject(0);
                                    Publicacion oPost = new Publicacion(oObject);
                                    oNextAct.putExtra("seguido", oPost.getsUsername());
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

        List<Comment> oComentarios = new ArrayList<>();

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
                                        Comment oComment = new Comment(jsonObject.getString("usuario"), jsonObject.getDouble("valoracion"), jsonObject.getString("comentario"), jsonObject.getString("avatar"));
                                        oComentarios.add(oComment);
                                    }

                                    //Mostramos el recyclerview a traves de nuestro adapter
                                    CommentAdapter adapter = new CommentAdapter(oComentarios, ForumView.this);
                                    recyclerView.setAdapter(adapter);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(ForumView.this));

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
        tvUsername.setText(oPost.getsUsername());
        tvDesc.setText(oPost.getsDesc());
        tvTest.setText(oPost.getsText());
        tvName.setText(oPost.getsNombre());

        Glide.with(ForumView.this).load(oPost.getsAvatar()).apply(RequestOptions.circleCropTransform()).into(imgbtnUser);

    }
}