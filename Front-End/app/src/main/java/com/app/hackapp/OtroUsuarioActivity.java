package com.app.hackapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
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

import java.util.HashMap;
import java.util.Map;

public class OtroUsuarioActivity extends AppCompatActivity {

    private String sUsuario, sToken, sSeguido;
    private Intent intent;
    private TextView tvNombreUsuario, tvPost, tvSeguidores, tvSeguidos, tvBio, tvTexto;
    private ImageView imgvUsuario, imgImg;
    private ImageButton imgBtnOpciones;
    private RecyclerView recyclerView;
    RequestQueue requestQueue;
    private Button btnSeguir;
    private int nSoliID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_user_activity);

        // Creamos la cola de peticiones
        requestQueue = Volley.newRequestQueue(this);

        // Boton para volver a la mainActivy
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // seteamos los ids
        setIds();
        tvTexto.setVisibility(View.GONE);
        imgImg.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        intent = getIntent();
        sSeguido = intent.getStringExtra("seguido");

        // Obtenemos los datos de las sharedpreferences y los datos del usuario
        getUserPreferences();
        getUserInfo();


        btnSeguir.setOnClickListener(v -> {
            if (btnSeguir.getText().toString().equalsIgnoreCase("seguir")) {
                onSendFollowRequest();
                getUserInfo();
            } else {
                onSendUnfollowRequest();
                getUserInfo();

            }
        });
    }

    private void setIds () {
        tvPost = findViewById(R.id.activity_other_user_number_post);
        tvSeguidores = findViewById(R.id.activity_other_user_number_followers);
        tvSeguidos = findViewById(R.id.activity_other_user_number_follows);
        tvBio = findViewById(R.id.activity_other_user_bio_text);
        tvTexto = findViewById(R.id.activity_other_user_text);
        recyclerView = findViewById(R.id.activity_other_user_recycler);
        imgvUsuario = findViewById(R.id.activity_other_user_image);
        imgBtnOpciones = findViewById(R.id.fragment_user_settings);
        tvNombreUsuario = findViewById(R.id.activity_other_user_Usuario);
        imgImg = findViewById(R.id.activity_other_user_img);
        btnSeguir = findViewById(R.id.activity_other_user_follow_button);
    }

    private void getUserPreferences () {
        SharedPreferences sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        sUsuario = sharedPreferences.getString("Usuario", null);
        sToken = sharedPreferences.getString("token", null);
    }

    private void getUserInfo () {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                Server.getServer() + "v1/datosUsuario/" + sSeguido,
                null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            tvNombreUsuario.setText(sSeguido);
                            tvPost.setText(String.valueOf(response.getInt("posts")));
                            tvSeguidos.setText(String.valueOf(response.getInt("seguidos")));
                            tvSeguidores.setText(String.valueOf(response.getInt("seguidores")));
                            if (response.getString("biografia").equals(null)) {
                                tvBio.setText(response.getString("biografia"));
                            }
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                        }
                        try {
                            Glide.with(OtroUsuarioActivity.this).load(
                                            response.getString("avatar"))
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(imgvUsuario);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            //Comprobamos si el usuario sigue al usuario que se carga en la vista
                            isFollowed(response.getBoolean("cuenta_privada"));
                        } catch (JSONException | NullPointerException e) {
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
        request.setTag("request");
        requestQueue.add(request);
    }

    private void isFollowed (Boolean bPrivateAcc) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                Server.getServer() + "v1/comprobarSeguimiento?seguidor=" + sUsuario + "&seguido=" + sSeguido,
                null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Obtener el valor booleano del JSON
                            boolean isFollowing = response.getBoolean("comprobacion");
                            nSoliID = response.getInt("id");

                            if (isFollowing) {
                                //Cambiar el boton y añadir objetos al recycler y muestro los datos
                                recyclerView.setVisibility(View.VISIBLE);
                                tvTexto.setVisibility(View.GONE);
                                imgImg.setVisibility(View.GONE);
                                btnSeguir.setBackgroundResource(R.drawable.transparent_button_color_999999);
                                btnSeguir.setText("Siguiendo");
                                getuserPosts();
                            }
                            else {
                                //oculto el recycler, cambiar estilo boton a solicitado background y texto ocultar recycler y mostar imgview y texto
                                recyclerView.setVisibility(View.GONE);
                                tvTexto.setVisibility(View.VISIBLE);
                                imgImg.setVisibility(View.VISIBLE);
                                btnSeguir.setBackgroundResource(R.drawable.transparent_button_color_999999);
                                btnSeguir.setText("Solicitado");
                            }

                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            int statusCode = error.networkResponse.statusCode;
                            if (statusCode == 404) {
                                // Verificar si la cuenta es privada
                                if (bPrivateAcc) {
                                    btnSeguir.setBackgroundResource(R.drawable.blue_button);
                                    btnSeguir.setText("Seguir");
                                    recyclerView.setVisibility(View.GONE);
                                    tvTexto.setVisibility(View.VISIBLE);
                                    imgImg.setVisibility(View.VISIBLE);
                                }
                                else {
                                    btnSeguir.setBackgroundResource(R.drawable.blue_button);
                                    btnSeguir.setText("Seguir");
                                    recyclerView.setVisibility(View.VISIBLE);
                                    tvTexto.setVisibility(View.GONE);
                                    imgImg.setVisibility(View.GONE);
                                    getuserPosts();
                                }
                            }
                        }
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
        request.setTag("request");
        requestQueue.add(request);
    }

    private void getuserPosts () {
        JsonArrayRequest request = new JsonArrayRequest
                (Request.Method.GET,
                        Server.getServer() + "v1/publicacion/0?Usuario="+sSeguido,
                        null,
                        new Response.Listener<JSONArray>(){
                            @Override
                            public void onResponse(JSONArray response) {
                                //Mostramos el recyclerview a traves de nuestro adapter
                                UsuarioAdapter adapter = null;
                                try {
                                    adapter = new UsuarioAdapter(response, OtroUsuarioActivity.this, "");
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                                recyclerView.setAdapter(adapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(OtroUsuarioActivity.this));
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
        request.setTag("request");
        requestQueue.add(request);
    }

    private void onSendFollowRequest () {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                Server.getServer() + "v1/mandarPeticiones?user=" + sSeguido,
                null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(OtroUsuarioActivity.this, "Solicitud enviada", Toast.LENGTH_SHORT).show();
                        btnSeguir.setBackgroundResource(R.drawable.transparent_button_color_999999);
                        btnSeguir.setText("Solicitado");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(OtroUsuarioActivity.this, "No se ha podido realizar la solicitud", Toast.LENGTH_SHORT).show();
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
        request.setTag("request");
        requestQueue.add(request);
    }

    private void onSendUnfollowRequest () {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.DELETE,
                Server.getServer() + "v1/manejarPeticiones?id=" + nSoliID,
                null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(OtroUsuarioActivity.this, "Solicitud cancelada", Toast.LENGTH_SHORT).show();
                        btnSeguir.setBackgroundResource(R.drawable.blue_button);
                        btnSeguir.setText("Seguir");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(OtroUsuarioActivity.this, "No se ha podido realizar la solicitud", Toast.LENGTH_SHORT).show();
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
        request.setTag("request");
        requestQueue.add(request);
    }

}