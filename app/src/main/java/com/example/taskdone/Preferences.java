package com.example.taskdone;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    private static final String STRING_PREFERENCES = "Finanzas";

    public static void savePreferenceBoolean(Context c, boolean b, String key) {
        SharedPreferences preferences = c.getSharedPreferences(STRING_PREFERENCES, Context.MODE_PRIVATE);
        preferences.edit().putBoolean(key, b).apply();
    }

    public static boolean getPreferenceBoolean(Context c, String key) {
        SharedPreferences preferences = c.getSharedPreferences(STRING_PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);//Si es que nunca se ha guardado nada en esta key pues retornara false
    }

    public static void savePreferenceString(Context c, String b, String key) {
        SharedPreferences preferences = c.getSharedPreferences(STRING_PREFERENCES, Context.MODE_PRIVATE);
        preferences.edit().putString(key, b).apply();
    }

    public static String getPreferenceString(Context c, String key) {
        SharedPreferences preferences = c.getSharedPreferences(STRING_PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getString(key, "");//Si es que nunca se ha guardado nada en esta key pues retornara una cadena vacia
    }

    public static void deletePreferenceString(Context c, String key) {
        SharedPreferences preferences = c.getSharedPreferences(STRING_PREFERENCES, Context.MODE_PRIVATE);
        preferences.edit().remove(key).apply();
    }

    public static void deleteAllPreferenceString(Context c) {
        SharedPreferences preferences = c.getSharedPreferences(STRING_PREFERENCES, Context.MODE_PRIVATE);
        preferences.edit().clear().apply();
    }

    public static void cleanPreferencesGastoPendiente(Context c){
        Preferences.deletePreferenceString(c, "gasto_fecha");
        Preferences.deletePreferenceString(c, "gasto_moneda_index");
        Preferences.deletePreferenceString(c, "gasto_cantidad");
        Preferences.deletePreferenceString(c, "gasto_motivo");
        Preferences.deletePreferenceString(c, "gasto_ingreso");
        Preferences.deletePreferenceString(c, "gasto_tags");
        Preferences.deletePreferenceString(c, "scroll_tags");
    }

}


