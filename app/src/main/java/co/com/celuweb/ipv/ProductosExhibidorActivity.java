package co.com.celuweb.ipv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Vector;

import businessObject.DataBaseBO;
import component.Alert;
import component.Progress;
import component.Util;
import config.Const;
import config.Synchronizer;
import dataObject.Cliente;
import dataObject.ExhibidorEncabezado;
import dataObject.Exhibidores;
import dataObject.Main;
import dataObject.Producto;
import es.dmoral.toasty.Toasty;
import sharedPreferences.PreferencesCliente;

public class ProductosExhibidorActivity extends AppCompatActivity implements Synchronizer {

    private LinearLayout llTitulosPropios;
    private LinearLayout llTitulosCompetencia;

    private TextView tvTituloPropios;
    private TextView tvCantidadCarasPropias;
    private TextView tvPorcentajePropios;
    private TextView tvTituloCompetencia;
    private TextView tvCantidadCarasCompetencia;
    private TextView tvPorcentajeCompetencia;
    private TextView tvLabelRazonSocial;
    private TextView tvLabelCompetencia;
    private TextView tvLabelPropios;

    private LinearLayout llListaPropios;
    private LinearLayout llListaCompetencia;
    private Vector<Producto> listaProductosPropios;
    private Vector<Producto> listaProductosCompetencia;

    private LinearLayout llContenedorBotonesAgregarProductos;

    private Button btnTerminarProductosExhibidor;
    private Button btnEditarProductosExhibidor;
    private LinearLayout llComponentesTotal;

    private String idExhibidor;
    private String idCategoria="";
    private int exhibidorRegistroHoy;
    private int estadoEdicion = 0;

    private FrameLayout flInformacionProductosPropiosExhibidores;
    private FrameLayout flInformacionProductosCompetenciaExhibidores;

    private final static String TAG = ProductosExhibidorActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_productos_exhibidor);

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

        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            if (extras.containsKey("IDCATEGORIA"))
                idCategoria = extras.getString("IDCATEGORIA");

        }

        cargarClienteSel();
        cargarListaProductosPropiosExhibidor(false);
        cargarListaProductosCompetenciaExhibidor(false);
        setComponentesVista();

        // SE CAMBIA LA VISTA PARA QUE TENGA LOGICA DE TAB
        configurarTabsInformacionExhibicionProductos();
    }

    private void configurarTabsInformacionExhibicionProductos() {

        // SE CARGA EL TAB
        TabLayout tabs = findViewById(R.id.tabsExhibidoresProductos);
        tabs.addTab(tabs.newTab().setText("PROPIOS"));
        tabs.addTab(tabs.newTab().setText("COMPETENCIA"));

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                int position = tab.getPosition();

                if(position == 0) {

                    // 1. SE MUESTRA LA LISTA DE PRODUCTOS PROPIOS
                    flInformacionProductosPropiosExhibidores = findViewById(R.id.flInformacionProductosPropiosExhibidores);
                    flInformacionProductosPropiosExhibidores.setVisibility(View.VISIBLE);

                    llTitulosPropios = findViewById(R.id.llTitulosPropios);
                    llTitulosPropios.setVisibility(View.VISIBLE);


                    // 2. SE OCULTA LA VISTA DE PRODUCTOS COMPETENCIA
                    flInformacionProductosCompetenciaExhibidores = findViewById(R.id.flInformacionProductosCompetenciaExhibidores);
                    flInformacionProductosCompetenciaExhibidores.setVisibility(View.GONE);

                    llTitulosCompetencia = findViewById(R.id.llTitulosCompetencia);
                    llTitulosCompetencia.setVisibility(View.GONE);


                } else {

                    // 1. SE OCULTA LA VISTA DE PRODUCTOS PROPIOS
                    flInformacionProductosPropiosExhibidores = findViewById(R.id.flInformacionProductosPropiosExhibidores);
                    flInformacionProductosPropiosExhibidores.setVisibility(View.GONE);

                    llTitulosPropios = findViewById(R.id.llTitulosPropios);
                    llTitulosPropios.setVisibility(View.GONE);

                    // 2. SE MUESTRA LA VISTA DE PRODUCTOS COMPETENCIA
                    flInformacionProductosCompetenciaExhibidores = findViewById(R.id.flInformacionProductosCompetenciaExhibidores);
                    flInformacionProductosCompetenciaExhibidores.setVisibility(View.VISIBLE);

                    llTitulosCompetencia = findViewById(R.id.llTitulosCompetencia);
                    llTitulosCompetencia.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void cargarListaProductosPropiosExhibidor(boolean productoNuevo) {

        if(!productoNuevo) {

            // SE DETERMINA SI TIENE MEDICIONES PARA EL DIA
            boolean tieneMedicionesHoy = DataBaseBO.hayMedicionesExhibidorParaElDia(idExhibidor);

            if(tieneMedicionesHoy) {

                listaProductosPropios = DataBaseBO.obtenerListaProductosPropiosExhibidor(idExhibidor, 0, Main.cliente,idCategoria);

            } else {

                // SE DETERMINA SI TIENE MEDICION HISTORICA
                boolean tieneMedicionesHistoricas = DataBaseBO.hayMedicionesExhibidorHistoricas(idExhibidor);

                if(tieneMedicionesHistoricas) {

                    listaProductosPropios = DataBaseBO.obtenerListaProductosPropiosExhibidor(idExhibidor, 1, Main.cliente,idCategoria);

                } else {

                    listaProductosPropios = DataBaseBO.obtenerListaProductosPropiosExhibidor(idExhibidor, 2, Main.cliente,idCategoria);
                }
            }
        }

        llListaPropios = findViewById(R.id.llListaPropios);
        llListaPropios.removeAllViews();

        for(int pos = 0; pos < listaProductosPropios.size(); pos++) {

            // AGREGA AL LINEARLAYOUT UNA VISTA POR CADA ITEM DE LA LISTA
            final VistaItemListaPropios vProductoPropio = new VistaItemListaPropios(this);
            vProductoPropio.tvNombreProducto.setText(listaProductosPropios.elementAt(pos).nombre);
            vProductoPropio.position = pos;

            // 1. SE DETERMINA SI EL PRODUCTO ESTA MODIFICADO
            if(listaProductosPropios.elementAt(pos).esModificado == true) {

                vProductoPropio.etCantidadAgotado.setText(String.valueOf(listaProductosPropios.elementAt(pos).cantidadAct));

            } else {

                vProductoPropio.etCantidadAgotado.setText(String.valueOf(""));
            }

            vProductoPropio.etCantidadAgotado.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    if(charSequence.length() > 0) {

                        listaProductosPropios.elementAt(vProductoPropio.position).cantidadAct = Util.toInt(String.valueOf(charSequence));
                        listaProductosPropios.elementAt(vProductoPropio.position).esModificado = true;

                    } else {

                        listaProductosPropios.elementAt(vProductoPropio.position).cantidadAct = 0;
                        listaProductosPropios.elementAt(vProductoPropio.position).esModificado = false;
                    }

                    recalcularDatosVista();
                }

                @Override
                public void afterTextChanged(Editable editable) {}
            });

            if(listaProductosPropios.elementAt(vProductoPropio.position).codigo.equals("9999")) {

                vProductoPropio.ivProductoAgotado.setImageResource(R.mipmap.icono_otros_pro);
            }

            llListaPropios.addView(vProductoPropio);
        }
    }

    private void cargarListaProductosCompetenciaExhibidor(boolean productoNuevo) {

        if(!productoNuevo) {

            // SE DETERMINA SI TIENE MEDICIONES PARA EL DIA
            boolean tieneMedicionesHoy = DataBaseBO.hayMedicionesExhibidorParaElDia(idExhibidor);
            String idDetalle = Util.obtenerId(Main.usuario.codigo);

            if(tieneMedicionesHoy) {

                listaProductosCompetencia = DataBaseBO.obtenerListaProductosCompetenciaExhibidor(idExhibidor, 0, Main.cliente,idCategoria);

            } else {

                // SE DETERMINA SI TIENE MEDICION HISTORICA
                boolean tieneMedicionesHistoricas = DataBaseBO.hayMedicionesExhibidorHistoricas(idExhibidor);

                if(tieneMedicionesHistoricas) {

                    listaProductosCompetencia = DataBaseBO.obtenerListaProductosCompetenciaExhibidor(idExhibidor, 1, Main.cliente,idCategoria);

                } else {

                    listaProductosCompetencia = DataBaseBO.obtenerListaProductosCompetenciaExhibidor(idExhibidor, 2, Main.cliente,idCategoria);
                }
            }
        }

        llListaCompetencia = findViewById(R.id.llListaCompetencia);
        llListaCompetencia.removeAllViews();

        for(int pos = 0; pos < listaProductosCompetencia.size(); pos++) {

            // AGREGA AL LINEARLAYOUT UNA VISTA POR CADA ITEM DE LA LISTA
            final VistaItemListaCompetencia vProductoCompetencia = new VistaItemListaCompetencia(this);
            vProductoCompetencia.tvNombreProducto.setText(listaProductosCompetencia.elementAt(pos).nombre);
            vProductoCompetencia.position = pos;

            // 1. SE DETERMINA SI EL PRODUCTO ESTA MODIFICADO
            if(listaProductosCompetencia.elementAt(pos).esModificado == true) {

                vProductoCompetencia.etCantidadAgotado.setText(String.valueOf(listaProductosCompetencia.elementAt(pos).cantidadAct));

            } else {

                vProductoCompetencia.etCantidadAgotado.setText(String.valueOf(""));
            }

            vProductoCompetencia.etCantidadAgotado.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    if(charSequence.length() > 0) {

                        listaProductosCompetencia.elementAt(vProductoCompetencia.position).cantidadAct = Util.toInt(String.valueOf(charSequence));
                        listaProductosCompetencia.elementAt(vProductoCompetencia.position).esModificado = true;

                    } else {

                        listaProductosCompetencia.elementAt(vProductoCompetencia.position).cantidadAct = 0;
                        listaProductosCompetencia.elementAt(vProductoCompetencia.position).esModificado = false;
                    }

                    recalcularDatosVista();
                }

                @Override
                public void afterTextChanged(Editable editable) {}
            });

            if(listaProductosCompetencia.elementAt(vProductoCompetencia.position).codigo.equals("8888")) {

                vProductoCompetencia.ivProductoAgotado.setImageResource(R.mipmap.icono_otros_com);
            }

            llListaCompetencia.addView(vProductoCompetencia);
        }

        recalcularDatosVista();
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

            if (extras.containsKey("IDEXHIBIDOR"))
                idExhibidor = extras.getString("IDEXHIBIDOR");

            if (extras.containsKey("EXHIBIDORREGISTROHOY"))
                exhibidorRegistroHoy = extras.getInt("EXHIBIDORREGISTROHOY");

            if (extras.containsKey("IDCATEGORIA"))
                idCategoria = extras.getString("IDCATEGORIA");
        }
    }

    private void setComponentesVista() {

        tvLabelRazonSocial = findViewById(R.id.tvLabelRazonSocial);
        tvLabelRazonSocial.setTypeface(Const.letraSemibold);

        tvLabelCompetencia = findViewById(R.id.tvLabelCompetencia);
        tvLabelCompetencia.setTypeface(Const.letraSemibold);

        tvLabelPropios = findViewById(R.id.tvLabelPropios);
        tvLabelPropios.setTypeface(Const.letraSemibold);

        // TITULOS Y CALCULOS
        tvTituloPropios = findViewById(R.id.tvTituloPropios);
        tvTituloPropios.setTypeface(Const.letraRegular);
        tvCantidadCarasPropias = findViewById(R.id.tvCantidadCarasPropias);
        tvCantidadCarasPropias.setTypeface(Const.letraRegular);
        tvPorcentajePropios = findViewById(R.id.tvPorcentajePropios);
        tvPorcentajePropios.setTypeface(Const.letraRegular);

        tvTituloCompetencia = findViewById(R.id.tvTituloCompetencia);
        tvTituloCompetencia.setTypeface(Const.letraRegular);
        tvCantidadCarasCompetencia = findViewById(R.id.tvCantidadCarasCompetencia);
        tvCantidadCarasCompetencia.setTypeface(Const.letraRegular);
        tvPorcentajeCompetencia = findViewById(R.id.tvPorcentajeCompetencia);
        tvPorcentajeCompetencia.setTypeface(Const.letraRegular);

        try {

            Thread.sleep(100);
            recalcularDatosVista();

        } catch (InterruptedException e) {

            e.printStackTrace();
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
    public void onBackPressed() {}

    public void on_ClickEditarProductosExhibidor(View view) {

        if(estadoEdicion == 0) {

            estadoEdicion = 1;

            // SE MUESTRAN LOS BOTONES DE AGREGAR PRODUCTOS
            llContenedorBotonesAgregarProductos = findViewById(R.id.llContenedorBotonesAgregarProductos);
            llContenedorBotonesAgregarProductos.setVisibility(View.VISIBLE);

            // SE DESHABILITA EL BOTON DE TERMINAR LA GESTION
            btnTerminarProductosExhibidor = findViewById(R.id.btnTerminarProductosExhibidor);
            //btnTerminarProductosExhibidor.setBackground(this.getResources().getDrawable(R.drawable.buttonmsgdisable));
//            btnTerminarProductosExhibidor.setEnabled(false);

            // SE CAMBIA EL NOMBRE DEL BOTON
            btnEditarProductosExhibidor = findViewById(R.id.btnEditarProductosExhibidor);
            btnEditarProductosExhibidor.setText("TERMINAR EDI");

            // SE OCULTAN COMPONENTES TOTAL
            llComponentesTotal = findViewById(R.id.llComponentesTotal);
            llComponentesTotal.setVisibility(View.GONE);

        } else {

            estadoEdicion = 0;

            // SE OCULTAN LOS BOTONES DE AGREGAR PRODUCTOS
            llContenedorBotonesAgregarProductos = findViewById(R.id.llContenedorBotonesAgregarProductos);
            llContenedorBotonesAgregarProductos.setVisibility(View.GONE);

            // SE HABILITA EL BOTON DE TERMINAR LA GESTION
            btnTerminarProductosExhibidor = findViewById(R.id.btnTerminarProductosExhibidor);
            //btnTerminarProductosExhibidor.setBackground(this.getResources().getDrawable(R.drawable.buttonmsg));
            btnTerminarProductosExhibidor.setEnabled(true);

            // SE CAMBIA EL NOMBRE DEL BOTON
            btnEditarProductosExhibidor = findViewById(R.id.btnEditarProductosExhibidor);
            btnEditarProductosExhibidor.setText("EDITAR");

            // SE MUESTRAN COMPONENTES TOTAL
            llComponentesTotal = findViewById(R.id.llComponentesTotal);
            llComponentesTotal.setVisibility(View.VISIBLE);
        }
    }

    public void on_ClickAgregarProductoPropios(View view) {

        Intent agregarProducto = new Intent(ProductosExhibidorActivity.this, BusquedaProductosActivity.class);
        startActivityForResult(agregarProducto, Const.AGREGAR_PRODUCTO);
    }

    public void on_ClickAgregarProductoCompetencia(View view) {

        Intent agregarProducto = new Intent(ProductosExhibidorActivity.this, BusquedaProductosActivity.class);
        agregarProducto.putExtra("TIPOPRODUCTOCOMP", 1);
        agregarProducto.putExtra("OPCIONACTUALLOGICA", 10);
        startActivityForResult(agregarProducto, Const.AGREGAR_PRODUCTO_COMPETENCIA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == Const.AGREGAR_PRODUCTO) {

            if(resultCode == Activity.RESULT_OK) {

                String objetoSeleccionadoAgregar = data.getStringExtra("OBJETOSEL");

                // SE PASA EL JSON A OBJETO
                Gson gsObject = new Gson();
                Producto productoSelAgregar = gsObject.fromJson(objetoSeleccionadoAgregar, Producto.class);

                boolean productoEsta = false;

                for(int i = 0; i < listaProductosPropios.size(); i++) {

                    if(listaProductosPropios.elementAt(i).codigo.equals(productoSelAgregar.codigo)) {

                        productoEsta = true;
                    }
                }

                if(productoEsta) {

                    Toasty.warning(ProductosExhibidorActivity.this, "El producto ya existe en la lista.", Toast.LENGTH_LONG).show();

                } else {

                    if(listaProductosPropios.size() == 0) {

                        // SE AGREGA EL PRODUTO A LA LISTA DE PRODUCTOS AGOTADOS A AGREGAR
                        Producto productoPropio = new Producto();
                        productoPropio.nombre      = productoSelAgregar.nombre;
                        productoPropio.codigo      = productoSelAgregar.codigo;
                        productoPropio.cantidadAnt = 0;
                        productoPropio.cantidadAct = 0;

                        listaProductosPropios.add(productoPropio);

                        Toasty.success(ProductosExhibidorActivity.this, "El producto se agrego de forma exitosa.", Toast.LENGTH_LONG).show();

                        cargarListaProductosPropiosExhibidor(true);

                    } else {

                        int ultimaPosLista = (listaProductosPropios.size() - 1);

                        Producto productoOtrosPropio = listaProductosPropios.elementAt(ultimaPosLista);

                        // SE AGREGA EL PRODUTO A LA LISTA DE PRODUCTOS AGOTADOS A AGREGAR
                        Producto productoPropio = new Producto();
                        productoPropio.nombre      = productoSelAgregar.nombre;
                        productoPropio.codigo      = productoSelAgregar.codigo;
                        productoPropio.cantidadAnt = 0;
                        productoPropio.cantidadAct = 0;

                        listaProductosPropios.add(ultimaPosLista, productoPropio);

                        Toasty.success(ProductosExhibidorActivity.this, "El producto se agrego de forma exitosa.", Toast.LENGTH_LONG).show();

                        cargarListaProductosPropiosExhibidor(true);
                    }
                }
            }

        } else if(requestCode == Const.AGREGAR_PRODUCTO_COMPETENCIA) {

            if(resultCode == Activity.RESULT_OK) {

                String objetoSeleccionadoAgregar = data.getStringExtra("OBJETOSEL");

                // SE PASA EL JSON A OBJETO
                Gson gsObject = new Gson();
                Producto productoSelAgregar = gsObject.fromJson(objetoSeleccionadoAgregar, Producto.class);

                boolean productoEsta = false;

                for(int i = 0; i < listaProductosCompetencia.size(); i++) {

                    if(listaProductosCompetencia.elementAt(i).codigo.equals(productoSelAgregar.codigo)) {

                        productoEsta = true;
                    }
                }

                if(productoEsta) {

                    Toasty.warning(ProductosExhibidorActivity.this, "El producto ya existe en la lista.", Toast.LENGTH_LONG).show();

                } else {

                    if(listaProductosCompetencia.size() == 0) {

                        // SE AGREGA EL PRODUTO A LA LISTA DE PRODUCTOS AGOTADOS A AGREGAR
                        Producto productoCompetencia = new Producto();
                        productoCompetencia.nombre       = productoSelAgregar.nombre;
                        productoCompetencia.codigo       = Util.obtenerIdProducto();
                        productoCompetencia.cantidadAnt  = productoSelAgregar.cantidadAnt;
                        productoCompetencia.cantidadAct  = productoSelAgregar.cantidadAct;
                        productoCompetencia.esModificado = productoSelAgregar.esModificado;

                        listaProductosCompetencia.add(productoCompetencia);

                        Toasty.success(ProductosExhibidorActivity.this, "El producto se agrego de forma exitosa.", Toast.LENGTH_LONG).show();

                        cargarListaProductosCompetenciaExhibidor(true);

                    } else {

                        int ultimaPosLista = (listaProductosCompetencia.size() - 1);

                        Producto productoOtrosCompetencia = listaProductosCompetencia.elementAt(ultimaPosLista);

                        // SE AGREGA EL PRODUTO A LA LISTA DE PRODUCTOS AGOTADOS A AGREGAR
                        Producto productoCompetencia = new Producto();
                        productoCompetencia.nombre       = productoSelAgregar.nombre;
                        productoCompetencia.codigo       = Util.obtenerIdProducto();
                        productoCompetencia.cantidadAnt  = productoSelAgregar.cantidadAnt;
                        productoCompetencia.cantidadAct  = productoSelAgregar.cantidadAct;
                        productoCompetencia.esModificado = productoSelAgregar.esModificado;

                        listaProductosCompetencia.add(ultimaPosLista, productoCompetencia);

                        Toasty.success(ProductosExhibidorActivity.this, "El producto se agrego de forma exitosa.", Toast.LENGTH_LONG).show();

                        cargarListaProductosCompetenciaExhibidor(true);
                    }
                }
            }
        }
        else if (requestCode == Const.FOTO) {

                setResult(Activity.RESULT_OK);
                finish();
        }
    }

    public void on_ClickTerminarProductosExhibidor(View view) {

        btnTerminarProductosExhibidor = findViewById(R.id.btnTerminarProductosExhibidor);
        btnTerminarProductosExhibidor.setEnabled(false);

        boolean hayElementosSinCarasPropios = false;
        boolean hayElementosSinCarasCompetencia = false;
        boolean noHyinfo=false;

        llListaPropios = findViewById(R.id.llListaPropios);
        llListaCompetencia = findViewById(R.id.llListaCompetencia);

        if(llListaPropios.getChildCount() == 0 && llListaCompetencia.getChildCount() == 0) {

            noHyinfo = true;

        } else {

            for(int iPro = 0; iPro < llListaPropios.getChildCount(); iPro++) {

                VistaItemListaPropios vProductoPropio = (VistaItemListaPropios) llListaPropios.getChildAt(iPro);
                String valorCaras = vProductoPropio.etCantidadAgotado.getText().toString();

                if(valorCaras.equals("")) {

                    hayElementosSinCarasPropios = true;
                    break;
                }
            }

            for(int iCom = 0; iCom < llListaCompetencia.getChildCount(); iCom++) {

                VistaItemListaCompetencia vProductoCompetencia = (VistaItemListaCompetencia) llListaCompetencia.getChildAt(iCom);
                String valorCaras = vProductoCompetencia.etCantidadAgotado.getText().toString();

                if(valorCaras.equals("")) {

                    hayElementosSinCarasCompetencia = true;
                    break;
                }
            }
        }

        if(noHyinfo){
            ProductosExhibidorActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    btnTerminarProductosExhibidor.setEnabled(true);

                    //ALERT
                    Alert.nutresaShow(ProductosExhibidorActivity.this, "INFORMACIÓN", "No hay información de la medición por enviar.",
                            "OK",
                            null,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    Alert.dialogo.cancel();
                                }

                            }, null);

                    return;
                }
            });
        }else if(!hayElementosSinCarasPropios && !hayElementosSinCarasCompetencia) {

            terminarMedicion();

        } else {

            btnTerminarProductosExhibidor.setEnabled(true);

            ProductosExhibidorActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    //ALERT
                    Alert.nutresaShow(ProductosExhibidorActivity.this, "INFORMACIÓN", "Hay productos de la lista sin cantidad caras asignado.",
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
    }

    private void terminarMedicion() {

        // SE UTILIZA LA BANDERA "exhibidorRegistroHoy" PARA DETERMINAR SI EL ENCABEZADO
        // DE LA MEDICION ES HISTORICO O ESTA EN LA TABLA DE ACTUAL Y CON ESO SE REALIZA
        // LA RESPECTIVA LOGICA
        int totalCaras = 0;
        String idDetalle = Util.obtenerId(Main.usuario.codigo);

        if(exhibidorRegistroHoy == 1) {

            // SE EJECUTA LA LOGICA NORMAL
            // SE CALCULAN LOS DEMAS DATOS A AGREGAR EN EL ENCABEZADO
            int cantidadCarasPropias = 0;
            int cantidadCarasCompetencia = 0;

            for(int i = 0; i < listaProductosPropios.size(); i++) {

                cantidadCarasPropias += listaProductosPropios.elementAt(i).cantidadAct;
            }

            for(int i = 0; i < listaProductosCompetencia.size(); i++) {

                cantidadCarasCompetencia += listaProductosCompetencia.elementAt(i).cantidadAct;
            }

            // 1. SE OBTIENE EL REGISTO DEL ENCABEZADO ACTUAL Y SE INSERTA DE NUEVO EN TEMP
            ExhibidorEncabezado exhibidorEncabezadoHistorico = DataBaseBO.obtenerExhibidorEncabezado3_ACTUAL(idExhibidor);
            exhibidorEncabezadoHistorico.fechaMovil = Util.obtenerFechaActual();

            totalCaras = cantidadCarasPropias + cantidadCarasCompetencia;
            int cantidadPorcentajePropias = Math.round((cantidadCarasPropias * 100) / totalCaras);
            int cantidadPorcentajeCompetencia = Math.round((cantidadCarasCompetencia * 100) / totalCaras);

            // SE ELIMINA EL ENCABEZADO EXISTENTE PARA REEMPLAZRLO POR EL MAS ACTUALIZADO
            DataBaseBO.eliminarExhibidorEncabeza(idExhibidor);
            DataBaseBO.actualizarMedicionExhibidorActualizada_Productos(cantidadCarasPropias, cantidadCarasCompetencia, cantidadPorcentajePropias, cantidadPorcentajeCompetencia, idExhibidor, exhibidorEncabezadoHistorico,idCategoria, idDetalle);

        } else {



            // SE DEBE OBTENER EL ENCABEZADO DEL HISTORICO Y PONERLO EN LA TABLA ACTUAL
            // ES UN REGISTRO HISTORICO
            // 1. SE OBTIENE EL REGISTO DEL HISTORICO
            ExhibidorEncabezado exhibidorEncabezadoHistorico = DataBaseBO.obtenerExhibidorEncabezado3(idExhibidor);

            // SE CALCULAN LOS DEMAS DATOS A AGREGAR EN EL ENCABEZADO
            int cantidadCarasPropias = 0;
            int cantidadCarasCompetencia = 0;

            for(int i = 0; i < listaProductosPropios.size(); i++) {

                cantidadCarasPropias += listaProductosPropios.elementAt(i).cantidadAct;
            }

            for(int i = 0; i < listaProductosCompetencia.size(); i++) {

                cantidadCarasCompetencia += listaProductosCompetencia.elementAt(i).cantidadAct;
            }

            totalCaras = cantidadCarasPropias + cantidadCarasCompetencia;

            int cantidadPorcentajePropias = 0;
            int cantidadPorcentajeCompetencia = 0;


            if(totalCaras!=0){
                cantidadPorcentajePropias = Math.round((cantidadCarasPropias * 100) / totalCaras);
                cantidadPorcentajeCompetencia = Math.round((cantidadCarasCompetencia * 100) / totalCaras);
            }


            exhibidorEncabezadoHistorico.totalPropios = cantidadCarasPropias;
            exhibidorEncabezadoHistorico.totalCompetencia = cantidadCarasCompetencia;
            exhibidorEncabezadoHistorico.porcentajePropios = Float.parseFloat(String.valueOf(cantidadPorcentajePropias));
            exhibidorEncabezadoHistorico.porcentajeCompetencia = Float.parseFloat(String.valueOf(cantidadPorcentajeCompetencia));
            exhibidorEncabezadoHistorico.fechaMovil = Util.obtenerFechaActual();

            DataBaseBO.insertarMedicionExhibidorActualizada_Productos(exhibidorEncabezadoHistorico,idCategoria, idDetalle);
        }

        // SE ELIMINAN LOS REGISTROS DE LA MEDICION ANTERIOR SI HAY EN LA TABLA DE DETALLES
        DataBaseBO.eliminarDetallesExhibidor(idExhibidor);

        String id = idExhibidor;
        String fecha = Util.obtenerFechaActual();

        boolean guardo = DataBaseBO.guardarProductosExhibidor(id, idDetalle, fecha, listaProductosPropios, listaProductosCompetencia, Main.cliente.codigo, totalCaras);

        if(guardo) {

            // SE OBTIENE EL EXHIBIDOR ACTUAL DE LA LISTA DE EXHIBIDORES CLIENTE, LUEGO SE ELIMINA Y SE
            // ACTUAIZA EN EL DATABASE Y SE INSERTA EN LA TEMP PARA ACTUALIZAR REGISTRO EN EL SERVIDOR
            // 1. SE OBTIENE
            Exhibidores exhibidorActual = DataBaseBO.obtenerExhibidor(idExhibidor);
            exhibidorActual.finalizado = "1";

            // 2. SE ELIMINA
            DataBaseBO.eliminarExhibidorCliente(idExhibidor);

            // 3. SE INSERTA Y ACTUALIZA
            DataBaseBO.actualizarExhibidorCliente(exhibidorActual);

            Alert.nutresaShow(ProductosExhibidorActivity.this, "INFORMACÓN", "La informacion se almaceno de forma exitosa.",
                    "OK", null,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Alert.dialogo.cancel();
//                            finish();
//                            Sync sync = new Sync(ProductosExhibidorActivity.this, Const.ENVIAR_INFO);
//                            Progress.show(ProductosExhibidorActivity.this, "Sincronizador", "Enviando Informacion...", false);
//                            sync.start();
//
                            Intent moduloFotosExhibidor = new Intent(ProductosExhibidorActivity.this, FotosActivity.class);
                            moduloFotosExhibidor.putExtra("IDEXHIBIDOR", idExhibidor);
                            moduloFotosExhibidor.putExtra("EXHIBIDORREGISTROHOY", exhibidorRegistroHoy);
                            moduloFotosExhibidor.putExtra("ESEXHIBIDOR", true);
                            moduloFotosExhibidor.putExtra("IDCATEGORIA", idCategoria);

                            startActivityForResult(moduloFotosExhibidor,Const.FOTO);


                        }

                    }, null, false);

        } else {

            btnTerminarProductosExhibidor = findViewById(R.id.btnTerminarProductosExhibidor);
            btnTerminarProductosExhibidor.setEnabled(true);

            //ALERT
            Alert.nutresaShow(ProductosExhibidorActivity.this, "ERROR", "No se pudo guardar la medición de forma correcta.",
                    "OK", null,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            Alert.dialogo.cancel();
                        }

                    }, null, false);
        }
    }

    @Override
    public void respSync(boolean ok, String respuestaServer, String msg, int codeRequest) {

        try {

            Progress.hide();
            Alert.dialogo.cancel();

            if (ok) {

                ProductosExhibidorActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //ALERT
                        Alert.nutresaShow(ProductosExhibidorActivity.this, "INFORMACIÓN", "Información enviada de forma correcta.",
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

                ProductosExhibidorActivity.this.runOnUiThread(new Runnable() {

                    public void run() {

                        //ALERT
                        Alert.nutresaShow(ProductosExhibidorActivity.this, "ERROR", "No se pudo realizar el envio de información.",
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

    private void recalcularDatosVista() {

        // SE TOMAN AMBAS LISTA DE PRODUCTO Y SE SUMAN LAS CARAS TOTALES
        int cantidadCarasPropias = 0;
        int cantidadCarasCompetencia = 0;

        int cantidadHijosCompetencia = 0;
        int cantidadHijosPropios = 0;

        // 1. LISTA PROPIOS
        for(int i = 0; i < listaProductosPropios.size(); i++) {

            cantidadCarasPropias += listaProductosPropios.elementAt(i).cantidadAct;
        }

        for(int i = 0; i < listaProductosCompetencia.size(); i++) {

            cantidadCarasCompetencia += listaProductosCompetencia.elementAt(i).cantidadAct;
        }

        tvCantidadCarasPropias = findViewById(R.id.tvCantidadCarasPropias);
        tvCantidadCarasPropias.setText(String.valueOf(cantidadCarasPropias));

        tvCantidadCarasCompetencia = findViewById(R.id.tvCantidadCarasCompetencia);
        tvCantidadCarasCompetencia.setText(String.valueOf(cantidadCarasCompetencia));

        // SE CALCULAN LOS PORCENTAJES PARA CADA UNA DE LAS MEDICIONES
        int cantidadTotalCaras = cantidadCarasPropias + cantidadCarasCompetencia;

        tvPorcentajePropios = findViewById(R.id.tvPorcentajePropios);
        tvPorcentajeCompetencia = findViewById(R.id.tvPorcentajeCompetencia);


        if(cantidadTotalCaras == 0) {

            tvPorcentajePropios.setText("0%");
            tvPorcentajeCompetencia.setText("0%");

        } else {

            if(cantidadCarasPropias == 0 || cantidadCarasCompetencia == 0) {

                if(cantidadCarasPropias == 0) {

                    tvPorcentajePropios.setText("0%");
                    tvPorcentajeCompetencia.setText("100%");

                } else if(cantidadCarasCompetencia == 0) {

                    tvPorcentajePropios.setText("100%");
                    tvPorcentajeCompetencia.setText("0%");
                }

            } else {

                // HAY CANTIDAD EN CARAS PROPIAS Y COMPETENCIA, SE CALCULAN LOS PORCENYAJES
                int cantidadPorcentajePropias = Math.round((cantidadCarasPropias * 100) / cantidadTotalCaras);
                int cantidadPorcentajeCompetencia = Math.round((cantidadCarasCompetencia * 100) / cantidadTotalCaras);

                tvPorcentajePropios.setText(String.valueOf(cantidadPorcentajePropias) + "%");
                tvPorcentajeCompetencia.setText(String.valueOf(cantidadPorcentajeCompetencia) + "%");
            }
        }


        /////// SE SETEAN LOS PORCENTAJES PARA CADA ITEM CON EL NUEVO VALOR DE CANTIDAD TOTAL ///////
        /////////////////////////////////////////////////////////////////////////////////////////////
        /** PROPIOS **/
        llListaPropios = findViewById(R.id.llListaPropios);

        cantidadHijosPropios = llListaPropios.getChildCount();

        if(cantidadHijosPropios > 0) {

            for(int i = 0; i < cantidadHijosPropios; i++) {

                VistaItemListaPropios vProductosPropios = (VistaItemListaPropios) llListaPropios.getChildAt(i);
                String cantidadAsignada = vProductosPropios.etCantidadAgotado.getText().toString();
                int cantidadAsignadaEntero = Util.toInt(cantidadAsignada);

                if(cantidadTotalCaras > 0) {

                    int valorPorcentaje = Math.round((cantidadAsignadaEntero * 100) / cantidadTotalCaras);
                    vProductosPropios.tvCalculoPorcentaje.setText(String.valueOf(valorPorcentaje) + "%");
                }
            }
        }

        /** COMPETENCIA **/
        llListaCompetencia = findViewById(R.id.llListaCompetencia);

        cantidadHijosCompetencia = llListaCompetencia.getChildCount();

        if(cantidadHijosCompetencia > 0) {

            for(int i = 0; i < cantidadHijosCompetencia; i++) {

                VistaItemListaCompetencia vProductosCompetencia = (VistaItemListaCompetencia) llListaCompetencia.getChildAt(i);
                String cantidadAsignada = vProductosCompetencia.etCantidadAgotado.getText().toString();
                int cantidadAsignadaEntero = Util.toInt(cantidadAsignada);

                if(cantidadTotalCaras > 0) {

                    int valorPorcentaje = Math.round((cantidadAsignadaEntero * 100) / cantidadTotalCaras);
                    vProductosCompetencia.tvCalculoPorcentaje.setText(String.valueOf(valorPorcentaje) + "%");
                }
            }
        }
        /////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////
    }



}
