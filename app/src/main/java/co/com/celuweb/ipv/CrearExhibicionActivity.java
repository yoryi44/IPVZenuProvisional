package co.com.celuweb.ipv;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.sql.DatabaseMetaData;
import java.util.Vector;

import businessObject.DataBaseBO;
import component.Alert;
import component.Progress;
import component.Util;
import config.Const;
import dataObject.Exhibidores;
import dataObject.Main;
import dataObject.Producto;
import dataObject.TipoExhibidor;
import dataObject.UbicacionExhibidor;
import dataObject.Usuario;
import es.dmoral.toasty.Toasty;
import sharedPreferences.PreferencesCliente;
import synchronizer.Sync;

public class CrearExhibicionActivity extends AppCompatActivity {

    private EditText etNombreExhibidor;
    private EditText etAnchoExhibidor;
    private EditText etAltoExhibidor;
    private Spinner spTipoExhibidor;
    private Spinner spUbicacionExhibidor;

    private TextView tvNombreExhibidor;
    private TextView tvAnchoExhibidor;
    private TextView tvAltoExhibidor;
    private TextView tvTipoExhibidor;
    private TextView tvUbicacionExhibidor;

    private Vector<TipoExhibidor> listaTipoExhibidor;
    private Vector<UbicacionExhibidor> listaUbicacionExhibidor;

    private boolean tipoCreacion;
    private String idExhibidorMod;
    private Exhibidores exhibidor;

    private final static String TAG = CrearExhibicionActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_crear_exhibicion);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        setSupportActionBar(toolbar);

        // SE INICIALIZA EL COMPONENTE PRINCIPAL DE LA APLICACION
        Main.contexto = getApplicationContext();

        TextView tvTituloModulo = findViewById(R.id.tvTituloModulo);
        tvTituloModulo.setTypeface(Const.letraSemibold);
        tvTituloModulo.setText("CREAR EXHIBIDOR");

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // SE DETERMINA SI ES UNA CREACION O UNA MODIFICACION DEL EXHIBIDOR
        Bundle extraInfo = getIntent().getExtras();

        if(extraInfo != null) {

            tipoCreacion = extraInfo.getBoolean("TIPO");

            if(tipoCreacion == true) {

                idExhibidorMod = extraInfo.getString("IDEXHIBIDOR");
                exhibidor = DataBaseBO.obtenerExhibidor(idExhibidorMod);
            }
        }

        setComponentesVista(tipoCreacion);
        cargarTiposExhibidor(tipoCreacion);
        cargarUbicacionExhibidor(tipoCreacion);
    }

    private void cargarTiposExhibidor(boolean precarga) {

        String[] items;
        Vector<String> listaItems = new Vector<String>();
        listaTipoExhibidor = DataBaseBO.obtenerListaTipoExhibidor(listaItems, Main.cliente.canal);

        if (listaItems.size() > 0) {

            items = new String[listaItems.size()];
            listaItems.copyInto(items);

        } else {

            items = new String[] {};

            if (listaTipoExhibidor != null)
                listaTipoExhibidor.removeAllElements();
        }

        spTipoExhibidor = findViewById(R.id.spTipoExhibidor);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipoExhibidor.setAdapter(adapter);

        if(precarga) {

            int pos = 0;

            for(int i = 0; i < listaTipoExhibidor.size(); i++) {

                if((listaTipoExhibidor.elementAt(i).codigoTipo).equals(exhibidor.codTipoExhibidor)) {

                    pos = i;
                }
            }

            if(listaTipoExhibidor.size() > 0) {

                spTipoExhibidor.setSelection(pos);
            }
        }
    }

    private void cargarUbicacionExhibidor(boolean precarga) {

        String[] items;
        Vector<String> listaItems = new Vector<String>();
        listaUbicacionExhibidor = DataBaseBO.obtenerListaUbicacionExhibidor(listaItems);

        if (listaItems.size() > 0) {

            items = new String[listaItems.size()];
            listaItems.copyInto(items);

        } else {

            items = new String[] {};

            if (listaUbicacionExhibidor != null)
                listaUbicacionExhibidor.removeAllElements();
        }

        spUbicacionExhibidor = findViewById(R.id.spUbicacionExhibidor);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUbicacionExhibidor.setAdapter(adapter);

        if(precarga) {

            int pos = 0;

            for(int i = 0; i < listaUbicacionExhibidor.size(); i++) {

                if((listaUbicacionExhibidor.elementAt(i).codigoUbicacion).equals(exhibidor.codUbicacion)) {

                    pos = i;
                }
            }

            if(listaUbicacionExhibidor.size() > 0) {

                spUbicacionExhibidor.setSelection(pos);
            }
        }
    }

    private void setComponentesVista(boolean precarga) {

        etNombreExhibidor = findViewById(R.id.etNombreExhibidor);
        etNombreExhibidor.setTypeface(Const.letraRegular);

        etAnchoExhibidor = findViewById(R.id.etAnchoExhibidor);
        etAnchoExhibidor.setTypeface(Const.letraRegular);

        etAltoExhibidor = findViewById(R.id.etAltoExhibidor);
        etAltoExhibidor.setTypeface(Const.letraRegular);

        tvNombreExhibidor = findViewById(R.id.tvNombreExhibidor);
        tvNombreExhibidor.setTypeface(Const.letraSemibold);

        tvAnchoExhibidor = findViewById(R.id.tvAnchoExhibidor);
        tvAnchoExhibidor.setTypeface(Const.letraSemibold);

        tvAltoExhibidor = findViewById(R.id.tvAltoExhibidor);
        tvAltoExhibidor.setTypeface(Const.letraSemibold);

        tvTipoExhibidor = findViewById(R.id.tvTipoExhibidor);
        tvTipoExhibidor.setTypeface(Const.letraSemibold);

        tvUbicacionExhibidor = findViewById(R.id.tvUbicacionExhibidor);
        tvUbicacionExhibidor.setTypeface(Const.letraSemibold);

        if(precarga) {

            etNombreExhibidor.setText(exhibidor.nombre);
            etAnchoExhibidor.setText(exhibidor.ancho);
            etAltoExhibidor.setText(exhibidor.alto);
        }
    }

    public void on_ClickAgregarExhibidor(View view) {

        //********** SE OBTIENEN LOS DATOS Y SE ALMACENA EL EXHIBIDOR **********//
        //////////////////////////////////////////////////////////////////////////

        // NOMMBRE EXHIBIDOR
        etNombreExhibidor = findViewById(R.id.etNombreExhibidor);
        String nombreExhibidor = etNombreExhibidor.getText().toString();

        // ANCHO EXHIBIDOR
        etAnchoExhibidor = findViewById(R.id.etAnchoExhibidor);
        String anchoExhibidor = etAnchoExhibidor.getText().toString();

        // ALTO EXHIBIDOR
        etAltoExhibidor = findViewById(R.id.etAltoExhibidor);
        String altoExhibidor = etAltoExhibidor.getText().toString();

        String codigoTipoExhibidor = "";
        String codigoUbicacionExhibidor = "";

        if(listaTipoExhibidor.size() > 0) {

            int positionSel = spTipoExhibidor.getSelectedItemPosition();
            codigoTipoExhibidor = listaTipoExhibidor.elementAt(positionSel).codigoTipo;

        } else {

            Toasty.warning(CrearExhibicionActivity.this, "No hay tipo de exhibidor seleccionado.", Toast.LENGTH_LONG).show();
            return;
        }

        if(listaUbicacionExhibidor.size() > 0) {

            int positionSel = spUbicacionExhibidor.getSelectedItemPosition();
            codigoUbicacionExhibidor = listaUbicacionExhibidor.elementAt(positionSel).codigoUbicacion;

        } else {

            Toasty.warning(CrearExhibicionActivity.this, "No hay ubicación de exhibidor seleccionado.", Toast.LENGTH_LONG).show();
            return;
        }

        if(nombreExhibidor.equals("") || anchoExhibidor.equals("") || altoExhibidor.equals("") || codigoTipoExhibidor.equals("") || codigoUbicacionExhibidor.equals("")) {

            //ALERT
            Alert.nutresaShow(CrearExhibicionActivity.this, "INFORMACIÓN", "Debe completar la información del exhibidor.",
                    "OK", null,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            Alert.dialogo.cancel();
                        }

                    }, null);

        } else {

            String codigoCliente = PreferencesCliente.obtenerCodigoClienteSeleccionado(getApplicationContext());
            Usuario usuario = Main.usuario;
            String id = Util.obtenerId(Main.usuario.codigo);
            String fecha = Util.obtenerFechaActual();
            int tipoUsuario = DataBaseBO.obtenerTipoUsuario();

            Exhibidores exhibidorCreado = new Exhibidores();

            exhibidorCreado.nombre                   = nombreExhibidor;
            exhibidorCreado.ancho                    = anchoExhibidor;
            exhibidorCreado.alto                     = altoExhibidor;
            exhibidorCreado.codigoTipoExhibidor      = codigoTipoExhibidor;
            exhibidorCreado.codigoUbicacionExhibidor = codigoUbicacionExhibidor;

            if(tipoCreacion) {

                // ES MODIFICACIÓN
                DataBaseBO.eliminarExhibidor(idExhibidorMod);
                id = idExhibidorMod;
            }

            boolean guardo = DataBaseBO.guardarExhibidorNuevo(codigoCliente, usuario, id, fecha, exhibidorCreado, tipoUsuario,"");

            if(guardo) {

                // SE ENVIA LA RESPUESTA CON EL OBJERO EN JSON STRING
                Intent respuestaCreacionExhibidor = new Intent();
                setResult(Activity.RESULT_OK, respuestaCreacionExhibidor);
                finish();

            } else {

                //ALERT
                Alert.nutresaShow(CrearExhibicionActivity.this, "ERROR", "No se pudo guardar la medición de forma correcta.",
                        "OK", null,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                Alert.dialogo.cancel();
                            }

                        }, null);
            }
        }
    }

    public void on_ClickCancelarExhibidor(View view) {

        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {}
}
