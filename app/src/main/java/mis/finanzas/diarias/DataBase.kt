package mis.finanzas.diarias

import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import mis.finanzas.diarias.viewmodels.UserViewModel

class DataBase(  //private static final String COL_ID_USUARIO = "id_usuario";       //int  definido arriba
    private var userViewModel: UserViewModel,
    var context: Context
) : SQLiteOpenHelper(context, "DATABASE", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTableGastos =
            ("CREATE TABLE " + TABLE_GASTO + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_FECHA + " TEXT, " +
                    COL_TOTAL_GASTO + " REAL, " + COL_MOTIVO + " TEXT, " + COL_INGRESO + " TEXT, "
                    + COL_ID_MONEDA + " INTEGER, " + COL_ID_USUARIO + " INTEGER, "
                    + " FOREIGN KEY (" + COL_ID_MONEDA + ") REFERENCES " + TABLE_MONEDA + "(" + COL_ID + "), "
                    + " FOREIGN KEY (" + COL_ID_USUARIO + ") REFERENCES " + TABLE_USUARIO + "(" + COL_ID + "));")
        db.execSQL(createTableGastos)
        val createTableUser =
            "CREATE TABLE " + TABLE_USUARIO + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_USERNAME + " TEXT, " + COL_PASSWORD + " TEXT);"
        db.execSQL(createTableUser)
        val createTableTag =
            "CREATE TABLE " + TABLE_TAG + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_NOMBRE + " TEXT);"
        db.execSQL(createTableTag)
        val createTableTagUsuario =
            "CREATE TABLE " + TABLE_TAG_USUARIO + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_ID_TAG + " INTEGER, " +
                    COL_ID_USUARIO + " INTEGER, " + " FOREIGN KEY (" + COL_ID_TAG + ") REFERENCES " + TABLE_TAG + "(" + COL_ID + "), " +
                    " FOREIGN KEY (" + COL_ID_USUARIO + ") REFERENCES " + TABLE_USUARIO + "(" + COL_ID + "));"
        db.execSQL(createTableTagUsuario)
        val createTableTagGasto =
            "CREATE TABLE " + TABLE_TAG_GASTO + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_ID_TAG + " INTEGER, " +
                    COL_ID_GASTO + " INTEGER, " + " FOREIGN KEY (" + COL_ID_TAG + ") REFERENCES " + TABLE_TAG + "(" + COL_ID + "), " +
                    " FOREIGN KEY (" + COL_ID_GASTO + ") REFERENCES " + TABLE_GASTO + "(" + COL_ID + "));"
        db.execSQL(createTableTagGasto)
        val createTableMonedaCantidad =
            "CREATE TABLE " + TABLE_MONEDA + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_NOMBRE + " TEXT, " + COL_CANTIDAD + " REAL, " + COL_SIMBOLO + " TEXT, " + COL_ID_USUARIO + " INTEGER, " + "FOREIGN KEY (" + COL_ID_USUARIO + ") REFERENCES " + TABLE_USUARIO + "(" + COL_ID + "));"
        db.execSQL(createTableMonedaCantidad)
    }

    override fun onUpgrade(db: SQLiteDatabase, i: Int, i1: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GASTO)
        onCreate(db)
    }

    fun getGastoById(id: Int): Cursor {
        val db = this.writableDatabase
        val query =
            "SELECT * FROM " + TABLE_GASTO + " WHERE " + TABLE_GASTO + "." + COL_ID + " = " + id
        return db.rawQuery(query, null)
    }

    fun addGastos(
        fecha: String?,
        nombre_moneda: String,
        total_gasto: Float,
        motivo: String?,
        tags: List<String>,
        ingreso: String
    ): Boolean {
        var total_gasto = total_gasto
        val db = this.writableDatabase
        val moneda = getMonedaByNombre(nombre_moneda)
        var id_moneda = 0
        var cantidad = 0f
        while (moneda.moveToNext()) {
            id_moneda = moneda.getInt(0)
            cantidad = moneda.getFloat(2)
        }
        val contentValues = ContentValues()
        contentValues.put(COL_FECHA, fecha)
        contentValues.put(COL_TOTAL_GASTO, total_gasto)
        contentValues.put(COL_MOTIVO, motivo)
        contentValues.put(COL_INGRESO, ingreso)
        contentValues.put(COL_ID_MONEDA, id_moneda)
        contentValues.put(COL_ID_USUARIO, userViewModel.id.value)
        val result = db.insert(TABLE_GASTO, null, contentValues)
        return if (result == -1L) {
            false
        } else {
            if (ingreso == "0") {
                total_gasto = -total_gasto
            }
            cantidad += total_gasto
            updateDineroUser(id_moneda, cantidad)
            for (t in tags) {
                val tag = getTagByNombre(t)
                var id_tag = 0
                while (tag.moveToNext()) {
                    id_tag = tag.getInt(0)
                }
                val b = addTagGasto(id_tag, result.toInt())
                if (!b) {
                    return false
                }
            }
            true
        }
    }

    fun editGasto(
        id: Int,
        fecha: String?,
        nombre_moneda: String,
        total_gasto: Float,
        motivo: String?,
        tags: List<String>,
        ingreso: String
    ): Boolean {
        var total_gasto = total_gasto
        val db = this.writableDatabase
        val moneda = getMonedaByNombre(nombre_moneda)
        var id_moneda = 0
        var cantidad = 0f
        while (moneda.moveToNext()) {
            id_moneda = moneda.getInt(0)
            cantidad = moneda.getFloat(2)
        }
        val gasto_antiguo = getGastoById(id)
        var total_gasto_antiguo = 0f
        var nombre_moneda_antiguo = ""
        var id_moneda_antigua = 0
        var ingreso_antiguo = ""
        while (gasto_antiguo.moveToNext()) {
            total_gasto_antiguo = gasto_antiguo.getFloat(2)
            id_moneda_antigua = gasto_antiguo.getInt(5)
            ingreso_antiguo = gasto_antiguo.getString(4)
        }
        val moneda_antigua = getMonedaById(id_moneda_antigua)
        var cantidad_antigua = 0f
        while (moneda_antigua.moveToNext()) {
            id_moneda_antigua = moneda_antigua.getInt(0)
            nombre_moneda_antiguo = moneda_antigua.getString(1)
            cantidad_antigua = moneda_antigua.getFloat(2)
        }
        val contentValues = ContentValues()
        contentValues.put(COL_FECHA, fecha)
        contentValues.put(COL_TOTAL_GASTO, total_gasto)
        contentValues.put(COL_MOTIVO, motivo)
        contentValues.put(COL_INGRESO, ingreso)
        contentValues.put(COL_ID_MONEDA, id_moneda)
        contentValues.put(COL_ID_USUARIO, userViewModel.id.value)
        val result =
            db.update(TABLE_GASTO, contentValues, "ID = ?", arrayOf(Integer.toString(id))).toLong()
        if (result == -1L) {
            return false
        } else {
            deleteTagsByGastoId(id)
            for (t in tags) {
                val tag = getTagByNombre(t)
                var id_tag = 0
                while (tag.moveToNext()) {
                    id_tag = tag.getInt(0)
                }
                val b = addTagGasto(id_tag, id)
                if (!b) {
                    return false
                }
            }


            //Si no editó la moneda...
            if (nombre_moneda_antiguo == nombre_moneda) {
                total_gasto = if (ingreso == ingreso_antiguo) {
                    Math.abs(total_gasto - total_gasto_antiguo)
                } else {
                    Math.abs(total_gasto + total_gasto_antiguo)
                }
                cantidad = if (ingreso == "0") {
                    cantidad - total_gasto
                } else {
                    cantidad + total_gasto
                }
                updateDineroUser(id_moneda_antigua, cantidad)
            } else {
                if (ingreso == "0") {
                    updateDineroUser(id_moneda_antigua, cantidad - total_gasto)
                } else {
                    updateDineroUser(id_moneda_antigua, cantidad + total_gasto)
                }

                //Si era un gasto, al desaparecer se suma
                if (ingreso_antiguo == "0") {
                    updateDineroUser(id_moneda_antigua, cantidad_antigua + total_gasto_antiguo)
                } else {
                    updateDineroUser(id_moneda_antigua, cantidad_antigua - total_gasto)
                }
            }
        }
        return true
    }

    fun deleteTagsByGastoId(id: Int): Boolean {
        val db = this.writableDatabase
        val result =
            db.delete(TABLE_TAG_GASTO, COL_ID_GASTO + " = ?", arrayOf(Integer.toString(id)))
                .toLong()
        return result > 0
    }

    fun deleteTagsByNombre(nombre: String): Boolean {
        val db = this.writableDatabase
        val tag = getTagByNombre(nombre)
        var id_tag = 0
        while (tag.moveToNext()) {
            id_tag = tag.getInt(0)
        }
        db.delete(TABLE_TAG, COL_ID + " = ?", arrayOf(Integer.toString(id_tag)))
        db.delete(
            TABLE_TAG_GASTO,
            COL_ID_TAG + " = ?",
            arrayOf(java.lang.Long.toString(id_tag.toLong()))
        )
        return id_tag != 0
    }

    fun deleteMonedaByNombre(nombre: String): Boolean {
        val db = this.writableDatabase

        //Obtener gastos, y tag_gasto
        val gastos_a_borrar = getGastosByMonedaNombre(nombre)
        while (gastos_a_borrar.moveToNext()) {
            deleteGastoById(
                gastos_a_borrar.getInt(0),
                gastos_a_borrar.getFloat(2),
                gastos_a_borrar.getString(4),
                nombre
            )
        }
        val moneda_a_borrar = getMonedaByNombre(nombre)
        var result: Long = -1
        while (moneda_a_borrar.moveToNext()) {
            result = db.delete(
                TABLE_MONEDA,
                COL_ID + " = ?",
                arrayOf(Integer.toString(moneda_a_borrar.getInt(0)))
            ).toLong()
        }
        return result != 0L
    }

    fun deleteGastoById(id: Int, total: Float, ingreso: String, nombre_moneda: String): Boolean {
        val moneda = getMonedaByNombre(nombre_moneda)
        var id_moneda = 0
        var cantidad = 0f
        while (moneda.moveToNext()) {
            id_moneda = moneda.getInt(0)
            cantidad = moneda.getFloat(2)
        }

        //Si se elimina un gasto, se recupera el dinero
        if (ingreso == "0") {
            cantidad += total
        } else {
            cantidad -= total
        }
        updateDineroUser(id_moneda, cantidad)
        deleteTagsByGastoId(id)
        val db = this.writableDatabase
        return db.delete(TABLE_GASTO, COL_ID + "=" + id, null) > 0
    }

    fun getGastosByMonedaNombre(nombre: String): Cursor {
        val db = this.writableDatabase
        val query =
            ("SELECT g." + COL_ID + ", g." + COL_FECHA + ", g." + COL_TOTAL_GASTO + ", g." + COL_MOTIVO + ", g." + COL_INGRESO
                    + ", mc." + COL_NOMBRE + ", mc." + COL_SIMBOLO
                    + " FROM " + TABLE_GASTO + " AS g INNER JOIN " + TABLE_MONEDA + " AS mc "
                    + "ON g." + COL_ID_MONEDA + " = mc." + COL_ID + " AND mc." + COL_NOMBRE + " = '" + nombre + "'"
                    + " INNER JOIN " + TABLE_USUARIO + " AS u "
                    + "ON u." + COL_ID + " = g." + COL_ID_USUARIO + " AND u." + COL_ID + " = '" + userViewModel.id.value + "'")
        return db.rawQuery(query, null)
    }

    // Devuelve -> fecha, cantidad, motivo, ingreso, nombreMoneda, simboloMoneda
    fun getGastosBySessionUser(desde: String, hasta: String): Cursor {
        val db = this.writableDatabase
        val query =
            ("SELECT g. " + COL_ID + ", g." + COL_FECHA + ", g." + COL_TOTAL_GASTO + ", g." + COL_MOTIVO + ", g." + COL_INGRESO
                    + ", mc." + COL_NOMBRE + ", mc." + COL_SIMBOLO
                    + " FROM " + TABLE_GASTO + " AS g INNER JOIN " + TABLE_MONEDA + " AS mc "
                    + "ON g." + COL_ID_MONEDA + " = mc." + COL_ID + " AND g." + COL_FECHA + " BETWEEN '" + desde + "' AND '" + hasta + "'"
                    + " ORDER BY g." + COL_FECHA + " DESC")
        return db.rawQuery(query, null)
    }

    fun getTotalGastosMensualesPorYearMoneda(
        year: String,
        ingreso: String,
        nombre_moneda: String
    ): Cursor {
        val db = this.writableDatabase
        val query = ("SELECT SUM(g." + COL_TOTAL_GASTO + "), g." + COL_FECHA
                + " FROM " + TABLE_GASTO + " AS g INNER JOIN " + TABLE_USUARIO + " AS u"
                + " ON u." + COL_ID + " = g." + COL_ID_USUARIO + " AND u." + COL_ID + " = '" + userViewModel.id.value + "' AND g." + COL_INGRESO + " = '" + ingreso + "'"
                + " AND substr(g." + COL_FECHA + ", 0, 5) = '" + year + "'"
                + " INNER JOIN " + TABLE_MONEDA + " AS mc"
                + " ON g." + COL_ID_MONEDA + " = mc." + COL_ID + " AND mc." + COL_NOMBRE + "= '" + nombre_moneda + "'"
                + " GROUP BY substr(g." + COL_FECHA + ", 6, 2)")
        return db.rawQuery(query, null)
    }

    fun getTotalGastosPorYearMesMoneda(
        year: String,
        mes: String,
        nombre_moneda: String,
        ingreso: String
    ): Cursor {
        val db = this.writableDatabase
        val query = ("SELECT SUM(g." + COL_TOTAL_GASTO + "), g." + COL_FECHA
                + " FROM " + TABLE_GASTO + " AS g INNER JOIN " + TABLE_USUARIO + " AS u"
                + " ON u." + COL_ID + " = g." + COL_ID_USUARIO + " AND u." + COL_ID + " = '" + userViewModel.id.value+ "' AND g." + COL_INGRESO + " = '" + ingreso + "'"
                + " AND substr(g." + COL_FECHA + ", 0, 5) = '" + year + "' AND substr(g." + COL_FECHA + ", 7, 1) = '" + mes + "'"
                + " INNER JOIN " + TABLE_MONEDA + " AS mc"
                + " ON g." + COL_ID_MONEDA + " = mc." + COL_ID + " AND mc." + COL_NOMBRE + "= '" + nombre_moneda + "'")
        return db.rawQuery(query, null)
    }

    fun getTotalGastosPorYearMesMonedaGroupByTags(
        year: String,
        mes: String,
        nombre_moneda: String,
        ingreso: String
    ): Cursor {
        val db = this.writableDatabase
        val query = ("SELECT SUM(g." + COL_TOTAL_GASTO + "), t." + COL_NOMBRE
                + " FROM " + TABLE_GASTO + " AS g INNER JOIN " + TABLE_USUARIO + " AS u"
                + " ON u." + COL_ID + " = g." + COL_ID_USUARIO + " AND u." + COL_ID + " = '" + userViewModel.id.value+ "' AND g." + COL_INGRESO + " = '" + ingreso + "'"
                + " AND substr(g." + COL_FECHA + ", 0, 5) = '" + year + "' AND substr(g." + COL_FECHA + ", 7, 1) = '" + mes + "'"
                + " INNER JOIN " + TABLE_MONEDA + " AS mc"
                + " ON g." + COL_ID_MONEDA + " = mc." + COL_ID + " AND mc." + COL_NOMBRE + "= '" + nombre_moneda + "'"
                + " INNER JOIN " + TABLE_TAG_GASTO + " AS tg "
                + "ON g." + COL_ID + " = tg." + COL_ID_GASTO
                + " INNER JOIN " + TABLE_TAG + " AS t "
                + "ON t." + COL_ID + " = tg." + COL_ID_TAG
                + " GROUP BY T." + COL_ID)
        return db.rawQuery(query, null)
    }

    //Devuelve cantidadGastos, total, nombreMoneda
    fun getTotalGastosBetweenFechasGroupByMonedas(
        desde: String,
        hasta: String,
        ingreso: String
    ): Cursor {
        val db = this.writableDatabase
        val query =
            ("SELECT COUNT(g." + COL_TOTAL_GASTO + "), SUM(g." + COL_TOTAL_GASTO + "), mc." + COL_NOMBRE + ", mc." + COL_SIMBOLO
                    + " FROM " + TABLE_GASTO + " AS g INNER JOIN " + TABLE_MONEDA + " AS mc "
                    + "ON g." + COL_ID_MONEDA + " = mc." + COL_ID + " AND g." + COL_INGRESO + " = '" + ingreso + "' AND g." + COL_FECHA + " BETWEEN '" + desde + "' AND '" + hasta + "'"
                    + " INNER JOIN " + TABLE_USUARIO + " AS u "
                    + "ON u." + COL_ID + " = g." + COL_ID_USUARIO + " AND u." + COL_ID + " = '" + userViewModel.id.value+ "'"
                    + " GROUP BY mc." + COL_NOMBRE)
        return db.rawQuery(query, null)
    }

    fun getTotalGastosGroupByTagIngresoMoneda(desde: String, hasta: String): Cursor {
        val db = this.writableDatabase
        val query =
            ("SELECT COUNT(g." + COL_TOTAL_GASTO + "), SUM(g." + COL_TOTAL_GASTO + "), mc." + COL_NOMBRE + ", mc." + COL_SIMBOLO + ", t." + COL_NOMBRE + ", g." + COL_INGRESO
                    + " FROM " + TABLE_GASTO + " AS g INNER JOIN " + TABLE_MONEDA + " AS mc "
                    + "ON g." + COL_ID_MONEDA + " = mc." + COL_ID + " AND g." + COL_FECHA + " BETWEEN '" + desde + "' AND '" + hasta + "'"
                    + " INNER JOIN " + TABLE_TAG_GASTO + " AS tg "
                    + "ON g." + COL_ID + " = tg." + COL_ID_GASTO
                    + " INNER JOIN " + TABLE_USUARIO + " AS u "
                    + "ON u." + COL_ID + " = g." + COL_ID_USUARIO + " AND u." + COL_ID + " = '" + userViewModel.id.value+ "'"
                    + " INNER JOIN " + TABLE_TAG + " AS t "
                    + "ON t." + COL_ID + " = tg." + COL_ID_TAG
                    + " GROUP BY g." + COL_INGRESO + ", t." + COL_NOMBRE + ", mc." + COL_ID)
        return db.rawQuery(query, null)
    }

    fun getMonedaByNombre(nombre_moneda: String): Cursor {
        val db = this.writableDatabase
        val query =
            ("SELECT * FROM " + TABLE_MONEDA + " WHERE " + COL_NOMBRE + "='" + nombre_moneda + "'"
                    + " AND " + COL_ID_USUARIO + "='" + userViewModel.id.value + "'")
        return db.rawQuery(query, null)
    }

    fun getMonedaById(id: Int): Cursor {
        val db = this.writableDatabase
        val query =
            "SELECT * FROM " + TABLE_MONEDA + " WHERE " + TABLE_MONEDA + "." + COL_ID + " = " + id
        return db.rawQuery(query, null)
    }

    fun addUser(username: String?, password: String?): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_USERNAME, username)
        contentValues.put(COL_PASSWORD, password)
        val result = db.insert(TABLE_USUARIO, null, contentValues)
        return result != -1L
    }

    fun updateDineroUser(id_moneda: Int, cantidad: Float) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_CANTIDAD, cantidad)
        db.update(TABLE_MONEDA, contentValues, "ID = ?", arrayOf(Integer.toString(id_moneda)))
    }

    /*

    public Cursor getUserById(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_USUARIO + " WHERE ID='" + id +"'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }*/
    val user: Cursor
        get() {
            val db = this.writableDatabase
            val query = "SELECT * FROM " + TABLE_USUARIO
            return db.rawQuery(query, null)
        }

    fun getUserByUsername(username: String): Cursor {
        val db = this.writableDatabase
        val query = "SELECT * FROM " + TABLE_USUARIO + " WHERE usuario='" + username + "'"
        return db.rawQuery(query, null)
    }

    fun getUserByUsernameAndPassword(username: String, password: String): Cursor {
        val db = this.writableDatabase
        val query =
            "SELECT * FROM " + TABLE_USUARIO + " WHERE usuario='" + username + "' AND contraseña='" + password + "'"
        return db.rawQuery(query, null)
    }

    fun addTag(tag: String?): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_NOMBRE, tag)
        val result = db.insert(TABLE_TAG, null, contentValues)
        return if (result == -1L) {
            false
        } else addTagUsuario(result.toInt(), userViewModel.getId())
    }

    fun updateTag(tag_viejo: String, tag_nuevo: String?): Boolean {
        val db = this.writableDatabase
        val tag = getTagByNombre(tag_viejo)
        var id_tag = 0
        while (tag.moveToNext()) {
            id_tag = tag.getInt(0)
        }
        val contentValues = ContentValues()
        contentValues.put(COL_NOMBRE, tag_nuevo)
        val result =
            db.update(TABLE_TAG, contentValues, COL_ID + " = ?", arrayOf(Integer.toString(id_tag)))
                .toLong()
        return result != -1L
    }

    fun updateMoneda(nombre_viejo: String, nombre: String?, simbolo: String?): Boolean {
        val db = this.writableDatabase
        val moneda = getMonedaByNombre(nombre_viejo)
        var id_moneda = 0
        while (moneda.moveToNext()) {
            id_moneda = moneda.getInt(0)
        }
        val contentValues = ContentValues()
        contentValues.put(COL_NOMBRE, nombre)
        contentValues.put(COL_SIMBOLO, simbolo)
        val result = db.update(
            TABLE_MONEDA,
            contentValues,
            COL_ID + " = ?",
            arrayOf(Integer.toString(id_moneda))
        ).toLong()
        return result != -1L
    }

    /* public Cursor getAllTags(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_TAG ;
        return db.rawQuery(query, null);
    }*/
    fun getTagsByUserId(id: Int): Cursor {
        val db = this.writableDatabase
        val query =
            ("SELECT " + TABLE_TAG + ".* FROM " + TABLE_TAG + ", " + TABLE_TAG_USUARIO + " WHERE " + TABLE_TAG_USUARIO + "." + COL_ID_USUARIO + "='" + id + "' AND "
                    + TABLE_TAG + ".ID=" + TABLE_TAG_USUARIO + "." + COL_ID_TAG)
        return db.rawQuery(query, null)
    }

    fun getTagsByGastoId(id: Int): Cursor {
        val db = this.writableDatabase
        val query =
            ("SELECT " + TABLE_TAG + ".* FROM " + TABLE_TAG + ", " + TABLE_TAG_GASTO + " WHERE " + TABLE_TAG_GASTO + "." + COL_ID_GASTO + "='" + id + "' AND "
                    + TABLE_TAG + ".ID=" + TABLE_TAG_GASTO + "." + COL_ID_TAG)
        return db.rawQuery(query, null)
    }

    fun getTagByNombre(nombre: String): Cursor {
        val db = this.writableDatabase
        val query =
            ("SELECT t.* FROM " + TABLE_TAG + " AS t INNER JOIN " + TABLE_TAG_USUARIO + " AS tu "
                    + "ON t." + COL_NOMBRE + " = '" + nombre + "'"
                    + " AND t." + COL_ID + " = tu." + COL_ID_TAG
                    + " AND tu." + COL_ID_USUARIO + " = '" + userViewModel.id.value + "'")
        return db.rawQuery(query, null)
    }

    private fun addTagUsuario(tag: Int, idUsuario: Int): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_ID_TAG, tag)
        contentValues.put(
            COL_ID_USUARIO,
            idUsuario
        ) //Por ahora solo el 0, ya que solo permito una única cuenta
        val result = db.insert(TABLE_TAG_USUARIO, null, contentValues)
        return result != 1L
    }

    private fun addTagGasto(tag: Int, id_gasto: Int): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_ID_TAG, tag)
        contentValues.put(
            COL_ID_GASTO,
            id_gasto
        ) //Por ahora solo el 0, ya que solo permito una única cuenta
        val result = db.insert(TABLE_TAG_GASTO, null, contentValues)
        return result != -1L
    }

    fun addMonedaCantidad(moneda: String?, cantidad: Float, simbolo: String?): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_NOMBRE, moneda)
        contentValues.put(COL_CANTIDAD, cantidad)
        contentValues.put(COL_SIMBOLO, simbolo)
        contentValues.put(COL_ID_USUARIO, userViewModel.id.value)
        val result = db.insert(TABLE_MONEDA, null, contentValues)
        return result != -1L
    }

    /*
    public Cursor getAllMonedas(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_MONEDA;
        Cursor data = db.rawQuery(query, null);
        return data;

    }*/
    fun getMonedasByUserId(id_usuario: Int): Cursor {
        val db = this.writableDatabase
        val query =
            "SELECT * FROM " + TABLE_MONEDA + " WHERE " + TABLE_MONEDA + "." + COL_ID_USUARIO + " = " + id_usuario
        return db.rawQuery(query, null)
    }

    companion object {
        private const val TABLE_GASTO = "Gasto"
        private const val COL_ID = "ID"
        private const val COL_FECHA = "fecha" // string
        private const val COL_TOTAL_GASTO = "total_gasto" // int
        private const val COL_MOTIVO = "motivo" // string
        private const val COL_INGRESO = "ingreso" // string
        private const val COL_ID_MONEDA = "id_moneda" // int
        private const val COL_ID_USUARIO = "id_usuario" // int
        private const val TABLE_USUARIO = "Usuario"
        private const val COL_USERNAME = "usuario"
        private const val COL_PASSWORD = "contraseña"
        private const val TABLE_TAG = "Tag"
        private const val COL_NOMBRE = "nombre" //string
        private const val TABLE_TAG_USUARIO = "Tag_Usuario"
        private const val COL_ID_TAG = "id_tag" //int

        //private static final String COL_ID_USUARIO = "id_usuario";      //int  definido arriba
        private const val TABLE_TAG_GASTO = "Tag_Gasto"

        //private static final String COL_ID_TAG = "id_tag";              //int  definido arriba
        private const val COL_ID_GASTO = "id_gasto" //int
        private const val TABLE_MONEDA = "Moneda"

        //private static final String COL_ID_MONEDA_CANTIDAD               //int  definido arriba
        //private static final String COL_NOMBRE = "nombre";               // definido arriba
        private const val COL_CANTIDAD = "cantidad" // int
        private const val COL_SIMBOLO = "simbolo" // string
    }
}