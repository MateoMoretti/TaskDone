package mis.finanzas.diarias;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import com.example.taskdone.R;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utils {

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public static void setContext(Context context) {
        Utils.context = context;
    }

    public static String getDia(String fecha){
        String idioma = Preferences.getPreferenceString(context, "idioma");
        Locale locale = new Locale(idioma);
        DateFormat df;
        if(idioma.equals("")){
            Locale.setDefault(Locale.getDefault());
            df = DateFormat.getDateInstance(DateFormat.FULL, context.getResources().getConfiguration().locale);
        }
        else {
            df = DateFormat.getDateInstance(DateFormat.FULL, locale);
        }
        Calendar c = Calendar.getInstance();
        c.set(Integer.parseInt(fecha.substring(0,4)), Integer.parseInt(fecha.substring(5,7))-1, Integer.parseInt(fecha.substring(8,10)));
        String dia = df.format(c.getTime());
        return dia.substring(0, 1).toUpperCase() + dia.substring(1);
    }

    public static String getMesPorNumero(int num) {
        String month = "wrong";
        String idioma = Preferences.getPreferenceString(context, "idioma");
        Locale locale = new Locale(idioma);
        DateFormatSymbols dfs = new DateFormatSymbols(locale);
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11 ) {
            month = months[num];
        }
        return month;
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

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date fecha = sdf.parse(f);
        assert fecha != null;
        return fecha.after(Calendar.getInstance().getTime());
    }

    /*public static Calendar getPrimerDiaDelMes() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        return c;
    }

    public static Calendar getPrimerDiaDelYear() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_YEAR, 1);
        return c;
    }

    public static boolean isPrimerDiaDelMes(String fecha){
        return getPrimerDiaDelMes().get(Calendar.YEAR) == Integer.parseInt(fecha.substring(0,4))
                && getPrimerDiaDelMes().get(Calendar.MONTH)+1 == Integer.parseInt(fecha.substring(5,7))
                && getPrimerDiaDelMes().get(Calendar.DAY_OF_MONTH) == Integer.parseInt(fecha.substring(8,10));
    }*/


    public static boolean isHoy(String fecha){
        return Calendar.getInstance().get(Calendar.YEAR) == Integer.parseInt(fecha.substring(0,4))
                && Calendar.getInstance().get(Calendar.MONTH)+1 == Integer.parseInt(fecha.substring(5,7))
                && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == Integer.parseInt(fecha.substring(8,10));
    }

    public static String dateToString(Date d){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
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

    public static Number stringToFloat(String s) throws ParseException {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(0);
        return df.parse(s);
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


    @SuppressLint("RtlHardcoded")
    public static void popupAyuda(Context c, Activity activity, ArrayList<String> strings){
        LayoutInflater inflater = activity.getLayoutInflater();
        @SuppressLint("InflateParams") View vista = inflater.inflate(R.layout.popup_informacion, null);


        TextView texto_1 = vista.findViewById(R.id.texto_1);
        TextView texto_2 = vista.findViewById(R.id.texto_2);
        TextView texto_3 = vista.findViewById(R.id.texto_3);
        TextView texto_4 = vista.findViewById(R.id.texto_4);
        TextView texto_5 = vista.findViewById(R.id.texto_5);

        ArrayList<TextView> textos = new ArrayList<>(Arrays.asList(texto_1, texto_2, texto_3, texto_4, texto_5));
        for(int x =0; x!=strings.size();x++){
            textos.get(x).setText(strings.get(x));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(c.getResources().getString(R.string.informacion));
        builder.setView(vista)
                .setPositiveButton(c.getResources().getString(R.string.aceptar), (dialog, which) ->
                        dialog.dismiss()
                );
        builder.setCancelable(true);


        Dialog dialog = builder.create();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER | Gravity.RIGHT);
        }

        dialog.show();
        dialog.getWindow().clearFlags( WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }



}

