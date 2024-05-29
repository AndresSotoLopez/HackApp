package com.app.hackapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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

import java.util.HashMap;
import java.util.Map;

public class NewsView extends AppCompatActivity {

    private TextView tvNombreUsuario, tvNombre, tvDesc;
    private ImageButton imtbtnUsuario;
    private ImageView imgvImagenNoticia;
    private String sToken, sID, sNombreUsuario;
    private Intent intent, oProxAct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_view_activity);

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

        getPosts();

        // Vista del usuario que ha publicado
        imtbtnUsuario.setOnClickListener(v -> {
            if (!(tvNombreUsuario.getText().toString().equalsIgnoreCase(sNombreUsuario))) {
                startActivity(oProxAct);
            }
        });
    }

    private void getData () {
        SharedPreferences sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        sToken = sharedPreferences.getString("token", null);
        sNombreUsuario = sharedPreferences.getString("Usuario", null);
        sID = intent.getStringExtra("id_pub");
    }

    private void setIds () {

        tvNombreUsuario = findViewById(R.id.fragment_news_view_text_Usuario);
        tvDesc = findViewById(R.id.fragment_news_view_et_poc);
        tvNombre = findViewById(R.id.fragment_news_view_et_newName);
        imgvImagenNoticia = findViewById(R.id.fragment_news_view_et_newsImage);
        imtbtnUsuario = findViewById(R.id.fragment_news_view_et_userImg);
    }

    private void getPosts() {
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
                                    oProxAct.putExtra("seguido", oPost.getsUsuario());
                                    setData(oPost);
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(NewsView.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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

    private void setData(Publicacion oPost) {
        tvNombreUsuario.setText(oPost.getsUsuario());
        tvDesc.setText(oPost.getsDesc());
        tvNombre.setText(oPost.getsNombre());

        Glide.with(NewsView.this).load(oPost.getsAvatar()).apply(RequestOptions.circleCropTransform()).into(imtbtnUsuario);
        if (!oPost.getsImagen().equals("")){
            Glide.with(NewsView.this).load(oPost.getsImagen()).fitCenter().into(imgvImagenNoticia);
            imgvImagenNoticia.setBackgroundColor(Color.TRANSPARENT);
        }


    }
}