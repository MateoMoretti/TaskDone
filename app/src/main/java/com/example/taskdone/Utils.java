package com.example.taskdone;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class Utils {


    //BISIESTOS 2020-2024-2028

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int diasMes(int año, int mes){
        YearMonth yearMonthObject = YearMonth.of(año, mes);
        return yearMonthObject.lengthOfMonth();
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

    public static String dateToString(Date d){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/mm/dd");
        String a = sdf.format(d);
        return sdf.format(d);

    }


    public static String twoDigits(int n) {
        return (n <= 9) ? ("0" + n) : String.valueOf(n);
    }

    public static String formatoCantidad(String c){
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        if(c.contains("-")) {
            c = c.replaceAll("-", "");
        }
        return df.format(Float.parseFloat(c));
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

