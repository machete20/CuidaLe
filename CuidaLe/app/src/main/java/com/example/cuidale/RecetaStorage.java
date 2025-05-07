package com.example.cuidale;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class RecetaStorage {

    private static final String PREFS_NAME = "recetas_prefs";
    private static final String KEY_RECETAS = "recetas";

    public static void guardarRecetas(Context context, ArrayList<Receta> recetas) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(recetas);
        editor.putString(KEY_RECETAS, json);
        editor.apply();
    }

    public static ArrayList<Receta> cargarRecetas(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_RECETAS, null);

        if (json == null) return new ArrayList<>();

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Receta>>(){}.getType();
        return gson.fromJson(json, type);
    }
}
