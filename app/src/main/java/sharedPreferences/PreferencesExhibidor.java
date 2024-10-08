/**
 * 
 */
package sharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Clase que ayuda a la insercion y obtencion de datos por medio de
 * SharedPreferences.
 * @author JICZ
 */
public class PreferencesExhibidor {

	/**
	 * String para identificar que el proceso de facturacion aun no ha iniciado
	 */
	public static final String EXHIBIDOR = "EXHIBIDOR";


	/**
	 * constante para conservar el nombre del archivo de preferencias
	 */
	private static final String ID = "exhibidor";

	/**
	 * Permite guardar numero de documento de la factura.
	 */
	public static void guardarIdExhibidor(Context context, String codigo) {

		SharedPreferences settings = context.getSharedPreferences(ID, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(EXHIBIDOR, codigo);
		editor.commit();
	}

	public static String obtenerIdExhibidor(Context context) {

		SharedPreferences settings = context.getSharedPreferences(ID, Context.MODE_PRIVATE);
		return settings.getString(EXHIBIDOR, "");
	}

	/**
	 * vaciar el preference. Remover todos los datos guardados.
	 * @param context
	 */
	public static void vaciarPreferencesExhibidor(Context context) {

		SharedPreferences settings = context.getSharedPreferences(ID, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.clear();
		editor.commit();
	}
}
