package co.com.celuweb.ipv;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import businessObject.DataBaseBO;
import component.Alert;
import component.Progress;
import component.Util;
import config.Const;
import config.Synchronizer;
import dataObject.ItemListView;
import dataObject.Main;
import dataObject.Usuario;
import synchronizer.Sync;

public class LogInActivity extends AppCompatActivity implements Synchronizer {

    // VARIABLES DE COMPONENTES GRAFICOS CONTENIDO
    private TextView tvTituloModulo;
    private EditText etClave;
    private EditText etUsuario;
    private TextView tvEntrar;
    private LinearLayout llContenido;

    // VARIABLES DE COMPONENTES GRAFICOS SIN CONEXION
    private TextView tvMensajeSinConnexion1;
    private TextView tvMensajeSinConnexion2;
    private TextView tvTituloBotonReintentar;
    private LinearLayout llRespuestaInternet;

    public static final String TAG = LogInActivity.class.getName();
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    /**
     * CREATE DE LA ACTIVIDAD
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_log_in);

        if(Build.VERSION.SDK_INT >= 23) {

            if(checkAndRequestPermissions()) {

                inicializacionConfiguracionApp();
            }

        } else {

            inicializacionConfiguracionApp();
        }
    }

    private boolean checkAndRequestPermissions() {

        int permisoInternet         = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        int permisoEstadoRed        = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE);
        int permisoEstadoTelefono   = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int permisoEscrituraExterna = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permisoLecturaExterna   = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permisoGPS              = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permisoWIFI             = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE);
        int permisoCamara           = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (permisoInternet != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.INTERNET);
        }

        if (permisoEstadoRed != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_NETWORK_STATE);
        }

        if (permisoEstadoTelefono != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (permisoEscrituraExterna != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (permisoLecturaExterna != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (permisoGPS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (permisoWIFI != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_WIFI_STATE);
        }

        if (permisoCamara != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }

        if (!listPermissionsNeeded.isEmpty()) {

            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            inicializacionConfiguracionApp();
            return false;
        }

        return true;
    }

    private void inicializacionConfiguracionApp() {

        boolean existeArchivoConfig = Util.existeArchivoConfig();

        if(existeArchivoConfig) {

            // SE OBTIENE EL USUARIO ACTUAL, SI ES DIFERENTE DE NULL ENTONCES SE PASA A LA VISTA PRINCIPAL
            Usuario usuario = DataBaseBO.obtenerUsuario();

            if(usuario == null) {

                configuracionGraficaInterfaz();

            } else {

                Main.usuario = usuario;
                Intent intent = new Intent(LogInActivity.this, PrincipalActivity.class);
                startActivity(intent);
            }

        } else {

            configuracionGraficaInterfaz();
        }
    }

    private void configuracionGraficaInterfaz() {

        DataBaseBO.crearConfigDB();

        // SE INICIALIZA EL COMPONENTE PRINCIPAL DE LA APLICACION
        Main.contexto = getApplicationContext();

        tvTituloModulo = findViewById(R.id.tvTituloModulo);
        tvTituloModulo.setTypeface(Const.letraRegular);

        etClave = findViewById(R.id.etClave);
        etClave.setTypeface(Const.letraRegular);

        etUsuario = findViewById(R.id.etUsuario);
        etUsuario.setTypeface(Const.letraRegular);

        tvEntrar = findViewById(R.id.tvEntrar);
        tvEntrar.setTypeface(Const.letraRegular);

        tvMensajeSinConnexion1 = findViewById(R.id.tvMensajeSinConnexion1);
        tvMensajeSinConnexion1.setTypeface(Const.letraRegular);

        tvMensajeSinConnexion2 = findViewById(R.id.tvMensajeSinConnexion2);
        tvMensajeSinConnexion2.setTypeface(Const.letraRegular);

        tvTituloBotonReintentar = findViewById(R.id.tvTituloBotonReintentar);
        tvTituloBotonReintentar.setTypeface(Const.letraRegular);

        llContenido = findViewById(R.id.llContenido);
        llContenido.setVisibility(View.VISIBLE);

        llRespuestaInternet = findViewById(R.id.llRespuestaInternet);
        llRespuestaInternet.setVisibility(View.GONE);
    }


    /**
     * METODO AL PRESIONAR EL BOTON DE LOGIN
     */
    public void on_ClickEntrar(View view) {

        etUsuario = findViewById(R.id.etUsuario);
        etClave = findViewById(R.id.etClave);

        String usuario = etUsuario.getText().toString().trim();
        String clave   = etClave.getText().toString().trim();

        if(usuario.equals("")) {

            etUsuario.setError("Ingrese un usuario.");

        } else if(clave.equals("")) {

            etClave.setError("Ingrese la clave.");

        } else {

            if(Util.isNetworkAvailable(this)) {

                Progress.show(this, "Cargando", "Validando información...", false);
                loginUsuario(usuario, clave);

            } else {

                llContenido = findViewById(R.id.llContenido);
                llContenido.setVisibility(View.GONE);

                llRespuestaInternet = findViewById(R.id.llRespuestaInternet);
                llRespuestaInternet.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * REALIZA LA PETICION CON EL SERVIDOR
     * @param usuario
     * @param clave
     */
    private void loginUsuario(String usuario, String clave) {

        Sync sync = new Sync(LogInActivity.this, Const.LOGIN);
        sync.user       = usuario;
        sync.password   = clave;
        sync.start();
    }

    @Override
    public void respSync(boolean ok, String respuestaServer, String msg, int codeRequest) {

        switch (codeRequest) {

            case Const.LOGIN:
                respuestaLogin(ok, respuestaServer, msg);
                break;
        }
    }

    public void respuestaLogin(final boolean ok, final String respuestaServer, final String msg) {

        try {

            Thread.sleep(1000);
            Progress.hide();

            if (ok) {

                getVendedores(respuestaServer, etUsuario.getText().toString(), etClave.getText().toString());
                LogInActivity.this.runOnUiThread(new Runnable() {

                    public void run() {

                        etUsuario.setText("");
                        etClave.setText("");
                    }
                });

            } else {

                LogInActivity.this.runOnUiThread(new Runnable() {
                    public void run() {

                        etUsuario.setText("");
                        etClave.setText("");

                        //ALERT
                        Alert.nutresaShow(LogInActivity.this, "INFORMACIÓN", "EL usuario o clave no es valido",
                                "OK",
                                null,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        Alert.dialogo.cancel();
                                    }

                                }, null);

                    }
                });
            }

        } catch (Exception e) {

            String mensaje = e.getMessage();
            Log.e(TAG, "respuestaLogin -> " + mensaje, e);
        }
    }

    private void getVendedores(String cadena, String usuario, String clave) {

        String [] listRespuesta = cadena.split(";");

        if(listRespuesta.length > 0) {

            /**OBTIENE EL USUARIO QUE ESTA LOGUEADO*/
            String [] nombreVendedor = listRespuesta[1].split(",");

            Usuario user = new Usuario();
            user.codigo = nombreVendedor[0];
            user.nombre = nombreVendedor[1];
            user.tipo = listRespuesta[2];
            Main.usuario = user;

            /**OBTIENE LA CANTIDAD DE VENDEDORES*/
            Main.cantVendedores = Integer.parseInt(listRespuesta[2]);

            /**OBTIENE LA LISTA DE TODOS LOS VENDEDORES*/
            Vector<ItemListView> listVendedores = new Vector<ItemListView>();
            String [] ArrayVendedores = listRespuesta[3].split(",");

            for(int i=0; i<ArrayVendedores.length; i++) {

                String[] separar = ArrayVendedores[i].split("_");
                ItemListView vendedor = new ItemListView();
                vendedor.codigo = separar[0];
                vendedor.nombre = separar[1];
                vendedor.seleccionado = 0;

                listVendedores.addElement(vendedor);
            }

            Main.listaVendedores = listVendedores;

            // SE ALMACENAN LOS VENDEDORES EN LA BASE DE DATOS
            DataBaseBO.guardarVendedoresSincronizador(listVendedores);
            DataBaseBO.guardarUsuarioSincronizador(Main.usuario);
        }

        Intent intent = new Intent(LogInActivity.this, PrincipalActivity.class);
        startActivity(intent);
    }

    public void on_ClickReintentar(View view) {

        llContenido = findViewById(R.id.llContenido);
        llContenido.setVisibility(View.VISIBLE);

        llRespuestaInternet = findViewById(R.id.llRespuestaInternet);
        llRespuestaInternet.setVisibility(View.GONE);

        // SE REINTENTA EL LOGIN
        String usuario = etUsuario.getText().toString().trim();
        String clave   = etClave.getText().toString().trim();

        if(usuario.equals("")) {

            etUsuario.setError("Ingrese un usuario.");

        } else if(clave.equals("")) {

            etClave.setError("Ingrese la clave.");

        } else {

            if(Util.isNetworkAvailable(this)) {

                Progress.show(this, "Cargando", "Validando información...", false);
                loginUsuario(usuario, clave);

            } else {

                llContenido = findViewById(R.id.llContenido);
                llContenido.setVisibility(View.GONE);

                llRespuestaInternet = findViewById(R.id.llRespuestaInternet);
                llRespuestaInternet.setVisibility(View.VISIBLE);
            }
        }
    }
}
