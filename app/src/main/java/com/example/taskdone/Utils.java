package com.example.taskdone;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.time.YearMonth;
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

    public static List<String> diasOrdenadosPorMesAno(int mes, int año){

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d;
        d = new Date(año, mes, 1);
        String dia_elegido =  sdf.format(d);

        if(dia_elegido.equals("lunes")){
             return new ArrayList<>(Arrays.asList("Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo"));
        }
        if(dia_elegido.equals("martes")) {
            return new ArrayList<>(Arrays.asList("Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo", "Lunes"));
        }
        if(dia_elegido.equals("miércoles")){
            return new ArrayList<>(Arrays.asList("Miercoles", "Jueves", "Viernes", "Sabado", "Domingo", "Lunes", "Martes"));
        }
        if(dia_elegido.equals("jueves")){
            return new ArrayList<>(Arrays.asList("Jueves", "Viernes", "Sabado", "Domingo", "Lunes", "Martes", "Miercoles"));
        }
        if(dia_elegido.equals("viernes")){
            return new ArrayList<>(Arrays.asList("Viernes", "Sabado", "Domingo", "Lunes", "Martes", "Miercoles", "Jueves"));
        }
        if(dia_elegido.equals("sábado")){
            return new ArrayList<>(Arrays.asList("Sabado", "Domingo", "Lunes", "Martes", "Miercoles", "Jueves", "Viernes"));
        }
        if(dia_elegido.equals("domingo")){
            return new ArrayList<>(Arrays.asList("Domingo","Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado"));
        }
        return null;
    }

    public static String getDiaHoy(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");
        Calendar cal = new GregorianCalendar();
        dateFormat.setTimeZone(cal.getTimeZone());
        return dateFormat.format(cal.getTime());
    }

    public static String getFechaHoy(){

        final Calendar c = Calendar.getInstance();

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return year + "-" + twoDigits(month + 1) + "-" + twoDigits(day);

    }


    public static String twoDigits(int n) {
        return (n <= 9) ? ("0" + n) : String.valueOf(n);
    }

    public static String formatoCantidad(String c){
        switch (c.length()){
            case 4:
                c = c.substring(0,1) + "." + c.substring(1,4);
                break;
            case 5:
                c = c.substring(0,2) + "." + c.substring(2,5);
                break;
            case 6:
                c = c.substring(0,3) + "." + c.substring(3,6);
                break;
            case 7:
                c = c.substring(0,1) + "." + c.substring(1,4) + "." + c.substring(4,7);
                break;
        }
        return c;
    }


}

