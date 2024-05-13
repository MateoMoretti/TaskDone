package my.life.tracker

import android.annotation.SuppressLint
import kotlin.Throws
import android.app.Activity
import android.app.Dialog
import android.content.Context
import my.life.tracker.R
import android.widget.TextView
import android.content.DialogInterface
import android.os.Build
import android.view.Gravity
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import java.lang.Exception
import java.lang.StringBuilder
import java.text.*
import java.time.temporal.ChronoUnit
import java.util.*

object Utils {

    fun getDia(fecha: String, context: Context?): String {
        val idioma = Preferences.getPreferenceString(context, "idioma")
        val locale = Locale(idioma)
        val df: DateFormat
        df = if (idioma == "") {
            Locale.setDefault(Locale.getDefault())
            DateFormat.getDateInstance(
                DateFormat.FULL,
                context!!.resources.configuration.locale
            )
        } else {
            DateFormat.getDateInstance(DateFormat.FULL, locale)
        }
        val c = Calendar.getInstance()
        c[fecha.substring(0, 4).toInt(), fecha.substring(5, 7).toInt() - 1] =
            fecha.substring(8, 10).toInt()
        val dia = df.format(c.time)
        return dia.substring(0, 1).uppercase(Locale.getDefault()) + dia.substring(1)
    }

    fun getMesPorNumero(context: Context?, num: Int): String {
        var month = "wrong"
        val idioma = Preferences.getPreferenceString(context, "idioma")
        val locale = Locale(idioma)
        val dfs = DateFormatSymbols(locale)
        val months = dfs.months
        if (num >= 0 && num <= 11) {
            month = months[num]
        }
        return month
    }

    fun dateToCalendar(date: Date?): Calendar {
        val cal = Calendar.getInstance()
        cal.time = date
        return cal
    }

    fun calendarToString(c: Calendar): String {
        val year = c[Calendar.YEAR]
        val month = c[Calendar.MONTH]
        val day = c[Calendar.DAY_OF_MONTH]
        return year.toString() + "/" + twoDigits(month + 1) + "/" + twoDigits(day)
    }

    @Throws(ParseException::class)
    fun fechaMayorQueHoy(f: String?): Boolean {
        @SuppressLint("SimpleDateFormat") val sdf = SimpleDateFormat("yyyy/MM/dd")
        val fecha = sdf.parse(f)!!
        return fecha.after(Calendar.getInstance().time)
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

    private fun equalsStringCalendar(fecha: String, calendar: Calendar): Boolean {
        return calendar[Calendar.YEAR] == fecha.substring(0, 4).toInt()
                && calendar[Calendar.MONTH] + 1 == fecha.substring(5, 7).toInt()
                && calendar[Calendar.DAY_OF_MONTH] == fecha.substring(8, 10).toInt()
    }

    fun isMonthAgo(fecha: String): Boolean {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -30)
        return equalsStringCalendar(fecha, calendar)
    }
    fun isHoy(date: String): Boolean = equalsStringCalendar(date, Calendar.getInstance())

    fun isHoy(date: Date): Boolean = isHoy(dateToString(date))

    fun dateToString(d: Date?): String {
        @SuppressLint("SimpleDateFormat") val sdf = SimpleDateFormat("yyyy/MM/dd")
        return sdf.format(d)
    }

    fun stringToDate(s: String?): Date {
        s?.let {
            try {
                @SuppressLint("SimpleDateFormat") val sdf = SimpleDateFormat("yyyy/MM/dd")
                return sdf.parse(s)
            }catch (exception:Exception){
                return Calendar.getInstance().time
            }
        }
        return Calendar.getInstance().time

    }

    fun twoDigits(n: Int): String {
        return if (n <= 9) "0$n" else n.toString()
    }

    //Agrega los puntos y lo hace bonito
    fun formatAmount(n: Number): String {
        val df = DecimalFormat()
        df.maximumFractionDigits = 2
        return df.format(n.toDouble())
    }

    @Throws(ParseException::class)
    fun stringToFloat(s: String?): Number {
        val df = DecimalFormat()
        df.maximumFractionDigits = 0
        return df.parse(s)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun diferenciaDeDias(from: Date, to: Date): Long {
        return ChronoUnit.DAYS.between(from.toInstant(), to.toInstant()) + 1
    }

    fun arrayListToString(a: List<String>): String {
        val concatenada = StringBuilder()
        for (x in a.indices) {
            concatenada.append(a[x])
            if (x + 1 != a.size) {
                concatenada.append(", ")
            }
        }
        return concatenada.toString()
    }

    @SuppressLint("RtlHardcoded")
    fun popupAyuda(c: Context, activity: Activity, strings: ArrayList<String?>) {
        val inflater = activity.layoutInflater
        @SuppressLint("InflateParams") val vista =
            inflater.inflate(R.layout.popup_informacion, null)
        val texto_1 = vista.findViewById<TextView>(R.id.texto_1)
        val texto_2 = vista.findViewById<TextView>(R.id.texto_2)
        val texto_3 = vista.findViewById<TextView>(R.id.texto_3)
        val texto_4 = vista.findViewById<TextView>(R.id.texto_4)
        val texto_5 = vista.findViewById<TextView>(R.id.texto_5)
        val textos = ArrayList(Arrays.asList(texto_1, texto_2, texto_3, texto_4, texto_5))
        for (x in strings.indices) {
            textos[x].text = strings[x]
        }
        val builder = AlertDialog.Builder(c)
        builder.setTitle(c.resources.getString(R.string.informacion))
        builder.setView(vista)
            .setPositiveButton(
                c.resources.getString(R.string.aceptar)
            ) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
        builder.setCancelable(true)
        val dialog: Dialog = builder.create()
        val window = dialog.window
        window?.setGravity(Gravity.CENTER or Gravity.RIGHT)
        dialog.show()
        dialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }

}