package com.example.taskdone;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class Preferencias {

    private static final String STRING_PREFERENCES = "USUARIO";

    public static final String PRIMERA_VEZ_AGENDA = "primera.vez.agenda";
    public static final String PRIMERA_VEZ_OBJETIVOS = "primera.vez.objetivos";
    public static final String PRIMERA_VEZ_FINANZAS = "primera.vez.finanzas";
    public static final String PRIMERA_VEZ_COMIDAS = "primera.vez.comidas";
    public static final String PRIMERA_VEZ_LOGROS = "primera.vez.logros";
    public static final String PRIMERA_VEZ_PUNTAJE = "primera.vez.puntaje";

    public static void savePreferenceBoolean(Context c, boolean b, String key) {
        SharedPreferences preferences = c.getSharedPreferences(STRING_PREFERENCES, Context.MODE_PRIVATE);
        preferences.edit().putBoolean(key, b).apply();
    }

    public static boolean obtenerPreferenceBoolean(Context c, String key) {
        SharedPreferences preferences = c.getSharedPreferences(STRING_PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);//Si es que nunca se ha guardado nada en esta key pues retornara false
    }

    public static void savePreferenceString(Context c, String b, String key) {
        SharedPreferences preferences = c.getSharedPreferences(STRING_PREFERENCES, Context.MODE_PRIVATE);
        preferences.edit().putString(key, b).apply();
    }

    public static String obtenerPreferenceString(Context c, String key) {
        SharedPreferences preferences = c.getSharedPreferences(STRING_PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getString(key, "");//Si es que nunca se ha guardado nada en esta key pues retornara una cadena vacia
    }

}

