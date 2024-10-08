package co.com.celuweb.ipv;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.aakira.expandablelayout.ExpandableLinearLayout;

import java.io.File;
import java.util.Calendar;
import java.util.Vector;

import businessObject.ConfigBO;
import businessObject.DataBaseBO;
import component.Alert;
import component.ListViewAdapterListaClientes;
import component.Progress;
import component.Util;
import config.Const;
import config.Synchronizer;
import dataObject.ActividadesCliente;
import dataObject.Cliente;
import dataObject.Config;
import dataObject.ItemListViewClientes;
import dataObject.Main;
import dataObject.Usuario;
import dataObject.Vendedor;
import es.dmoral.toasty.Toasty;
import sharedPreferences.PreferencesCliente;
import synchronizer.Sync;

public class PrincipalActivity extends AppCompatActivity implements Synchronizer {

    private Spinner spDiasSemana;
    private EditText etOpcionBusqueda;
    private Vector<String> listaDiasRutero;
    private Vector<Cliente> listaClientes;
    private RecyclerView lvListaClientes;
    private TextView tvVersionApp;

    private CheckBox cbVisitados;
    private LinearLayout llCheckVisitados;

    private ListViewAdapterListaClientes adapter;

    private Vector<Vendedor> listaVendedoresSincronizacion;

    private ImageView ivIndicadorLista;
    private LinearLayout llContenedorListaVendedores;
    private ExpandableLinearLayout expandableLayout;
    boolean estado = false;

    private static final String CERO = "0";
    private static final String BARRA = "/";

    //Calendario para obtener fecha & hora
    public final Calendar c = Calendar.getInstance();

    //Variables para obtener la fecha
    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);

    int fechaSelAnio = 0;
    int fechaSelMes = 0;
    int fechaSelDia = 0;

    public static Dialog dialogo;

    private final static String TAG = PrincipalActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_principal);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        //navigationView.setNavigationItemSelectedListener(this);

        // SE INICIALIZA EL COMPONENTE PRINCIPAL DE LA APLICACION
        Main.contexto = getApplicationContext();

        TextView tvTituloModulo = findViewById(R.id.tvTituloModulo);
        tvTituloModulo.setTypeface(Const.letraSemibold);
        tvTituloModulo.setText("RUTERO");

        ConfigBO.CrearConfigDB();

    }

    @Override
    protected void onResume() {

        super.onResume();
        // CONFIGURACION INFORMACION DE USUARIO - MENU LATERAL
        configuracionVisitados();
        setComponentesInterfaz();
        setMenuLateral();
        ConfigBO.CrearConfigDB();

    }

    private void setComponentesInterfaz() {

        etOpcionBusqueda = findViewById(R.id.etOpcionBusqueda);
        etOpcionBusqueda.setTypeface(Const.letraRegular);

        etOpcionBusqueda.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

                String parametroBusqueda = s.toString();
//                buscarClientes(parametroBusqueda, true);
                filtroBusqueda(parametroBusqueda);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
        });

        lvListaClientes = findViewById(R.id.lvListaClientes);
        setListinerListView();

        // SI HAY BASE DE DATOS SE CARGA LA INFORMACION
        if(!Util.existeArchivoDataBase()) {

            // SE DETERMINA SI HAY INFORMACION POR ENVIAR
            if(!DataBaseBO.hayInformacionXEnviar()) {

                cargarDiasRutero(true);

                // SE OBTIENEN LA LISTA DE LOS VENDEDORES COMPLETA
                Main.listaVendedores = DataBaseBO.obtenerListaDeVendedores();

                if(Main.usuario.tipo != "4" && Main.listaVendedores.size() < 2) {

                    Sync sync = new Sync(PrincipalActivity.this, Const.DOWNLOAD_VENDEDORES);
                    Progress.show(PrincipalActivity.this, "Sincronizador", "Descargando...", false);
                    sync.start();
                }
            }

        } else {

            // SI NO HAY BASE DE DATOS SE INICIA DIA DE FORMA AUTOMATICA
            cargarDiasRutero(false);
        }
    }

    private void setMenuLateral() {

        Usuario usuarioApp = Main.usuario;

        String nombre = usuarioApp.nombre;
        String codigo = usuarioApp.codigo;

        TextView tvCodigoPanelLateral = findViewById(R.id.tvCodigoPanelLateral);
        tvCodigoPanelLateral.setText(codigo);

        TextView tvNombrePanelLateral = findViewById(R.id.tvNombrePanelLateral);
        tvNombrePanelLateral.setText(nombre);

        //****************************************************************************************//
        //******************* SE AGRGA EL DISEÑO DE LOS ITEM DEL MENU LATERAL ********************//
        //****************************************************************************************//

        // OPCION CLIENTES
        LinearLayout llOpcionClientes = findViewById(R.id.llOpcionClientes);
        llOpcionClientes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        TextView tvOpcionClientes = findViewById(R.id.tvOpcionClientes);
        tvOpcionClientes.setTypeface(Const.letraRegular);

        // OPCION CONFIGURACION
        expandableLayout = findViewById(R.id.expandableLayout);
        ivIndicadorLista = findViewById(R.id.ivIndicadorLista);

        LinearLayout llOpcionConfiguracion = findViewById(R.id.llOpcionConfiguracion);

        //****************************************************************************************//
        //******************* SE AGRGA EL DISEÑO DE LOS ITEM DEL MENU LATERAL ********************//
        //****************************************************************************************//

        // OPCION INFORMACION ENCUESTAS
        LinearLayout llOpcionInformeEncuestas = findViewById(R.id.llOpcionInformeEncuestas);
        llOpcionInformeEncuestas.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                mostrarDialogoInformeVentas();
            }
        });

        TextView tvOpcionInformeEncuestas = findViewById(R.id.tvOpcionInformeEncuestas);
        tvOpcionInformeEncuestas.setTypeface(Const.letraRegular);

        //****************************************************************************************//
        //****************************************************************************************//
        // SI ES USUARIO SUPERVISOR, SE AGREGA LA VISTA CON VENDEDORES Y SE AGREGA ICONO DE EXPANDIR
        TextView tvOpcionConfiguracion = findViewById(R.id.tvOpcionConfiguracion);
        tvOpcionConfiguracion.setTypeface(Const.letraRegular);

        if(DataBaseBO.obtenerListaDeVendedores().size() > 1) {

            llOpcionConfiguracion.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    if(estado) {

                        expandableLayout.collapse();
                        ivIndicadorLista.setImageResource(R.mipmap.icono_contraido);
                        estado = false;

                    } else {

                        expandableLayout.expand();
                        ivIndicadorLista.setImageResource(R.mipmap.icono_expandido);
                        estado = true;
                    }
                }
            });

            // SE MUESTRA EL INDICADOR DE LA VISTA
            ivIndicadorLista = findViewById(R.id.ivIndicadorLista);
            ivIndicadorLista.setVisibility(View.VISIBLE);

            LinearLayout llContenedorVendedoreSincroniar = findViewById(R.id.llContenedorListaVendedores);

            listaVendedoresSincronizacion = DataBaseBO.obtenerListaDeVendedoresSincronizacion();

            for(int i = 0; i < listaVendedoresSincronizacion.size(); i++) {

                final VistaItemListaVendedoresSincronizar vVendedor = new VistaItemListaVendedoresSincronizar(this);
                vVendedor.tvNombreVendedor.setText(String.valueOf(listaVendedoresSincronizacion.elementAt(i).nombreVendedor));
                vVendedor.position = i;

                vVendedor.ivBotonVendedorSincronizar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(listaVendedoresSincronizacion.elementAt(vVendedor.position).estadoActual == true) {

                            listaVendedoresSincronizacion.elementAt(vVendedor.position).estadoActual = false;
                            vVendedor.ivBotonVendedorSincronizar.setImageResource(R.mipmap.iconook_gris);

                        } else {

                            listaVendedoresSincronizacion.elementAt(vVendedor.position).estadoActual = true;
                            vVendedor.ivBotonVendedorSincronizar.setImageResource(R.mipmap.iconook);
                        }
                    }
                });

                if(listaVendedoresSincronizacion.elementAt(i).seleccionado == 1) {

                    vVendedor.ivBotonVendedorSincronizar.setImageResource(R.mipmap.iconook);
                    listaVendedoresSincronizacion.elementAt(i).estadoActual = true;

                } else {

                    vVendedor.ivBotonVendedorSincronizar.setImageResource(R.mipmap.iconook_gris);
                    listaVendedoresSincronizacion.elementAt(i).estadoActual = false;
                }

                llContenedorVendedoreSincroniar.addView(vVendedor);
            }
        }
        //****************************************************************************************//
        //****************************************************************************************//

        // OPCION SINCRONIZAR
        LinearLayout llOpcionSincronizar = findViewById(R.id.llOpcionSincronizar);
        llOpcionSincronizar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                sincronizarInformacion();
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        TextView tvOpcionSincronizar = findViewById(R.id.tvOpcionSincronizar);
        tvOpcionSincronizar.setTypeface(Const.letraRegular);

        // OPCION INICIAR DIA
        LinearLayout llOpcionIniciarDia = findViewById(R.id.llOpcionIniciarDia);
        llOpcionIniciarDia.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                descargarInformacion(1);
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        TextView tvOpcionIniciarDia = findViewById(R.id.tvOpcionIniciarDia);
        tvOpcionIniciarDia.setTypeface(Const.letraRegular);

        // OPCION ENVIAR INFO
        LinearLayout llOpcionEnviarInfo = findViewById(R.id.llOpcionEnviarInfo);
        llOpcionEnviarInfo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                enviarInformacion(Const.ENVIAR_INFO);
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        TextView tvOpcionEnviarInfo = findViewById(R.id.tvOpcionEnviarInfo);
        tvOpcionEnviarInfo.setTypeface(Const.letraRegular);

        // OPCION CERRAR SESION
        LinearLayout llOpcionCerrarSesion = findViewById(R.id.llOpcionCerrarSesion);
        llOpcionCerrarSesion.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                cerrarSesion();
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        TextView tvOpcionCerrarSesion = findViewById(R.id.tvOpcionCerrarSesion);
        tvOpcionCerrarSesion.setTypeface(Const.letraRegular);
        //****************************************************************************************//
        //****************************************************************************************//

        //****************************************************************************************//
        //****************************************************************************************//
        tvVersionApp = findViewById(R.id.tvVersionApp);
        String versionName = BuildConfig.VERSION_NAME;
        tvVersionApp.setText(new StringBuilder().append("Version: ").append(versionName).append(" - ").append("Ultraprocesados").append(" - ").append(Const.TITULO).toString());
        //****************************************************************************************//
        //****************************************************************************************//
    }

    private void mostrarDialogoConfigInicial() {

        dialogo = new Dialog(PrincipalActivity.this);
        dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogo.setContentView(R.layout.config_inicial);


        final RadioButton rbUnoAUno = (dialogo.findViewById(R.id.rbUnoAUno));
        RadioButton rbBach = (dialogo.findViewById(R.id.rbBach));
        rbUnoAUno.setChecked(true);
        Config config = ConfigBO.ObtenerConfigUsuario();

        if (config != null) {

            if (config.conexion == Const.CONEXION_BACH) {

                rbUnoAUno.setChecked(false);
                rbBach.setChecked(true);
            }
        }

        Button btnConsultarDialogoInformeEncuestas = dialogo.findViewById(R.id.btnAceptarConfig);
        btnConsultarDialogoInformeEncuestas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean guardo = ConfigBO.GuardarConfigUsuario( Main.usuario.codigo, 1, rbUnoAUno.isChecked() ? Const.CONEXION_UNO_UNO : Const.CONEXION_BACH);

                if (guardo) {
                    // 1. SE ACTUALIZAN EN LA TABLA DE VENDEDOR EN CONFIGURACION
                    DataBaseBO.actualizarTablaVendedoresASincronizar(listaVendedoresSincronizacion);

                    // SE REALIZA LA SINCRONIZACION CON DICHOS VENDEDORES
                    Main.listaVendedores = DataBaseBO.obtenerListaDeVendedores();
                    dialogo.cancel();
                    Sync sync = new Sync(PrincipalActivity.this, Const.DOWNLOAD_VENDEDORES);
                    Progress.show(PrincipalActivity.this, "Sincronizador", "Descargando...", false);
                    sync.start();
                }


            }
        });

        Button btnCerrarDialogoInformeEncuestas = dialogo.findViewById(R.id.btnCerrarConfig);
        btnCerrarDialogoInformeEncuestas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogo.cancel();
            }
        });

        dialogo.setCancelable(false);
        dialogo.show();
    }

    private void mostrarDialogoInformeVentas() {

        dialogo = new Dialog(PrincipalActivity.this);
        dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogo.setContentView(R.layout.informeencuestas);

        final TextView tvTextoFechaInicial = dialogo.findViewById(R.id.tvTextoFechaInicial);
        final TextView tvTextoFechaFinal = dialogo.findViewById(R.id.tvTextoFechaFinal);

        Button btnObtenerFechaInicial = dialogo.findViewById(R.id.btnObtenerFechaInicial);
        btnObtenerFechaInicial.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                DatePickerDialog recogerFecha = new DatePickerDialog(PrincipalActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        //Esta variable lo que realiza es aumentar en uno el mes ya que comienza desde 0 = enero
                        final int mesActual = month + 1;

                        //Formateo el día obtenido: antepone el 0 si son menores de 10
                        String diaFormateado = (dayOfMonth < 10)? CERO + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);

                        //Formateo el mes obtenido: antepone el 0 si son menores de 10
                        String mesFormateado = (mesActual < 10)? CERO + String.valueOf(mesActual):String.valueOf(mesActual);

                        //Muestro la fecha con el formato deseado
                        fechaSelAnio = year;
                        fechaSelMes = month;
                        fechaSelDia = dayOfMonth;
                        tvTextoFechaInicial.setText(year + BARRA + mesFormateado + BARRA + diaFormateado);
                    }

                }, anio, mes, dia);

                recogerFecha.getDatePicker().setMaxDate(System.currentTimeMillis());
                recogerFecha.show();
            }
        });

        Button btnObtenerFechaFinal = dialogo.findViewById(R.id.btnObtenerFechaFinal);
        btnObtenerFechaFinal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(fechaSelAnio > 0) {

                    DatePickerDialog recogerFecha = new DatePickerDialog(PrincipalActivity.this, new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                            //Esta variable lo que realiza es aumentar en uno el mes ya que comienza desde 0 = enero
                            final int mesActual = month + 1;

                            //Formateo el día obtenido: antepone el 0 si son menores de 10
                            String diaFormateado = (dayOfMonth < 10)? CERO + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);

                            //Formateo el mes obtenido: antepone el 0 si son menores de 10
                            String mesFormateado = (mesActual < 10)? CERO + String.valueOf(mesActual):String.valueOf(mesActual);

                            //Muestro la fecha con el formato deseado
                            tvTextoFechaFinal.setText(year + BARRA + mesFormateado + BARRA + diaFormateado);
                        }

                    }, anio, mes, dia);

                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.YEAR, fechaSelAnio);
                    c.set(Calendar.MONTH, fechaSelMes);
                    c.set(Calendar.DAY_OF_MONTH, fechaSelDia);
                    recogerFecha.getDatePicker().setMinDate(c.getTimeInMillis());
                    recogerFecha.getDatePicker().setMaxDate(System.currentTimeMillis());

                    recogerFecha.show();
                }
            }
        });

        Button btnConsultarDialogoInformeEncuestas = dialogo.findViewById(R.id.btnConsultarDialogoInformeEncuestas);
        btnConsultarDialogoInformeEncuestas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String fechaInicial = tvTextoFechaInicial.getText().toString();
                String fechaFinal = tvTextoFechaFinal.getText().toString();

                if(fechaInicial.equals("") || fechaFinal.equals("")) {

                    Toasty.warning(PrincipalActivity.this, "Debe poner todas las fechas para la consulta.", Toast.LENGTH_LONG).show();

                } else {

                    Progress.show(PrincipalActivity.this, "Cargando", "Consultando información...", false);
                    Sync sync = new Sync(PrincipalActivity.this, Const.INFORMEENCUESTA);
                    sync.usuario = Main.usuario.codigo;
                    sync.fechaInicial = fechaInicial.replace("/", "");
                    sync.fechaFinal = fechaFinal.replace("/", "");
                    sync.start();
                }
            }
        });

        Button btnCerrarDialogoInformeEncuestas = dialogo.findViewById(R.id.btnCerrarDialogoInformeEncuestas);
        btnCerrarDialogoInformeEncuestas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fechaSelAnio = 0;
                fechaSelMes = 0;
                fechaSelDia = 0;
                dialogo.cancel();
            }
        });

        dialogo.setCancelable(false);
        dialogo.show();
    }

    @Override
    public void onBackPressed() {

        cerrarSesion();
    }

    /**
     * Carga los dias de rutero con el dia actual
     */
    public void cargarDiasRutero(boolean inicioAutomatico) {

        String[] items;

        listaDiasRutero = new Vector();

        listaDiasRutero.add("LUNES");
        listaDiasRutero.add("MARTES");
        listaDiasRutero.add("MIERCOLES");
        listaDiasRutero.add("JUEVES");
        listaDiasRutero.add("VIERNES");
        listaDiasRutero.add("SABADO");
        listaDiasRutero.add("DOMINGO");

        if (listaDiasRutero.size() > 0) {

            items = new String[listaDiasRutero.size()];
            listaDiasRutero.copyInto(items);

        } else {

            items = new String[] {};

            if (listaDiasRutero != null)
                listaDiasRutero.removeAllElements();
        }

        spDiasSemana = findViewById(R.id.spDiasSemana);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDiasSemana.setAdapter(adapter);

        spDiasSemana.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                etOpcionBusqueda = findViewById(R.id.etOpcionBusqueda);
                String parametroBusqueda = etOpcionBusqueda.getText().toString();
                boolean parametro = false;

                if(parametroBusqueda.length() > 0) {

                    parametro = true;
                }

                buscarClientes(parametroBusqueda, parametro);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) { }
        });

        int dia = DataBaseBO.getDiaNumeroSemana();
        spDiasSemana.setSelection(dia, true);
    }

    @Override
    public void respSync(boolean ok, String respuestaServer, String msg, int codeRequest) {

        if(codeRequest == Const.DOWNLOAD_VENDEDORES) {

            respuestaDescargaVendedores(ok, respuestaServer, msg);

        } else if(codeRequest == Const.ENVIAR_INFO) {

            respuestaEnvioInformacion(ok, respuestaServer, msg);

        } else if(codeRequest == Const.ENVIAR_INFO_DESCARGAR) {

            respuestaEnvioInformacionDescarga(ok, respuestaServer, msg);

        } else if(codeRequest == Const.DESCARGA_VERSION) {

            respuestaDescargaVersionApp(ok, respuestaServer, msg);

        } else if(codeRequest == Const.INFORMEENCUESTA) {

            respuestaInformeEncuesta(ok, respuestaServer, msg);
        }
    }

    private void respuestaInformeEncuesta(boolean ok, String respuestaServer, String msg) {

        try {

            Progress.hide();
            TextView tvCantidadEncuesta = dialogo.findViewById(R.id.tvCantidadEncuestas);
            tvCantidadEncuesta.setText("0");
            if (ok) {

                // SE ASIGNA 0 EN EL RESULTADO
                int posRespuesta = respuestaServer.indexOf("Mensaje");
                String mensajeFiltrado = respuestaServer.substring(posRespuesta + 10);
                String mensajeFiltradoFinal = mensajeFiltrado.replace("\"", "");
                mensajeFiltradoFinal = mensajeFiltradoFinal.replace("}", "");

                TextView tvCantidadEncuestas = dialogo.findViewById(R.id.tvCantidadEncuestas);
                tvCantidadEncuestas.setText("" + mensajeFiltradoFinal);

            } else {

                // SE ASIGNA 0 EN EL RESULTADO
                TextView tvCantidadEncuestas = dialogo.findViewById(R.id.tvCantidadEncuestas);
                tvCantidadEncuestas.setText("0");
            }

        } catch (Exception e) {

            String mensaje = e.getMessage();
            Log.e(TAG, "respuestaLogin -> " + mensaje, e);
        }
    }

    private void respuestaEnvioInformacion(boolean ok, String respuestaServer, String msg) {

        try {

            Progress.hide();

            if(Alert.dialogo != null) {

                Alert.dialogo.cancel();
            }

            if (ok) {

                PrincipalActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //ALERT
                        Alert.nutresaShow(PrincipalActivity.this, "INFORMACIÓN", "Información enviada de forma correcta.",
                                "OK", null,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        Alert.dialogo.cancel();
                                    }

                                }, null);
                    }
                });

            } else {

                PrincipalActivity.this.runOnUiThread(new Runnable() {

                    public void run() {

                        //ALERT
                        Alert.nutresaShow(PrincipalActivity.this, "ERROR", "No se pudo realizar el envio de información.",
                                "OK", null,
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

        } catch (Exception e) {

            String mensaje = e.getMessage();
            Log.e(TAG, "respuestaLogin -> " + mensaje, e);
        }
    }

    private void respuestaEnvioInformacionDescarga(boolean ok, String respuestaServer, String msg) {

        try {

            Progress.hide();

            if(Alert.dialogo != null) {

                Alert.dialogo.cancel();
            }

            if (ok) {

                PrincipalActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //ALERT
                        Alert.nutresaShow(PrincipalActivity.this, "INFORMACIÓN", "Información enviada de forma correcta.",
                                "OK", null,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        Alert.dialogo.cancel();
                                        descargarInformacion(0);
                                    }

                                }, null);
                    }
                });

            } else {

                PrincipalActivity.this.runOnUiThread(new Runnable() {

                    public void run() {

                        //ALERT
                        Alert.nutresaShow(PrincipalActivity.this, "ERROR", "No se pudo realizar el envio de información.",
                                "OK", null,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        Alert.dialogo.cancel();
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

    private void respuestaDescargaVendedores(boolean ok, String respuestaServer, String msg) {

        try {

            Progress.hide();

            if (ok) {

                PrincipalActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        buscarClientes("", false);
                        actualizarVersionApp();

                    }
                });

            } else {

                PrincipalActivity.this.runOnUiThread(new Runnable() {

                    public void run() {

                        //ALERT
                        Alert.nutresaShow(PrincipalActivity.this, "ERROR", "No se pudo realizar la descarga de información.",
                                "OK", null,
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
        } catch (Exception e) {

            String mensaje = e.getMessage();
            Log.e(TAG, "respuestaLogin -> " + mensaje, e);
        }
    }

    public void filtroBusqueda(String parametroBusqueda){
        Vector<Cliente> listaAux= new Vector<Cliente>();
        for (Cliente c: listaClientes){
            if(c.codigo.contains(parametroBusqueda) ||
                    c.direccion.toLowerCase().toLowerCase().contains(parametroBusqueda.toLowerCase()) ||
                    c.nombre.toLowerCase().contains(parametroBusqueda.toLowerCase()) ||
                    c.razonSocial.toLowerCase().contains(parametroBusqueda.toLowerCase())){
                listaAux.add(c);
            }
        }

        adapter = new ListViewAdapterListaClientes(PrincipalActivity.this,listaAux);
        lvListaClientes.setLayoutManager(new LinearLayoutManager(PrincipalActivity.this, LinearLayoutManager.VERTICAL,false));
        lvListaClientes.setAdapter(adapter);
    }

    /**
     * Metodo para la busqueda del cliente en rutero con filtro por dia
     */
    public void buscarClientes(String parametroBusqueda, boolean parametro) {

        int index = spDiasSemana.getSelectedItemPosition();
        String diaSemanaSel = (String) spDiasSemana.getAdapter().getItem(index);

        final Vector<ItemListViewClientes> listaItems = new Vector<ItemListViewClientes>();

        if(parametro && parametroBusqueda.length() > 0) {

            listaClientes = DataBaseBO.listaClientesRutero(listaItems, diaSemanaSel, parametroBusqueda, cbVisitados.isChecked());

        } else {

            listaClientes = DataBaseBO.listaClientesRutero(listaItems, diaSemanaSel, "", cbVisitados.isChecked());
        }

//        ItemListViewClientes[] items;
//
//        if (listaItems.size() > 0) {
//
//            items = new ItemListViewClientes[listaItems.size()];
//            listaItems.copyInto(items);
//
//        } else {
//
//            items = new ItemListViewClientes[] {};
//
//            if (listaClientes != null)
//                listaClientes.removeAllElements();
//        }


        adapter = new ListViewAdapterListaClientes(PrincipalActivity.this,listaClientes);
        lvListaClientes.setLayoutManager(new LinearLayoutManager(PrincipalActivity.this, LinearLayoutManager.VERTICAL,false));
        lvListaClientes.setAdapter(adapter);
//
//        adapter = new ListViewAdapterListaClientes(PrincipalActivity.this, items);
//        adapter.notifyDataSetChanged();
//        lvListaClientes.setAdapter(adapter);

        setMenuLateral();
    }

    private void cerrarSesion() {

        if(DataBaseBO.hayInformacionXEnviar()) {

            // SE MUESTRA UN DIALOGO PARA PREGUNTAR POR CIERRE DE SESION
            Alert.nutresaShow(PrincipalActivity.this, "INFORMACIÓN", "Debe enviar la información almacenada antes de cerrar sesión.",
                            "ACEPTAR",
                            null,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    Alert.dialogo.cancel();
                                }

                            }, null);

        } else {

            // SE MUESTRA UN DIALOGO PARA PREGUNTAR POR CIERRE DE SESION
            Alert.nutresaShow(PrincipalActivity.this, "INFORMACIÓN", "Esta seguro de cerrar sesión?, al cerrar sesión toda la información será eliminada.",
                            "ACEPTAR",
                            "CANCELAR",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    Alert.dialogo.cancel();
                                    PrincipalActivity.this.finish();

                                    // 1. ELIMINAR EL DATABASE
                                    Util.eliminarDataBase();

                                    // 2. Eliminar LA INFORMACION DEL USUARIO DEL CONFIG
                                    DataBaseBO.eliminarUsuario();
                                }

                            },
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    Alert.dialogo.cancel();
                                }
                            });
        }
    }

    private void enviarInformacion(int opcionEnvio) {

        if(DataBaseBO.hayInformacionXEnviar()) {
            if(clientesGestion()){
                Sync sync = new Sync(PrincipalActivity.this, opcionEnvio);
                Progress.show(PrincipalActivity.this, "Sincronizador", "Enviando Informacion...", false);
                sync.start();
            }else{
                Alert.nutresaShow(PrincipalActivity.this, "INFORMACIÓN", "Para poder sincronizar información por favor asegúrese de gestionar todas las mediciones de las categorías que haya iniciado en los clientes",
                        "OK", null,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                Alert.dialogo.cancel();
                            }

                        }, null);
            }

        } else {

            // SE MUESTRA UN DIALOGO PARA PREGUNTAR POR CIERRE DE SESION
            Alert.nutresaShow(PrincipalActivity.this, "INFORMACIÓN", "No hay información para enviar.",
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

    public boolean clientesGestion(){
        Vector<Cliente> listaClientes = DataBaseBO.getClientessyncronizar();
        for (Cliente c: listaClientes){
            if (gestionCompleta(c)){
                continue;
            }else{
                return false;
            }
        }
        return true;
    }

    public boolean gestionCompleta(Cliente c){

        Vector<ActividadesCliente> listagestionada=DataBaseBO.obtenerListaGestionada( c.canal, c.codigo, null);
        int cont=0;
        for (ActividadesCliente a: listagestionada){
            //tieneGestion=0 VERDE
            if (a.tieneGestion==0){
                cont++;
            }
        }

        if (cont==0){
            return true;
        }
        if (listagestionada.size()==cont){
            return true;
        }else{
            return false;
        }
    }

    private void descargarInformacion(int sinc) {

        // SE DEBE OBTENER LA INFORMACION DE LOS VENDEDORES SELECCIONADOS PARA LA DESCARGA
        // SI NO HAY NINGUNO SELECCIONADO, SE REALIZA LA DESCARGA DE INFORMACION CON TODOS
        boolean hayVendedoresSeleccionados = false;

        if(DataBaseBO.obtenerListaDeVendedores().size() > 1) {

            for(int i = 0; i < listaVendedoresSincronizacion.size(); i++) {

                if(listaVendedoresSincronizacion.elementAt(i).estadoActual == true) {

                    hayVendedoresSeleccionados = true;
                    break;
                }
            }

        } else {

            hayVendedoresSeleccionados = true;
        }

        if(hayVendedoresSeleccionados) {

            if(sinc==1){
                mostrarDialogoConfigInicial();
            }else{
                // 1. SE ACTUALIZAN EN LA TABLA DE VENDEDOR EN CONFIGURACION
                DataBaseBO.actualizarTablaVendedoresASincronizar(listaVendedoresSincronizacion);

                // SE REALIZA LA SINCRONIZACION CON DICHOS VENDEDORES
                Main.listaVendedores = DataBaseBO.obtenerListaDeVendedores();

                Sync sync = new Sync(PrincipalActivity.this, Const.DOWNLOAD_VENDEDORES);
                Progress.show(PrincipalActivity.this, "Sincronizador", "Descargando...", false);
                sync.start();
            }
        } else {

            //ALERT
            Alert.nutresaShow(PrincipalActivity.this, "INFORMACIÓN", "No hay vendedores seleccionados para sincronizar.",
                    "OK", null,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Alert.dialogo.cancel();
                        }

                    }, null);
        }
    }

    public void actualizarVersionApp() {

        final String versionSvr = DataBaseBO.obtenerVersionApp();
        String versionApp = Util.obtenerVersion(getApplicationContext());

        if (versionSvr != null && versionApp != null) {

            float versionServer = Util.toFloat(versionSvr.replace(".", ""));
            float versionLocal = Util.toFloat(versionApp.replace(".", ""));

            if (versionLocal < versionServer) {

                //ALERT
                Alert.nutresaShow(PrincipalActivity.this, "INFORMACIÓN", "Hay una versión de la aplicación: " + versionSvr,
                        "OK", null,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Alert.dialogo.cancel();

                                Sync sync = new Sync(PrincipalActivity.this, Const.DESCARGA_VERSION);
                                Progress.show(PrincipalActivity.this, "Sincronizador", "Descargando versión...", false);
                                sync.start();
                            }

                        }, null);

            } else {

                //ALERT
                Alert.nutresaShow(PrincipalActivity.this, "INFORMACIÓN", "Información descargada correctamente.",
                        "OK", null,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Alert.dialogo.cancel();
                            }

                        }, null);
            }

        } else {

            //ALERT
            Alert.nutresaShow(PrincipalActivity.this, "INFORMACIÓN", "Información descargada correctamente.",
                    "OK", null,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Alert.dialogo.cancel();
                        }

                    }, null);
        }
    }

    private void respuestaDescargaVersionApp(boolean ok, final String respuestaServer, String msg) {

        try {

            Progress.hide();
            Alert.dialogo.cancel();

            if (ok) {

                PrincipalActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        File fileApp = new File(Util.dirApp(), Const.fileNameApk);

                        if (fileApp.exists()) {

                            Uri uri = Uri.fromFile(fileApp);

                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(uri, "application/vnd.android.package-archive");
                            startActivityForResult(intent, Const.RESP_ACTUALIZAR_VERSION);

                        } else {

                            //ALERT
                            Alert.nutresaShow(PrincipalActivity.this, "ERROR", "No se pudo actualizar la version.",
                                    "OK", null,
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            Alert.dialogo.cancel();
                                        }

                                    }, null);
                        }
                    }
                });

            } else {

                PrincipalActivity.this.runOnUiThread(new Runnable() {

                    public void run() {

                        //ALERT
                        Alert.nutresaShow(PrincipalActivity.this, "ERROR", respuestaServer + ".",
                                "OK", null,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        Alert.dialogo.cancel();
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

    private void sincronizarInformacion() {

        // SE VERIFICA SI HAY INFORMACION PARA ENVIAR
        if(DataBaseBO.hayInformacionXEnviar()) {

            enviarInformacion(Const.ENVIAR_INFO_DESCARGAR);

        } else {

            descargarInformacion(0);
        }
    }

    private void setListinerListView() {

        final RecyclerView lvListaClientes = (RecyclerView)findViewById(R.id.lvListaClientes);
//        lvListaClientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                String codigoClienteSel = listaClientes.elementAt(position).codigo;
//                PreferencesCliente.guardarCodigoCliente(getApplicationContext(), codigoClienteSel);
//                Main.cliente = listaClientes.elementAt(position);
//
//                Intent opcionesClienteActivity = new Intent(PrincipalActivity.this, OpcionesClienteActivity.class);
//                startActivity(opcionesClienteActivity);
//            }
//        });
    }

    private void configuracionVisitados() {

        // SI ES USUARIO SUPERVISOR, SE MIUESTRA LA OPCION DEL CHECKBOX
        int tipoUsuario = DataBaseBO.obtenerTipoUsuario();

        etOpcionBusqueda = findViewById(R.id.etOpcionBusqueda);
        llCheckVisitados = findViewById(R.id.llCheckVisitados);
        cbVisitados = findViewById(R.id.cbVisitados);

        cbVisitados.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                String parametroBusqeuda = etOpcionBusqueda.getText().toString();
                boolean hayParametro = (parametroBusqeuda.length() > 0) ? true: false;

                buscarClientes(parametroBusqeuda, hayParametro);
            }
        });

        if(tipoUsuario == 3) { // SUPEVISOR

            llCheckVisitados.setVisibility(View.VISIBLE);
            cbVisitados.setTypeface(Const.letraRegular);
            cbVisitados.setChecked(false);

        } else {

            llCheckVisitados.setVisibility(View.GONE);
            cbVisitados.setTypeface(Const.letraRegular);
            cbVisitados.setChecked(false);
        }
    }
}
