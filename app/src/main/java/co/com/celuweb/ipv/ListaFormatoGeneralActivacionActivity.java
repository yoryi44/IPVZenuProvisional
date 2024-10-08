package co.com.celuweb.ipv;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Vector;

import businessObject.DataBaseBO;
import component.Alert;
import component.ListViewAdapterListaComponentesActivacion;
import config.Const;
import dataObject.Cliente;
import dataObject.ComponenteActivacion;
import dataObject.ItemListViewComponenteActivacion;
import dataObject.Main;
import sharedPreferences.PreferencesCliente;
import sharedPreferences.PreferencesObjetoActivacion;
import sharedPreferences.PreferencesOpcionSelOtra;

public class ListaFormatoGeneralActivacionActivity extends AppCompatActivity {

    private String opcionTitulo;
    private int opcionActualLogica;
    private int opcionTipoSel;
    private TextView tvLabelRazonSocial;

    private String idMedicion;

    private ImageView ivImagenTipoSeleccionado;
    private TextView tvTituloTipoSeleccionado;
    private ListView lvListaGeneralActivacion;
    private Vector<ItemListViewComponenteActivacion> listaItems;
    private Vector<ComponenteActivacion> listaComponentesActivacion;
    private ItemListViewComponenteActivacion[] items;
    private ListViewAdapterListaComponentesActivacion adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_lista_formato_general_activacion);

        opcionTitulo = getIntent().getExtras().getString("OPCIONTITULO");
        opcionActualLogica = getIntent().getExtras().getInt("OPCIONACTUALLOGICA");
        opcionTipoSel = getIntent().getExtras().getInt("OPCIONTIPOSEL");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        setSupportActionBar(toolbar);

        // SE INICIALIZA EL COMPONENTE PRINCIPAL DE LA APLICACION
        Main.contexto = getApplicationContext();

        TextView tvTituloModulo = findViewById(R.id.tvTituloModulo);
        tvTituloModulo.setTypeface(Const.letraSemibold);
        tvTituloModulo.setText("ACTIVACIÓN COMERCIAL " + opcionTitulo);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        cargarClienteSel();
        setComponentesVista();
        cargarListaTipoSeleccionado(false);
        setListinerListView();
    }

    private void cargarListaTipoSeleccionado(boolean recarga) {

        lvListaGeneralActivacion = findViewById(R.id.lvListaGeneralActivacion);

        if(!recarga) {

            listaItems = new Vector<>();
            listaComponentesActivacion = DataBaseBO.obtenerListaComponenteActivacion(listaItems, Main.cliente.codigo, opcionActualLogica, opcionTipoSel);
        }

        if (listaItems.size() > 0) {

            items = new ItemListViewComponenteActivacion[listaItems.size()];
            listaItems.copyInto(items);

        } else {

            items = new ItemListViewComponenteActivacion[] {};

            if (listaComponentesActivacion != null)
                listaComponentesActivacion.removeAllElements();
        }

        adapter = new ListViewAdapterListaComponentesActivacion(ListaFormatoGeneralActivacionActivity.this, items);
        lvListaGeneralActivacion.setAdapter(adapter);
    }

    private void setComponentesVista() {

        ivImagenTipoSeleccionado = findViewById(R.id.ivImagenTipoSeleccionado);

        tvTituloTipoSeleccionado = findViewById(R.id.tvTituloTipoSeleccionado);
        tvTituloTipoSeleccionado.setTypeface(Const.letraSemibold);
        tvTituloTipoSeleccionado.setText("SELECCIONE EL TIPO DE " + ((opcionTipoSel <= 2) ? "PROMOCIÓN" : "MATERIAL POP"));
    }

    private void cargarClienteSel() {

        if(Main.cliente == null) {

            Cliente cliente = DataBaseBO.obtenerClienteSeleccionado(PreferencesCliente.obtenerCodigoClienteSeleccionado(getApplicationContext()));
            Main.cliente = cliente;
        }

        tvLabelRazonSocial = findViewById(R.id.tvLabelRazonSocial);
        tvLabelRazonSocial.setText(String.valueOf(Main.cliente.razonSocial));

        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            if (extras.containsKey("IDMEDICION"))
                idMedicion = extras.getString("IDMEDICION");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            //ALERT
            Alert.nutresaShow(ListaFormatoGeneralActivacionActivity.this, "ALERTA", "Toda la información de la medición actual sera eliminada, desea continuar.",
                    "ACEPTAR",
                    "CANCELAR",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Alert.dialogo.cancel();
                            DataBaseBO.eliminarFotosMedicionActual(idMedicion);

                            // LIMIAR CONTENEDORES LOGICA
                            PreferencesObjetoActivacion.vaciarPreferencesObjetoActivacion(ListaFormatoGeneralActivacionActivity.this);

                            finish();
                        }

                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Alert.dialogo.cancel();
                        }
                    });
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {}

    private void setListinerListView() {

        final ListView lvListaGeneralActivacion = findViewById(R.id.lvListaGeneralActivacion);
        lvListaGeneralActivacion.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                for(int i = 0; i < listaItems.size(); i++) {

                    listaItems.elementAt(i).seleccionado = 0;
                    listaComponentesActivacion.elementAt(i).seleccionado = 0;
                }

                listaItems.elementAt(position).seleccionado = 1;
                listaComponentesActivacion.elementAt(position).seleccionado = 1;
                cargarListaTipoSeleccionado(true);
            }
        });
    }

    public void on_ClickContinuarOpcionGeneral(View view) {

        boolean hayComponenteSeleccionado = false;
        String codigoMedicion = "";
        String nombreComponenteSeleccionado = "";

        for(int i = 0; i < listaComponentesActivacion.size(); i++) {

            if(listaComponentesActivacion.elementAt(i).seleccionado == 1) {

                hayComponenteSeleccionado = true;
                codigoMedicion = listaComponentesActivacion.elementAt(i).codigo;

                if((listaComponentesActivacion.elementAt(i).descripcion).equals("Otra")) {

                    nombreComponenteSeleccionado = listaComponentesActivacion.elementAt(i).descripcion + " - " + PreferencesOpcionSelOtra.obtenerObservacion(this);
                    PreferencesOpcionSelOtra.vaciarPreferencesObservacion(this);

                } else {

                    nombreComponenteSeleccionado = listaComponentesActivacion.elementAt(i).descripcion;
                }

                break;
            }
        }

        if(hayComponenteSeleccionado) {

            Intent productosFormatoGeneralActivacionActivity = new Intent(ListaFormatoGeneralActivacionActivity.this, ProductosFormatoGeneralActivacionActivity.class);
            productosFormatoGeneralActivacionActivity.putExtra("OPCIONACTUALLOGICA", opcionActualLogica);
            productosFormatoGeneralActivacionActivity.putExtra("OPCIONTIPOSEL", opcionTipoSel);

            if(opcionActualLogica == 1) {

                productosFormatoGeneralActivacionActivity.putExtra("OPCIONTITULO", "PROPIA");

            } else if(opcionActualLogica == 10) {

                productosFormatoGeneralActivacionActivity.putExtra("OPCIONTITULO", "COMPETENCIA");
            }

            // IDMEDICION PARA LA MEDICION GENERAL
            productosFormatoGeneralActivacionActivity.putExtra("IDMEDICION", idMedicion);

            // CODIGO COMPONENTE SELECCIONADO
            productosFormatoGeneralActivacionActivity.putExtra("CODIGOCOMPONENTESELECCIONADO", codigoMedicion);

            // NOMBRE COMPONENTE SELECCIONADO
            productosFormatoGeneralActivacionActivity.putExtra("NOMBRECOMPONENTESELECCIONADO", nombreComponenteSeleccionado);

            startActivityForResult(productosFormatoGeneralActivacionActivity, Const.RESP_TERMINARACTIVACION);

        } else {

            //ALERT
            Alert.nutresaShow(ListaFormatoGeneralActivacionActivity.this, "INFORMACIÓN", "No ha seleccionado elemento en la lista.",
                    "ACEPTAR",
                    null,
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

        if(requestCode == Const.RESP_TERMINARACTIVACION) {

            if (resultCode == RESULT_OK) {

                finish();
            }
        }
    }
}
