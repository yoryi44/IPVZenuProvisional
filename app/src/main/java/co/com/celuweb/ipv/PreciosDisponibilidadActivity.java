package co.com.celuweb.ipv;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
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
import dataObject.ItemListViewAgotados;
import dataObject.Main;
import dataObject.Producto;
import dataObject.Usuario;
import es.dmoral.toasty.Toasty;
import sharedPreferences.PreferencesCliente;

public class PreciosDisponibilidadActivity extends AppCompatActivity implements Synchronizer {

    private TextView tvLabelRazonSocial;
    private LinearLayout llListaProductosPropios;
    private LinearLayout llListaProductosCompetencia;
    private Vector<Producto> listaProductosPropios;
    private Vector<Producto> listaProductosCompetencia;

    private FrameLayout flInformacionProductosPropiosPrecios;
    private FrameLayout flInformacionProductosCompetenciaPrecios;

    String idCategoria = "";

    private final static String TAG = PrecioProductosActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_precio_productos);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        setSupportActionBar(toolbar);

        // SE INICIALIZA EL COMPONENTE PRINCIPAL DE LA APLICACION
        Main.contexto = getApplicationContext();

        TextView tvTituloModulo = findViewById(R.id.tvTituloModulo);
        tvTituloModulo.setTypeface(Const.letraSemibold);
        tvTituloModulo.setText("PRECIOS Y DISPONIBILIDAD");

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
        cargarListaProductosPropios(false);
        cargarListaProductosCompetencia(false);
        setComponentesVista();

        // SE CAMBIA LA VISTA PARA QUE TENGA LOGICA DE TAB
        configurarTabsInformacionPrecioProductos();

        findViewById(R.id.llInfoAgotados).setVisibility(View.VISIBLE);
//        findViewById(R.id.btnAgregarDisponibilidad).setVisibility(View.VISIBLE);
    }

    private void configurarTabsInformacionPrecioProductos() {

        // SE CARGA EL TAB
        TabLayout tabs = findViewById(R.id.tabsPrecioProductos);
        tabs.addTab(tabs.newTab().setText("PROPIOS"));
        tabs.addTab(tabs.newTab().setText("COMPETENCIA"));

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                int position = tab.getPosition();

                if(position == 0) {

                    // 1. SE MUESTRA LA LISTA DE PRODUCTOS PROPIOS
                    flInformacionProductosPropiosPrecios = findViewById(R.id.flInformacionProductosPropiosPrecios);
                    flInformacionProductosPropiosPrecios.setVisibility(View.VISIBLE);

                    findViewById(R.id.llInfoAgotados).setVisibility(View.VISIBLE);
//                    findViewById(R.id.btnAgregarDisponibilidad).setVisibility(View.VISIBLE);

                    // 2. SE OCULTA LA VISTA DE PRODUCTOS COMPETENCIA
                    flInformacionProductosCompetenciaPrecios = findViewById(R.id.flInformacionProductosCompetenciaPrecios);
                    flInformacionProductosCompetenciaPrecios.setVisibility(View.GONE);

                    findViewById(R.id.llInfoCompetencia).setVisibility(View.GONE);
                    findViewById(R.id.btnAgregarProductoCompetencia).setVisibility(View.GONE);

                } else {

                    // 1. SE OCULTA LA VISTA DE PRODUCTOS PROPIOS
                    flInformacionProductosPropiosPrecios = findViewById(R.id.flInformacionProductosPropiosPrecios);
                    flInformacionProductosPropiosPrecios.setVisibility(View.GONE);

                    findViewById(R.id.llInfoAgotados).setVisibility(View.GONE);
                    findViewById(R.id.btnAgregarDisponibilidad).setVisibility(View.GONE);

                    // 2. SE MUESTRA LA VISTA DE PRODUCTOS COMPETENCIA
                    flInformacionProductosCompetenciaPrecios = findViewById(R.id.flInformacionProductosCompetenciaPrecios);
                    flInformacionProductosCompetenciaPrecios.setVisibility(View.VISIBLE);

                    findViewById(R.id.llInfoCompetencia).setVisibility(View.VISIBLE);
//                    findViewById(R.id.btnAgregarProductoCompetencia).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void cargarListaProductosPropios(boolean nuevoProducto) {

        if(!nuevoProducto) {

            listaProductosPropios = DataBaseBO.obtenerListaProductosPropios(Main.cliente.codigo, idCategoria);
        }

        llListaProductosPropios = findViewById(R.id.llListaProductosPropios);
        llListaProductosPropios.removeAllViews();

        for(int pos = 0; pos < listaProductosPropios.size(); pos++) {

            // AGREGA AL LINEARLAYOUT UNA VISTA POR CADA ITEM DE LA LISTA
            final VistaListasPreciosDisponibles vProductoPropio = new VistaListasPreciosDisponibles(this);
            vProductoPropio.tvNombreProducto.setText(listaProductosPropios.elementAt(pos).nombre);
            vProductoPropio.tvPrecioProducto.setText("Precio: $" + Util.separarMilesSinDecimal(Util.redondear(String.valueOf(listaProductosPropios.elementAt(pos).PrecioFinal), 0)) + " - Margen: " + calcularMargen(listaProductosPropios.elementAt(pos).precioCliente, 0) + "%");
            vProductoPropio.position = pos;

            if(listaProductosPropios.elementAt(pos).esModificado == true) {

                if(listaProductosPropios.elementAt(pos).cantidadAct >= 0) {

                    vProductoPropio.etCantidadAgotado.setText(String.valueOf(listaProductosPropios.elementAt(pos).cantidadAct));
                }

            } else {

                vProductoPropio.etCantidadAgotado.setText("");

                if(listaProductosPropios.elementAt(pos).cantidadAnt >= 0) {

                    vProductoPropio.etCantidadAgotado.setHint(String.valueOf(listaProductosPropios.elementAt(pos).cantidadAnt));
                }
            }

            if(listaProductosPropios.elementAt(pos).agotado == true) {
                vProductoPropio.ivAgotado.setBackgroundResource(R.drawable.buttonmsg);
            } else {
                vProductoPropio.ivAgotado.setBackgroundResource(R.drawable.buttonmsgnegative);
            }

            if(listaProductosPropios.elementAt(pos).seVende != false) {
                vProductoPropio.llFondoProducto.setBackgroundColor(Color.WHITE);
                vProductoPropio.ivAgotado.setVisibility(View.VISIBLE);
                vProductoPropio.ivBotonCopiarCantidadAgotados.setVisibility(View.VISIBLE);
                vProductoPropio.etCantidadAgotado.setEnabled(true);
            } else {
                vProductoPropio.llFondoProducto.setBackgroundColor(getResources().getColor(R.color.colorRojoFondo));
                vProductoPropio.ivAgotado.setVisibility(View.INVISIBLE);
                vProductoPropio.ivBotonCopiarCantidadAgotados.setVisibility(View.INVISIBLE);
                vProductoPropio.etCantidadAgotado.setText("0");
                vProductoPropio.etCantidadAgotado.setEnabled(false);
            }

            String cant=vProductoPropio.etCantidadAgotado.getText().toString();
            String cant2=String.valueOf(vProductoPropio.etCantidadAgotado.getHint());
            String precioFinal= !cant.equals("")? cant : cant2;
            if(precioFinal != null && !precioFinal.equals("")) {

                String valorHint = precioFinal;
                vProductoPropio.etCantidadAgotado.setText(valorHint);

                if(!valorHint.equals("")) {

                    listaProductosPropios.elementAt(vProductoPropio.position).esModificado = true;
                    listaProductosPropios.elementAt(vProductoPropio.position).cantidadAct = Util.toInt(valorHint);

                    int valorHintEntero = Util.toInt(valorHint);

                    if(valorHintEntero == 0) {
                        // SE HACE EL CAMBIO DE ICONO Y EL COLOR DEL BORDE DEL EDITTEXT
                        vProductoPropio.ivBotonCopiarCantidadAgotados.setImageResource(R.mipmap.iconook_gris);
                        vProductoPropio.llBordeCantidad.setBackground(ContextCompat.getDrawable(PreciosDisponibilidadActivity.this, R.drawable.edittext_rounded));

                    }else if(valorHintEntero > 1000 && nuevoProducto) {
//                    }else if(valorHintEntero > 0 && valorHintEntero < 1000 ) {

                        // SE HACE EL CAMBIO DE ICONO Y EL COLOR DEL BORDE DEL EDITTEXT
                        vProductoPropio.ivBotonCopiarCantidadAgotados.setImageResource(R.mipmap.iconook);
                        vProductoPropio.llBordeCantidad.setBackground(ContextCompat.getDrawable(PreciosDisponibilidadActivity.this, R.drawable.edittext_rounded_green));


                    } else {
                        // SE HACE EL CAMBIO DE ICONO Y EL COLOR DEL BORDE DEL EDITTEXT
                        vProductoPropio.ivBotonCopiarCantidadAgotados.setImageResource(R.mipmap.iconook_naranja);
                        vProductoPropio.llBordeCantidad.setBackground(ContextCompat.getDrawable(PreciosDisponibilidadActivity.this, R.drawable.edittext_rounded_orange));

                    }
                }
            }
            vProductoPropio.llFondoProducto.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if(listaProductosPropios.elementAt(vProductoPropio.position).recienAgregado){
                        //ALERT
                        Alert.nutresaShow(PreciosDisponibilidadActivity.this, "INFORMACIÓN", "Desea eliminar el producto?",
                                "OK",
                                "CANCELAR",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        listaProductosPropios.removeElementAt(vProductoPropio.position);
                                        cargarListaProductosPropios(true);
                                        Alert.dialogo.cancel();
                                    }

                                }, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        Alert.dialogo.cancel();
                                    }

                                });
                    }
                    return true;
                }

            });
            
            vProductoPropio.ivAgotado.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    if(listaProductosPropios.elementAt(vProductoPropio.position).esModificado == true) {

                        listaProductosPropios.elementAt(vProductoPropio.position).esModificado = false;
                        listaProductosPropios.elementAt(vProductoPropio.position).agotado = false;
                        listaProductosPropios.elementAt(vProductoPropio.position).cantAgotado = 0;
                        vProductoPropio.ivAgotado.setBackgroundResource(R.drawable.buttonmsgnegative);

                    } else {

                        listaProductosPropios.elementAt(vProductoPropio.position).esModificado = true;
                        listaProductosPropios.elementAt(vProductoPropio.position).agotado = true;
                        listaProductosPropios.elementAt(vProductoPropio.position).cantAgotado = 1;
                        vProductoPropio.ivAgotado.setBackgroundResource(R.drawable.buttonmsg);
                    }
                }
            });

            vProductoPropio.ivNoVenta.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    if(vProductoPropio.vendido) {

                        vProductoPropio.vendido = false;
                        vProductoPropio.llFondoProducto.setBackgroundColor(getResources().getColor(R.color.colorRojoFondo));
                        listaProductosPropios.elementAt(vProductoPropio.position).seVende = false;
                        listaProductosPropios.elementAt(vProductoPropio.position).cantAgotado = -1;
                        vProductoPropio.ivAgotado.setVisibility(View.INVISIBLE);
                        vProductoPropio.ivBotonCopiarCantidadAgotados.setVisibility(View.INVISIBLE);
                        vProductoPropio.etCantidadAgotado.setText("0");
                        vProductoPropio.etCantidadAgotado.setEnabled(false);

                    } else {

                        vProductoPropio.vendido = true;
                        vProductoPropio.llFondoProducto.setBackgroundColor(Color.WHITE);
                        listaProductosPropios.elementAt(vProductoPropio.position).seVende = true;
                        listaProductosPropios.elementAt(vProductoPropio.position).cantAgotado = 0;
                        vProductoPropio.ivAgotado.setVisibility(View.VISIBLE);
                        vProductoPropio.ivBotonCopiarCantidadAgotados.setVisibility(View.VISIBLE);

//                        if(listaProductosPropios.elementAt(vProductoPropio.position).cantidadAct >= 0) {
//
//                            vProductoPropio.etCantidadAgotado.setText(String.valueOf(listaProductosPropios.elementAt(vProductoPropio.position).cantidadAct));
//                        }
                        vProductoPropio.etCantidadAgotado.setEnabled(true);
                    }
                }
            });

            vProductoPropio.ivBotonCopiarCantidadAgotados.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    if(vProductoPropio.etCantidadAgotado.getHint() != null) {

                        String valorHint = String.valueOf(vProductoPropio.etCantidadAgotado.getHint());
                        vProductoPropio.etCantidadAgotado.setText(valorHint);

                        ////////////////////////////////////////////////////////// SE CALCULA EL MARGEN
                        String dataAsignada = vProductoPropio.tvPrecioProducto.getText().toString();
                        int posFinal = dataAsignada.indexOf("M");

                        float valorVenta = Util.toFloat(((dataAsignada.substring(9, posFinal - 3)).replace(",", "")).replace(".", ""));
                        float valorIPVUsuario = Util.toFloat(valorHint);

                        vProductoPropio.tvPrecioProducto.setText(dataAsignada.substring(0, posFinal + 8) + calcularMargen(valorVenta, valorIPVUsuario) + "%");
                        //////////////////////////////////////////////////////////
                        //////////////////////////////////////////////////////////

                        if(!valorHint.equals("")) {

                            listaProductosPropios.elementAt(vProductoPropio.position).esModificado = true;
                            listaProductosPropios.elementAt(vProductoPropio.position).cantidadAct = Util.toInt(valorHint);

                            int valorHintEntero = Util.toInt(valorHint);

                            if(valorHintEntero >= 0 && valorHintEntero < 1000) {

                                // SE HACE EL CAMBIO DE ICONO Y EL COLOR DEL BORDE DEL EDITTEXT
                                vProductoPropio.ivBotonCopiarCantidadAgotados.setImageResource(R.mipmap.iconook_naranja);
                                vProductoPropio.llBordeCantidad.setBackground(ContextCompat.getDrawable(PreciosDisponibilidadActivity.this, R.drawable.edittext_rounded_orange));

                            } else {

                                // SE HACE EL CAMBIO DE ICONO Y EL COLOR DEL BORDE DEL EDITTEXT
                                vProductoPropio.ivBotonCopiarCantidadAgotados.setImageResource(R.mipmap.iconook);
                                vProductoPropio.llBordeCantidad.setBackground(ContextCompat.getDrawable(PreciosDisponibilidadActivity.this, R.drawable.edittext_rounded_green));
                            }
                        }
                    }
                }
            });

            vProductoPropio.etCantidadAgotado.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    if(charSequence.length() > 0) {

                        listaProductosPropios.elementAt(vProductoPropio.position).cantidadAct = Util.toInt(String.valueOf(charSequence));
                        listaProductosPropios.elementAt(vProductoPropio.position).esModificado = true;

                        ////////////////////////////////////////////////////////// SE CALCULA EL MARGEN
                        String dataAsignada = vProductoPropio.tvPrecioProducto.getText().toString();
                        int posFinal = dataAsignada.indexOf("M");

                        float valorVenta = Util.toFloat(((dataAsignada.substring(9, posFinal - 3)).replace(",", "")).replace(".", ""));
                        float valorIPVUsuario = Util.toFloat(String.valueOf(charSequence));

                        vProductoPropio.tvPrecioProducto.setText(dataAsignada.substring(0, posFinal + 8) + calcularMargen(valorVenta, valorIPVUsuario) + "%" );
                        //////////////////////////////////////////////////////////
                        //////////////////////////////////////////////////////////

                        int valorIngresado = Util.toInt(String.valueOf(charSequence));

                        if(valorIngresado >= 0 && valorIngresado < 1000) {

                            // SE HACE EL CAMBIO DE ICONO Y EL COLOR DEL BORDE DEL EDITTEXT
                            vProductoPropio.ivBotonCopiarCantidadAgotados.setImageResource(R.mipmap.iconook_naranja);
                            vProductoPropio.llBordeCantidad.setBackground(ContextCompat.getDrawable(PreciosDisponibilidadActivity.this, R.drawable.edittext_rounded_orange));

                        } else {

                            // SE HACE EL CAMBIO DE ICONO Y EL COLOR DEL BORDE DEL EDITTEXT
                            vProductoPropio.ivBotonCopiarCantidadAgotados.setImageResource(R.mipmap.iconook);
                            vProductoPropio.llBordeCantidad.setBackground(ContextCompat.getDrawable(PreciosDisponibilidadActivity.this, R.drawable.edittext_rounded_green));
                        }

                    } else {

                        ////////////////////////////////////////////////////////// SE CALCULA EL MARGEN
                        String dataAsignada = vProductoPropio.tvPrecioProducto.getText().toString();
                        int posFinal = dataAsignada.indexOf("M");

                        float valorVenta = Util.toFloat(((dataAsignada.substring(9, posFinal - 3)).replace(",", "")).replace(".", ""));
                        float valorIPVUsuario = Util.toFloat(String.valueOf(charSequence));

                        vProductoPropio.tvPrecioProducto.setText(dataAsignada.substring(0, posFinal + 8) + calcularMargen(valorVenta, valorIPVUsuario) + "%" );
                        //////////////////////////////////////////////////////////
                        //////////////////////////////////////////////////////////

                        listaProductosPropios.elementAt(vProductoPropio.position).cantidadAct = -1;
                        listaProductosPropios.elementAt(vProductoPropio.position).esModificado = false;

                        // SE HACE EL CAMBIO DE ICONO Y EL COLOR DEL BORDE DEL EDITTEXT
                        vProductoPropio.ivBotonCopiarCantidadAgotados.setImageResource(R.mipmap.iconook_gris);
                        vProductoPropio.llBordeCantidad.setBackground(ContextCompat.getDrawable(PreciosDisponibilidadActivity.this, R.drawable.edittext_rounded));
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {}
            });



            llListaProductosPropios.addView(vProductoPropio);
        }
    }

    private String calcularMargen(float valorVenta, float valorIPVUsuario) {

        String valorFinalMargen;

        if(valorVenta == 0 || valorIPVUsuario == 0) {

            valorFinalMargen = "0";

        } else {

            valorFinalMargen = Util.redondear((String.valueOf((1 - (valorVenta / valorIPVUsuario)) * 100)), 1);
        }

        return valorFinalMargen;
    }

    private void cargarListaProductosCompetencia(boolean nuevoProducto) {

        if(!nuevoProducto) {
            listaProductosCompetencia = DataBaseBO.obtenerListaProductosCompetencia(Main.cliente.codigo, Main.cliente.agencia, Main.cliente.GC4, idCategoria);
        }

        llListaProductosCompetencia = findViewById(R.id.llListaProductosCompetencia);
        llListaProductosCompetencia.removeAllViews();

        for(int pos = 0; pos < listaProductosCompetencia.size(); pos++) {

            // AGREGA AL LINEARLAYOUT UNA VISTA POR CADA ITEM DE LA LISTA
            final VistaItemListaProductosCompetenciaPrecio vProductoCompetencia = new VistaItemListaProductosCompetenciaPrecio(this);
            vProductoCompetencia.tvNombreProducto.setText(listaProductosCompetencia.elementAt(pos).nombre);
            vProductoCompetencia.position = pos;

            if(listaProductosCompetencia.elementAt(pos).esModificado == true) {

                if(listaProductosCompetencia.elementAt(pos).cantidadAct >= 0) {

                    vProductoCompetencia.etCantidadAgotado.setText(String.valueOf(listaProductosCompetencia.elementAt(pos).cantidadAct));
                }

            } else {

                vProductoCompetencia.etCantidadAgotado.setText("");

                if(listaProductosCompetencia.elementAt(pos).cantidadAnt >= 0) {
//                    if(nuevoProducto) {
//                        vProductoCompetencia.etCantidadAgotado.setText(String.valueOf(listaProductosCompetencia.elementAt(pos).cantidadAct));
//                    }else{
                        vProductoCompetencia.etCantidadAgotado.setHint(String.valueOf(listaProductosCompetencia.elementAt(pos).cantidadAnt));
//                    }

                }
            }
            String cant=vProductoCompetencia.etCantidadAgotado.getText().toString();
            String cant2=String.valueOf(vProductoCompetencia.etCantidadAgotado.getHint());
            String precioFinal= !cant.equals("")? cant : cant2;
            if(precioFinal != null && !precioFinal.equals("")) {

                String valorHint = precioFinal;
//                vProductoCompetencia.etCantidadAgotado.setText(valorHint);
                vProductoCompetencia.etCantidadAgotado.setHint(valorHint);

                if(!valorHint.equals("")) {

//                    listaProductosCompetencia.elementAt(vProductoCompetencia.position).cantidadAct = Util.toInt(valorHint);

                    int valorHintEntero = Util.toInt(valorHint);

                    if(valorHintEntero == 0) {
                        // SE HACE EL CAMBIO DE ICONO Y EL COLOR DEL BORDE DEL EDITTEXT
                        vProductoCompetencia.ivBotonCopiarCantidadAgotados.setImageResource(R.mipmap.iconook_gris);
                        vProductoCompetencia.llBordeCantidad.setBackground(ContextCompat.getDrawable(PreciosDisponibilidadActivity.this, R.drawable.edittext_rounded));

                    }else if(valorHintEntero > 1000 && nuevoProducto) {
//                    }else if(valorHintEntero > 0 && valorHintEntero < 1000) {

                        // SE HACE EL CAMBIO DE ICONO Y EL COLOR DEL BORDE DEL EDITTEXT
                        vProductoCompetencia.ivBotonCopiarCantidadAgotados.setImageResource(R.mipmap.iconook);
                        vProductoCompetencia.llBordeCantidad.setBackground(ContextCompat.getDrawable(PreciosDisponibilidadActivity.this, R.drawable.edittext_rounded_green));

                    } else {
                        // SE HACE EL CAMBIO DE ICONO Y EL COLOR DEL BORDE DEL EDITTEXT
                        vProductoCompetencia.ivBotonCopiarCantidadAgotados.setImageResource(R.mipmap.iconook_naranja);
                        vProductoCompetencia.llBordeCantidad.setBackground(ContextCompat.getDrawable(PreciosDisponibilidadActivity.this, R.drawable.edittext_rounded_orange));

                    }
                }
            }

            vProductoCompetencia.ivBotonCopiarCantidadAgotados.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    if(vProductoCompetencia.etCantidadAgotado.getHint() != null) {

                        String valorHint = String.valueOf(vProductoCompetencia.etCantidadAgotado.getHint());
                        vProductoCompetencia.etCantidadAgotado.setText(valorHint);

                        if(!valorHint.equals("")) {

                            listaProductosCompetencia.elementAt(vProductoCompetencia.position).esModificado = true;
                            listaProductosCompetencia.elementAt(vProductoCompetencia.position).cantidadAct = Util.toInt(valorHint);

                            int valorHintEntero = Util.toInt(valorHint);

                            if(valorHintEntero >= 0 && valorHintEntero < 1000) {

                                // SE HACE EL CAMBIO DE ICONO Y EL COLOR DEL BORDE DEL EDITTEXT
                                vProductoCompetencia.ivBotonCopiarCantidadAgotados.setImageResource(R.mipmap.iconook_naranja);
                                vProductoCompetencia.llBordeCantidad.setBackground(ContextCompat.getDrawable(PreciosDisponibilidadActivity.this, R.drawable.edittext_rounded_orange));

                            } else {

                                // SE HACE EL CAMBIO DE ICONO Y EL COLOR DEL BORDE DEL EDITTEXT
                                vProductoCompetencia.ivBotonCopiarCantidadAgotados.setImageResource(R.mipmap.iconook);
                                vProductoCompetencia.llBordeCantidad.setBackground(ContextCompat.getDrawable(PreciosDisponibilidadActivity.this, R.drawable.edittext_rounded_green));
                            }
                        }
                    }
                }
            });

            vProductoCompetencia.etCantidadAgotado.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    if(charSequence.length() > 0) {

                        listaProductosCompetencia.elementAt(vProductoCompetencia.position).cantidadAct = Util.toInt(String.valueOf(charSequence));
                        listaProductosCompetencia.elementAt(vProductoCompetencia.position).esModificado = true;

                        int valorIngresado = Util.toInt(String.valueOf(charSequence));

                        if(valorIngresado >= 0 && valorIngresado < 1000) {

                            // SE HACE EL CAMBIO DE ICONO Y EL COLOR DEL BORDE DEL EDITTEXT
                            vProductoCompetencia.ivBotonCopiarCantidadAgotados.setImageResource(R.mipmap.iconook_naranja);
                            vProductoCompetencia.llBordeCantidad.setBackground(ContextCompat.getDrawable(PreciosDisponibilidadActivity.this, R.drawable.edittext_rounded_orange));

                        } else {

                            // SE HACE EL CAMBIO DE ICONO Y EL COLOR DEL BORDE DEL EDITTEXT
                            vProductoCompetencia.ivBotonCopiarCantidadAgotados.setImageResource(R.mipmap.iconook);
                            vProductoCompetencia.llBordeCantidad.setBackground(ContextCompat.getDrawable(PreciosDisponibilidadActivity.this, R.drawable.edittext_rounded_green));
                        }

                    } else {

                        listaProductosCompetencia.elementAt(vProductoCompetencia.position).cantidadAct = -1;
                        listaProductosCompetencia.elementAt(vProductoCompetencia.position).esModificado = false;

                        // SE HACE EL CAMBIO DE ICONO Y EL COLOR DEL BORDE DEL EDITTEXT
                        vProductoCompetencia.ivBotonCopiarCantidadAgotados.setImageResource(R.mipmap.iconook_gris);
                        vProductoCompetencia.llBordeCantidad.setBackground(ContextCompat.getDrawable(PreciosDisponibilidadActivity.this, R.drawable.edittext_rounded));
                    }
                }



                @Override
                public void afterTextChanged(Editable editable) {}
            });

            vProductoCompetencia.llFondoProducto.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if(listaProductosCompetencia.elementAt(vProductoCompetencia.position).recienAgregado){
                        //ALERT
                        Alert.nutresaShow(PreciosDisponibilidadActivity.this, "INFORMACIÓN", "Desea eliminar el producto?",
                                "OK",
                                "CANCELAR",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view)
                                    {
                                       boolean result= DataBaseBO.eliminarProductoCompetencia(listaProductosCompetencia.elementAt(vProductoCompetencia.position).codigo);
                                       if (result){
                                           listaProductosCompetencia.removeElementAt(vProductoCompetencia.position);
                                           cargarListaProductosCompetencia(true);
                                       }

                                        Alert.dialogo.cancel();
                                    }

                                }, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        Alert.dialogo.cancel();
                                    }

                                });
                    }
                    return true;
                }

            });
            llListaProductosCompetencia.addView(vProductoCompetencia);
        }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        PreciosDisponibilidadActivity.this.finish();
    }

    public void on_ClickTerminarPrecios(View view) {

        // SE DETERMINA SI ALMENOS UNA DE LAS LISTAS TIENE INFORMACION
        if(listaProductosPropios.size() > 0 || listaProductosCompetencia.size() > 0) {

            boolean hayElementosSinCantidadPropios = false;
            boolean hayElementosSinCantidadCompetencia = false;

            boolean hayElementosCantidadPropiosMenorA = false;
            boolean hayElementosCantidadCompetenciaMenorA = false;

            for(int i = 0; i < listaProductosPropios.size(); i++) {

                if(listaProductosPropios.elementAt(i).cantidadAct <= 0 && listaProductosPropios.elementAt(i).seVende) {

                    hayElementosSinCantidadPropios = true;
                    break;
                }

                if(listaProductosPropios.elementAt(i).cantidadAct <= 999 && listaProductosPropios.elementAt(i).seVende) {

                    if(!(listaProductosPropios.elementAt(i).cantidadAct == 0)) {

                        hayElementosCantidadPropiosMenorA = true;
                        break;
                    }
                }
            }

            for(int i = 0; i < listaProductosCompetencia.size(); i++) {

                if(listaProductosCompetencia.elementAt(i).cantidadAct < 0) {

                    hayElementosSinCantidadCompetencia = true;
                    break;
                }

                if(listaProductosCompetencia.elementAt(i).cantidadAct <= 999) {

                    if(!(listaProductosCompetencia.elementAt(i).cantidadAct == 0)) {

                        hayElementosCantidadCompetenciaMenorA = true;
                        break;
                    }
                }
            }

            if(!hayElementosSinCantidadPropios && !hayElementosSinCantidadCompetencia) {

                if(!hayElementosCantidadPropiosMenorA && !hayElementosCantidadCompetenciaMenorA) {

                    terminarMedicion();

                } else {

                    PreciosDisponibilidadActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            //ALERT
                            Alert.nutresaShow(PreciosDisponibilidadActivity.this, "INFORMACIÓN", "Hay productos de la lista con precios menores a 4 dígitos.",
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

            } else {

                PreciosDisponibilidadActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        //ALERT
                        Alert.nutresaShow(PreciosDisponibilidadActivity.this, "INFORMACIÓN", "Hay productos de la lista sin precio asignado.",
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

            PreciosDisponibilidadActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    //ALERT
                    Alert.nutresaShow(PreciosDisponibilidadActivity.this, "INFORMACIÓN", "No hay información de la medición por enviar.",
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

        String codigoCliente = PreferencesCliente.obtenerCodigoClienteSeleccionado(getApplicationContext());
        Usuario usuario = Main.usuario;
        int tipoUsuario = DataBaseBO.obtenerTipoUsuario();
        String id = Util.obtenerId(Main.usuario.codigo);
        String fecha = Util.obtenerFechaActual();

        boolean guardoAgotados = DataBaseBO.guardarProductosPreciosDistribucionAgotados(codigoCliente, usuario, tipoUsuario, id, fecha, listaProductosPropios,idCategoria);

        boolean guardo = DataBaseBO.guardarProductosPrecios(codigoCliente, usuario, tipoUsuario, id, fecha, listaProductosPropios, listaProductosCompetencia,idCategoria);

        if(guardo && guardoAgotados ) {

            Alert.nutresaShow(PreciosDisponibilidadActivity.this, "INFORMACÓN", "La informacion se almaceno de forma exitosa.",
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
            Alert.nutresaShow(PreciosDisponibilidadActivity.this, "ERROR", "No se pudo guardar la medición de forma correcta.",
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
    public void respSync(boolean ok, String respuestaServer, String msg, int codeRequest) {

        try {

            Progress.hide();
            Alert.dialogo.cancel();

            if (ok) {

                PreciosDisponibilidadActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //ALERT
                        Alert.nutresaShow(PreciosDisponibilidadActivity.this, "INFORMACIÓN", "Información enviada de forma correcta.",
                                "OK", null,

                                new View.OnClickListener() {

                                    @Override
                                    public void onClick(View view) {

                                        Alert.dialogo.cancel();
                                        finish();
                                    }

                                }, null);
                    }
                });

            } else {

                PreciosDisponibilidadActivity.this.runOnUiThread(new Runnable() {

                    public void run() {

                        //ALERT
                        Alert.nutresaShow(PreciosDisponibilidadActivity.this, "ERROR", "No se pudo realizar el envio de información.",
                                "OK", null,

                                new View.OnClickListener() {

                                    @Override
                                    public void onClick(View view) {

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

    public void on_ClickAgregarProductoAgotados(View view) {

        Intent agregarProducto = new Intent(PreciosDisponibilidadActivity.this, BusquedaProductosActivity.class);
        startActivityForResult(agregarProducto, Const.AGREGAR_PRODUCTO);
    }

    public void on_ClickAgregarProductoCompetencia(View view) {

        Intent agregarProducto = new Intent(PreciosDisponibilidadActivity.this, AgregarProcutoCompretenciaActivity.class);
        agregarProducto.putExtra("IDCATEGORIA",idCategoria);
        startActivityForResult(agregarProducto, Const.AGREGAR_PRODUCTO_COMP);
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

                    Toasty.warning(PreciosDisponibilidadActivity.this, "El producto ya existe en la lista de agotados.", Toast.LENGTH_LONG).show();

                } else {

                    Toasty.success(PreciosDisponibilidadActivity.this, "El producto se agrego de forma exitosa.", Toast.LENGTH_LONG).show();

                    // SE AGREGA EL PRODUTO A LA LISTA DE PRODUCTOS AGOTADOS A AGREGAR
                    Producto productoAgotado = new Producto();
                    productoAgotado.nombre      = productoSelAgregar.nombre;
                    productoAgotado.codigo      = productoSelAgregar.codigo;
                    productoAgotado.cantidadAnt = 0;
                    productoAgotado.precioCliente = productoSelAgregar.precioCliente;

                    float[] descs;
                    descs = DataBaseBO.calcularDescuentosInventarioProducto(productoSelAgregar, false);
                    productoAgotado.Descuento = descs[0];
                    productoAgotado.ValorDesc = descs[1];

                    productoAgotado.PrecioFinal = ((productoAgotado.precioCliente * 1) - productoAgotado.ValorDesc) * (1 + (productoAgotado.Iva / 100)) ;
                    productoAgotado.recienAgregado = true;

                    ItemListViewAgotados item = new ItemListViewAgotados();
                    item.nombre       = productoSelAgregar.nombre;
                    item.codigo       = productoSelAgregar.codigo;
                    item.esModificado = false;

                    listaProductosPropios.addElement(productoAgotado);

                    cargarListaProductosPropios(true);
                }
            }
        }
        if(requestCode == Const.AGREGAR_PRODUCTO_COMP) {
            if(resultCode == Activity.RESULT_OK) {
                String objetoSeleccionadoAgregar = data.getStringExtra("OBJETOSEL");

                // SE PASA EL JSON A OBJETO
                Gson gsObject = new Gson();
                Producto productoSelAgregar = gsObject.fromJson(objetoSeleccionadoAgregar, Producto.class);

                boolean productoEsta = false;

                for (int i = 0; i < listaProductosCompetencia.size(); i++) {

                    if (listaProductosCompetencia.elementAt(i).codigo.equals(productoSelAgregar.codigo)) {

                        productoEsta = true;
                    }
                }

                if (productoEsta) {

                    Toasty.warning(PreciosDisponibilidadActivity.this, "El producto ya existe en la lista de agotados.", Toast.LENGTH_LONG).show();

                } else {

                    Toasty.success(PreciosDisponibilidadActivity.this, "El producto se agrego de forma exitosa.", Toast.LENGTH_LONG).show();

                    // SE AGREGA EL PRODUTO A LA LISTA DE PRODUCTOS AGOTADOS A AGREGAR
                    Producto productoAgotado = new Producto();
                    productoAgotado.nombre = productoSelAgregar.nombre;
                    productoAgotado.codigo = productoSelAgregar.codigo;
                    productoAgotado.cantidadAct = productoSelAgregar.precioCliente;

                    ItemListViewAgotados item = new ItemListViewAgotados();
                    item.nombre = productoSelAgregar.nombre;
                    item.codigo = productoSelAgregar.codigo;
                    item.esModificado = false;
                    productoAgotado.recienAgregado = true;

                    listaProductosCompetencia.addElement(productoAgotado);

                    cargarListaProductosCompetencia(true);
                }
            }
        }
    }
}