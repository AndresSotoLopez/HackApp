package com.app.hackapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {

    //Definicion de las variables
    private final JSONArray aJsonObtecs;
    private final LayoutInflater layoutInflater;
    private final Context context;

    //Constructor
    public UserAdapter(JSONArray aJsonObtecs, Context context) {
        this.layoutInflater = LayoutInflater.from(context);
        this.aJsonObtecs = aJsonObtecs;
        this.context = context;
    }

    // Esta funcion nos permite obtener el tipo de publicacion para poder alternar entre las distintas carview
    @Override
    public int getItemViewType(int position) {
        // Retorna un valor único para cada tipo de publicación (puede ser un atributo de tu modelo)
        try {
            return aJsonObtecs.getJSONObject(position).getInt("tipo_publicacion");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    //Funcion que nos permite crear la vista de nuestra recyclerview
    @NonNull
    @Override
    public UserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        switch (viewType) {
            case 1:
                view = inflater.inflate(R.layout.news_forum_card_view, parent, false);
                return new MyViewHolder(view, 1);
            case 2:
                view = inflater.inflate(R.layout.exploit_card_view, parent, false);
                return new MyViewHolder(view, 2);
            default:
                view = inflater.inflate(R.layout.news_forum_card_view, parent, false);
                return new MyViewHolder(view, 3);
        }
    }

    //Funcion que nos permite actualizar la vista de nuestro recyclerview
    @Override
    public void onBindViewHolder(@NonNull UserAdapter.MyViewHolder holder, int position) {


        try {
            int tipo = aJsonObtecs.getJSONObject(position).getInt("tipo_publicacion");
            switch (tipo) {
                case 2:
                    holder.title.setText(aJsonObtecs.getJSONObject(position).getString("nombre"));
                    holder.nTipo = 2;
                    switch (aJsonObtecs.getJSONObject(position).getInt("gravedad")) {
                        case 1:
                            holder.image.setColorFilter(0xFF3366FF);
                            break;
                        case 2:
                            holder.image.setColorFilter(0xFFFFFF00);
                            break;
                        case 3:
                            holder.image.setColorFilter(0xFFFFA500);
                            break;
                        default:
                            holder.image.setColorFilter(0xFFFF0000);
                            break;
                    }
                    break;

                default:
                    Glide.with(context).load(aJsonObtecs.getJSONObject(position).getString("imagen")).apply(RequestOptions.circleCropTransform()).into(holder.image);
                    String test = aJsonObtecs.getJSONObject(position).getString("nombre");
                    holder.title.setText(test);
                    String test2 = aJsonObtecs.getJSONObject(position).getString("descripcion");
                    holder.description.setText(test2);
                    holder.nTipo = tipo;
                    break;
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        //Cuando se haga click en la vista de nuestra recyclerview
        holder.itemView.setOnClickListener(v -> {

            Intent intent;

            switch (holder.nTipo) {
                case 1:
                    intent = new Intent(context, NewsView.class);
                    break;
                case 2:
                    intent = new Intent(context, ExploitView.class);
                    break;
                default:
                    intent = new Intent(context, ForumView.class);
            }
            try {
                intent.putExtra("id_pub", String.valueOf(aJsonObtecs.getJSONObject(position).getInt("id")));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            context.startActivity(intent);
        });

    }

    // Funcion que nos permite obtener el tamaño de la lista
    @Override
    public int getItemCount() {
        return aJsonObtecs.length();
    }

    //Clase que nos permite obtener la vista de nuestra recyclerview
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView title, description;
        int nTipo = 0;
        public MyViewHolder(@NonNull View itemView, int postType) {
            super(itemView);

            if (postType == 2) {
                image = itemView.findViewById(R.id.iconImageView);
                title = itemView.findViewById(R.id.exploit_title);
            } else {
                image = itemView.findViewById(R.id.iconImageView);
                title = itemView.findViewById(R.id.news_title);
                description = itemView.findViewById(R.id.news_description);

            }
        }
    }
}