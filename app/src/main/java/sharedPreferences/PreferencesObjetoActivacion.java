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
public class PreferencesObjetoActivacion {

	/**
	 * String para identificar que el proceso de facturacion aun no ha iniciado
	 */
	public static final String OBJETOACTIVACION = "OBJETOACTIVACION";


	/**
	 * constante para conservar el nombre del archivo de preferencias
	 */
	private static final String NOMBRE = "objetoactivacion";

	/**
	 * Permite guardar numero de documento de la factura.
	 */
	public static void guardarObjetoActivacion(Context context, String objetoActivacion) {

		SharedPreferences settings = context.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(OBJETOACTIVACION, objetoActivacion);
		editor.commit();
	}

	public static String obtenerObjetoActivacion(Context context) {

		SharedPreferences settings = context.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
		return settings.getString(OBJETOACTIVACION, "");
	}

	/**
	 * vaciar el preference. Remover todos los datos guardados.
	 * @param context
	 */
	public static void vaciarPreferencesObjetoActivacion(Context context) {

		SharedPreferences settings = context.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.clear();
		editor.commit();
	}
}
