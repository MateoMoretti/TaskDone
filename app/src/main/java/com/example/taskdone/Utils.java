package com.example.taskdone;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public class Utils {

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
        Calendar c = Calendar.getInstance();
        c.set(Integer.parseInt(fecha.substring(0,4)), Integer.parseInt(fecha.substring(5,7))-1, Integer.parseInt(fecha.substring(8,10)));
        return sdf.format(c.getTime());
    }

    public static String getDiaFormateado(String fecha){
        return getDia(fecha).toUpperCase() + " " + fecha.substring(8,10) + ", " + getMes(Integer.parseInt(fecha.substring(5,7))) + " " + fecha.substring(0,4);

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




}

