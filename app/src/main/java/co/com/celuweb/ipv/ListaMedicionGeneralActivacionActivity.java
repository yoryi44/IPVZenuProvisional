package co.com.celuweb.ipv;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Vector;

import businessObject.DataBaseBO;
import component.Alert;
import component.ListViewAdapterListaActivacionTerminada;
import component.ListViewAdapterListaAgotados;
import component.Util;
import config.Const;
import dataObject.Cliente;
import dataObject.ComponenteActivacionTerminado;
import dataObject.ItemListViewAgotados;
import dataObject.ItemListViewMedicionActivacion;
import dataObject.Main;
import sharedPreferences.PreferencesCliente;
import sharedPreferences.PreferencesObjetoActivacion;

public class ListaMedicionGeneralActivacionActivity extends AppCompatActivity {

    private TextView tvLabelRazonSocial;
    private ListView lvListaMedicionTerminada;
    private ItemListViewMedicionActivacion[] items;
    private ListViewAdapterListaActivacionTerminada adapter;
    private Vector<ItemListViewMedicionActivacion> listaItems;
    private Vector<ComponenteActivacionTerminado> listaMedicionActivacionTerminada;

    private String opcionTitulo;
    private int opcionActualLogica;
    private int opcionTipoSel;

    private String idMedicion = "";
    int posMedicionSeleccionada = -1;

    private final static String TAG = ListaMedicionGeneralActivacionActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_lista_medicion_general_activacion);

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
        cargarListaMedicionTerminada();
        setComponentesVista();

        // SE AGREGA LA LOGICA PARA SELECCIONAR MEDICIONES Y ELIMINAR
        setListinerListView();
    }

    private void cargarListaMedicionTerminada() {

        lvListaMedicionTerminada = findViewById(R.id.lvListaMedicionTerminada);

        listaItems = new Vector<>();
        listaMedicionActivacionTerminada = DataBaseBO.obtenerListaMedicionActivacionTerminada(listaItems, Main.cliente.codigo, opcionActualLogica, opcionTipoSel);

        if (listaItems.size() > 0) {

            items = new ItemListViewMedicionActivacion[listaItems.size()];
            listaItems.copyInto(items);

        } else {

            items = new ItemListViewMedicionActivacion[] {};

            if (listaMedicionActivacionTerminada != null)
                listaMedicionActivacionTerminada.removeAllElements();
        }

        adapter = new ListViewAdapterListaActivacionTerminada(ListaMedicionGeneralActivacionActivity.this, items);
        lvListaMedicionTerminada.setAdapter(adapter);
    }

    private void setListinerListView() {

        final ListView lvListaProductos = (ListView)findViewById(R.id.lvListaMedicionTerminada);
        lvListaProductos.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                posMedicionSeleccionada = position;
                final ComponenteActivacionTerminado medicionSel = listaMedicionActivacionTerminada.elementAt(posMedicionSeleccionada);
                boolean sePuedeBorrarMedicion = DataBaseBO.sePuedeBorrarMedicion(medicionSel);

                if(sePuedeBorrarMedicion) {

                    Alert.nutresaShow(ListaMedicionGeneralActivacionActivity.this, "MEDICIÓN", "Desea eliminar la medición seleccionada.",
                            "ACEPTAR",
                            "CANCELAR",
                            new View.OnClickListener() {

                                @Override
                                public void onClick(View view) {

                                    listaMedicionActivacionTerminada.remove(posMedicionSeleccionada);
                                    listaItems.remove(posMedicionSeleccionada);

                                    // SE AGREGA LA LOGICA DE ELIMINAR TODA LA MEDICION EXISTENTE
                                    DataBaseBO.eliminarMedicionGeneral(medicionSel);

                                    cargarListaMedicionTerminada();
                                    Alert.dialogo.cancel();
                                }

                            },
                            new View.OnClickListener() {

                                @Override
                                public void onClick(View view) {

                                    Alert.dialogo.cancel();
                                }
                            });

                } else {

                    // SE MUESTRA UN DIALOGO PARA PREGUNTAR POR CIERRE DE SESION
                    Alert.nutresaShow(ListaMedicionGeneralActivacionActivity.this, "MEDICIÓN", "La medición seleccionada no puede ser eliminada.",
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
        });
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

    private void cargarClienteSel() {

        if(Main.cliente == null) {

            Cliente cliente = DataBaseBO.obtenerClienteSeleccionado(PreferencesCliente.obtenerCodigoClienteSeleccionado(getApplicationContext()));
            Main.cliente = cliente;
        }

        tvLabelRazonSocial = findViewById(R.id.tvLabelRazonSocial);
        tvLabelRazonSocial.setText(String.valueOf(Main.cliente.razonSocial));
    }

    public void on_ClickAgregarMedicion(View view) {

        // SE ENVIA AL MODULO DE FOTOS CON LA LOGICA DE INICIO DE MEDICION ACTIVACION NUEVA
        idMedicion = Util.obtenerId(Main.usuario.codigo);

        if(opcionActualLogica == 1) {

            // SI HAY MEDICION
            Intent fotosActivacionActivity = new Intent(ListaMedicionGeneralActivacionActivity.this, FotosActivacionActivity.class);
            fotosActivacionActivity.putExtra("OPCIONTITULO", "PROPIA");
            fotosActivacionActivity.putExtra("OPCIONACTUALLOGICA", opcionActualLogica);
            fotosActivacionActivity.putExtra("OPCIONTIPOSEL", opcionTipoSel);

            // IDMEDICION PARA LA MEDICION GENERAL
            fotosActivacionActivity.putExtra("IDMEDICION", idMedicion);

            startActivity(fotosActivacionActivity);

            // LIMIAR CONTENEDORES LOGICA
            PreferencesObjetoActivacion.vaciarPreferencesObjetoActivacion(ListaMedicionGeneralActivacionActivity.this);

            finish();

        } else if(opcionActualLogica == 10) {

            // SI HAY MEDICION
            Intent fotosActivacionActivity = new Intent(ListaMedicionGeneralActivacionActivity.this, FotosActivacionActivity.class);
            fotosActivacionActivity.putExtra("OPCIONTITULO", "COMPETENCIA");
            fotosActivacionActivity.putExtra("OPCIONACTUALLOGICA", opcionActualLogica);
            fotosActivacionActivity.putExtra("OPCIONTIPOSEL", opcionTipoSel);

            // IDMEDICION PARA LA MEDICION GENERAL
            fotosActivacionActivity.putExtra("IDMEDICION", idMedicion);

            startActivity(fotosActivacionActivity);

            // LIMIAR CONTENEDORES LOGICA
            PreferencesObjetoActivacion.vaciarPreferencesObjetoActivacion(ListaMedicionGeneralActivacionActivity.this);

            finish();
        }
    }

    public void on_ClickTerminarMedicion(View view) {

        if(opcionActualLogica == 1) {

            if(opcionTipoSel == 2) {

                // LIMPIAR CONTENEDORES LOGICA
                PreferencesObjetoActivacion.vaciarPreferencesObjetoActivacion(ListaMedicionGeneralActivacionActivity.this);

                Intent activacionComecialPActivity = new Intent(ListaMedicionGeneralActivacionActivity.this, PreguntaFormatoGeneralActivacionActivity.class);
                activacionComecialPActivity.putExtra("OPCIONTITULO", "PROPIA");

                // PROPIO ES 1
                activacionComecialPActivity.putExtra("OPCIONACTUALLOGICA", opcionActualLogica);

                // PREGUNTA ES 1 - PRODUCTOS PROMOCION + CODIGO PROMOCION ES 2 -
                // PREGUNTA ES 3 - MATERIAL POP + CODIGO PROMOCION ES 4
                // PREGUNTA ES 5
                activacionComecialPActivity.putExtra("OPCIONTIPOSEL", 3);
                startActivity(activacionComecialPActivity);
                finish();

            } else if(opcionTipoSel == 4) {

                // LIMPIAR CONTENEDORES LOGICA
                PreferencesObjetoActivacion.vaciarPreferencesObjetoActivacion(ListaMedicionGeneralActivacionActivity.this);

                Intent activacionComecialPActivity = new Intent(ListaMedicionGeneralActivacionActivity.this, PreguntaFormatoGeneralActivacionActivity.class);
                activacionComecialPActivity.putExtra("OPCIONTITULO", "PROPIA");

                // PROPIO ES 1
                activacionComecialPActivity.putExtra("OPCIONACTUALLOGICA", opcionActualLogica);

                // PREGUNTA ES 1 - PRODUCTOS PROMOCION + CODIGO PROMOCION ES 2 -
                // PREGUNTA ES 3 - MATERIAL POP + CODIGO PROMOCION ES 4
                // PREGUNTA ES 5
                activacionComecialPActivity.putExtra("OPCIONTIPOSEL", 5);
                startActivity(activacionComecialPActivity);
                finish();
            }

        } else if(opcionActualLogica == 10) {

            if(opcionTipoSel == 2) {

                // LIMPIAR CONTENEDORES LOGICA
                PreferencesObjetoActivacion.vaciarPreferencesObjetoActivacion(ListaMedicionGeneralActivacionActivity.this);

                Intent activacionComecialCActivity = new Intent(ListaMedicionGeneralActivacionActivity.this, PreguntaFormatoGeneralActivacionActivity.class);
                activacionComecialCActivity.putExtra("OPCIONTITULO", "COMPETENCIA");

                // COMPETENCIA ES 10
                activacionComecialCActivity.putExtra("OPCIONACTUALLOGICA", opcionActualLogica);

                // PREGUNTA ES 1 - PRODUCTOS PROMOCION + CODIGO PROMOCION ES 2 -
                // PREGUNTA ES 3 - MATERIAL POP + CODIGO PROMOCION ES 4
                // PREGUNTA ES 5
                activacionComecialCActivity.putExtra("OPCIONTIPOSEL", 3);
                startActivity(activacionComecialCActivity);
                finish();

            } else if(opcionTipoSel == 4) {

                // LIMPIAR CONTENEDORES LOGICA
                PreferencesObjetoActivacion.vaciarPreferencesObjetoActivacion(ListaMedicionGeneralActivacionActivity.this);

                Intent activacionComecialCActivity = new Intent(ListaMedicionGeneralActivacionActivity.this, PreguntaFormatoGeneralActivacionActivity.class);
                activacionComecialCActivity.putExtra("OPCIONTITULO", "COMPETENCIA");

                // COMPETENCIA ES 10
                activacionComecialCActivity.putExtra("OPCIONACTUALLOGICA", opcionActualLogica);

                // PREGUNTA ES 1 - PRODUCTOS PROMOCION + CODIGO PROMOCION ES 2 -
                // PREGUNTA ES 3 - MATERIAL POP + CODIGO PROMOCION ES 4
                // PREGUNTA ES 5
                activacionComecialCActivity.putExtra("OPCIONTIPOSEL", 5);
                startActivity(activacionComecialCActivity);
                finish();
            }
        }
    }
}
