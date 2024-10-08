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
import component.ListViewAdapterListaActividadesCliente;
import component.Progress;
import component.Util;
import config.Const;
import config.Synchronizer;
import dataObject.ActividadesCliente;
import dataObject.Cliente;
import dataObject.Config;
import dataObject.Exhibidores;
import dataObject.ItemListViewActividadesCliente;
import dataObject.Main;
import sharedPreferences.PreferencesCliente;
import sharedPreferences.PreferencesObjetoActivacion;
import synchronizer.Sync;

public class OpcionesTareasActivity extends AppCompatActivity implements Synchronizer {

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
    String idCategoria = "";

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
        tvTituloModulo.setText("TAREAS VISITA");

        // add back arrow to toolbar
        if (getSupportActionBar() != null){

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            if (extras.containsKey("IDCATEGORIA"))
                idCategoria = extras.getString("IDCATEGORIA");

        }

        cargarClienteSel();
        cargarDatosCliente();
        cargarListaActividadesCliente();
    }

    private void cargarListaActividadesCliente() {

        lvListaTareasCliente = findViewById(R.id.lvListaTareasCliente);
        final Vector<ItemListViewActividadesCliente> listaItems = new Vector<ItemListViewActividadesCliente>();
        listaActividadesCliente = DataBaseBO.obtenerListaActividadesCliente(listaItems, Main.cliente.canal, Main.cliente.codigo, idCategoria);

        ItemListViewActividadesCliente[] items;

        if (listaItems.size() > 0) {

            items = new ItemListViewActividadesCliente[listaItems.size()];
            listaItems.copyInto(items);

        } else {

            items = new ItemListViewActividadesCliente[] {};

            if (listaActividadesCliente != null)
                listaActividadesCliente.removeAllElements();
        }

        ListViewAdapterListaActividadesCliente adapter = new ListViewAdapterListaActividadesCliente(OpcionesTareasActivity.this, items);
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

                lvListaTareasCliente.setEnabled(false);

                String nombreTarea = listaActividadesCliente.elementAt(position).nombreTarea;

                switch (nombreTarea) {

                    case "DISTRIBUCIÓN Y AGOTADOS":

                        Intent agotadosActivity = new Intent(OpcionesTareasActivity.this, AgotadosActivity.class);
                        startActivity(agotadosActivity);
                        break;

                    case "EXHIBICIÓN DE PRODUCTOS":

                        Exhibidores exhibidores = DataBaseBO.obtenerExhibidorMarca(Main.cliente.codigo);
                        // SE CARGA DIRECTAMENTE EL MODULO DE PRODUCTOS PARA LA MEDICION
                        Intent moduloProductosExhibidor = new Intent(OpcionesTareasActivity.this, ProductosExhibidorActivity.class);
                        moduloProductosExhibidor.putExtra("IDEXHIBIDOR", exhibidores.id);
                        moduloProductosExhibidor.putExtra("IDCATEGORIA",idCategoria);
                        moduloProductosExhibidor.putExtra("EXHIBIDORREGISTROHOY", exhibidores.registroHoy);
                        startActivityForResult(moduloProductosExhibidor,Const.EXHIBIDOR);
                        break;

                    case "PRECIO DE PRODUCTOS":

                        // SE AGREGA LA LOGICA DE PRECIOS
                        DataBaseBO.organizarTmpProductos();

                        Intent precioProductosActivity = new Intent(OpcionesTareasActivity.this, PrecioProductosActivity.class);
                        precioProductosActivity.putExtra("IDCATEGORIA",idCategoria);
                        startActivity(precioProductosActivity);
                        break;

                    case "ACTIVACIÓN COMERCIAL PROPIA":

                        // LIMIAR CONTENEDORES LOGICA
                        PreferencesObjetoActivacion.vaciarPreferencesObjetoActivacion(OpcionesTareasActivity.this);

                        Intent activacionComecialPActivity = new Intent(OpcionesTareasActivity.this, PreguntaFormatoGeneralActivacionActivity.class);
                        activacionComecialPActivity.putExtra("OPCIONTITULO", "PROPIA");

                        // PROPIO ES 1
                        activacionComecialPActivity.putExtra("OPCIONACTUALLOGICA", 1);

                        // PREGUNTA ES 1 - PRODUCTOS PROMOCION + CODIGO PROMOCION ES 2 -
                        // PREGUNTA ES 3 - MATERIAL POP + CODIGO PROMOCION ES 4
                        // PREGUNTA ES 5
                        activacionComecialPActivity.putExtra("OPCIONTIPOSEL",1);
                        startActivity(activacionComecialPActivity);
                        break;

                    case "ACTIVACIÓN COMERCIAL COMPETENCIA":

                        // LIMIAR CONTENEDORES LOGICA
                        PreferencesObjetoActivacion.vaciarPreferencesObjetoActivacion(OpcionesTareasActivity.this);

                        Intent activacionComecialCActivity = new Intent(OpcionesTareasActivity.this,PreguntaFormatoGeneralActivacionActivity.class);
                        activacionComecialCActivity.putExtra("OPCIONTITULO", "COMPETENCIA");

                        // COMPETENCIA ES 10
                        activacionComecialCActivity.putExtra("OPCIONACTUALLOGICA", 10);

                        // PREGUNTA ES 1 - PRODUCTOS PROMOCION + CODIGO PROMOCION ES 2 -
                        // PREGUNTA ES 3 - MATERIAL POP + CODIGO PROMOCION ES 4
                        // PREGUNTA ES 5
                        activacionComecialCActivity.putExtra("OPCIONTIPOSEL",1);
                        startActivity(activacionComecialCActivity);
                        break;

                    case "PRECIOS Y DISPONIBILIDAD":

                        // SE AGREGA LA LOGICA DE PRECIOS
                        DataBaseBO.organizarTmpProductos();

                        Intent precioActivity = new Intent(OpcionesTareasActivity.this, PreciosDisponibilidadActivity.class);
                        precioActivity.putExtra("IDCATEGORIA",idCategoria);
                        startActivity(precioActivity);

                        break;

                    case "ACTIVACIÓN":

                        // LIMIAR CONTENEDORES LOGICA
                        PreferencesObjetoActivacion.vaciarPreferencesObjetoActivacion(OpcionesTareasActivity.this);
                        Intent activacionActivity = new Intent(OpcionesTareasActivity.this, ActivacionActivity.class);
                        activacionActivity.putExtra("IDCATEGORIA",idCategoria);
                        startActivity(activacionActivity);
                        break;
                }
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
        if(Util.isNetworkAvailable(OpcionesTareasActivity.this)) {

            if(DataBaseBO.hayInformacionXEnviar()) {

                Config config = ConfigBO.ObtenerConfigUsuario();
                if (config != null && config.conexion == Const.CONEXION_UNO_UNO) {

                    if(gestionCompleta()){
                        Sync sync = new Sync(OpcionesTareasActivity.this, Const.ENVIAR_INFO);
                        Progress.show(OpcionesTareasActivity.this, "Sincronizador", "Enviando Informacion...", false);
                        sync.start();
                    }else{
                        Alert.nutresaShow(OpcionesTareasActivity.this, "INFORMACIÓN", "Para poder sincronizar información por favor asegúrese de gestionar todas las mediciones de la categoría",
                                "OK", null,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        Alert.dialogo.cancel();
                                        OpcionesTareasActivity.this.finish();
                                    }

                                }, null);
                    }



                } else {
                    OpcionesTareasActivity.this.finish();
                }

            } else {

                OpcionesTareasActivity.this.finish();
            }




        } else {

            OpcionesTareasActivity.this.finish();
        }
    }


    public void on_ClickSincronizar(View view) {
        // SE CREA LA LOGICA PARA ENVIO DE INFORMACION
        if(Util.isNetworkAvailable(OpcionesTareasActivity.this)) {

            if(DataBaseBO.hayInformacionXEnviar()) {

                if(gestionCompleta()){
                    Sync sync = new Sync(OpcionesTareasActivity.this, Const.ENVIAR_INFO);
                    Progress.show(OpcionesTareasActivity.this, "Sincronizador", "Enviando Informacion...", false);
                    sync.start();
                }else{
                    Alert.nutresaShow(OpcionesTareasActivity.this, "INFORMACIÓN", "Para poder sincronizar información por favor asegúrese de gestionar todas las mediciones de la categoría",
                            "OK", null,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view)
                                {
                                    Alert.dialogo.cancel();
                                    OpcionesTareasActivity.this.finish();
                                }

                            }, null);
                }


            } else {
                OpcionesTareasActivity.this.finish();
            }
        } else {
            OpcionesTareasActivity.this.finish();
        }
    }

    public boolean gestionCompleta(){

        Vector<ActividadesCliente> listagestionada=DataBaseBO.obtenerListaGestionada( Main.cliente.canal, Main.cliente.codigo, idCategoria);
        int cont=0;
        for (ActividadesCliente a: listagestionada){
            //tieneGestion=0 VERDE
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

            OpcionesTareasActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    //ALERT
                    Alert.nutresaShow(OpcionesTareasActivity.this, "INFORMACIÓN", "Información enviada de forma correcta.",
                            "OK", null,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view)
                                {
                                    Alert.dialogo.cancel();
                                    OpcionesTareasActivity.this.finish();
                                }

                            }, null);
                }
            });

        } else {

            OpcionesTareasActivity.this.runOnUiThread(new Runnable() {

                public void run() {

                    //ALERT
                    Alert.nutresaShow(OpcionesTareasActivity.this, "ERROR", "No se pudo realizar el envio de información.",
                            "OK", null,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view)
                                {
                                    Alert.dialogo.cancel();
                                    OpcionesTareasActivity.this.finish();
                                }

                            }, null);

                }
            });
        }
    }
}

