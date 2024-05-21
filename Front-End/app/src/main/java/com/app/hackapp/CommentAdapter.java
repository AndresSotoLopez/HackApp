package com.app.hackapp;

import android.content.Context;
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

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {

    private final List<Comment> lComentarios;
    private final LayoutInflater layoutInflater;
    private final Context context;

    public CommentAdapter(List<Comment> lComentarios, Context context) {
        this.lComentarios = lComentarios;
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public CommentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.comment_card, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.MyViewHolder holder, int position) {
        Comment oComentario = lComentarios.get(position);

        Glide.with(context).load(oComentario.getsAvatar()).apply(RequestOptions.circleCropTransform()).into(holder.image);
        holder.sUserName.setText(oComentario.getsUsername());
        holder.sVal.setText(String.valueOf(oComentario.getfVal()));
        holder.sComment.setText(oComentario.getsComment());
    }

    @Override
    public int getItemCount() {
        return lComentarios.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView sUserName, sComment, sVal;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.user_img);
            sUserName = itemView.findViewById(R.id.user_name);
            sVal = itemView.findViewById(R.id.valoracion);
            sComment = itemView.findViewById(R.id.comment);
        }

    }

}
