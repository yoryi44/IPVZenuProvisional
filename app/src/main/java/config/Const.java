package config;

import android.content.res.AssetManager;
import android.graphics.Typeface;

import java.util.Locale;

import dataObject.Main;

/**
 * Created by Cw Desarrollo on 16/09/2016.
 */
public class Const {

    public static final String TITULO;
    public final static String URL_SYNC;
    public final static String URL_DOWNLOAD_NEW_VERSION;
    public final static String URL_SYNC_CATALOGO;

    public final static int PRUEBAS    = 1;
    public final static int PRODUCCION = 2;

    public final static String nameDirApp = "IPVZenu";

    public final static int APLICACION = PRUEBAS;

    static {

        switch (APLICACION) {

            case PRUEBAS:
                TITULO                   = "PRUEBAS";
                URL_SYNC                 = "http://qas.movilidadcn.com/EmartWebService/IPV/";
                URL_DOWNLOAD_NEW_VERSION = "";
                URL_SYNC_CATALOGO        = "";
                break;

            case PRODUCCION:
                TITULO                   = "PRODUCCION";
                URL_SYNC                 = "http://movilidadcn.com/EmartWebService/IPV/";
                URL_DOWNLOAD_NEW_VERSION = "";
                URL_SYNC_CATALOGO        = "";
                break;

            default:
                TITULO                   = " - Sin Definir";
                URL_SYNC                 = "Sin_Definir";
                URL_DOWNLOAD_NEW_VERSION = "Sin_Definir";
                URL_SYNC_CATALOGO        = "Sin_Definir";
                break;
        }
    }

    public static final AssetManager am = Main.contexto.getAssets();
    public static final Typeface letraRegular = Typeface.createFromAsset(am, String.format(Locale.US, "fonts/%s", "Montserrat-Regular.ttf"));
    public static final Typeface letraSemibold = Typeface.createFromAsset(am, String.format(Locale.US, "fonts/%s", "Montserrat-SemiBold.ttf"));

    //URL de la aplicacion
    public final static String URL_LOGIN = "Login.aspx";
    //URL de descarga de bd
    public final static String URL_BD    = "CrearDB.aspx";
    // NOMBRE DEL APK
    public final static String fileNameApk = "IPVZenu.apk";

    //Constantes del Sincronizador
    public static final int LOGIN                        = 1;
    public static final int DOWNLOAD_VENDEDORES          = 2;
    public static final int ENVIAR_INFO                  = 3;
    public static final int DESCARGA_VERSION             = 4;
    public static final int RESP_ACTUALIZAR_VERSION      = 5;
    public static final int ENVIAR_INFO_DESCARGAR        = 6;
    public static final int AGREGAR_PRODUCTO             = 7;
    public static final int AGREGAR_EXHIBIDOR            = 8;
    public static final int RESP_TOMAR_FOTO              = 9;
    public static final int RESP_TERMINARACTIVACION      = 10;
    public static final int AGREGAR_PRODUCTO_COMPETENCIA = 11;
    public static final int INFORMEENCUESTA              = 12;
    public static final int CONEXION_BACH                = 13;
    public static final int CONEXION_UNO_UNO             = 14;
    public static final int EXHIBIDOR                    = 15;
    public static final int FOTO                         = 15;
    public static final int AGREGAR_PRODUCTO_COMP        = 16;

    public static final String[] listaPreguntas = {"Hay Promociones ", "Hay Material POP ", "Hay Impulsadores "};
}
