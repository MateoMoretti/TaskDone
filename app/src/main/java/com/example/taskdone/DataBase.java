package com.example.taskdone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

public class DataBase extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String TABLE_GASTO = "Gasto";
    private static final String COL_ID = "ID";
    private static final String COL_FECHA = "fecha";                // string
    private static final String COL_TOTAL_GASTO = "total_gasto";          // int
    private static final String COL_MOTIVO = "motivo";              // string
    private static final String COL_INGRESO = "ingreso";            // string
    private static final String COL_ID_MONEDA_CANTIDAD = "id_moneda_cantidad";    // int
    private static final String COL_ID_USUARIO = "id_usuario";      // int

    private static final String TABLE_USUARIO = "Usuario";
    private static final String COL_USERNAME = "usuario";
    private static final String COL_PASSWORD = "contraseña";

    private static final String TABLE_TAG = "Tag";
    private static final String COL_NOMBRE_TAG = "nombre_tag";            //string


    private static final String TABLE_TAG_USUARIO = "Tag_Usuario";
    private static final String COL_ID_TAG = "id_tag";                //int
    //private static final String COL_ID_USUARIO = "id_usuario";      //int  definido arriba

    private static final String TABLE_TAG_GASTO = "Tag_Gasto";
    //private static final String COL_ID_TAG = "id_tag";              //int  definido arriba
    private static final String COL_ID_GASTO = "id_gasto";            //int


    private static final String TABLE_MONEDA_CANTIDAD = "MonedaCantidad";
    //private static final String COL_ID_MONEDA_CANTIDAD = "id_moneda_cantidad";      //int  definido arriba
    private static final String COL_MONEDA = "moneda";    // string
    private static final String COL_CANTIDAD = "cantidad";    // int
    private static final String COL_SIMBOLO = "simbolo";    // string
    //private static final String COL_ID_USUARIO = "id_usuario";      //int  definido arriba


    Context context;

    public DataBase(Context context) {
        super(context, "DATABASE", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableGastos = "CREATE TABLE " + TABLE_GASTO + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_FECHA + " TEXT, " +
                COL_TOTAL_GASTO + " REAL, " + COL_MOTIVO + " TEXT, " + COL_INGRESO + " TEXT, "
                + COL_ID_USUARIO + " INTEGER,  " + " FOREIGN KEY ("+COL_ID_USUARIO+") REFERENCES "+TABLE_USUARIO+"("+COL_ID+"), "
                + " FOREIGN KEY ("+COL_ID_USUARIO+") REFERENCES "+TABLE_USUARIO+"("+COL_ID+"));";;
        db.execSQL(createTableGastos);


        String createTableUser = "CREATE TABLE " + TABLE_USUARIO + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_USERNAME + " TEXT, "+ COL_PASSWORD + " TEXT);";
        db.execSQL(createTableUser);


        String createTableTag = "CREATE TABLE " + TABLE_TAG + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_NOMBRE_TAG + " TEXT);";
        db.execSQL(createTableTag);


        String createTableTagUsuario = "CREATE TABLE " + TABLE_TAG_USUARIO + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_ID_TAG + " INTEGER, " +
                COL_ID_USUARIO + " INTEGER, "  + " FOREIGN KEY ("+COL_ID_TAG+") REFERENCES "+TABLE_TAG+"("+COL_ID+"), " +
                " FOREIGN KEY ("+COL_ID_USUARIO+") REFERENCES "+TABLE_USUARIO+"("+COL_ID+"));";
        db.execSQL(createTableTagUsuario);

        String createTableTagGasto = "CREATE TABLE " + TABLE_TAG_GASTO + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_ID_TAG + " INTEGER, " +
                COL_ID_GASTO + " INTEGER, "  + " FOREIGN KEY ("+COL_ID_TAG+") REFERENCES "+TABLE_TAG+"("+COL_ID+"), " +
                " FOREIGN KEY ("+COL_ID_GASTO+") REFERENCES "+ TABLE_GASTO +"("+COL_ID+"));";
        db.execSQL(createTableTagGasto);


        String createTableMonedaCantidad = "CREATE TABLE " + TABLE_MONEDA_CANTIDAD + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_ID_MONEDA_CANTIDAD + " INTEGER, " +
                COL_MONEDA + " TEXT, "  + COL_CANTIDAD + " REAL, " + COL_SIMBOLO + " TEXT, " +" FOREIGN KEY ("+COL_ID_USUARIO+") REFERENCES "+TABLE_USUARIO+"("+COL_ID+"));";
        db.execSQL(createTableMonedaCantidad);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GASTO);
        onCreate(db);
    }

    public boolean addGastos(String fecha, String tipo_moneda, String cantidad, String motivo, List<String> tags, String ingreso, int idUsuario){
        SQLiteDatabase db = this.getWritableDatabase();

        DataBase dataBase = new DataBase(context);
        Cursor moneda = dataBase.getMonedasByUserId(UsuarioSingleton.getInstance().getID());
        int id_moneda = 0;
        float cantidad_moneda = 0;
        while (moneda.moveToNext()){
            id_moneda = moneda.getInt(0);
            cantidad_moneda = moneda.getInt(1);
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_FECHA, fecha);
        contentValues.put(COL_TOTAL_GASTO, cantidad);
        contentValues.put(COL_MOTIVO, motivo);
        contentValues.put(COL_INGRESO, ingreso);
        contentValues.put(COL_ID_MONEDA_CANTIDAD, id_moneda);
        contentValues.put(COL_ID_USUARIO, idUsuario);

        long result = db.insert(TABLE_GASTO, null, contentValues);

        if(result == -1){
            return false;
        }
        else{
            Cursor data = getUserById(idUsuario);
            int cant = Integer.parseInt(cantidad);
            if(ingreso.equals("0")){
                cant = -cant;
            }

            cantidad_moneda += cant;

            updateDineroUser(id_moneda, cantidad_moneda);



            for(String t:tags){
                Cursor tag = getTagByNombre(t);
                int id_tag = 0;
                while (tag.moveToNext()) {
                    id_tag = tag.getInt(0);
                }
                addTagGasto(id_tag, (int)result);
            }

            return true;
        }
    }

    public Cursor getAllGastos(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_GASTO + " ORDER BY " + COL_FECHA + " DESC";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getGastosByUserId(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_GASTO+ " WHERE " + COL_ID_USUARIO +"='"+id+"'" + " ORDER BY " + COL_FECHA + " DESC";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getMonedaIdByNombre(String nombre_moneda){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_MONEDA_CANTIDAD+ " WHERE " + COL_MONEDA +"='"+nombre_moneda+"'"
                + " AND " + COL_ID_USUARIO +"='"+ UsuarioSingleton.getInstance().getID();
        Cursor data = db.rawQuery(query, null);
        return data;
    }



    public boolean addUser(String username, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_USERNAME, username);
        contentValues.put(COL_PASSWORD, password);

        long result = db.insert(TABLE_USUARIO, null, contentValues);

        if(result == -1){
            return false;
        }
        return true;
    }


    //HACER
    public boolean updateDineroUser(int moneda, float cantidad){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_CANTIDAD, cantidad);


        db.update(TABLE_MONEDA_CANTIDAD, contentValues, "ID = ?", new String[]{Integer.toString(moneda)});
        return true;
    }

    public Cursor getAllUsers(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_USUARIO;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getUserById(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_USUARIO + " WHERE ID='" + id +"'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getUserByUsername(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_USUARIO + " WHERE usuario='" + username +"'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getUserByUsernameAndPassword(String username, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_USUARIO + " WHERE usuario='" + username + "' AND contraseña='" + password +"'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }







    public boolean addTag(String tag){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NOMBRE_TAG, tag);

        long result = db.insert(TABLE_TAG, null, contentValues);

        if(result == -1){
            return false;
        }
        addTagUsuario((int)result, UsuarioSingleton.getInstance().getID()); //Por ahora solo el 0, ya que solo permito una única cuenta
        return true;
    }

    public Cursor getAllTags(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_TAG ;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getTagsByUserId(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + TABLE_TAG + ".* FROM " + TABLE_TAG + ", " + TABLE_TAG_USUARIO + " WHERE " + TABLE_TAG_USUARIO + "." +COL_ID_USUARIO +"='" + id + "' AND "
                + TABLE_TAG +".ID=" + TABLE_TAG_USUARIO + "." + COL_ID_TAG;
        Cursor data = db.rawQuery(query, null);
        return data;
    }


    public Cursor getTagsByGastoId(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + TABLE_TAG + ".* FROM " + TABLE_TAG + " INNER JOIN " + TABLE_TAG_GASTO + " ON "+ TABLE_TAG +".ID=" + TABLE_TAG_GASTO + "." + COL_ID_TAG +
                " WHERE " + TABLE_TAG_GASTO + "." +COL_ID_GASTO +"='" + id +"'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getTagByNombre(String nombre){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_TAG + " WHERE " + COL_NOMBRE_TAG + " = '" + nombre + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    private boolean addTagUsuario(int tag, int id_usuario){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ID_TAG, tag);
        contentValues.put(COL_ID_USUARIO, id_usuario);  //Por ahora solo el 0, ya que solo permito una única cuenta

        long result = db.insert(TABLE_TAG_USUARIO, null, contentValues);

        if(result == -1){
            return false;
        }
        return true;
    }

    private boolean addTagGasto(int tag, int id_gasto){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ID_TAG, tag);
        contentValues.put(COL_ID_GASTO, id_gasto);  //Por ahora solo el 0, ya que solo permito una única cuenta

        long result = db.insert(TABLE_TAG_GASTO, null, contentValues);

        if(result == -1){
            return false;
        }
        return true;
    }


    public boolean addMonedaCantidad(String moneda, int cantidad, String simbolo){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_MONEDA, moneda);
        contentValues.put(COL_CANTIDAD, cantidad);
        contentValues.put(COL_SIMBOLO, simbolo);
        contentValues.put(COL_ID_USUARIO, UsuarioSingleton.getInstance().getID());

        long result = db.insert(TABLE_MONEDA_CANTIDAD, null, contentValues);

        if(result == -1){
            return false;
        }
        return true;
    }

    public Cursor getMonedasByUserId(int id_usuario){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_MONEDA_CANTIDAD + " WHERE " + COL_ID_USUARIO + " = '" + id_usuario + "'";
        Cursor data = db.rawQuery(query, null);
        return data;

    }




}
