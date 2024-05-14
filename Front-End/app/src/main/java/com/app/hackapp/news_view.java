package com.app.hackapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class news_view extends AppCompatActivity {

    private TextView tvUsername, tvName, tvDesc;
    private ImageButton imgbtnUser;
    private ImageView imgvNewsImage;
    private String sToken, sID;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_view_activity);

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
    }

    private void getData () {
        SharedPreferences sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        sToken = sharedPreferences.getString("token", null);

        sID = intent.getStringExtra("id_pub");
    }

    private void setIds () {

        tvUsername = findViewById(R.id.fragment_news_view_text_Username);
        tvDesc = findViewById(R.id.fragment_news_view_et_poc);
        tvName = findViewById(R.id.fragment_news_view_et_newName);
        imgvNewsImage = findViewById(R.id.fragment_news_view_et_newsImage);
        imgbtnUser = findViewById(R.id.fragment_news_view_et_userImg);
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
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }

                            }
                        },
                        new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(news_view.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

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
        tvUsername.setText(oPost.getsUsername());
        tvDesc.setText(oPost.getsDesc());
        tvName.setText(oPost.getsNombre());

        Glide.with(news_view.this).load(oPost.getsAvatar()).apply(RequestOptions.circleCropTransform()).into(imgbtnUser);
        Glide.with(news_view.this).load(oPost.getsImagen()).into(imgvNewsImage);

    }
}