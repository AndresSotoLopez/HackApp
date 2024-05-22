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

public class news_adapter extends RecyclerView.Adapter<news_adapter.MyViewHolder>{

    private final List<News> aNews;
    private final LayoutInflater layoutInflater;
    private final Context context;

    //Constructor
    public news_adapter(List<News> aNews, Context context) {
        this.layoutInflater = LayoutInflater.from(context);
        this.aNews = aNews;
        this.context = context;
    }

    @NonNull
    @Override
    public news_adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.news_forum_card_view, null);
        return new news_adapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull news_adapter.MyViewHolder holder, int position) {

        News notica = aNews.get(position);

        holder.sID = String.valueOf(notica.getnId());
        holder.sTitle.setText(notica.getsName());
        holder.sDesc.setText(notica.getsDescrip());
        Glide.with(context).load(notica.getsLinkImage()).apply(RequestOptions.circleCropTransform()).into(holder.image);

    }

    @Override
    public int getItemCount() { return aNews.size(); }

    //Clase que nos permite obtener la vista de nuestro recyclerview
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView sTitle, sDesc;
        String sID = "";

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.iconImageView);
            sTitle = itemView.findViewById(R.id.news_title);
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
