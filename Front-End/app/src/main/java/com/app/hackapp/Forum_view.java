package com.app.hackapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Forum_view extends AppCompatActivity {

    private TextView tvUsername, tvName, tvDesc, tvTest;
    private ImageButton imgbtnUser;
    private String sToken, sID;
    private RecyclerView recyclerView;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_view);
        intent = getIntent();

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

        imgbtnUser.setOnClickListener(v -> {

        });
    }

    private void getData () {
        SharedPreferences sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        sToken = sharedPreferences.getString("token", null);

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
                                Toast.makeText(Forum_view.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

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

        List<comment> oComentarios = new ArrayList<>();

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
                                        comment oComment = new comment(jsonObject.getString("usuario"), jsonObject.getDouble("valoracion"), jsonObject.getString("comentario"), jsonObject.getString("avatar"));
                                        oComentarios.add(oComment);
                                    }

                                    //Mostramos el recyclerview a traves de nuestro adapter
                                    comment_adapter adapter = new comment_adapter(oComentarios, Forum_view.this);
                                    recyclerView.setAdapter(adapter);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(Forum_view.this));

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
        tvTest.setText(oPost.getsTest());
        tvName.setText(oPost.getsNombre());

        Glide.with(Forum_view.this).load(oPost.getsAvatar()).apply(RequestOptions.circleCropTransform()).into(imgbtnUser);

    }
}