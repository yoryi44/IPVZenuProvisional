package co.com.celuweb.ipv;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Vector;

import businessObject.DataBaseBO;
import component.Alert;
import component.Util;
import config.Const;
import dataObject.Cliente;
import dataObject.Exhibidores;
import dataObject.Main;
import dataObject.Usuario;
import sharedPreferences.PreferencesCliente;

public class AgregarAdicionalActivity extends AppCompatActivity {


    private TextView tvLabelRazonSocial;
    private Spinner spinnerTipo;
    private Spinner spinnerEstado;
    String idExhibidor;
    String posicion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_agregar_adicional);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        setSupportActionBar(toolbar);

        // SE INICIALIZA EL COMPONENTE PRINCIPAL DE LA APLICACION
        Main.contexto = getApplicationContext();

        TextView tvTituloModulo = findViewById(R.id.tvTituloModulo);
        tvTituloModulo.setTypeface(Const.letraSemibold);
        tvTituloModulo.setText("EXHIBICIONES ADICIONALES");

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        cargarClienteSel();
        setComponentesVista();
        cargarTipoExhibidor();
        cargarEstadoExhibidor();
        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            if (extras.containsKey("IDEXHIBIDOR"))
                idExhibidor = extras.getString("IDEXHIBIDOR");

            if (extras.containsKey("POSICION"))
                posicion = extras.getString("POSICION");
        }
        // SE CAMBIA LA VISTA PARA QUE TENGA LOGICA DE TAB

    }

    private void cargarClienteSel() {

        if(Main.cliente == null) {

            Cliente cliente = DataBaseBO.obtenerClienteSeleccionado(PreferencesCliente.obtenerCodigoClienteSeleccionado(getApplicationContext()));
            Main.cliente = cliente;
        }

        tvLabelRazonSocial = findViewById(R.id.tvLabelRazonSocial);
        tvLabelRazonSocial.setText(String.valueOf(Main.cliente.razonSocial));
    }
    private void setComponentesVista() {

        tvLabelRazonSocial = findViewById(R.id.tvLabelRazonSocial);
        tvLabelRazonSocial.setTypeface(Const.letraSemibold);
    }


    private void cargarTipoExhibidor(){

        spinnerTipo = findViewById(R.id.spTipoExhibidor);
        Vector<Exhibidores> listaGramos  = DataBaseBO.obtenerTipoExhibidores();

        ArrayAdapter<Exhibidores> adapter = new ArrayAdapter<Exhibidores>(AgregarAdicionalActivity.this,android.R.layout.simple_spinner_item,listaGramos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adapter);
    }

    private void cargarEstadoExhibidor(){

        spinnerEstado = findViewById(R.id.spEstado);
        Vector<Exhibidores> listaGramos  = DataBaseBO.obtenerEstadosExhibidores();

        ArrayAdapter<Exhibidores> adapter = new ArrayAdapter<Exhibidores>(AgregarAdicionalActivity.this,android.R.layout.simple_spinner_item,listaGramos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapter);
    }

    private void terminarMedicion() {

        Usuario usuario = Main.usuario;
        String id = Util.obtenerId(Main.usuario.codigo);
        String fecha = Util.obtenerFechaActual();

        int index =  spinnerTipo.getSelectedItemPosition();
        Exhibidores exhibidores = (Exhibidores) spinnerTipo.getAdapter().getItem(index);

        int index2=  spinnerEstado.getSelectedItemPosition();
        Exhibidores estado = (Exhibidores) spinnerEstado.getAdapter().getItem(index2);




        boolean guardo = DataBaseBO.guardarDetalleExhibidor(idExhibidor, id,usuario,exhibidores,estado, fecha);
//        boolean guardo = true;
        if(guardo) {

            Alert.nutresaShow(AgregarAdicionalActivity.this, "INFORMACÓN", "La informacion se almaceno de forma exitosa.",
                    "OK", null,
                    new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            Alert.dialogo.cancel();
                            finish();
//                            Sync sync = new Sync(PrecioProductosActivity.this, Const.ENVIAR_INFO);
//                            Progress.show(PrecioProductosActivity.this, "Sincronizador", "Enviando Informacion...", false);
//                            sync.start();
                        }

                    }, null);

        } else {

            //ALERT
            Alert.nutresaShow(AgregarAdicionalActivity.this, "ERROR", "No se pudo guardar la medición de forma correcta.",
                    "OK", null,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Alert.dialogo.cancel();
                        }

                    }, null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Const.FOTO) {

        }
    }

    public void on_ClickGuardar(View view) {
        terminarMedicion();
    }

    public void on_ClickCancelar(View view) {
        finish();
    }

    public void on_ClickFoto(View view) {
        Intent moduloFotosExhibidor = new Intent(AgregarAdicionalActivity.this, FotosActivity.class);
        moduloFotosExhibidor.putExtra("IDEXHIBIDOR", idExhibidor);
        moduloFotosExhibidor.putExtra("EXHIBIDORREGISTROHOY", 0 );
        moduloFotosExhibidor.putExtra("ACTIVACION", true);
        moduloFotosExhibidor.putExtra("POSICION", posicion);
        moduloFotosExhibidor.putExtra("COMPETENCIA", false);
        startActivityForResult(moduloFotosExhibidor, Const.FOTO);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            finish();
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {

        AgregarAdicionalActivity.this.finish();
    }
}
