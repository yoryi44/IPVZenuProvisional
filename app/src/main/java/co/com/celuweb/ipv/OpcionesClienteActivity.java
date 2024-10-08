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
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableLinearLayout;

import java.util.Vector;

import businessObject.ConfigBO;
import businessObject.DataBaseBO;
import component.Alert;
import component.ListViewAdapterListaCategoriasCliente;
import component.Progress;
import component.Util;
import config.Const;
import config.Synchronizer;
import dataObject.ActividadesCliente;
import dataObject.Cliente;
import dataObject.Config;
import dataObject.ItemListViewActividadesCliente;
import dataObject.Main;
import sharedPreferences.PreferencesCliente;
import synchronizer.Sync;

public class OpcionesClienteActivity extends AppCompatActivity implements Synchronizer {

    private TextView tvLabelRazonSocial;

    private TextView tvCodigoClienteInfo;
    private TextView tvNitClienteInfo;
    private TextView tvNombreClienteInfo;
    private TextView tvDireccionClienteInfo;
    private TextView tvBarrioClienteInfo;
    private TextView tvCPClienteInfo;
    private TextView tvEmailClienteInfo;
    private TextView tvCiudadClienteInfo;
    private TextView tvTelefonoClienteInfo;
    private TextView tvCanalClienteInfo;
    private TextView tvSubcanalClienteInfo;
    private TextView tvTipologiaClienteInfo;
    private TextView tvSegmentacionClienteInfo;

    private LinearLayout llBotonExpandir;

    private ExpandableLinearLayout expandableLayout;

    private TextView tvLabelCodigo;
    private TextView tvLabelNit;
    private TextView tvLabelNombre;
    private TextView tvLabelDireccion;
    private TextView tvLabelCiudad;
    private TextView tvLabelTelefono;
    private TextView tvLabelBarrio;
    private TextView tvLabelEmail;
    private TextView tvLabelCP;
    private TextView tvLabelCanal;
    private TextView tvLabelSubcanal;
    private TextView tvLabelTipologia;
    private TextView tvLabelSegmentacion;

    private TextView tvLabelGestion;
    private TextView tvGestionClienteInfo;

    boolean estado = false;

    private Vector<ActividadesCliente> listaActividadesCliente;
    private ListView lvListaTareasCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_opciones_cliente);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        setSupportActionBar(toolbar);

        // SE INICIALIZA EL COMPONENTE PRINCIPAL DE LA APLICACION
        Main.contexto = getApplicationContext();

        TextView tvTituloModulo = findViewById(R.id.tvTituloModulo);
        tvTituloModulo.setTypeface(Const.letraSemibold);
        tvTituloModulo.setText("CATEGORIAS VISITA");

        // add back arrow to toolbar
        if (getSupportActionBar() != null){

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        cargarClienteSel();
        cargarDatosCliente();
        cargarListaActividadesCliente();
    }

    private void cargarListaActividadesCliente() {

        lvListaTareasCliente = findViewById(R.id.lvListaTareasCliente);
        final Vector<ItemListViewActividadesCliente> listaItems = new Vector<ItemListViewActividadesCliente>();
        listaActividadesCliente = DataBaseBO.obtenerCategorias(listaItems);

        ItemListViewActividadesCliente[] items;

        if (listaItems.size() > 0) {

            items = new ItemListViewActividadesCliente[listaItems.size()];
            listaItems.copyInto(items);

        } else {

            items = new ItemListViewActividadesCliente[] {};

            if (listaActividadesCliente != null)
                listaActividadesCliente.removeAllElements();
        }

        ListViewAdapterListaCategoriasCliente adapter = new ListViewAdapterListaCategoriasCliente(OpcionesClienteActivity.this, items);
        adapter.notifyDataSetChanged();
        lvListaTareasCliente.setAdapter(adapter);

        setListinerListView();
    }

    @Override
    protected void onResume() {

        cargarListaActividadesCliente();
        ListView lvListaTareasCliente = (ListView)findViewById(R.id.lvListaTareasCliente);
        lvListaTareasCliente.setEnabled(true);
        super.onResume();
    }

    private void setListinerListView() {

        final ListView lvListaTareasCliente = (ListView)findViewById(R.id.lvListaTareasCliente);
        lvListaTareasCliente.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                view.setEnabled(false);

                lvListaTareasCliente.setEnabled(false);

                String codigo = listaActividadesCliente.elementAt(position).codigo;
                Main.idCategoria = codigo;
                Intent exhibicionProductosActivity = new Intent(OpcionesClienteActivity.this, OpcionesTareasActivity.class);
                exhibicionProductosActivity.putExtra("IDCATEGORIA", codigo);
                startActivity(exhibicionProductosActivity);

            }
        });
    }

    private void cargarDatosCliente() {

        tvLabelRazonSocial = (TextView) findViewById(R.id.tvLabelRazonSocial);
        tvLabelRazonSocial.setText(Main.cliente.razonSocial);
        tvCodigoClienteInfo = (TextView) findViewById(R.id.tvCodigoClienteInfo);
        tvCodigoClienteInfo.setText(String.valueOf(Main.cliente.codigo));
        tvNitClienteInfo = (TextView) findViewById(R.id.tvNitClienteInfo);
        tvNitClienteInfo.setText(String.valueOf(Main.cliente.nit));
        tvNombreClienteInfo = (TextView) findViewById(R.id.tvNombreClienteInfo);
        tvNombreClienteInfo.setText(String.valueOf(Main.cliente.nombre));
        tvDireccionClienteInfo = (TextView) findViewById(R.id.tvDireccionClienteInfo);
        tvDireccionClienteInfo.setText(String.valueOf(Main.cliente.direccion));
        tvBarrioClienteInfo = (TextView) findViewById(R.id.tvBarrioClienteInfo);
        tvBarrioClienteInfo.setText(String.valueOf(Main.cliente.barrio));
        tvCPClienteInfo = (TextView) findViewById(R.id.tvCPClienteInfo);
        tvCPClienteInfo.setText(String.valueOf(DataBaseBO.getDescripcionCondicionPago(Main.cliente.condPago)));
        tvEmailClienteInfo = (TextView) findViewById(R.id.tvEmailClienteInfo);
        tvEmailClienteInfo.setText(String.valueOf(Main.cliente.email));
        tvCiudadClienteInfo = (TextView) findViewById(R.id.tvCiudadClienteInfo);
        tvCiudadClienteInfo.setText(String.valueOf(Main.cliente.ciudad));
        tvTelefonoClienteInfo = (TextView) findViewById(R.id.tvTelefonoClienteInfo);
        tvTelefonoClienteInfo.setText(String.valueOf(Main.cliente.telefono));

        tvCanalClienteInfo = (TextView) findViewById(R.id.tvCanalClienteInfo);
        tvCanalClienteInfo.setText(String.valueOf(Main.cliente.canal));
        tvSubcanalClienteInfo = (TextView) findViewById(R.id.tvSubcanalClienteInfo);
        tvSubcanalClienteInfo.setText(String.valueOf(Main.cliente.subCanal));
        tvTipologiaClienteInfo = (TextView) findViewById(R.id.tvTipologiaClienteInfo);
        tvTipologiaClienteInfo.setText(String.valueOf(Main.cliente.actividad));
        tvSegmentacionClienteInfo = (TextView) findViewById(R.id.tvSegmentacionClienteInfo);

        String seg = String.valueOf(Main.cliente.GC4);

        if(seg.equals("null")) {

            seg = "";
        }

        tvSegmentacionClienteInfo.setText(seg);

        tvGestionClienteInfo = (TextView) findViewById(R.id.tvGestionClienteInfo);
        tvGestionClienteInfo.setText(String.valueOf(Main.cliente.cantidadGes));

        expandableLayout = (ExpandableLinearLayout) findViewById(R.id.expandableLayout);
        llBotonExpandir = (LinearLayout) findViewById(R.id.llBotonExpandir);

        final ImageButton btnExpandir = (ImageButton) findViewById(R.id.btnExpandir);
        btnExpandir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(estado) {

                    expandableLayout.collapse();
                    btnExpandir.setImageDrawable(getResources().getDrawable(R.mipmap.botonmostrarmas));
                    estado = false;

                } else {

                    expandableLayout.expand();
                    btnExpandir.setImageDrawable(getResources().getDrawable(R.mipmap.botonmostrarmenos));
                    estado = true;
                }
            }
        });

        setFontComponents();
        DataBaseBO.portafolio_core_3(Main.cliente.vendedorCliente,
                                     Main.cliente.distrito,
                                     Main.cliente.codigo,
                                     Main.cliente.actividad,
                                     Main.cliente.codDane,
                                     Main.cliente.GC2,
                                     Main.cliente.GC3,
                                     Main.cliente.GC4);

        String condPago = Main.cliente.condPago;

        if (condPago.indexOf("C") != -1) {

            condPago = condPago.substring(condPago.indexOf("C") + 1, condPago.length());
        }

        DataBaseBO.descuentosTmp(Main.cliente.distrito,
                                 Main.cliente.codigo,
                                 Main.cliente.canal,
                                 Main.cliente.subCanal,
                                 Main.cliente.actividad,
                                 Main.cliente.grupoPrecio,
                                 Main.cliente.GC2,
                                 Main.cliente.GC3,
                                 Main.cliente.GC4,
                                 condPago);
    }

    private void setFontComponents() {

        // SE INICIALIZA EL COMPONENTE PRINCIPAL DE LA APLICACION
        Main.contexto = getApplicationContext();

        tvLabelCodigo = findViewById(R.id.tvLabelCodigo);
        tvLabelCodigo.setTypeface(Const.letraSemibold);
        tvLabelNit = findViewById(R.id.tvLabelNit);
        tvLabelNit.setTypeface(Const.letraSemibold);
        tvLabelNombre = findViewById(R.id.tvLabelNombre);
        tvLabelNombre.setTypeface(Const.letraSemibold);
        tvLabelRazonSocial = findViewById(R.id.tvLabelRazonSocial);
        tvLabelRazonSocial.setTypeface(Const.letraSemibold);
        tvLabelDireccion = findViewById(R.id.tvLabelDireccion);
        tvLabelDireccion.setTypeface(Const.letraSemibold);
        tvLabelCiudad = findViewById(R.id.tvLabelCiudad);
        tvLabelCiudad.setTypeface(Const.letraSemibold);
        tvLabelTelefono = findViewById(R.id.tvLabelTelefono);
        tvLabelTelefono.setTypeface(Const.letraSemibold);
        tvLabelBarrio = findViewById(R.id.tvLabelBarrio);
        tvLabelBarrio.setTypeface(Const.letraSemibold);
        tvLabelEmail = findViewById(R.id.tvLabelEmail);
        tvLabelEmail.setTypeface(Const.letraSemibold);
        tvLabelCP = findViewById(R.id.tvLabelCP);
        tvLabelCP.setTypeface(Const.letraSemibold);
        tvLabelCanal = findViewById(R.id.tvLabelCanal);
        tvLabelCanal.setTypeface(Const.letraSemibold);
        tvLabelSubcanal = findViewById(R.id.tvLabelSubcanal);
        tvLabelSubcanal.setTypeface(Const.letraSemibold);
        tvLabelTipologia = findViewById(R.id.tvLabelTipologia);
        tvLabelTipologia.setTypeface(Const.letraSemibold);
        tvLabelSegmentacion = findViewById(R.id.tvLabelSegmentacion);
        tvLabelSegmentacion.setTypeface(Const.letraSemibold);
        tvLabelGestion = findViewById(R.id.tvLabelGestion);
        tvLabelGestion.setTypeface(Const.letraSemibold);

        tvCodigoClienteInfo.setTypeface(Const.letraRegular);
        tvNitClienteInfo.setTypeface(Const.letraRegular);
        tvNombreClienteInfo.setTypeface(Const.letraRegular);
        tvDireccionClienteInfo.setTypeface(Const.letraRegular);
        tvCiudadClienteInfo.setTypeface(Const.letraRegular);
        tvTelefonoClienteInfo.setTypeface(Const.letraRegular);
        tvBarrioClienteInfo.setTypeface(Const.letraRegular);
        tvEmailClienteInfo.setTypeface(Const.letraRegular);
        tvCPClienteInfo.setTypeface(Const.letraRegular);
        tvCanalClienteInfo.setTypeface(Const.letraRegular);
        tvSubcanalClienteInfo.setTypeface(Const.letraRegular);
        tvTipologiaClienteInfo.setTypeface(Const.letraRegular);
        tvSegmentacionClienteInfo.setTypeface(Const.letraRegular);
        tvGestionClienteInfo.setTypeface(Const.letraRegular);
    }

    private void cargarClienteSel() {

        if(Main.cliente == null) {

            Cliente cliente = DataBaseBO.obtenerClienteSeleccionado(PreferencesCliente.obtenerCodigoClienteSeleccionado(getApplicationContext()));
            Main.cliente = cliente;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finalizar();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finalizar();
    }

    // valida el tipo de configuracion (uno a uno, bach) y envia informacion
    public void finalizar(){
        // SE CREA LA LOGICA PARA ENVIO DE INFORMACION
        if(Util.isNetworkAvailable(OpcionesClienteActivity.this)) {

            if(DataBaseBO.hayInformacionXEnviar()) {

                Config config = ConfigBO.ObtenerConfigUsuario();
                if (config != null && config.conexion == Const.CONEXION_UNO_UNO) {

                    if(gestionCompleta()){
                        Sync sync = new Sync(OpcionesClienteActivity.this, Const.ENVIAR_INFO);
                        Progress.show(OpcionesClienteActivity.this, "Sincronizador", "Enviando Informacion...", false);
                        sync.start();

                    }else{
                        Alert.nutresaShow(OpcionesClienteActivity.this, "INFORMACIÓN", "Para poder sincronizar información por favor asegúrese de gestionar todas las mediciones de las categorías que haya iniciado",
                                "OK", null,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        Alert.dialogo.cancel();
                                        OpcionesClienteActivity.this.finish();
                                    }

                                }, null);
                    }


                } else {
                    OpcionesClienteActivity.this.finish();
                }

            } else {

                OpcionesClienteActivity.this.finish();
            }




        } else {

            OpcionesClienteActivity.this.finish();
        }
    }


    public void on_ClickSincronizar(View view) {
        // SE CREA LA LOGICA PARA ENVIO DE INFORMACION
        if(Util.isNetworkAvailable(OpcionesClienteActivity.this)) {

            if(DataBaseBO.hayInformacionXEnviar()) {

                if(gestionCompleta()){
                    Sync sync = new Sync(OpcionesClienteActivity.this, Const.ENVIAR_INFO);
                    Progress.show(OpcionesClienteActivity.this, "Sincronizador", "Enviando Informacion...", false);
                    sync.start();
                }else{
                    Alert.nutresaShow(OpcionesClienteActivity.this, "INFORMACIÓN", "Para poder sincronizar información por favor asegúrese de gestionar todas las mediciones de las categorías que haya iniciado",
                            "OK", null,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view)
                                {
                                    Alert.dialogo.cancel();
                                    OpcionesClienteActivity.this.finish();
                                }

                            }, null);
                }


            } else {
                OpcionesClienteActivity.this.finish();
            }
        } else {
            OpcionesClienteActivity.this.finish();
        }
    }

    public boolean gestionCompleta(){

        Vector<ActividadesCliente> listagestionada=DataBaseBO.obtenerListaGestionada( Main.cliente.canal, Main.cliente.codigo, null);
        int cont=0;
        for (ActividadesCliente a: listagestionada){
            if (a.tieneGestion==0){
                cont++;
            }
        }
        if (listagestionada.size()==cont){
            return true;
        }else{
            return false;
        }
    }
    @Override
    public void respSync(boolean ok, String respuestaServer, String msg, int codeRequest) {

        if(codeRequest == Const.ENVIAR_INFO) {

            respuestaEnvioInformacion(ok, respuestaServer, msg);
        }
    }

    private void respuestaEnvioInformacion(boolean ok, String respuestaServer, String msg) {

        Progress.hide();

        if (ok) {

            OpcionesClienteActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    //ALERT
                    Alert.nutresaShow(OpcionesClienteActivity.this, "INFORMACIÓN", "Información enviada de forma correcta.",
                            "OK", null,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view)
                                {
                                    Alert.dialogo.cancel();
                                    OpcionesClienteActivity.this.finish();
                                }

                            }, null);
                }
            });

        } else {

            OpcionesClienteActivity.this.runOnUiThread(new Runnable() {

                public void run() {

                    //ALERT
                    Alert.nutresaShow(OpcionesClienteActivity.this, "ERROR", "No se pudo realizar el envio de información.",
                            "OK", null,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view)
                                {
                                    Alert.dialogo.cancel();
                                    OpcionesClienteActivity.this.finish();
                                }

                            }, null);

                }
            });
        }
    }
}
