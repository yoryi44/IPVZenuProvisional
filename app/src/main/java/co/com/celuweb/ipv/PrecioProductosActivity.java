package co.com.celuweb.ipv;

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

import java.util.Vector;

import businessObject.DataBaseBO;
import component.Alert;
import component.Progress;
import component.Util;
import config.Const;
import config.Synchronizer;
import dataObject.Cliente;
import dataObject.Main;
import dataObject.Producto;
import dataObject.Usuario;
import sharedPreferences.PreferencesCliente;

public class PrecioProductosActivity extends AppCompatActivity implements Synchronizer {

    private TextView tvLabelRazonSocial;
    private LinearLayout llListaProductosPropios;
    private LinearLayout llListaProductosCompetencia;
    private Vector<Producto> listaProductosPropios;
    private Vector<Producto> listaProductosCompetencia;

    private FrameLayout flInformacionProductosPropiosPrecios;
    private FrameLayout flInformacionProductosCompetenciaPrecios;
    private String idCategoria="";

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
        tvTituloModulo.setText("PRECIO DE PRODUCTOS");

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
        cargarListaProductosPropios();
        cargarListaProductosCompetencia();
        setComponentesVista();

        // SE CAMBIA LA VISTA PARA QUE TENGA LOGICA DE TAB
        configurarTabsInformacionPrecioProductos();
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

                    // 2. SE OCULTA LA VISTA DE PRODUCTOS COMPETENCIA
                    flInformacionProductosCompetenciaPrecios = findViewById(R.id.flInformacionProductosCompetenciaPrecios);
                    flInformacionProductosCompetenciaPrecios.setVisibility(View.GONE);

                } else {

                    // 1. SE OCULTA LA VISTA DE PRODUCTOS PROPIOS
                    flInformacionProductosPropiosPrecios = findViewById(R.id.flInformacionProductosPropiosPrecios);
                    flInformacionProductosPropiosPrecios.setVisibility(View.GONE);

                    // 2. SE MUESTRA LA VISTA DE PRODUCTOS COMPETENCIA
                    flInformacionProductosCompetenciaPrecios = findViewById(R.id.flInformacionProductosCompetenciaPrecios);
                    flInformacionProductosCompetenciaPrecios.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void cargarListaProductosPropios() {

        listaProductosPropios = DataBaseBO.obtenerListaProductosPropios(Main.cliente.codigo, idCategoria);

        llListaProductosPropios = findViewById(R.id.llListaProductosPropios);
        llListaProductosPropios.removeAllViews();

        for(int pos = 0; pos < listaProductosPropios.size(); pos++) {

            // AGREGA AL LINEARLAYOUT UNA VISTA POR CADA ITEM DE LA LISTA
            final VistaItemListaProductosPropiosPrecio vProductoPropio = new VistaItemListaProductosPropiosPrecio(this);
            vProductoPropio.tvNombreProducto.setText(listaProductosPropios.elementAt(pos).nombre);
            vProductoPropio.tvPrecioProducto.setText("Precio: $" + Util.separarMilesSinDecimal(String.valueOf(listaProductosPropios.elementAt(pos).PrecioFinal)) + " - Margen: " + calcularMargen(listaProductosPropios.elementAt(pos).precioCliente, 0) + "%");
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
                                vProductoPropio.llBordeCantidad.setBackground(ContextCompat.getDrawable(PrecioProductosActivity.this, R.drawable.edittext_rounded_orange));

                            } else {

                                // SE HACE EL CAMBIO DE ICONO Y EL COLOR DEL BORDE DEL EDITTEXT
                                vProductoPropio.ivBotonCopiarCantidadAgotados.setImageResource(R.mipmap.iconook);
                                vProductoPropio.llBordeCantidad.setBackground(ContextCompat.getDrawable(PrecioProductosActivity.this, R.drawable.edittext_rounded_green));
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
                            vProductoPropio.llBordeCantidad.setBackground(ContextCompat.getDrawable(PrecioProductosActivity.this, R.drawable.edittext_rounded_orange));

                        } else {

                            // SE HACE EL CAMBIO DE ICONO Y EL COLOR DEL BORDE DEL EDITTEXT
                            vProductoPropio.ivBotonCopiarCantidadAgotados.setImageResource(R.mipmap.iconook);
                            vProductoPropio.llBordeCantidad.setBackground(ContextCompat.getDrawable(PrecioProductosActivity.this, R.drawable.edittext_rounded_green));
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
                        vProductoPropio.llBordeCantidad.setBackground(ContextCompat.getDrawable(PrecioProductosActivity.this, R.drawable.edittext_rounded));
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

    private void cargarListaProductosCompetencia() {

        listaProductosCompetencia = DataBaseBO.obtenerListaProductosCompetencia(Main.cliente.codigo, Main.cliente.agencia, Main.cliente.GC4, idCategoria);

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

                    vProductoCompetencia.etCantidadAgotado.setHint(String.valueOf(listaProductosCompetencia.elementAt(pos).cantidadAnt));
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
                                vProductoCompetencia.llBordeCantidad.setBackground(ContextCompat.getDrawable(PrecioProductosActivity.this, R.drawable.edittext_rounded_orange));

                            } else {

                                // SE HACE EL CAMBIO DE ICONO Y EL COLOR DEL BORDE DEL EDITTEXT
                                vProductoCompetencia.ivBotonCopiarCantidadAgotados.setImageResource(R.mipmap.iconook);
                                vProductoCompetencia.llBordeCantidad.setBackground(ContextCompat.getDrawable(PrecioProductosActivity.this, R.drawable.edittext_rounded_green));
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
                            vProductoCompetencia.llBordeCantidad.setBackground(ContextCompat.getDrawable(PrecioProductosActivity.this, R.drawable.edittext_rounded_orange));

                        } else {

                            // SE HACE EL CAMBIO DE ICONO Y EL COLOR DEL BORDE DEL EDITTEXT
                            vProductoCompetencia.ivBotonCopiarCantidadAgotados.setImageResource(R.mipmap.iconook);
                            vProductoCompetencia.llBordeCantidad.setBackground(ContextCompat.getDrawable(PrecioProductosActivity.this, R.drawable.edittext_rounded_green));
                        }

                    } else {

                        listaProductosCompetencia.elementAt(vProductoCompetencia.position).cantidadAct = -1;
                        listaProductosCompetencia.elementAt(vProductoCompetencia.position).esModificado = false;

                        // SE HACE EL CAMBIO DE ICONO Y EL COLOR DEL BORDE DEL EDITTEXT
                        vProductoCompetencia.ivBotonCopiarCantidadAgotados.setImageResource(R.mipmap.iconook_gris);
                        vProductoCompetencia.llBordeCantidad.setBackground(ContextCompat.getDrawable(PrecioProductosActivity.this, R.drawable.edittext_rounded));
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {}
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

        PrecioProductosActivity.this.finish();
    }

    public void on_ClickTerminarPrecios(View view) {

        // SE DETERMINA SI ALMENOS UNA DE LAS LISTAS TIENE INFORMACION
        if(listaProductosPropios.size() > 0 || listaProductosCompetencia.size() > 0) {

            boolean hayElementosSinCantidadPropios = false;
            boolean hayElementosSinCantidadCompetencia = false;

            boolean hayElementosCantidadPropiosMenorA = false;
            boolean hayElementosCantidadCompetenciaMenorA = false;

            for(int i = 0; i < listaProductosPropios.size(); i++) {

                if(listaProductosPropios.elementAt(i).cantidadAct < 0) {

                    hayElementosSinCantidadPropios = true;
                    break;
                }

                if(listaProductosPropios.elementAt(i).cantidadAct <= 999) {

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

                    PrecioProductosActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            //ALERT
                            Alert.nutresaShow(PrecioProductosActivity.this, "INFORMACIÓN", "Hay productos de la lista con precios menores a 4 dígitos.",
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

                PrecioProductosActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        //ALERT
                        Alert.nutresaShow(PrecioProductosActivity.this, "INFORMACIÓN", "Hay productos de la lista sin precio asignado.",
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

            PrecioProductosActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    //ALERT
                    Alert.nutresaShow(PrecioProductosActivity.this, "INFORMACIÓN", "No hay información de la medición por enviar.",
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

        boolean guardo = DataBaseBO.guardarProductosPrecios(codigoCliente, usuario, tipoUsuario, id, fecha, listaProductosPropios, listaProductosCompetencia,idCategoria);

        if(guardo) {

            Alert.nutresaShow(PrecioProductosActivity.this, "INFORMACÓN", "La informacion se almaceno de forma exitosa.",
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
            Alert.nutresaShow(PrecioProductosActivity.this, "ERROR", "No se pudo guardar la medición de forma correcta.",
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

                PrecioProductosActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //ALERT
                        Alert.nutresaShow(PrecioProductosActivity.this, "INFORMACIÓN", "Información enviada de forma correcta.",
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

                PrecioProductosActivity.this.runOnUiThread(new Runnable() {

                    public void run() {

                        //ALERT
                        Alert.nutresaShow(PrecioProductosActivity.this, "ERROR", "No se pudo realizar el envio de información.",
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
}
