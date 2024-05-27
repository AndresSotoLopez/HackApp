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

import java.util.List;

public class NoticiasAdapter extends RecyclerView.Adapter<NoticiasAdapter.MyViewHolder>{

    private final List<NoticiasClase> aNoticias;
    private final LayoutInflater layoutInflater;
    private final Context context;

    //Constructor
    public NoticiasAdapter(List<NoticiasClase> aNoticias, Context context) {
        this.layoutInflater = LayoutInflater.from(context);
        this.aNoticias = aNoticias;
        this.context = context;
    }

    @NonNull
    @Override
    public NoticiasAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.news_forum_card_view, null);
        return new NoticiasAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticiasAdapter.MyViewHolder holder, int position) {

        NoticiasClase notica = aNoticias.get(position);

        holder.sID = String.valueOf(notica.getnId());
        holder.sTitulo.setText(notica.getsNombre());
        holder.sDesc.setText(notica.getsDescrip());
        Glide.with(context).load(notica.getsLinkImagen()).apply(RequestOptions.circleCropTransform()).into(holder.image);

    }

    @Override
    public int getItemCount() { return aNoticias.size(); }

    //Clase que nos permite obtener la vista de nuestro recyclerview
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView sTitulo, sDesc;
        String sID = "";

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.iconImageView);
            sTitulo = itemView.findViewById(R.id.news_title);
            sDesc = itemView.findViewById(R.id.news_description);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context cContext = v.getContext();
                    Intent intent = new Intent(cContext, NewsView.class);
                    intent.putExtra("id_pub", sID);
                    cContext.startActivity(intent);
                }
            });
        }

    }
}
