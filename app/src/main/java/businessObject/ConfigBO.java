package businessObject;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;

import component.Util;
import dataObject.Config;

public class ConfigBO {

    public static final String TAG = "BusinessObject.ConfigBO";

    public static String mensaje;

    public static boolean CrearConfigDB() {

        SQLiteDatabase db = null;

        try {

            File dbFile = new File(Util.dirApp(), "Config.db");
            db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);

            try {

                String query = "SELECT conexion FROM Config";
                db.rawQuery(query, null);

            } catch (Exception e) {

                String query = "DROP TABLE IF EXISTS Config";
                db.execSQL(query);

                query = "CREATE TABLE IF NOT EXISTS Config(usuario varchar(20), iniciarDia int, conexion int)";
                db.execSQL(query);
            }

            return true;

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "CrearConfigDB -> " + mensaje, e);
            return false;

        } finally {

            if (db != null)
                db.close();
        }
    }

    public static boolean GuardarConfigUsuario(String usuario, int inciarDia, int conexion) {

        int total = 0;
        SQLiteDatabase db = null;

        try {

            File dbFile = new File(Util.dirApp(), "Config.db");

            if (dbFile.exists()) {

                db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

                String query = "SELECT COUNT(usuario) AS total FROM Config";
                Cursor cursor = db.rawQuery(query, null);

                if (cursor.moveToFirst()) {

                    total = cursor.getInt(cursor.getColumnIndex("total"));
                }

                if (cursor != null)
                    cursor.close();

                ContentValues values = new ContentValues();
                values.put("usuario", usuario.trim());
                values.put("iniciarDia", inciarDia);
                values.put("conexion", conexion);

                long rows = -1;

                if (total == 0) {

                    rows = db.insertOrThrow("Config", null, values);

                } else {

                    rows = db.update("Config", values, null, null);
                }

                return rows > 0;

            } else {

                Log.i(TAG, "GuardarConfigUsuario: No Existe la Base de Datos Config.db o No tiene Acceso a la SD");
                return false;
            }

        } catch (Exception e) {

            mensaje = e.getMessage();
            return false;

        } finally {

            if (db != null)
                db.close();
        }
    }

    public static Config ObtenerConfigUsuario() {

        Config config = null;
        SQLiteDatabase db = null;

        try {

            File dbFile = new File(Util.dirApp(), "Config.db");

            if (dbFile.exists()) {

                db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

                try {

                    String query = "SELECT conexion FROM Config";
                    db.rawQuery(query, null);

                } catch (Exception e) {
                    // TODO: handle exception
                    dbFile.delete();
                    ConfigBO.CrearConfigDB();
                }

                String query = "SELECT usuario, iniciarDia, conexion FROM Config";
                Cursor cursor = db.rawQuery(query, null);

                if (cursor.moveToFirst()) {

                    config = new Config();
                    config.usuario = cursor.getString(cursor.getColumnIndex("usuario"));
                    config.iniciarDia = cursor.getInt(cursor.getColumnIndex("iniciarDia"));
                    config.conexion = cursor.getInt(cursor.getColumnIndex("conexion"));
                }

                if (cursor != null)
                    cursor.close();

            } else {

                Log.i(TAG, "ObtenerConfigUsuario: No Existe la Base de Datos Config.db o No tiene Acceso a la SD");
            }

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("CargarConfigUsuario", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return config;
    }
}
