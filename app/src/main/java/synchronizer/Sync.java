package synchronizer;

// import android.os.Environment;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import businessObject.DataBaseBO;
import component.Util;
import config.Const;
import config.Synchronizer;
import dataObject.Main;
import dataObject.Usuario;

/**
 * import org.apache.http.Header;
 * import org.apache.http.HttpEntity;
 * import org.apache.http.HttpResponse;
 * import org.apache.http.HttpStatus;
 * import org.apache.http.StatusLine;
 * import org.apache.http.client.HttpClient;
 * import org.apache.http.client.entity.UrlEncodedFormEntity;
 * import org.apache.http.client.methods.HttpPost;
 * import org.apache.http.impl.client.DefaultHttpClient;
 * import org.apache.http.message.BasicNameValuePair;
 * import org.apache.http.params.BasicHttpParams;
 * import org.apache.http.params.HttpConnectionParams;
 * import org.apache.http.params.HttpParams;
 * <p>
 * import businessObject.DataBaseBER;
 * import businessObject.FileBO;
 * import component.Util;
 **/
/**
 import businessObject.DataBaseBER;
 import businessObject.FileBO;
 import component.Util;
 **/
/**
 * import dataObject.Main;
 * import dataObject.Usuario;
 **/

/**
 * Created by Cw Desarrollo on 16/09/2016.
 */
public class Sync extends Thread {

    private final static String TAG = Sync.class.getName();

    Synchronizer sincronizador;
    int codeRequest;

    //Guarda la respuesta del servidor
    String respuestaServer = "";
    String mensaje = "";
    boolean ok;

    public String user;
    public String password;

    public String fechaInicial;
    public String fechaFinal;
    public String usuario;

    /**
     * Constructor de la clase
     *
     * @param sincronizador
     * @param codeRequest
     */
    public Sync(Synchronizer sincronizador, int codeRequest) {

        this.sincronizador = sincronizador;
        this.codeRequest = codeRequest;
    }

    public void run() {

        switch (codeRequest) {

            case Const.LOGIN:
                logIn();
                break;

            case Const.DOWNLOAD_VENDEDORES:
                downloadDataBase();
                break;

            case Const.ENVIAR_INFO:
                enviarInformacion();
                break;

            case Const.DESCARGA_VERSION:
                descargarVersionApp();
                break;

            case Const.INFORMEENCUESTA:
                obtenerInformeEncuesta();
                break;
        }
    }

    public void obtenerInformeEncuesta() {

        ok = false;
        int longTimeout = 1 * 45 * 1000;

        HttpURLConnection conexion = null;
        HttpURLConnection conexionService = null;

        String urlUpLoad = "http://movilidadcn.com/ServiciosIPV/Consultas.aspx?" + "opcion=nroEncuestas&usr=" + usuario + "&feI=" + fechaInicial + "&feF=" + fechaFinal;

        try {

            URL url = new URL(urlUpLoad);
            conexion = (HttpURLConnection) url.openConnection();

            conexion.setDoInput(true);    //Permite Entradas
            conexion.setDoOutput(true);   //Permite Salidas
            conexion.setUseCaches(false); //No usar cache

            conexion.setRequestMethod("GET");
            conexion.setRequestProperty("Accept", "application/json");

            /**
             * SE DEFINE EL TIEMPO DE ESPERA, MAXIMO ESPERA 2 MINUTOS
             **/
            conexion.setConnectTimeout(longTimeout);
            conexion.setReadTimeout(longTimeout);

            int statusCode = conexion.getResponseCode();
            respuestaServer = "";

            switch (statusCode) {

                case 200:

                    BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = br.readLine()) != null) {

                        sb.append(line + "\n");
                    }

                    br.close();

                    respuestaServer = sb.toString();

                    if(respuestaServer.startsWith("Error")) {

                        mensaje = "Error en login";
                        ok = false;

                    } else {

                        mensaje = "ok";
                        ok = true;
                    }

                    break;

                default:

                    mensaje = "Error en login";
                    respuestaServer = "error";
            }


        } catch (Exception e) {

            mensaje = "Login: Error inesperado -> " + e.getMessage();

        } finally {

            try {

                if (conexion != null)
                    conexion.disconnect();


                if (conexionService != null)
                    conexionService.disconnect();

                sincronizador.respSync(ok, respuestaServer, mensaje, codeRequest);

            } catch (Exception e) {

                Log.e("FileUpLoad", "Error cerrando conexion: " + e.getMessage(), e);
            }
        }
    }

    public void enviarInformacion() {

        ok = false;
        String msg = "";

        if (comprimirArchivo()) {

            File zipPedido = new File(Util.dirApp(), "Temp.zip");

            if (!zipPedido.exists()) {

                Log.i("EnviarPedido", "El archivo Temp.zip no Existe");
                sincronizador.respSync(ok, "", "El archivo Temp.zip no Existe", codeRequest);
                return;
            }

            DataOutputStream dos = null;
            HttpURLConnection conexion = null;
            BufferedReader bufferedReader = null;
            FileInputStream fileInputStream = null;

            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            int bytesRead, bytesAvailable, bufferSize;

            /************************************
             * Carga la Configuracion del Usuario.
             ************************************/
            Usuario usuario = DataBaseBO.obtenerUsuario();

            if (true) {

                int consecutivo = 0; // DataBaseBER.ObtenerConsecutivoVend();
                String urlUpLoad = Const.URL_SYNC + "RegistrarPedido.aspx?un=" + usuario.codigo + "&termino=0&ext=zip&fechaLabor="+ Util.fechaActual("yyyy/MM/dd") +"&co=0";
                Log.i("EnviarPedido", "URL Enviar Info = " + urlUpLoad);

                try {

                    URL url = new URL(urlUpLoad);
                    conexion = (HttpURLConnection) url.openConnection();

                    conexion.setDoInput(true);    //Permite Entradas
                    conexion.setDoOutput(true);   //Permite Salidas
                    conexion.setUseCaches(false); //No usar cache

                    /**
                     * SE ESTABLECEN LOS HEADERS
                     **/
                    conexion.setRequestMethod("POST");
                    conexion.setRequestProperty("Connection", "Keep-Alive");
                    conexion.setRequestProperty("Content-Type", "multipart/form-data; boundary=---------------------------4664151417711");

                    /**
                     * SE DEFINE EL TIEMPO DE ESPERA, MAXIMO ESPERA 1 MINUTO
                     **/
                    conexion.setConnectTimeout(60 * 1000);
                    conexion.setReadTimeout(60 * 1000);

                    new Thread(new InterruptThread(Thread.currentThread(), conexion)).start();

                    /**
                     * SE CREA EL BUFFER PARA ENVIAR LA INFORMACION DEL ARCHIVO
                     **/
                    fileInputStream = new FileInputStream( zipPedido );
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    /**
                     * ABRE LA CONEXION DEL FLUJO DE SALIDA. LEE Y ESCRIBE LA INFORMACION DEL ARCHIVO EN EL BUFFER
                     * Y ENVIA LA INFORMACION AL SERVIDOR.
                     **/
                    dos = new DataOutputStream( conexion.getOutputStream() );
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {

                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }

                    dos.flush();
                    Log.i("EnviarPedido", "Enviando informacion del Archivo");

                    /**
                     * LEE LA RESPUESTA DEL SERVIDOR
                     **/
                    bufferedReader = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                    String line;

                    respuestaServer = "";
                    while ((line = bufferedReader.readLine()) != null) {

                        respuestaServer += line;
                    }

                    if (respuestaServer.startsWith("ok")) {

                        ok = true;
                        DataBaseBO.borrarInfoTemp();

                    } else {

                        if (respuestaServer.equals(""))
                            msg = "Sin respuesta del servidor";
                        else
                            msg = respuestaServer;
                    }

                    Log.i("EnviarPedido", "respuesta: " + respuestaServer);

                } catch (Exception ex) {

                    msg = ex.getMessage();
                    Log.e("EnviarPedido", msg, ex);

                } finally {

                    try {

                        if (bufferedReader != null)
                            bufferedReader.close();

                        if (fileInputStream != null)
                            fileInputStream.close();

                        if (dos != null)
                            dos.close();

                        if (conexion != null)
                            conexion.disconnect();

                    } catch (IOException e) {

                        Log.e("FileUpLoad", "Error cerrando conexion: " + e.getMessage(), e);
                    }
                }

            } else {

                Log.i("EnviarPedido", "Falta establecer la configuracion del Usuario");
                mensaje = "Por favor, primero ingrese la configuracion del usuario";
            }

        } else {

            msg = "Error comprimiendo la Base de datos Pedido";
            Log.e("FileUpLoad", msg);
        }

        if (respuestaServer.equals(""))
            respuestaServer = "error, Sin respuesta del servidor";

        sincronizador.respSync(ok, respuestaServer, msg, codeRequest);
    }

    /**
     * Metodo que me permite realizar el Login
     */
    public void logIn() {

        ok = false;

        int longTimeout = 1 * 45 * 1000;

        HttpURLConnection conexion = null;
        HttpURLConnection conexionService = null;

        String urlUpLoad = Const.URL_SYNC + Const.URL_LOGIN + "?un=" + this.user + "&pw=" + this.password;

        try {

            URL url = new URL(urlUpLoad);
            conexion = (HttpURLConnection) url.openConnection();

            conexion.setDoInput(true);    //Permite Entradas
            conexion.setDoOutput(true);   //Permite Salidas
            conexion.setUseCaches(false); //No usar cache

            conexion.setRequestMethod("GET");
            conexion.setRequestProperty("Accept", "application/json");

            /**
             * SE DEFINE EL TIEMPO DE ESPERA, MAXIMO ESPERA 2 MINUTOS
             **/
            conexion.setConnectTimeout(longTimeout);
            conexion.setReadTimeout(longTimeout);

            int statusCode = conexion.getResponseCode();
            respuestaServer = "";

            switch (statusCode) {

                case 200:

                    BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = br.readLine()) != null) {

                        sb.append(line + "\n");
                    }

                    br.close();

                    respuestaServer = sb.toString();

                    if(respuestaServer.startsWith("Error")) {

                        mensaje = "Error en login";
                        ok = false;

                    } else {

                        mensaje = "ok";
                        ok = true;
                    }

                    break;

                default:

                    mensaje = "Error en login";
                    respuestaServer = "error";
            }


        } catch (Exception e) {

            mensaje = "Login: Error inesperado -> " + e.getMessage();

        } finally {

            try {

                if (conexion != null)
                    conexion.disconnect();


                if (conexionService != null)
                    conexionService.disconnect();

                sincronizador.respSync(ok, respuestaServer, mensaje, codeRequest);

            } catch (Exception e) {

                Log.e("FileUpLoad", "Error cerrando conexion: " + e.getMessage(), e);
            }
        }
    }

    public void downloadDataBase() {

        boolean ok = false;
        InputStream inputStream = null;
        FileOutputStream fileOutput = null;
        HttpURLConnection urlConnection = null;

        try {

            /************************************
             * Carga la Configuracion del Usuario.
             ************************************/
            Locale locale = Locale.getDefault();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd@HH_mm_ss", locale);
            String fechaMovil = dateFormat.format(new Date());

            String usu = Main.usuario.codigo;
            String guion="_";
            String version="1.0";
            String imei="1";

            for (int i = 0; i< Main.listaVendedores.size(); i++) {

                if (Main.listaVendedores.get(i).seleccionado == 1) {

                    usu += guion + Main.listaVendedores.get(i).codigo;
                    guion="()";
                }
            }

            String urlDataBase = Const.URL_SYNC + Const.URL_BD +"?un=" + usu + "&fe=" + fechaMovil + "&lo=" + locale + "&vr=" + version + "&i=" + imei;
            Log.i(TAG, "URL DB = " + urlDataBase);

            URL url = new URL(urlDataBase);
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.setConnectTimeout(240000);
            urlConnection.setReadTimeout(240000);

            new Thread(new InterruptThread(Thread.currentThread(), urlConnection)).start();

            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
            String contentDisposition = urlConnection.getHeaderField("Content-Disposition");

            if (contentDisposition != null) { //Viene Archivo Adjunto

                /**
                 * Se obtiene la ruta del SD Card, para guardar la Base de Datos.
                 * Y se crea el Archivo de la BD
                 **/
                String fileName = "Temporal.zip";
                File file = new File(Util.dirApp(), fileName);

                if (file.exists())
                    file.delete();

                if (file.createNewFile()) {

                    fileOutput = new FileOutputStream(file);

                    long downloadedSize = 0;
                    int bufferLength = 0;
                    byte[] buffer = new byte[1024];

                    /**
                     * SE LEE LA INFORMACION DEL BUFFER Y SE ESCRIBE EL CONTENIDO EN EL ARCHIVO DE SALIDA
                     **/
                    while ( (bufferLength = inputStream.read(buffer)) > 0 ) {

                        fileOutput.write(buffer, 0, bufferLength);
                        downloadedSize += bufferLength;
                    }

                    fileOutput.flush();
                    fileOutput.close();
                    inputStream.close();

                    long content_length = Util.toLong(urlConnection.getHeaderField("content-length"));

                    if (content_length == 0) {

                        mensaje = "No hay conexion, por favor intente de nuevo";

                    } else  if (content_length != downloadedSize) { // La longitud de descarga no es igual al Content Length del Archivo

                        mensaje = "No se pudo descargar la base de datos, por favor intente de nuevo";

                    } else {

                        descomprimir(file);

                        ok = true;
                        mensaje = "Descargo correctamente la Base de Datos";
                    }

                } else {

                    mensaje = "No se pudo crear el archivo de la Base de Datos";
                }

            } else { // No hay archivo adjunto, se procesa el Mensaje de respuesta del Servidor

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line = null;
                while ((line = reader.readLine()) != null) {

                    respuestaServer += line;
                }

                if (respuestaServer.equals(""))
                    mensaje = "No se pudo descargar la base de datos, por favor intente de nuevo.";
                else
                    mensaje = respuestaServer;
            }

        } catch (Exception e) {

            String motivo = e.getMessage();

            if(motivo != null) {

                if (motivo.startsWith("http://"))
                    motivo = "Pagina no Encontrada: CrearDB.aspx";
            }

            mensaje = "No se pudo descargar la Base de Datos\n\n";
            mensaje += "Motivo: " + motivo;

            Log.e(TAG, "DownloadDataBase: " + e.getMessage(), e);

        } finally {

            try {

                if (fileOutput != null)
                    fileOutput.close();

                if (inputStream != null)
                    inputStream.close();

            } catch (IOException e) { }

            if (urlConnection != null)
                urlConnection.disconnect();
        }

        sincronizador.respSync(ok, mensaje, mensaje, codeRequest);
    }

    /**
     * DESCOMPRIME LA BASE DE DATOS
     * @param fileZip
     */
    public void descomprimir(File fileZip) {

        try  {

            if (fileZip.exists()) {

                String nameFile = fileZip.getName().replace(".zip", "");
                File fileZipAux = new File(Util.dirApp(), nameFile);

                if (!fileZipAux.exists())
                    fileZipAux.delete();

                FileInputStream fin = new FileInputStream(fileZip);
                ZipInputStream zin = new ZipInputStream(fin);

                ZipEntry ze = null;

                while ((ze = zin.getNextEntry()) != null) {

                    Log.v("Descomprimir", "Unzipping " + ze.getName());

                    if(ze.isDirectory()) {

                        dirChecker(ze.getName());

                    } else {

                        String pathFile = Util.dirApp() + "/" + ze.getName();
                        File file = new File(pathFile);
                        FileOutputStream fout = new FileOutputStream(file);

                        int bufferLength = 0;
                        byte[] buffer = new byte[1024];

                        while ( (bufferLength = zin.read(buffer)) > 0 ) {

                            fout.write(buffer, 0, bufferLength);
                        }

                        zin.closeEntry();
                        fout.flush();
                        fout.close();
                    }
                }
                zin.close();
            }

        } catch(Exception e) {

            Log.e("Descomprimir", e.getMessage(), e);
        }
    }

    /**
     * VALIDA EL DIRECTORIO
     * @param dir
     */
    private void dirChecker(String dir) {

        File f = new File(Util.dirApp().getPath() + "/" + dir);

        if(!f.isDirectory()) {
            f.mkdirs();
        }
    }

    public boolean comprimirArchivo() {

        File zipPedido = new File(Util.dirApp(), "Temp.zip");

        if (zipPedido.exists())
            zipPedido.delete();

        FileOutputStream out = null;
        GZIPOutputStream gZipOut = null;
        FileInputStream fileInputStream = null;

        try {

            File dbFile = new File(Util.dirApp(), "Temp.db");

            if (dbFile.exists()) {

                fileInputStream = new FileInputStream(dbFile);

                int lenFile = fileInputStream.available();
                byte[] buffer = new byte[fileInputStream.available()];

                int byteRead = fileInputStream.read(buffer);

                if (byteRead == lenFile) {

                    out = new FileOutputStream(zipPedido);
                    gZipOut = new GZIPOutputStream(out);
                    gZipOut.write(buffer);
                    return true;
                }

                if (zipPedido.exists())
                    zipPedido.delete();
            }

            return false;

        } catch (Exception e) {

            if (zipPedido.exists())
                zipPedido.delete();

            Log.e("ComprimirArchivo", e.getMessage(), e);
            return false;

        } finally {

            try {

                if (gZipOut !=  null)
                    gZipOut.close();

                if (out != null)
                    out.close();

                if (fileInputStream != null)
                    fileInputStream.close();

            } catch (Exception e) {

                Log.e("ComprimirArchivo", e.getMessage(), e);
            }
        }
    }

    public void descargarVersionApp() {

        boolean ok = false;
        InputStream inputStream = null;
        FileOutputStream fileOutput = null;

        try {

            URL url = new URL(Const.URL_DOWNLOAD_NEW_VERSION);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            Log.i("DownloadVersionApp", "URL App = " + Const.URL_DOWNLOAD_NEW_VERSION);

            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);

            urlConnection.connect();
            inputStream = urlConnection.getInputStream();

            File file = new File(Util.dirApp(), Const.fileNameApk);

            if (file.exists())
                file.delete();

            if (file.createNewFile()) {

                fileOutput = new FileOutputStream(file);

                long downloadedSize = 0;
                int bufferLength = 0;
                byte[] buffer = new byte[1024];

                /**
                 * SE LEE LA INFORMACION DEL BUFFER Y SE ESCRIBE EL CONTENIDO EN EL ARCHIVO DE SALIDA
                 **/
                while ( (bufferLength = inputStream.read(buffer)) > 0 ) {

                    fileOutput.write(buffer, 0, bufferLength);
                    downloadedSize += bufferLength;
                }

                fileOutput.flush();
                fileOutput.close();
                inputStream.close();

                long content_length = Util.toLong(urlConnection.getHeaderField("content-length"));

                if (content_length == 0) {

                    ok = false;
                    mensaje = "Error de conexion, por favor intente de nuevo";

                } else  if (content_length != downloadedSize) { // La longitud de descarga no es igual al Content Length del Archivo

                    ok = false;
                    mensaje = "Error descargando la nueva version, por favor intente de nuevo";

                } else {

                    ok = true;
                    mensaje = "Descargo correctamente la Nueva Version";
                }

            } else {

                mensaje = "Error Creando el Archivo de la Nueva Version";
                ok = false;
            }

        } catch (ConnectException ex) {
            mensaje = "No es posible enviar Informacion, Debido a que El sistema no detecta señal de Internet";
        } catch (Exception e) {

            mensaje = "Error Descargando la Nueva version de la Aplicacion\n";
            mensaje += "Detalle Error: " + e.getMessage();
            Log.e("Sync DownloadVersionApp", e.getMessage(), e);
            ok = false;

        } finally {

            try {

                if (fileOutput != null)
                    fileOutput.close();

                if (inputStream != null)
                    inputStream.close();

            } catch (IOException e) { }
        }

        sincronizador.respSync(ok, mensaje, mensaje, codeRequest);
    }

    public class InterruptThread implements Runnable {

        Thread parent;
        URLConnection con;

        public InterruptThread(Thread parent, URLConnection con) {

            this.parent = parent;
            this.con = con;
        }

        public void run() {

            try {

                Thread.sleep(60000);

            } catch (InterruptedException e) {}

            System.out.println("Timer thread forcing parent to quit connection");
            ((HttpURLConnection)con).disconnect();
            System.out.println("Timer thread closed connection held by parent, exiting");
        }
    }
}
