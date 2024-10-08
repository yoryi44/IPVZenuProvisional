package sharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Clase que ayuda a la insercion y obtencion de datos por medio de
 * SharedPreferences.
 */
public class PreferencesOpcionSelOtra {

    public static final String OPCIONSEL = "OPCIONSEL";
    private static final String NOMBRE = "opcionsel";

    /**
     * Permite guardar observacion de opcion seleccionada
     */
    public static void guardarObservacion(Context context, String observacion) {

        SharedPreferences settings = context.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(OPCIONSEL, observacion);
        editor.commit();
    }

    public static String obtenerObservacion(Context context) {

        SharedPreferences settings = context.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
        return settings.getString(OPCIONSEL, "");
    }

    /**
     * vaciar el preference. Remover todos los datos guardados.
     * @param context
     */
    public static void vaciarPreferencesObservacion(Context context) {

        SharedPreferences settings = context.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.commit();
    }
}

