package com.app.hackapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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

public class User extends Fragment {

    private String sUser, sToken;
    private TextView tvUsername, tvPost, tvFollowers, tvFollows, tvBio;
    private ImageView imgvUser;
    private ImageButton imgbSettings;
    private RecyclerView recyclerView;

    RequestQueue requestQueue;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) throws IllegalStateException{
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.user_fragment, container, false);

        requestQueue = Volley.newRequestQueue(requireContext());

        // Obtenemos el nombre de usuario y el token
        getUsername();

        // Seteamos el nombre de usuario a la vista
        tvUsername = view.findViewById(R.id.fragment_user_username);

        // Asiganmos los id a los textview, obtenemos los datos del usuario y luego los asignamos.
        tvPost = view.findViewById(R.id.fragment_user_number_post);
        tvFollowers = view.findViewById(R.id.fragment_user_number_followers);
        tvFollows = view.findViewById(R.id.fragment_user_number_follows);
        tvBio = view.findViewById(R.id.fragment_user_bio_text);
        recyclerView = view.findViewById(R.id.fragment_user_recycler);
        imgvUser = view.findViewById(R.id.fragment_user_image);
        imgbSettings = view.findViewById(R.id.fragment_user_settings);

        if (sToken != null) {
            getUserInfo();
            getuserPosts();
        }

        imgbSettings.setOnClickListener(v -> {
            replace(new Options());
        });

        return view;
    }

    private void getUsername () {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        sUser = sharedPreferences.getString("username", null);
        sToken = sharedPreferences.getString("token", null);
    }
    private void getUserInfo () {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                Server.getServer() + "v1/datosUsuario/" + sUser,
                null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            tvUsername.setText(sUser);
                            tvPost.setText(String.valueOf(response.getInt("posts")));
                            tvFollows.setText(String.valueOf(response.getInt("seguidos")));
                            tvFollowers.setText(String.valueOf(response.getInt("seguidores")));
                            String sBio = response.getString("biografia");
                            if (!(sBio != null)) {
                                tvBio.setText(sBio);
                            }
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                        }
                        try {
                            Glide.with(requireContext()).load(
                                    response.getString("avatar"))
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(imgvUser);
                        } catch (JSONException e) {
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

    private void getuserPosts () {
        JsonArrayRequest request = new JsonArrayRequest
                (Request.Method.GET,
                        Server.getServer() + "v1/publicacion/0?username="+sUser,
                        null,
                        new Response.Listener<JSONArray>(){
                            @Override
                            public void onResponse(JSONArray response) {
                                //Mostramos el recyclerview a traves de nuestro adapter
                                UserAdapter adapter = null;
                                try {
                                    adapter = new UserAdapter(response, getActivity(), "");
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                                recyclerView.setAdapter(adapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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

    private void replace (Fragment fragment) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, fragment);
        transaction.commit();
    }
}
