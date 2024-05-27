package com.app.hackapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificacionesAdapter extends RecyclerView.Adapter<NotificacionesAdapter.MyViewHolder>{

    private final List<Notificaciones> aNotis;
    private String sToken = "";
    private final LayoutInflater layoutInflater;
    private final Context context;

    //Constructor
    public NotificacionesAdapter(List<Notificaciones> aNotis, Context context) {
        this.layoutInflater = LayoutInflater.from(context);
        this.aNotis = aNotis;
        this.context = context;

        SharedPreferences sharedPreferences = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        sToken = sharedPreferences.getString("token", null);
    }

    @NonNull
    @Override
    public NotificacionesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.friend_request_cell, null);
        return new NotificacionesAdapter.MyViewHolder(view);
    }


    @SuppressLint("SetTextI18n")
    public void onBindViewHolder(@NonNull NotificacionesAdapter.MyViewHolder holder, int position) {

        Notificaciones noti = aNotis.get(position);

        holder.sID = String.valueOf(noti.getnId());
        holder.sTitle.setText(noti.getsSeguido() + " ha solicitado seguirte");
        Glide.with(context).load(noti.getsAvatar()).apply(RequestOptions.circleCropTransform()).into(holder.image);

        holder.btnAceptar.setOnClickListener(v -> onSendAcceptRequest(position, String.valueOf(noti.getnId()), holder));
        holder.btnRechazar.setOnClickListener(v -> onSendRejectRequest(position, String.valueOf(noti.getnId()), holder));

    }

    @Override
    public int getItemCount() { return aNotis.size(); }

    private void onSendAcceptRequest (int position, String sID, MyViewHolder holder) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                Server.getServer() + "v1/manejarPeticiones?id=" + sID,
                null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(context, "Solicitud de amistad aceptada", Toast.LENGTH_SHORT).show();
                            holder.btnAceptar.setVisibility(View.INVISIBLE);
                            holder.btnRechazar.setVisibility(View.INVISIBLE);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "No se ha podido aceptar la solicitud", Toast.LENGTH_SHORT).show();
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

        Volley.newRequestQueue(context).add(request);
    }

    private void onSendRejectRequest (int position, String sID, MyViewHolder holder) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.DELETE,
                Server.getServer() + "v1/manejarPeticiones?id=" + sID,
                null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            aNotis.remove(position);
                            Toast.makeText(context, "Solicitud de amistad rechazada", Toast.LENGTH_SHORT).show();
                            holder.btnAceptar.setVisibility(View.INVISIBLE);
                            holder.btnRechazar.setVisibility(View.INVISIBLE);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "No se ha podido rechazar la solicitud", Toast.LENGTH_SHORT).show();
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

        Volley.newRequestQueue(context).add(request);
    }

    //Clase que nos permite obtener la vista de nuestro recyclerview
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView sTitle;
        String sID = "";

        Button btnAceptar, btnRechazar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.user_img);
            sTitle = itemView.findViewById(R.id.username);
            btnAceptar = itemView.findViewById(R.id.accept);
            btnRechazar = itemView.findViewById(R.id.reject);

        }
    }
}
