package com.app.hackapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Options extends Fragment {

    private String sUser, sToken;
    private ImageView imgvUser, imgvEditProfile, imgvAboutUs, imgvFAQS, imgvSecPriv, imgvCloseSession;
    private TextView tvUsername;
    private Switch swDarkMode, swNotis, swPrivAcc;
    private Drawable drawable;
    private int nScaleDP = 10;
    private SharedPreferences sharedPreferences;

    Boolean bNotis, bAcc;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.options_fragment, container, false);

        // Obtener el token y el nombre de usuario
        getData();

        //Set ID
        setIds(view);

        // Obtener los datos de nuestro usuario y manejar los clicks de los img button
        setDataCLicks();

        return view;
    }

    private void getData () {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        sUser = sharedPreferences.getString("username", null);
        sToken = sharedPreferences.getString("token", null);
    }

    private void setIds(View view) {
        tvUsername = view.findViewById(R.id.activity_options_username);
        tvUsername.setText(sUser);

        swDarkMode = view.findViewById(R.id.activity_options_darkMode);
        swNotis = view.findViewById(R.id.activity_options_notis);
        swPrivAcc = view.findViewById(R.id.activity_options_accmode);
        imgvUser = view.findViewById(R.id.activity_options_image);

        imgvEditProfile = view.findViewById(R.id.activity_options_editProfile);
        imgvAboutUs = view.findViewById(R.id.activity_options_info);
        imgvFAQS = view.findViewById(R.id.activity_options_faqs);
        imgvSecPriv = view.findViewById(R.id.activity_options_secpriv);
        imgvCloseSession = view.findViewById(R.id.activity_options_closseSes);
    }

    private void setDataCLicks() {
        setUserData();

        imgvEditProfile.setOnClickListener(v -> {
            //replace(new EditProfile());
        });
        imgvFAQS.setOnClickListener(v -> Toast.makeText(requireContext(), "Esta funcionalidad no esta disponible todavía.", Toast.LENGTH_SHORT).show());
        imgvAboutUs.setOnClickListener(v -> Toast.makeText(requireContext(), "Esta funcionalidad no esta disponible todavía.", Toast.LENGTH_SHORT).show());
        imgvSecPriv.setOnClickListener(v -> Toast.makeText(requireContext(), "Esta funcionalidad no esta disponible todavía.", Toast.LENGTH_SHORT).show());
        imgvCloseSession.setOnClickListener(v -> onSendCloseSSRequest());

        swNotis.setOnClickListener(v -> {
            bNotis = !bNotis;
            swNotis.setChecked(bNotis);
            onSendChengeSwitches("notificaciones", bNotis);
        });

        swPrivAcc.setOnClickListener(v -> {
            bAcc = !bAcc;
            swPrivAcc.setChecked(bAcc);
            onSendChengeSwitches("cuenta_privada", bAcc);
        });

    }

    private void setUserData () {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                Server.getServer() + "v1/datosUsuario/" + sUser,
                null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {

                        // Necesario para que cuando se setean los switch, no se manden las peticiones al servidor
                        swNotis.setOnCheckedChangeListener(null);
                        swPrivAcc.setOnCheckedChangeListener(null);

                        // Seteamos los swatiches segun el user lo tenga en la BBDD y cargamos su imagen de usuario
                        try {
                            bNotis = response.getBoolean("notificaciones");
                            swNotis.setChecked(bNotis);
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                        }
                        try {
                            bAcc = response.getBoolean("cuenta_privada");
                            swPrivAcc.setChecked(bAcc);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
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
                        Toast.makeText(requireContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {

            // Headers de la peticion
            @Override
            public Map<String, String> getHeaders() {
                // Adjuntar el token en la cabecera de la solicitud
                Map<String, String> headers = new HashMap<>();
                headers.put("token", sToken);
                return headers;
            }
        };

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private void onSendCloseSSRequest () {

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.DELETE,
                Server.getServer() + "v1/auth",
                null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        sharedPreferences = getContext().getSharedPreferences("NombreDeTuSharedPreferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("token");
                        editor.remove("username");
                        editor.apply();

                        startActivity(new Intent(requireContext(), LoginActivity.class));
                    }
                }
        ) {

            // Headers de la peticion
            @Override
            public Map<String, String> getHeaders() {
                // Adjuntar el token en la cabecera de la solicitud
                Map<String, String> headers = new HashMap<>();
                headers.put("token", sToken);
                return headers;
            }
        };

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private void onSendChengeSwitches (String sClave, boolean bChecked) {
        JSONObject oBodyRequest = new JSONObject();

        try {
            oBodyRequest.put(sClave, bChecked);

        } catch (JSONException e) {
            Toast.makeText(requireContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                Server.getServer() + "v1/cambioDatos",
                oBodyRequest,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(requireContext(), "Datos Grabados Correctamente", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        ) {

            // Headers de la peticion
            @Override
            public Map<String, String> getHeaders() {
                // Adjuntar el token en la cabecera de la solicitud
                Map<String, String> headers = new HashMap<>();
                headers.put("token", sToken);
                return headers;
            }
        };

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private void replace (Fragment fragment) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, fragment);
        transaction.commit();
    }
}