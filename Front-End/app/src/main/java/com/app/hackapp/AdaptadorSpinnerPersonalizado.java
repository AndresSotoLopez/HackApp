package com.app.hackapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class AdaptadorSpinnerPersonalizado extends ArrayAdapter<CharSequence> {
    private Context context;
    private int textColor;

    public AdaptadorSpinnerPersonalizado(Context context, int resource, List<CharSequence> objects, int textColor) {
        super(context, resource, objects);
        this.context = context;
        this.textColor = textColor;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView textView = (TextView) super.getView(position, convertView, parent);
        textView.setTextColor(textColor);
        return textView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
        textView.setTextColor(textColor);
        return textView;
    }
}

