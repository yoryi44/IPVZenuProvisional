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
import android.widget.ListView;
import android.widget.TextView;

import java.util.Vector;

import businessObject.DataBaseBO;
import component.Alert;
import component.ListViewAdapterListaCategoriasCliente;
import component.Util;
import config.Const;
import dataObject.AdicionalesExhibidor;
import dataObject.Cliente;
import dataObject.ItemListViewActividadesCliente;
import dataObject.Main;
import dataObject.Usuario;
import sharedPreferences.PreferencesCliente;

public class ExhibicionesAdicionalesActivity extends AppCompatActivity {


    private TextView tvLabelRazonSocial;
    String idExhibidor;
    String posicion;
    private ListView lvListaAdicionales;
    private Vector<AdicionalesExhibidor> listaAdicionales;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_exhibiciones_adicionales);

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
        cargarListaAdicionales();
        // SE CAMBIA LA VISTA PARA QUE TENGA LOGICA DE TAB
        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            if (extras.containsKey("IDEXHIBIDOR"))
                idExhibidor = extras.getString("IDEXHIBIDOR");

            if (extras.containsKey("POSICION"))
                posicion = extras.getString("POSICION");
        }
    }

    @Override
    protected void onResume() {

        cargarListaAdicionales();
        super.onResume();
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



    private void cargarListaAdicionales() {

        lvListaAdicionales = findViewById(R.id.lvListaAdicionales);
        final Vector<ItemListViewActividadesCliente> listaItems = new Vector<ItemListViewActividadesCliente>();
        listaAdicionales = DataBaseBO.obtenerListaAdicionales(listaItems);

        ItemListViewActividadesCliente[] items;

        if (listaItems.size() > 0) {

            items = new ItemListViewActividadesCliente[listaItems.size()];
            listaItems.copyInto(items);

        } else {

            items = new ItemListViewActividadesCliente[] {};

            if (listaAdicionales != null)
                listaAdicionales.removeAllElements();
        }

        ListViewAdapterListaCategoriasCliente adapter = new ListViewAdapterListaCategoriasCliente(ExhibicionesAdicionalesActivity.this, items);
        adapter.notifyDataSetChanged();
        lvListaAdicionales.setAdapter(adapter);

//        setListinerListView();
    }







    private void terminarMedicion() {

        String codigoCliente = PreferencesCliente.obtenerCodigoClienteSeleccionado(getApplicationContext());
        Usuario usuario = Main.usuario;
        int tipoUsuario = DataBaseBO.obtenerTipoUsuario();
        String id = Util.obtenerId(Main.usuario.codigo);
        String fecha = Util.obtenerFechaActual();





//        boolean guardo = DataBaseBO.guardarActivaciones(codigoCliente, usuario, tipoUsuario, id, fecha, listaModulosPropios, listaActivacionesCompetencia);
        boolean guardo = true;
        if(guardo) {

            Alert.nutresaShow(ExhibicionesAdicionalesActivity.this, "INFORMACÓN", "La informacion se almaceno de forma exitosa.",
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
            Alert.nutresaShow(ExhibicionesAdicionalesActivity.this, "ERROR", "No se pudo guardar la medición de forma correcta.",
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

    public void on_ClickAgregar(View view) {
        Intent moduloFotosExhibidor = new Intent(ExhibicionesAdicionalesActivity.this, AgregarAdicionalActivity.class);
        moduloFotosExhibidor.putExtra("IDEXHIBIDOR",idExhibidor);
        moduloFotosExhibidor.putExtra("POSICION",posicion);
        startActivity(moduloFotosExhibidor);
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

        ExhibicionesAdicionalesActivity.this.finish();
    }
}
