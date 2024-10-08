package co.com.celuweb.ipv;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Vector;

import businessObject.DataBaseBO;
import component.Alert;
import component.ListViewAdapterListaExhibidores;
import component.Progress;
import component.Util;
import config.Const;
import config.Synchronizer;
import dataObject.Cliente;
import dataObject.ExhibidorEncabezado;
import dataObject.Exhibidores;
import dataObject.ItemListViewExhibidores;
import dataObject.Main;
import dataObject.Usuario;
import sharedPreferences.PreferencesCliente;
import sharedPreferences.PreferencesExhibidor;

public class ExhibicionProductosActivity extends AppCompatActivity implements Synchronizer {

    private TextView tvLabelRazonSocial;
    private ListView lvListaExhibidores;
    private Vector<Exhibidores> listaExhibidores;
    private ListViewAdapterListaExhibidores adapter;
    private ItemListViewExhibidores[] items;
    private Vector<ItemListViewExhibidores> listaItems;

    private Button btnEditarExhibidor;
    private Button btnTerminarExhibicion;

    // BANDERAS PARA CONTROLAR EL ESTADO DE LOS BOTONES
    private int estadoEditar = -1;
    private int estadoTerminar = -1;

    // BROADCAST PARA ACCIONES DE LA LISTA
    private IntentFilter filterModificar;
    private IntentFilter filterEliminar;

    private final static String TAG = ExhibicionProductosActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_exhibicion_productos);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        setSupportActionBar(toolbar);

        // SE INICIALIZA EL COMPONENTE PRINCIPAL DE LA APLICACION
        Main.contexto = getApplicationContext();

        TextView tvTituloModulo = findViewById(R.id.tvTituloModulo);
        tvTituloModulo.setTypeface(Const.letraSemibold);
        tvTituloModulo.setText("EXHIBICIÓN DE PRODUCTOS");

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        cargarClienteSel();
        setComponentesVista();
        cargarListaExhibidores(false);
        setListinerListView();
    }

    private void cargarListaExhibidores(boolean recarga) {

        lvListaExhibidores = findViewById(R.id.lvListaExhibidores);

        listaItems = new Vector<>();
        listaExhibidores = DataBaseBO.obtenerListaExhibidores(listaItems, Main.cliente.codigo);

        if (listaItems.size() > 0) {

            items = new ItemListViewExhibidores[listaItems.size()];
            listaItems.copyInto(items);

        } else {

            items = new ItemListViewExhibidores[] {};

            if (listaExhibidores != null)
                listaExhibidores.removeAllElements();
        }

        adapter = new ListViewAdapterListaExhibidores(ExhibicionProductosActivity.this, items, listaExhibidores, recarga);
        adapter.notifyDataSetChanged();
        lvListaExhibidores.setAdapter(adapter);
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

        filterModificar = new IntentFilter();
        filterModificar.addAction("MODIFICAREXHIBIDOR");

        filterEliminar = new IntentFilter();
        filterEliminar.addAction("ELIMINAREXHIBIDOR");

        tvLabelRazonSocial = findViewById(R.id.tvLabelRazonSocial);
        tvLabelRazonSocial.setTypeface(Const.letraSemibold);
    }

    public void on_ClickEditarAgregarExhibidor(View view) {

        if(estadoEditar <= 0) {

            estadoEditar = 1;
            estadoTerminar = 1;

            // SE LEE LA LISTA DE NUEVO PERO SE MODIFICAN LOS ICONOS POR ITEM
            cargarListaExhibidores(true);

            btnEditarExhibidor    = (Button) findViewById(R.id.btnEditarExhibidor);
            btnEditarExhibidor.setText("AGREGAR EXHI");

            btnTerminarExhibicion = (Button) findViewById(R.id.btnTerminarExhibicion);
            btnTerminarExhibicion.setText("TERMINAR EDI");

        } else {

            // SE MUESTRA LA VISTA DE CREACION DE NUEVO EXHIBIDOR
            Intent crearNuevoExhibidor = new Intent(ExhibicionProductosActivity.this, CrearExhibicionActivity.class);
            crearNuevoExhibidor.putExtra("TIPO", false);
            startActivityForResult(crearNuevoExhibidor, Const.AGREGAR_EXHIBIDOR);
        }
    }

    public void on_ClickTerminarTerminarEdicion(View view) {

        if(estadoTerminar <= 0) {

            // TERMINAR LA MEDICION CON LOS EXHIBIDORES CREADOS
            boolean hayInformacionPorEnviarExhibidores = DataBaseBO.hayInformacionXEnviarExhibidores();

            if(hayInformacionPorEnviarExhibidores) {

                finish();
//              Sync sync = new Sync(ExhibicionProductosActivity.this, Const.ENVIAR_INFO);
//              Progress.show(ExhibicionProductosActivity.this, "Sincronizador", "Enviando Informacion...", false);
//              sync.start();

            } else {

                ExhibicionProductosActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        //ALERT
                        Alert.nutresaShow(ExhibicionProductosActivity.this, "INFORMACIÓN", "No hay información de la medición por enviar.",
                                "OK",
                                null,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        Alert.dialogo.cancel();
                                    }

                                }, null);
                    }
                });
            }

        } else {

            // REGRESAR AL ESTADO DE LISTA SIN MODIFICACION
            estadoEditar = 0;
            estadoTerminar = 0;

            // SE LEE LA LISTA DE NUEVO PERO SE MODIFICAN LOS ICONOS POR ITEM
            cargarListaExhibidores(false);

            btnEditarExhibidor    = (Button) findViewById(R.id.btnEditarExhibidor);
            btnEditarExhibidor.setText("EDITAR");

            btnTerminarExhibicion = (Button) findViewById(R.id.btnTerminarExhibicion);
            btnTerminarExhibicion.setText("TERMINAR");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == Const.AGREGAR_EXHIBIDOR) {

            if(resultCode == Activity.RESULT_OK) {

                cargarListaExhibidores(true);
            }
        }else if(requestCode == Const.EXHIBIDOR) {

            if(resultCode == Activity.RESULT_OK) {

                setResult(Activity.RESULT_OK);
                finish();
            }
        }
    }

    @Override
    public void respSync(boolean ok, String respuestaServer, String msg, int codeRequest) {

        try {

            Progress.hide();

            if (ok) {

                ExhibicionProductosActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //ALERT
                        Alert.nutresaShow(ExhibicionProductosActivity.this, "INFORMACIÓN", "Información enviada de forma correcta.",
                                "OK", null,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        Alert.dialogo.cancel();
                                        finish();
                                    }

                                }, null);
                    }
                });

            } else {

                ExhibicionProductosActivity.this.runOnUiThread(new Runnable() {

                    public void run() {

                        //ALERT
                        Alert.nutresaShow(ExhibicionProductosActivity.this, "ALERTA", "No se pudo realizar el envio de información.",
                                "OK", null,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        Alert.dialogo.cancel();
                                        finish();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        ExhibicionProductosActivity.this.finish();
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals("MODIFICAREXHIBIDOR")) {

                String idExhibidor  = intent.getExtras().getString("IDEXHIBIDOR");

                // SE MUESTRA LA VISTA DE CREACION DE NUEVO EXHIBIDOR
                Intent crearNuevoExhibidor = new Intent(ExhibicionProductosActivity.this, CrearExhibicionActivity.class);
                crearNuevoExhibidor.putExtra("IDEXHIBIDOR", idExhibidor);
                crearNuevoExhibidor.putExtra("TIPO", true);
                startActivityForResult(crearNuevoExhibidor, Const.AGREGAR_EXHIBIDOR);

            } else if(intent.getAction().equals("ELIMINAREXHIBIDOR")) {

                // SI LA POSICION DEL ELEMENTO A ELIMINAR ES 0 Y EL TAMAÑO DE LA LISTA DE
                // EXHIBIDORES ES 1, SE ELIMINA EL ELEMENTO Y SE DEJA LA LISTA EN EL ESTADO INICIAL
                String idExhibidor  = intent.getExtras().getString("IDEXHIBIDOR");
                String posExhibidor = intent.getExtras().getString("POSEXHIBIDOR");

                // SE ELIMINA EL ELEMENTO DE LA VISTA
                Usuario usuario = DataBaseBO.obtenerUsuario();
                DataBaseBO.eliminarExhibidorCompleto(idExhibidor, Main.cliente.codigo, Util.obtenerFechaActual(), usuario.codigo);

                if(posExhibidor.equals("0")) {

                    if(listaExhibidores.size() == 1) {

                        cargarListaExhibidores(false);

                        // SE DEJAN LOS BOTONES EN ESTADO INICIAL Y EL TITULO INICIAL
                        // REGRESAR AL ESTADO DE LISTA SIN MODIFICACION
                        estadoEditar = 0;
                        estadoTerminar = 0;

                        // SE LEE LA LISTA DE NUEVO PERO SE MODIFICAN LOS ICONOS POR ITEM
                        cargarListaExhibidores(false);

                        btnEditarExhibidor    = (Button) findViewById(R.id.btnEditarExhibidor);
                        btnEditarExhibidor.setText("EDITAR");

                        btnTerminarExhibicion = (Button) findViewById(R.id.btnTerminarExhibicion);
                        btnTerminarExhibicion.setText("TERMINAR");

                    } else {

                        cargarListaExhibidores(true);
                    }

                } else {

                    cargarListaExhibidores(true);
                }
            }
        }
    };

    @Override
    protected void onPause() {

        unregisterReceiver(myReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {

        super.onResume();
        cargarListaExhibidores(false);
        registerReceiver(myReceiver, filterModificar);
        registerReceiver(myReceiver, filterEliminar);
    }

    private void setListinerListView() {

        final ListView lvListaExhibidores = findViewById(R.id.lvListaExhibidores);

        lvListaExhibidores.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String idExhibidorSel = listaExhibidores.elementAt(position).id;
                PreferencesExhibidor.guardarIdExhibidor(getApplicationContext(), idExhibidorSel);

                int exhibidorRegistroHoy = listaExhibidores.elementAt(position).registroHoy;

                // SE AGREGA LA LOGICA PARA DETERMINAR SI TIENE PRECARGA DE DATOS DEL DIA ACTUAL O DE DIAS
                // ANTERIORES, DE LO CONTRARIO SE CONTINUA CON EL MODULO DE FOTOS PARA EL EXHIBIDOR ACTUAL.
                boolean entraFotosPrecarga = DataBaseBO.obtenerInformacionPrecargaFotosExhibidor(idExhibidorSel, exhibidorRegistroHoy);

                // SE DETERMINA SI TIENE PRECARGA DE DATOS PARA LA TEMPERATURA DEL DIA ACTUAL O DE DIAS
                // ANTERIORES, DE LO CONTRARIO SE CONTINUA CON EL MODULO DE TEMPERATURA PARA EL EXHIBIDOR ACTUAL
                boolean entraTemperaturaPrecarga = DataBaseBO.obtenerInformacionPrecargaTemperaturaExhibidor(idExhibidorSel, exhibidorRegistroHoy);

                if(entraFotosPrecarga && entraTemperaturaPrecarga) {

//                    Intent moduloFotosExhibidor = new Intent(ExhibicionProductosActivity.this, FotosActivity.class);
//                    moduloFotosExhibidor.putExtra("IDEXHIBIDOR", idExhibidorSel);
//                    moduloFotosExhibidor.putExtra("EXHIBIDORREGISTROHOY", exhibidorRegistroHoy);
//                    startActivity(moduloFotosExhibidor);

                    // 1. SE OBTIENE EL REGISTO DEL ENCABEZADO ACTUAL Y SE INSERTA DE NUEVO EN TEMP
                    ExhibidorEncabezado exhibidorEncabezadoHistorico = DataBaseBO.obtenerExhibidorEncabezado_ACTUAL_NOTEMPERATURA(idExhibidorSel);
                    exhibidorEncabezadoHistorico.fechaMovil = Util.obtenerFechaActual();

                    // SE ELIMINA EL ENCABEZADO EXISTENTE PARA REEMPLAZRLO POR EL MAS ACTUALIZADO
                    DataBaseBO.eliminarExhibidorEncabeza(idExhibidorSel);

                    DataBaseBO.actualizarMedicionExhibidorActual_Temperatura(0, idExhibidorSel, "", exhibidorEncabezadoHistorico,"");

                    // SE CARGA DIRECTAMENTE EL MODULO DE PRODUCTOS PARA LA MEDICION
                    Intent moduloProductosExhibidor = new Intent(ExhibicionProductosActivity.this, ProductosExhibidorActivity.class);
                    moduloProductosExhibidor.putExtra("IDEXHIBIDOR", idExhibidorSel);
                    moduloProductosExhibidor.putExtra("EXHIBIDORREGISTROHOY", exhibidorRegistroHoy);
                    startActivityForResult(moduloProductosExhibidor,Const.EXHIBIDOR);

                } else {

                    // SE DETERMINA EL CANAL DEL CLIENTE PARA SABER SI DEBE O NO MEDIR TEMPERATURA
                    boolean debeTenerMedicionTemperatura = DataBaseBO.debeTenerMedicionTemperatura(Main.cliente.canal);

                    if(debeTenerMedicionTemperatura) {

                        if(entraTemperaturaPrecarga) {

                            Intent moduloTemperaturaExhibidor = new Intent(ExhibicionProductosActivity.this, TemperaturaActivity.class);
                            moduloTemperaturaExhibidor.putExtra("IDEXHIBIDOR", idExhibidorSel);
                            moduloTemperaturaExhibidor.putExtra("EXHIBIDORREGISTROHOY", exhibidorRegistroHoy);
                            startActivity(moduloTemperaturaExhibidor);

                        } else {

                            // SE CARGA DIRECTAMENTE EL MODULO DE PRODUCTOS PARA LA MEDICION
                            Intent moduloProductosExhibidor = new Intent(ExhibicionProductosActivity.this, ProductosExhibidorActivity.class);
                            moduloProductosExhibidor.putExtra("IDEXHIBIDOR", idExhibidorSel);
                            moduloProductosExhibidor.putExtra("EXHIBIDORREGISTROHOY", exhibidorRegistroHoy);
                            startActivity(moduloProductosExhibidor);
                        }

                    } else {

                        // SE CARGA DIRECTAMENTE EL MODULO DE PRODUCTOS PARA LA MEDICION
                        Intent moduloProductosExhibidor = new Intent(ExhibicionProductosActivity.this, ProductosExhibidorActivity.class);
                        moduloProductosExhibidor.putExtra("IDEXHIBIDOR", idExhibidorSel);
                        moduloProductosExhibidor.putExtra("EXHIBIDORREGISTROHOY", exhibidorRegistroHoy);
                        startActivity(moduloProductosExhibidor);
                    }
                }
            }
        });
    }
}
