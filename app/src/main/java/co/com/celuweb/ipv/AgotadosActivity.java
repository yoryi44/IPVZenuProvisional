package co.com.celuweb.ipv;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Vector;

import businessObject.DataBaseBO;
import component.Alert;
import component.ListViewAdapterListaAgotados;
import component.Progress;
import component.Util;
import config.Const;
import config.Synchronizer;
import dataObject.Cliente;
import dataObject.ItemListViewAgotados;
import dataObject.Main;
import dataObject.Producto;
import dataObject.ProductoAgotado;
import dataObject.Usuario;
import es.dmoral.toasty.Toasty;
import sharedPreferences.PreferencesCliente;

public class AgotadosActivity extends AppCompatActivity implements Synchronizer {

    private TextView tvLabelRazonSocial;
    private TextView tvLabelNoVenta;
    private TextView tvLabelAgotado;
    private LinearLayout llListaProductosAgotado;
    private Vector<ProductoAgotado> listaProductosAgotados;
    private ListViewAdapterListaAgotados adapter;
    private ItemListViewAgotados[] items;

    private final static String TAG = AgotadosActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_agotados);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        setSupportActionBar(toolbar);

        // SE INICIALIZA EL COMPONENTE PRINCIPAL DE LA APLICACION
        Main.contexto = getApplicationContext();

        TextView tvTituloModulo = findViewById(R.id.tvTituloModulo);
        tvTituloModulo.setTypeface(Const.letraSemibold);
        tvTituloModulo.setText("DISTRIBUCIÓN Y AGOTADOS");

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        cargarClienteSel();
        cargarListaProductosAgotados(false);
        setComponentesVista();
    }

    private void setComponentesVista() {

        tvLabelRazonSocial = findViewById(R.id.tvLabelRazonSocial);
        tvLabelRazonSocial.setTypeface(Const.letraSemibold);

        tvLabelNoVenta = findViewById(R.id.tvLabelNoVenta);
        tvLabelNoVenta.setTypeface(Const.letraSemibold);

        tvLabelAgotado = findViewById(R.id.tvLabelAgotado);
        tvLabelAgotado.setTypeface(Const.letraSemibold);
    }

    private void cargarListaProductosAgotados(boolean nuevoProducto) {

        llListaProductosAgotado = findViewById(R.id.llListaProductosAgotado);
        llListaProductosAgotado.removeAllViews();

        if(!nuevoProducto) {

            listaProductosAgotados = DataBaseBO.obtenerListaProductosAgotados(Main.cliente.codigo);
        }

        for(int pos = 0; pos < listaProductosAgotados.size(); pos++) {

            // AGREGA AL LINEARLAYOUT UNA VISTA POR CADA ITEM DE LA LISTA
            final VistaItemListaProductosAgotados vProductosAgotados = new  VistaItemListaProductosAgotados(this);
            vProductosAgotados.tvNombreProducto.setText(listaProductosAgotados.elementAt(pos).nombre);
            vProductosAgotados.position = pos;

            if(listaProductosAgotados.elementAt(pos).esModificado == true) {

                vProductosAgotados.ivBotonCopiarCantidadAgotados.setImageResource(R.mipmap.iconook);

            } else {

                vProductosAgotados.ivBotonCopiarCantidadAgotados.setImageResource(R.mipmap.iconook_gris);
            }

            vProductosAgotados.ivBotonCopiarCantidadAgotados.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    if(listaProductosAgotados.elementAt(vProductosAgotados.position).esModificado == true) {

                        listaProductosAgotados.elementAt(vProductosAgotados.position).esModificado = false;
                        listaProductosAgotados.elementAt(vProductosAgotados.position).cantidadAct = 0;
                        vProductosAgotados.ivBotonCopiarCantidadAgotados.setImageResource(R.mipmap.iconook_gris);

                    } else {

                        listaProductosAgotados.elementAt(vProductosAgotados.position).esModificado = true;
                        listaProductosAgotados.elementAt(vProductosAgotados.position).cantidadAct = 1;
                        vProductosAgotados.ivBotonCopiarCantidadAgotados.setImageResource(R.mipmap.iconook);
                    }
                }
            });

            vProductosAgotados.ivProductoAgotado.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    if(vProductosAgotados.vendido) {

                        vProductosAgotados.vendido = false;
                        vProductosAgotados.llFondoProducto.setBackgroundColor(getResources().getColor(R.color.colorRojoFondo));
                        listaProductosAgotados.elementAt(vProductosAgotados.position).seVende = false;
                        vProductosAgotados.ivBotonCopiarCantidadAgotados.setVisibility(View.INVISIBLE);

                    } else {

                        vProductosAgotados.vendido = true;
                        vProductosAgotados.llFondoProducto.setBackgroundColor(Color.WHITE);
                        listaProductosAgotados.elementAt(vProductosAgotados.position).seVende = true;
                        vProductosAgotados.ivBotonCopiarCantidadAgotados.setVisibility(View.VISIBLE);
                    }
                }
            });

            llListaProductosAgotado.addView(vProductosAgotados);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void cargarClienteSel() {

        if(Main.cliente == null) {

            Cliente cliente = DataBaseBO.obtenerClienteSeleccionado(PreferencesCliente.obtenerCodigoClienteSeleccionado(getApplicationContext()));
            Main.cliente = cliente;
        }

        tvLabelRazonSocial = findViewById(R.id.tvLabelRazonSocial);
        tvLabelRazonSocial.setText(String.valueOf(Main.cliente.razonSocial));
    }

    public void on_ClickGuardarAgotados(View view) {

        // SE DETERMINA SI LA LISTA TIENE INFORMACION
        if(listaProductosAgotados.size() > 0) {

            boolean hayElementosSinCantidad = false;

            // SE ASIGNA A CADA ITEM EL VALOR CANTIDAD QUE LE CORRESPONDE
            for(int i = 0; i < listaProductosAgotados.size(); i++) {

                if(listaProductosAgotados.elementAt(i).cantidadAct < 0 && listaProductosAgotados.elementAt(i).seVende == true) {

                    hayElementosSinCantidad = true;
                }
            }

            if(!hayElementosSinCantidad) {

                terminarMedicion();

            } else {

                AgotadosActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        //ALERT
                        Alert.nutresaShow(AgotadosActivity.this, "INFORMACIÓN", "Hay productos de la lista sin cantidad asignada.",
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

            AgotadosActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    //ALERT
                    Alert.nutresaShow(AgotadosActivity.this, "INFORMACIÓN", "No hay información de la medición por enviar.",
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

        // SE OBTIENE EL CODIGO DE LA MEDICION ANTERIOR PARA EL CLIENTE EN ESTE MODULO
        String idMedidcionAnterior = DataBaseBO.obtenerIdMedicionAnteriorAgotados(Main.cliente.codigo);

        String codigoCliente = PreferencesCliente.obtenerCodigoClienteSeleccionado(getApplicationContext());
        Usuario usuario = Main.usuario;
        int tipoUsuario = DataBaseBO.obtenerTipoUsuario();
        String id = Util.obtenerId(Main.usuario.codigo);
        String fecha = Util.obtenerFechaActual();

        boolean guardo = DataBaseBO.guardarProductosAgotados(codigoCliente, usuario, tipoUsuario, id, fecha, listaProductosAgotados);

        if(guardo) {

            DataBaseBO.eliminarMedicionAgotadosClienteAnterior(idMedidcionAnterior);

            finish();
//            Sync sync = new Sync(AgotadosActivity.this, Const.ENVIAR_INFO);
//            Progress.show(AgotadosActivity.this, "Sincronizador", "Enviando Informacion...", false);
//            sync.start();


        } else {

            //ALERT
            Alert.nutresaShow(AgotadosActivity.this, "ERROR", "No se pudo guardar la medición de forma correcta.",
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

    public void on_ClickAgregarProductoAgotados(View view) {

        Intent agregarProducto = new Intent(AgotadosActivity.this, BusquedaProductosActivity.class);
        startActivityForResult(agregarProducto, Const.AGREGAR_PRODUCTO);
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

                for(int i = 0; i < listaProductosAgotados.size(); i++) {

                    if(listaProductosAgotados.elementAt(i).codigo.equals(productoSelAgregar.codigo)) {

                        productoEsta = true;
                    }
                }

                if(productoEsta) {

                    Toasty.warning(AgotadosActivity.this, "El producto ya existe en la lista de agotados.", Toast.LENGTH_LONG).show();

                } else {

                    Toasty.success(AgotadosActivity.this, "El producto se agrego de forma exitosa.", Toast.LENGTH_LONG).show();

                    // SE AGREGA EL PRODUTO A LA LISTA DE PRODUCTOS AGOTADOS A AGREGAR
                    ProductoAgotado productoAgotado = new ProductoAgotado();
                    productoAgotado.nombre      = productoSelAgregar.nombre;
                    productoAgotado.codigo      = productoSelAgregar.codigo;
                    productoAgotado.cantidadAnt = 0;
                    productoAgotado.cantidadAct = 0;

                    ItemListViewAgotados item = new ItemListViewAgotados();
                    item.nombre       = productoSelAgregar.nombre;
                    item.codigo       = productoSelAgregar.codigo;
                    item.cantidadAnt  = 0;
                    item.cantidadAct  = 0;
                    item.esModificado = false;

                    listaProductosAgotados.addElement(productoAgotado);

                    cargarListaProductosAgotados(true);
                }
            }
        }
    }

    @Override
    public void respSync(boolean ok, String respuestaServer, String msg, int codeRequest) {

        try {

            Progress.hide();

            if (ok) {

                AgotadosActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //ALERT
                        Alert.nutresaShow(AgotadosActivity.this, "INFORMACIÓN", "Información enviada de forma correcta.",
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

                AgotadosActivity.this.runOnUiThread(new Runnable() {

                    public void run() {

                        //ALERT
                        Alert.nutresaShow(AgotadosActivity.this, "ALERTA", "No se pudo realizar el envio de información.",
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
    public void onBackPressed() {

        AgotadosActivity.this.finish();
    }
}
