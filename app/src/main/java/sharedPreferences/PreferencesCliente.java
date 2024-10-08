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
public class PreferencesCliente {

	/**
	 * String para identificar que el proceso de facturacion aun no ha iniciado
	 */
	public static final String CLIENTE = "CLIENTE";


	/**
	 * constante para conservar el nombre del archivo de preferencias
	 */
	private static final String NOMBRE = "cliente";

	/**
	 * Permite guardar numero de documento de la factura.
	 */
	public static void guardarCodigoCliente(Context context, String codigo) {

		SharedPreferences settings = context.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(CLIENTE, codigo);
		editor.commit();
	}

	public static String obtenerCodigoClienteSeleccionado(Context context) {

		SharedPreferences settings = context.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
		return settings.getString(CLIENTE, "");
	}

	/**
	 * vaciar el preference. Remover todos los datos guardados.
	 * @param context
	 */
	public static void vaciarPreferencesCliente(Context context) {

		SharedPreferences settings = context.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.clear();
		editor.commit();
	}
}
