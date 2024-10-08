package co.com.celuweb.ipv;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Vector;

import businessObject.DataBaseBO;
import component.Alert;
import component.ListViewAdapterListaProductos;
import component.Progress;
import component.Util;
import config.Const;
import config.Synchronizer;
import dataObject.Cliente;
import dataObject.ItemListViewProductos;
import dataObject.Main;
import dataObject.ObjetoActivacion;
import dataObject.Producto;
import es.dmoral.toasty.Toasty;
import sharedPreferences.PreferencesCliente;
import sharedPreferences.PreferencesObjetoActivacion;

public class ProductosFormatoGeneralActivacionActivity extends AppCompatActivity implements Synchronizer {

    private String opcionTitulo;
    private int opcionActualLogica;
    private int opcionTipoSel;
    private TextView tvLabelRazonSocial;

    private ImageView ivImagenTipoSeleccionado;
    private TextView tvTituloTipoSeleccionado;

    private ListView lvListaGeneralProductosActivacion;
    private Vector<Producto> listaProductosAgregados;
    private ItemListViewProductos[] items;
    private ListViewAdapterListaProductos adapter;
    private Vector<ItemListViewProductos> listaItems;

    String idMedicion = "";
    String codigoComponenteSeleccionado = "";
    String nombreComponenteSeleccionado = "";
    int posProductoSeleccionado = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_productos_formato_general_activacion);

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
        cargarListaProductosActivacion(false);

        // SE AGREGA LA LOGICA PARA SELECCIONAR PRODUCTOS Y ELIMINAR
        setListinerListView();
    }

    private void setComponentesVista() {

        ivImagenTipoSeleccionado = findViewById(R.id.ivImagenTipoSeleccionado);

        tvTituloTipoSeleccionado = findViewById(R.id.tvTituloTipoSeleccionado);
        tvTituloTipoSeleccionado.setTypeface(Const.letraSemibold);

        if(opcionTipoSel == 6) {

            Button btnFinalizarMedicion = findViewById(R.id.btnFinalizarMedicion);
            btnFinalizarMedicion.setText("FINALIZAR");
        }
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

            if (extras.containsKey("CODIGOCOMPONENTESELECCIONADO"))
                codigoComponenteSeleccionado = extras.getString("CODIGOCOMPONENTESELECCIONADO");

            if (extras.containsKey("NOMBRECOMPONENTESELECCIONADO"))
                nombreComponenteSeleccionado = extras.getString("NOMBRECOMPONENTESELECCIONADO");
        }

        tvTituloTipoSeleccionado = findViewById(R.id.tvTituloTipoSeleccionado);

        if(opcionTipoSel == 6) {

            tvTituloTipoSeleccionado.setText("SELECCIONE LOS PRODUCTOS RELACIONADOS CON LOS IMPULSADORES");

        } else {

            tvTituloTipoSeleccionado.setText("SELECCIONE LOS PRODUCTOS RELACIONADOS CON " + (((opcionActualLogica <= 2) ? "LA PROMOCIÓN " : " EL MATERIAL POP ")) + (nombreComponenteSeleccionado.toUpperCase()));
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

    public void on_ClickAgregarProductoActivacion(View view) {

        Intent agregarProducto = new Intent(ProductosFormatoGeneralActivacionActivity.this, BusquedaProductosActivity.class);
        agregarProducto.putExtra("OPCIONACTUALLOGICA", opcionActualLogica);
        agregarProducto.putExtra("MOSTRAROPCIONCOMPETENCIA", true);
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

                for(int i = 0; i < listaProductosAgregados.size(); i++) {

                    if(listaProductosAgregados.elementAt(i).codigo.equals(productoSelAgregar.codigo)) {

                        productoEsta = true;
                    }
                }

                if(productoEsta) {

                    Toasty.warning(ProductosFormatoGeneralActivacionActivity.this, "El producto ya existe en la lista de agotados.", Toast.LENGTH_SHORT).show();

                } else {

                    Toasty.success(ProductosFormatoGeneralActivacionActivity.this, "El producto se agrego de forma exitosa.", Toast.LENGTH_SHORT).show();

                    // SE AGREGA EL PRODUTO A LA LISTA DE PRODUCTOS AGOTADOS A AGREGAR
                    Producto productoAgregado = new Producto();
                    productoAgregado.nombre   = productoSelAgregar.nombre;
                    productoAgregado.codigo   = productoSelAgregar.codigo;

                    ItemListViewProductos item = new ItemListViewProductos();
                    item.nombre         = productoAgregado.nombre;
                    item.codigo         = productoAgregado.codigo;

                    listaProductosAgregados.addElement(productoAgregado);
                    listaItems.add(item);

                    cargarListaProductosActivacion(true);
                }
            }
        }
    }

    private void cargarListaProductosActivacion(boolean esRecarga) {

        lvListaGeneralProductosActivacion = findViewById(R.id.lvListaGeneralProductosActivacion);

        if(!esRecarga) {

            listaItems = new Vector<>();
            listaProductosAgregados = new Vector<>();
        }

        if (listaItems.size() > 0) {

            items = new ItemListViewProductos[listaItems.size()];
            listaItems.copyInto(items);

        } else {

            items = new ItemListViewProductos[] {};

            if (listaProductosAgregados != null)
                listaProductosAgregados.removeAllElements();
        }

        adapter = new ListViewAdapterListaProductos(ProductosFormatoGeneralActivacionActivity.this, items);
        lvListaGeneralProductosActivacion.setAdapter(adapter);
    }

    private void setListinerListView() {

        final ListView lvListaProductos = (ListView)findViewById(R.id.lvListaGeneralProductosActivacion);
        lvListaProductos.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                posProductoSeleccionado = position;

                // SE MUESTRA UN DIALOGO PARA PREGUNTAR POR CIERRE DE SESION
                Alert.nutresaShow(ProductosFormatoGeneralActivacionActivity.this, "PRODUCTO", "Desea eliminar producto seleccionado.",
                        "ACEPTAR",
                        "CANCELAR",
                        new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {

                                listaProductosAgregados.remove(posProductoSeleccionado);
                                listaItems.remove(posProductoSeleccionado);
                                cargarListaProductosActivacion(true);
                                Alert.dialogo.cancel();
                            }

                        },
                        new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {

                                Alert.dialogo.cancel();
                            }
                        });
            }
        });
    }

    public void on_ClickContinuarOpcionGeneral(View view) {

        /** SE REALIZA EL GUARDADO DE LA DATA DE LA MEDICION COMPLETA
         ************************************************************/

        // SE DETERMINA SI LA MEDICION TIENE PRODUCTOS AGREGADOS,
        // DE LO CONTRARIO SE MUESTRA MENSAJE PARA AGREGAR PRODUCTOS
        int cantidadProductos = listaProductosAgregados.size();

        if(cantidadProductos > 0) {

            // SE PRECARGA LA INFORMACION DE LA MEDICION ANTES ALMACENADA EN EL SHAREDPREFERENCES

            // 1. SE OBTIENE EL OBJETO A STRING
            String objetoListaRespuestasActivacion = PreferencesObjetoActivacion.obtenerObjetoActivacion(this);
            ArrayList<ObjetoActivacion> listaRespuestaActivacion = null;

            if(objetoListaRespuestasActivacion.isEmpty()) {

                listaRespuestaActivacion = new ArrayList<>();

            } else {

                // 2. SE PASA EL OBJETO RESPUESTA A TIPO ARRAYLIST
                Gson gsObject = new Gson();
                listaRespuestaActivacion = gsObject.fromJson(objetoListaRespuestasActivacion, new TypeToken<ArrayList<ObjetoActivacion>>(){}.getType());
            }

            // 3. SE CREA POR CADA PRODUCTO UNA RESPUESTA Y SE ALMACENA EN EL ARRAYLIST DE RESPUESTAS
            if(opcionActualLogica == 1) {

                for(int i = 0; i < listaProductosAgregados.size(); i++) {

                    String codigoProducto = listaProductosAgregados.elementAt(i).codigo;
                    String nombreProducto = listaProductosAgregados.elementAt(i).nombre;

                    ObjetoActivacion objetoActivacion = new ObjetoActivacion();
                    objetoActivacion.codigoCliente  = Main.cliente.codigo;
                    objetoActivacion.codigoUsuario  = Main.usuario.codigo;
                    objetoActivacion.nombreUsuario  = Main.usuario.nombre;
                    objetoActivacion.tipoUsuario    = String.valueOf(DataBaseBO.obtenerTipoUsuario());
                    objetoActivacion.id             = idMedicion;
                    objetoActivacion.tipoOpcion     = String.valueOf(opcionTipoSel);
                    objetoActivacion.valor1         = codigoComponenteSeleccionado;
                    objetoActivacion.nombre1        = nombreComponenteSeleccionado;
                    objetoActivacion.codigoProducto = codigoProducto;
                    objetoActivacion.nombreProducto = nombreProducto;
                    objetoActivacion.core           = "0";
                    objetoActivacion.propio         = "1";
                    objetoActivacion.competencia    = "0";
                    objetoActivacion.fechaMovil     = Util.obtenerFechaActual();

                    listaRespuestaActivacion.add(objetoActivacion);
                }

            } else if(opcionActualLogica == 10) {

                for(int i = 0; i < listaProductosAgregados.size(); i++) {

                    String codigoProducto = listaProductosAgregados.elementAt(i).codigo;
                    String nombreProducto = listaProductosAgregados.elementAt(i).nombre;

                    ObjetoActivacion objetoActivacion = new ObjetoActivacion();
                    objetoActivacion.codigoCliente  = Main.cliente.codigo;
                    objetoActivacion.codigoUsuario  = Main.usuario.codigo;
                    objetoActivacion.nombreUsuario  = Main.usuario.nombre;
                    objetoActivacion.tipoUsuario    = String.valueOf(DataBaseBO.obtenerTipoUsuario());
                    objetoActivacion.id             = idMedicion;
                    objetoActivacion.tipoOpcion     = String.valueOf(opcionTipoSel);
                    objetoActivacion.valor1         = codigoComponenteSeleccionado;
                    objetoActivacion.nombre1        = nombreComponenteSeleccionado;
                    objetoActivacion.codigoProducto = codigoProducto;
                    objetoActivacion.nombreProducto = nombreProducto;
                    objetoActivacion.core           = "0";
                    objetoActivacion.propio         = "0";
                    objetoActivacion.competencia    = "1";
                    objetoActivacion.fechaMovil     = Util.obtenerFechaActual();

                    listaRespuestaActivacion.add(objetoActivacion);
                }
            }

            // 4. SE ALMACENA LA INFORMACION DE LAS RESPUESTAS EN LA BASE DE DATOS
            boolean guardoMedicionActivacion = DataBaseBO.almacenarRegistroActivacion(listaRespuestaActivacion, Main.cliente.codigo);

            if(guardoMedicionActivacion) {

                if(opcionTipoSel == 6) {

                    // SE CIERRA Y SE REALIZA EL ENVIO DE LA INFORMACION
//                    Sync sync = new Sync(ProductosFormatoGeneralActivacionActivity.this, Const.ENVIAR_INFO);
//                    Progress.show(ProductosFormatoGeneralActivacionActivity.this, "Sincronizador", "Enviando Informacion...", false);
//                    sync.start();
                    finish();

                } else {

                    Intent listaMedicionGeneralActivacionActivity = new Intent(ProductosFormatoGeneralActivacionActivity.this, ListaMedicionGeneralActivacionActivity.class);
                    listaMedicionGeneralActivacionActivity.putExtra("OPCIONACTUALLOGICA", opcionActualLogica);
                    listaMedicionGeneralActivacionActivity.putExtra("OPCIONTIPOSEL", opcionTipoSel);

                    if(opcionActualLogica == 1) {

                        listaMedicionGeneralActivacionActivity.putExtra("OPCIONTITULO", "PROPIA");

                    } else if(opcionActualLogica == 10) {

                        listaMedicionGeneralActivacionActivity.putExtra("OPCIONTITULO", "COMPETENCIA");
                    }

                    // SE ENVIA LA RESPUESTA DE CIERRE AL MODULO ANTERIOR
                    Intent returnIntent = getIntent();
                    setResult(RESULT_OK, returnIntent);

                    startActivity(listaMedicionGeneralActivacionActivity);
                    finish();
                }

            } else {

                //ALERT
                Alert.nutresaShow(ProductosFormatoGeneralActivacionActivity.this, "ERROR", "No se pudo guardar la medición de forma correcta.",
                        "ACEPTAR",
                        null,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Alert.dialogo.cancel();

                                // SE ENVIA LA RESPUESTA DE CIERRE AL MODULO ANTERIOR
                                Intent returnIntent = getIntent();
                                setResult(RESULT_OK, returnIntent);

                                finish();
                            }

                        }, null);
            }

        } else {

            Toasty.warning(ProductosFormatoGeneralActivacionActivity.this, "No hay productos para la medición actual.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void respSync(boolean ok, String respuestaServer, String msg, int codeRequest) {

        if(codeRequest == Const.ENVIAR_INFO) {

            respuestaEnvioInformacion(ok, respuestaServer, msg);
        }
    }

    private void respuestaEnvioInformacion(boolean ok, String respuestaServer, String msg) {

        try {

            Progress.hide();

            if(Alert.dialogo != null) {

                Alert.dialogo.cancel();
            }


            if (ok) {

                ProductosFormatoGeneralActivacionActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //ALERT
                        Alert.nutresaShow(ProductosFormatoGeneralActivacionActivity.this, "INFORMACIÓN", "Información enviada de forma correcta.",
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

                ProductosFormatoGeneralActivacionActivity.this.runOnUiThread(new Runnable() {

                    public void run() {

                        //ALERT
                        Alert.nutresaShow(ProductosFormatoGeneralActivacionActivity.this, "ERROR", "No se pudo realizar el envio de información.",
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
        }
    }
}
