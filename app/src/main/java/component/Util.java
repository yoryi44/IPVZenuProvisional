package component;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import config.Const;

public class Util {

    private final static String TAG = Util.class.getName();

    /**
     * @return
     */
    public static File dirApp() {

        File SDCardRoot = Environment.getExternalStorageDirectory();
        String aux = Const.nameDirApp;
        File dirApp = new File(SDCardRoot.getPath() + "/" + Const.nameDirApp);

        if (!dirApp.isDirectory())
            dirApp.mkdirs();

        return dirApp;
    }

    /**
     * OBTIENE LA FECHA ACTUAL DEL SISTEMA
     *
     * @param format
     * @return
     */
    public static String fechaActual(String format) {

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean existeArchivoDataBase() {

        File SDCardRoot = Environment.getExternalStorageDirectory();
        File database = new File(SDCardRoot.getPath() + "/" + Const.nameDirApp, "DataBase.db");
        return database.exists();
    }

    public static boolean existeArchivoConfig() {

        File SDCardRoot = Environment.getExternalStorageDirectory();
        File database = new File(SDCardRoot.getPath() + "/" + Const.nameDirApp, "Config.db");
        return database.exists();
    }

    public static void eliminarDataBase() {

        File SDCardRoot = Environment.getExternalStorageDirectory();
        File database = new File(SDCardRoot.getPath() + "/" + Const.nameDirApp, "DataBase.db");

        if (database.exists()) {

            database.delete();
        }
    }

    public static String obtenerVersion(Context contexto) {

        String versionApp;

        try {

            versionApp = contexto.getPackageManager().getPackageInfo(contexto.getPackageName(), 0).versionName;

        } catch (PackageManager.NameNotFoundException e) {

            versionApp = "0.0";
            Log.e("UsuarioActivity", "ObtenerVersion: " + e.getMessage(), e);
        }

        return versionApp;
    }

    public static float toFloat(String value) {

        try {

            return Float.valueOf(value);

        } catch (NumberFormatException e) {

            return 0F;
        }
    }

    /**
     * @param value
     * @return
     */
    public static long toLong(String value) {

        try {
            return Long.parseLong(value);

        } catch (NumberFormatException e) {

            return 0L;
        }
    }

    /**
     * @param value
     * @return
     */
    public static int toInt(String value) {

        try {
            return Integer.parseInt(value);

        } catch (NumberFormatException e) {

            return 0;
        }
    }

    public static String obtenerId(String codigo) {

        String encabezado = "A" + codigo + Util.fechaActual("yyyyMMddHHmmssSSS");
        return encabezado;
    }

    public static String obtenerIdProducto() {

        String encabezado = Util.fechaActual("yyyyMMddHHmmss");
        return encabezado;
    }

    public static String obtenerFechaActual() {

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    public static Drawable resizedImage(int newWidth, int newHeight) {

        Matrix matrix;
        FileInputStream fd = null;
        Bitmap resizedBitmap = null;
        Bitmap bitmapOriginal = null;

        try {

            File fileImg = new File(Util.dirApp(), "foto.jpg");

            if (fileImg.exists()) {

                fd = new FileInputStream(fileImg.getPath());
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inDither = false;
                option.inPurgeable = true;
                option.inInputShareable = true;
                option.inTempStorage = new byte[32 * 1024];
                option.inPreferredConfig = Bitmap.Config.RGB_565;

                bitmapOriginal = BitmapFactory.decodeFileDescriptor(fd.getFD(), null, option);

                int width = bitmapOriginal.getWidth();
                int height = bitmapOriginal.getHeight();

                // Reescala el Ancho y el Alto de la Imagen
                float scaleWidth = ((float) newWidth) / width;
                float scaleHeight = ((float) newHeight) / height;

                matrix = new Matrix();
                matrix.postScale(scaleWidth, scaleHeight);

                // Crea la Imagen con el nuevo Tamano
                resizedBitmap = Bitmap.createBitmap(bitmapOriginal, 0, 0, width, height, matrix, true);
                BitmapDrawable bmd = new BitmapDrawable(resizedBitmap);

                bitmapOriginal.recycle();
                bitmapOriginal = null;
                return bmd.mutate();
            }

            return null;

        } catch (Exception e) {

            Log.e(TAG, "resizedImage -> " + e.getMessage(), e);
            return null;

        } finally {

            if (fd != null) {

                try {

                    fd.close();

                } catch (IOException e) {
                }
            }

            fd = null;
            matrix = null;
            resizedBitmap = null;
            bitmapOriginal = null;
            System.gc();
        }
    }

    public static Drawable resizedImage(byte[] image, int newWidth, int newHeight) {

        Matrix matrix;
        Bitmap resizedBitmap = null;
        Bitmap bitmapOriginal = null;

        try {

            BitmapFactory.Options option = new BitmapFactory.Options();
            option.inDither = false;
            option.inPurgeable = true;
            option.inInputShareable = true;
            option.inTempStorage = new byte[32 * 1024];
            option.inPreferredConfig = Bitmap.Config.RGB_565;

            bitmapOriginal = BitmapFactory.decodeByteArray(image, 0, image.length, option);

            int width = bitmapOriginal.getWidth();
            int height = bitmapOriginal.getHeight();

            if (width == newWidth && height == newHeight) {

                return new BitmapDrawable(bitmapOriginal);
            }

            // Reescala el Ancho y el Alto de la Imagen
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;

            matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);

            // Crea la Imagen con el nuevo Tamano
            resizedBitmap = Bitmap.createBitmap(bitmapOriginal, 0, 0, width, height, matrix, true);
            String mensaje = "Imagen escalada correctamente";
            return new BitmapDrawable(resizedBitmap);

        } catch (Exception e) {

            String mensaje = "Error escalando la Imagen: " + e.toString();
            return null;

        } finally {

            matrix = null;
            resizedBitmap = null;
            bitmapOriginal = null;
            System.gc();
        }
    }

    public static Drawable resizedImage(Drawable imgOriginal, int newWidth, int newHeight) {

        Matrix matrix;
        Bitmap resizedBitmap = null;
        Bitmap bitmapOriginal = null;

        try {

            //bitmapOriginal = BitmapDrawable(resizedBitmap);
            bitmapOriginal = ((BitmapDrawable) imgOriginal).getBitmap();

            int width = bitmapOriginal.getWidth();
            int height = bitmapOriginal.getHeight();

            if (width == newWidth && height == newHeight) {

                return new BitmapDrawable(bitmapOriginal);
            }

            // Reescala el Ancho y el Alto de la Imagen
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;

            matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);

            // Crea la Imagen con el nuevo Tamano
            resizedBitmap = Bitmap.createBitmap(bitmapOriginal, 0, 0, width, height, matrix, true);
            String mensaje = "Imagen escalada correctamente";
            return new BitmapDrawable(resizedBitmap);

        } catch (Exception e) {

            String mensaje = "Error escalando la Imagen: " + e.toString();
            return null;

        } finally {

            matrix = null;
            resizedBitmap = null;
            bitmapOriginal = null;
            System.gc();
        }
    }

    public static String separarMilesSinDecimal(String numero) {

        String cantidad;
        String cantidadAux1;
        String cantidadAux2;

        int posPunto;
        int i;

        cantidad = "";
        cantidadAux1 = "";
        cantidadAux2 = "";

        //numero = QuitarE( numero );

        if (numero.indexOf("-") != -1) {

            String aux;
            aux = numero.substring(0, numero.indexOf("-"));
            aux = aux + numero.substring(numero.indexOf("-") + 1, numero.length());
            numero = aux;
        }

        if (numero.indexOf(".") == -1) {

            if (numero.length() > 3) {

                cantidad = ColocarComas(numero, numero.length());
            }

        } else {

            posPunto = numero.indexOf(".");

            for (i = 0; i < posPunto; i++) {

                cantidadAux1 = cantidadAux1 + numero.charAt(i);
            }

            for (i = posPunto; i < numero.length(); i++) {

                cantidadAux2 = cantidadAux2 + numero.charAt(i);
            }

            if (cantidadAux1.length() > 3) {

                cantidad = ColocarComas(cantidadAux1, posPunto);
            }
        }

        return cantidad;
    }

    public static String numeroSinDecimal(String numero) {

        String cantidad;
        String cantidadAux1;
        String cantidadAux2;

        int posPunto;
        int i;

        cantidad = "";
        cantidadAux1 = "";
        cantidadAux2 = "";

        //numero = QuitarE( numero );

        if (numero.indexOf("-") != -1) {

            String aux;
            aux = numero.substring(0, numero.indexOf("-"));
            aux = aux + numero.substring(numero.indexOf("-") + 1, numero.length());
            numero = aux;
        }

        if (numero.indexOf(".") == -1) {

            if (numero.length() > 3) {

                cantidad = numero;
            }

        } else {

            posPunto = numero.indexOf(".");

            for (i = 0; i < posPunto; i++) {

                cantidadAux1 = cantidadAux1 + numero.charAt(i);
            }

            for (i = posPunto; i < numero.length(); i++) {

                cantidadAux2 = cantidadAux2 + numero.charAt(i);
            }

            if (cantidadAux1.length() > 3) {

                cantidad = cantidadAux1;
            }
        }

        return cantidad;
    }

    private static String ColocarComas(String numero, int pos) {

        String cantidad;
        Vector<String> cantidadAux;
        String cantidadAux1;
        int i;
        int cont;

        cantidadAux = new Vector<>();
        cantidadAux1 = "";
        cont = 0;

        for (i = (pos - 1); i >= 0; i--) {

            if (cont == 3) {

                cantidadAux1 = "," + cantidadAux1;
                cantidadAux.addElement(cantidadAux1);
                cantidadAux1 = "";
                cont = 0;
            }

            cantidadAux1 = numero.charAt(i) + cantidadAux1;
            cont++;
        }

        cantidad = cantidadAux1;

        for (i = cantidadAux.size() - 1; i >= 0; i--) {

            cantidad = cantidad + cantidadAux.elementAt(i);
        }

        return cantidad;
    }

    public static String redondear(String numero, int cantDec) {

        int tamNumero = 0;
        double numRedondear;
        int cantAfterPunto;

        if (numero.indexOf(".") == -1) {

            return numero;
        }

        tamNumero = numero.length();
        cantAfterPunto = tamNumero - (numero.indexOf(".") + 1);

        if (cantAfterPunto <= cantDec)
            return numero;

        String numeroSumar = "0.";

        for (int i = 0; i < cantDec; i++) {

            numeroSumar = numeroSumar.concat("0");
        }

        numeroSumar = numeroSumar.concat("5");

        numRedondear = Double.parseDouble(numero);

        numRedondear = numRedondear + Double.parseDouble(numeroSumar);

        numero = String.valueOf(numRedondear);

        tamNumero = numero.length();
        cantAfterPunto = tamNumero - (numero.indexOf(".") + 1);

        if (cantAfterPunto <= cantDec)
            return numero;
        else {

            if (cantDec == 0)
                numero = numero.substring(0, numero.indexOf("."));
            else
                numero = numero.substring(0,
                        (numero.indexOf(".") + 1 + cantDec));

            return numero;
        }
    }
}
