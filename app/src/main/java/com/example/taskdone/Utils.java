package com.example.taskdone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.taskdone.Acceso.ActivityLogin;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utils {

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public static void setContext(Context context) {
        Utils.context = context;
    }

    public static String getMes(int mes){
        switch (mes){
            case 1:
                return "Enero";
            case 2:
                return "Febrero";
            case 3:
                return "Marzo";
            case 4:
                return "Abril";
            case 5:
                return "Mayo";
            case 6:
                return "Junio";
            case 7:
                return "Julio";
            case 8:
                return "Agosto";
            case 9:
                return "Septiembre";
            case 10:
                return "Octubre";
            case 11:
                return "Noviembre";
            case 12:
                return "Diciembre";
        }
        return "";
    }

    public static String getDia(String fecha){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        String idioma = Preferences.getPreferenceString(context, "idioma");
        DateFormat df;
        if(idioma.equals("")){
            Locale.setDefault(Locale.getDefault());
            df = DateFormat.getDateInstance(DateFormat.FULL, context.getResources().getConfiguration().locale);
        }
        else {
            df = DateFormat.getDateInstance(DateFormat.FULL, Locale.ENGLISH);
        }
        Calendar c = Calendar.getInstance();
        c.set(Integer.parseInt(fecha.substring(0,4)), Integer.parseInt(fecha.substring(5,7))-1, Integer.parseInt(fecha.substring(8,10)));
        String dia = df.format(c.getTime());
        return dia.substring(0, 1).toUpperCase() + dia.substring(1);
    }

    public static Calendar dateToCalendar(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public static String calendarToString(Calendar c){
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return year + "/" + twoDigits(month + 1) + "/" + twoDigits(day);
    }

    public static Boolean fechaMayorQueHoy(String f) throws ParseException {

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy/mm/dd");
        Date fecha = sdf.parse(f);
        assert fecha != null;
        return fecha.after(Calendar.getInstance().getTime());
    }

    public static Calendar getPrimerDiaDelMes() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        return c;
    }

    public static boolean isPrimerDiaDelMes(String fecha){
        return getPrimerDiaDelMes().get(Calendar.YEAR) == Integer.parseInt(fecha.substring(0,4))
                && getPrimerDiaDelMes().get(Calendar.MONTH)+1 == Integer.parseInt(fecha.substring(5,7))
                && getPrimerDiaDelMes().get(Calendar.DAY_OF_MONTH) == Integer.parseInt(fecha.substring(8,10));
    }


    public static boolean isHoy(String fecha){
        return Calendar.getInstance().get(Calendar.YEAR) == Integer.parseInt(fecha.substring(0,4))
                && Calendar.getInstance().get(Calendar.MONTH)+1 == Integer.parseInt(fecha.substring(5,7))
                && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == Integer.parseInt(fecha.substring(8,10));
    }

    public static String dateToString(Date d){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy/mm/dd");
        return sdf.format(d);

    }


    public static String twoDigits(int n) {
        return (n <= 9) ? ("0" + n) : String.valueOf(n);
    }

    //Agrega los puntos y lo hace bonito
    public static String formatoCantidad(float f){
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        return df.format(f);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static long diferenciaDeDias(Calendar desde, Calendar hasta){
        return ChronoUnit.DAYS.between(desde.getTime().toInstant(), hasta.getTime().toInstant()) +1;
    }

    public static String arrayListToString(ArrayList<String> a){
        StringBuilder concatenada = new StringBuilder();
        for (int x = 0; x != a.size(); x++) {
            concatenada.append(a.get(x));
            if (!(x + 1 == a.size())) {
                concatenada.append(", ");
            }
        }
        return concatenada.toString();
    }




}

