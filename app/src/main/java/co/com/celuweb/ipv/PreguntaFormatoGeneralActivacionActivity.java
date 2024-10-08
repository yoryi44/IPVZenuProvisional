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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

import businessObject.DataBaseBO;
import component.Alert;
import component.Progress;
import component.Util;
import config.Const;
import config.Synchronizer;
import dataObject.Cliente;
import dataObject.Main;
import dataObject.ObjetoActivacion;
import es.dmoral.toasty.Toasty;
import sharedPreferences.PreferencesCliente;
import sharedPreferences.PreferencesObjetoActivacion;

public class PreguntaFormatoGeneralActivacionActivity extends AppCompatActivity implements Synchronizer {

    private String opcionTitulo;
    private int opcionActualLogica;
    private int opcionTipoSel;
    private TextView tvLabelRazonSocial;
    private TextView tvTituloPreguntaFormatoGeneral;
    private String idMedicion = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_pregunta_formato_general);

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
    }

    private void setComponentesVista() {

        tvLabelRazonSocial = findViewById(R.id.tvLabelRazonSocial);
        tvLabelRazonSocial.setTypeface(Const.letraSemibold);

        tvTituloPreguntaFormatoGeneral = findViewById(R.id.tvTituloPreguntaFormatoGeneral);
        tvTituloPreguntaFormatoGeneral.setTypeface(Const.letraSemibold);

        if(opcionActualLogica == 1) {         // PROPIO

            String opcionMostrarTipo = "";

            // 1 - PROPIAS
            // 4 - PROPIO
            // 7 - PROPIOS
            int tituloSeleccion = 0;

            if(opcionTipoSel == 1) {

                opcionMostrarTipo = "Propias";
                tituloSeleccion = 0;
            }

            if(opcionTipoSel == 3) {

                opcionMostrarTipo = "Propio";
                tituloSeleccion = 1;
            }

            if(opcionTipoSel == 5) {

                opcionMostrarTipo = "Propios";
                tituloSeleccion = 2;
            }

            tvTituloPreguntaFormatoGeneral.setText(Const.listaPreguntas[tituloSeleccion] + opcionMostrarTipo);

        } else if(opcionActualLogica == 10) { // COMPETENCIA

            String opcionMostrarTipo = "";

            // 1 - PROPIAS
            // 4 - PROPIO
            // 7 - PROPIOS
            int tituloSeleccion = 0;

            if(opcionTipoSel == 1) {

                tituloSeleccion = 0;
            }

            if(opcionTipoSel == 3) {

                tituloSeleccion = 1;
            }

            if(opcionTipoSel == 5) {

                tituloSeleccion = 2;
            }

            // 10, 20, 30 - COMPETENCIA
            tvTituloPreguntaFormatoGeneral.setText(Const.listaPreguntas[tituloSeleccion] + "Competencia");
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if(opcionTipoSel == 1) {

            PreguntaFormatoGeneralActivacionActivity.this.finish();
        }
    }

    public void on_ClickSI(View view) {

        // SE CREA EL OBJETO GENERAL COMO VECTOR QUE CONTIENE
        // A SU VEZ LOS OBJETOS DE TIPO - "OBJETOACTIVACION"
        ArrayList<ObjetoActivacion> listaRespuestaActivacion = new ArrayList<>();
        idMedicion = Util.obtenerId(Main.usuario.codigo);

        if(opcionActualLogica == 1) {         // PROPIO

            ObjetoActivacion objetoActivacion = new ObjetoActivacion();
            objetoActivacion.codigoCliente  = Main.cliente.codigo;
            objetoActivacion.codigoUsuario  = Main.usuario.codigo;
            objetoActivacion.nombreUsuario  = Main.usuario.nombre;
            objetoActivacion.tipoUsuario    = String.valueOf(DataBaseBO.obtenerTipoUsuario());
            objetoActivacion.id             = idMedicion;
            objetoActivacion.tipoOpcion     = String.valueOf(opcionTipoSel);
            objetoActivacion.valor1         = "SI";
            objetoActivacion.codigoProducto = "";
            objetoActivacion.nombreProducto = "";
            objetoActivacion.core           = "";
            objetoActivacion.propio         = "1";
            objetoActivacion.competencia    = "";
            objetoActivacion.fechaMovil     = Util.obtenerFechaActual();
            objetoActivacion.nombre1        = "";

            listaRespuestaActivacion.add(objetoActivacion);

        } else if(opcionActualLogica == 10) { // COMPETENCIA

            ObjetoActivacion objetoActivacion = new ObjetoActivacion();
            objetoActivacion.codigoCliente  = Main.cliente.codigo;
            objetoActivacion.codigoUsuario  = Main.usuario.codigo;
            objetoActivacion.nombreUsuario  = Main.usuario.nombre;
            objetoActivacion.tipoUsuario    = String.valueOf(DataBaseBO.obtenerTipoUsuario());
            objetoActivacion.id             = idMedicion;
            objetoActivacion.tipoOpcion     = String.valueOf(opcionTipoSel);
            objetoActivacion.valor1         = "SI";
            objetoActivacion.codigoProducto = "";
            objetoActivacion.nombreProducto = "";
            objetoActivacion.core           = "";
            objetoActivacion.propio         = "";
            objetoActivacion.competencia    = "1";
            objetoActivacion.fechaMovil     = Util.obtenerFechaActual();
            objetoActivacion.nombre1        = "";

            listaRespuestaActivacion.add(objetoActivacion);
        }

        // SE ALMACENA EL OBJETO COMO JSON EN UN SHAREDPREFERENCES
        // PARA PASAR ENTRE LOS MODULOS HASTA TERMINAR LA MEDICION
        Gson gsObject = new Gson();
        String stringObjetoActivacion = gsObject.toJson(listaRespuestaActivacion);
        PreferencesObjetoActivacion.guardarObjetoActivacion(this, stringObjetoActivacion);

        if(opcionActualLogica == 1) {

            // SE DETERMINA SI "opcionTipoSel" ES 1 - SE ENVIA 2
            // DE LO CONTRARIO SI NO 1, ES 3 Y      - SE ENVIA 4
            if(opcionTipoSel == 1) {

                // SI HAY MEDICION
                Intent fotosActivacionActivity = new Intent(PreguntaFormatoGeneralActivacionActivity.this, FotosActivacionActivity.class);
                fotosActivacionActivity.putExtra("OPCIONTITULO", "PROPIA");
                fotosActivacionActivity.putExtra("OPCIONACTUALLOGICA", opcionActualLogica);
                fotosActivacionActivity.putExtra("OPCIONTIPOSEL", 2);

                // IDMEDICION PARA LA MEDICION GENERAL
                fotosActivacionActivity.putExtra("IDMEDICION", idMedicion);

                startActivity(fotosActivacionActivity);
                finish();

            } else if(opcionTipoSel == 3) {

                // SI HAY MEDICION
                Intent fotosActivacionActivity = new Intent(PreguntaFormatoGeneralActivacionActivity.this, FotosActivacionActivity.class);
                fotosActivacionActivity.putExtra("OPCIONTITULO", "PROPIA");
                fotosActivacionActivity.putExtra("OPCIONACTUALLOGICA", opcionActualLogica);
                fotosActivacionActivity.putExtra("OPCIONTIPOSEL", 4);

                // IDMEDICION PARA LA MEDICION GENERAL
                fotosActivacionActivity.putExtra("IDMEDICION", idMedicion);

                startActivity(fotosActivacionActivity);
                finish();

            } else if(opcionTipoSel == 5) {
                /**
                // SE INSERTA EL OBJETO DIRECTAMENTE EN LA BASE DE DATOS - "OBJETOACTIVACION"
                listaRespuestaActivacion = new ArrayList<>();
                idMedicion = Util.obtenerId(Main.usuario.codigo);

                ObjetoActivacion objetoActivacion = new ObjetoActivacion();
                objetoActivacion.codigoCliente  = Main.cliente.codigo;
                objetoActivacion.codigoUsuario  = Main.usuario.codigo;
                objetoActivacion.nombreUsuario  = Main.usuario.nombre;
                objetoActivacion.tipoUsuario    = String.valueOf(DataBaseBO.obtenerTipoUsuario());
                objetoActivacion.id             = idMedicion;
                objetoActivacion.tipoOpcion     = String.valueOf(opcionTipoSel);
                objetoActivacion.valor1         = "SI";
                objetoActivacion.codigoProducto = "";
                objetoActivacion.nombreProducto = "";
                objetoActivacion.core           = "";
                objetoActivacion.fechaMovil     = Util.obtenerFechaActual();
                objetoActivacion.nombre1        = "";

                if(opcionActualLogica == 1) {         // PROPIO

                    objetoActivacion.propio         = "1";
                    objetoActivacion.competencia    = "";

                } else if(opcionActualLogica == 10) { // COMPETENCIA

                    objetoActivacion.propio         = "";
                    objetoActivacion.competencia    = "1";
                }

                listaRespuestaActivacion.add(objetoActivacion);
                DataBaseBO.almacenarRegistroActivacion(listaRespuestaActivacion, Main.cliente.codigo);

                Sync sync = new Sync(PreguntaFormatoGeneralActivacionActivity.this, Const.ENVIAR_INFO);
                Progress.show(PreguntaFormatoGeneralActivacionActivity.this, "Sincronizador", "Enviando Informacion...", false);
                sync.start();
                 **/

                // SI HAY MEDICION
                Intent fotosActivacionActivity = new Intent(PreguntaFormatoGeneralActivacionActivity.this, FotosActivacionActivity.class);
                fotosActivacionActivity.putExtra("OPCIONTITULO", "PROPIA");
                fotosActivacionActivity.putExtra("OPCIONACTUALLOGICA", opcionActualLogica);
                fotosActivacionActivity.putExtra("OPCIONTIPOSEL", 6);

                // IDMEDICION PARA LA MEDICION GENERAL
                fotosActivacionActivity.putExtra("IDMEDICION", idMedicion);

                startActivity(fotosActivacionActivity);
                finish();
            }

        } else if(opcionActualLogica == 10) {

            // SE DETERMINA SI "opcionTipoSel" ES 1 - SE ENVIA 2
            // DE LO CONTRARIO SI NO 1, ES 3 Y      - SE ENVIA 4
            if(opcionTipoSel == 1) {

                // SI HAY MEDICION
                Intent fotosActivacionActivity = new Intent(PreguntaFormatoGeneralActivacionActivity.this, FotosActivacionActivity.class);
                fotosActivacionActivity.putExtra("OPCIONTITULO", "COMPETENCIA");
                fotosActivacionActivity.putExtra("OPCIONACTUALLOGICA", opcionActualLogica);
                fotosActivacionActivity.putExtra("OPCIONTIPOSEL", 2);

                // IDMEDICION PARA LA MEDICION GENERAL
                fotosActivacionActivity.putExtra("IDMEDICION", idMedicion);

                startActivity(fotosActivacionActivity);
                finish();

            } else if(opcionTipoSel == 3) {

                // SI HAY MEDICION
                Intent fotosActivacionActivity = new Intent(PreguntaFormatoGeneralActivacionActivity.this, FotosActivacionActivity.class);
                fotosActivacionActivity.putExtra("OPCIONTITULO", "COMPETENCIA");
                fotosActivacionActivity.putExtra("OPCIONACTUALLOGICA", opcionActualLogica);
                fotosActivacionActivity.putExtra("OPCIONTIPOSEL", 4);

                // IDMEDICION PARA LA MEDICION GENERAL
                fotosActivacionActivity.putExtra("IDMEDICION", idMedicion);

                startActivity(fotosActivacionActivity);
                finish();

            } else if(opcionTipoSel == 5) {

                /**
                // SE INSERTA EL OBJETO DIRECTAMENTE EN LA BASE DE DATOS - "OBJETOACTIVACION"
                listaRespuestaActivacion = new ArrayList<>();
                idMedicion = Util.obtenerId(Main.usuario.codigo);

                ObjetoActivacion objetoActivacion = new ObjetoActivacion();
                objetoActivacion.codigoCliente  = Main.cliente.codigo;
                objetoActivacion.codigoUsuario  = Main.usuario.codigo;
                objetoActivacion.nombreUsuario  = Main.usuario.nombre;
                objetoActivacion.tipoUsuario    = String.valueOf(DataBaseBO.obtenerTipoUsuario());
                objetoActivacion.id             = idMedicion;
                objetoActivacion.tipoOpcion     = String.valueOf(opcionTipoSel);
                objetoActivacion.valor1         = "SI";
                objetoActivacion.codigoProducto = "";
                objetoActivacion.nombreProducto = "";
                objetoActivacion.core           = "";
                objetoActivacion.fechaMovil     = Util.obtenerFechaActual();
                objetoActivacion.nombre1        = "";

                if(opcionActualLogica == 1) {         // PROPIO

                    objetoActivacion.propio         = "1";
                    objetoActivacion.competencia    = "";

                } else if(opcionActualLogica == 10) { // COMPETENCIA

                    objetoActivacion.propio         = "";
                    objetoActivacion.competencia    = "1";
                }

                listaRespuestaActivacion.add(objetoActivacion);
                DataBaseBO.almacenarRegistroActivacion(listaRespuestaActivacion, Main.cliente.codigo);

                Sync sync = new Sync(PreguntaFormatoGeneralActivacionActivity.this, Const.ENVIAR_INFO);
                Progress.show(PreguntaFormatoGeneralActivacionActivity.this, "Sincronizador", "Enviando Informacion...", false);
                sync.start();
                 **/

                // SI HAY MEDICION
                Intent fotosActivacionActivity = new Intent(PreguntaFormatoGeneralActivacionActivity.this, FotosActivacionActivity.class);
                fotosActivacionActivity.putExtra("OPCIONTITULO", "COMPETENCIA");
                fotosActivacionActivity.putExtra("OPCIONACTUALLOGICA", opcionActualLogica);
                fotosActivacionActivity.putExtra("OPCIONTIPOSEL", 6);

                // IDMEDICION PARA LA MEDICION GENERAL
                fotosActivacionActivity.putExtra("IDMEDICION", idMedicion);

                startActivity(fotosActivacionActivity);
                finish();
            }
        }
    }

    public void on_ClickNO(View view) {

        // SE INSERTA EL OBJETO DIRECTAMENTE EN LA BASE DE DATOS - "OBJETOACTIVACION"
        ArrayList<ObjetoActivacion> listaRespuestaActivacion = new ArrayList<>();
        idMedicion = Util.obtenerId(Main.usuario.codigo);

        ObjetoActivacion objetoActivacion = new ObjetoActivacion();
        objetoActivacion.codigoCliente  = Main.cliente.codigo;
        objetoActivacion.codigoUsuario  = Main.usuario.codigo;
        objetoActivacion.nombreUsuario  = Main.usuario.nombre;
        objetoActivacion.tipoUsuario    = String.valueOf(DataBaseBO.obtenerTipoUsuario());
        objetoActivacion.id             = idMedicion;
        objetoActivacion.tipoOpcion     = String.valueOf(opcionTipoSel);
        objetoActivacion.valor1         = "NO";
        objetoActivacion.codigoProducto = "";
        objetoActivacion.nombreProducto = "";
        objetoActivacion.core           = "";
        objetoActivacion.fechaMovil     = Util.obtenerFechaActual();
        objetoActivacion.nombre1        = "";

        if(opcionActualLogica == 1) {         // PROPIO

            objetoActivacion.propio         = "1";
            objetoActivacion.competencia    = "";

        } else if(opcionActualLogica == 10) { // COMPETENCIA

            objetoActivacion.propio         = "";
            objetoActivacion.competencia    = "1";
        }

        listaRespuestaActivacion.add(objetoActivacion);

        boolean guardoActivacion = DataBaseBO.almacenarRegistroActivacion(listaRespuestaActivacion, Main.cliente.codigo);

        if(guardoActivacion) {

            if(opcionActualLogica == 1) {

                // SE DETERMINA SI "opcionTipoSel" ES 1 - SE ENVIA 3
                // DE LO CONTRARIO SI NO 1, ES 3 Y      - SE ENVIA 5
                if(opcionTipoSel == 1) {

                    // SI HAY MEDICION
                    Intent fotosActivacionActivity = new Intent(PreguntaFormatoGeneralActivacionActivity.this, PreguntaFormatoGeneralActivacionActivity.class);
                    fotosActivacionActivity.putExtra("OPCIONTITULO", "PROPIA");
                    fotosActivacionActivity.putExtra("OPCIONACTUALLOGICA", opcionActualLogica);
                    fotosActivacionActivity.putExtra("OPCIONTIPOSEL", 3);
                    startActivity(fotosActivacionActivity);
                    finish();

                } else if(opcionTipoSel == 3) {

                    // SI HAY MEDICION
                    Intent fotosActivacionActivity = new Intent(PreguntaFormatoGeneralActivacionActivity.this, PreguntaFormatoGeneralActivacionActivity.class);
                    fotosActivacionActivity.putExtra("OPCIONTITULO", "PROPIA");
                    fotosActivacionActivity.putExtra("OPCIONACTUALLOGICA", opcionActualLogica);
                    fotosActivacionActivity.putExtra("OPCIONTIPOSEL", 5);
                    startActivity(fotosActivacionActivity);
                    finish();

                } else if(opcionTipoSel == 5) {

//                    Sync sync = new Sync(PreguntaFormatoGeneralActivacionActivity.this, Const.ENVIAR_INFO);
//                    Progress.show(PreguntaFormatoGeneralActivacionActivity.this, "Sincronizador", "Enviando Informacion...", false);
//                    sync.start();
                    finish();
                }

            } else if(opcionActualLogica == 10) {

                // SE DETERMINA SI "opcionTipoSel" ES 1 - SE ENVIA 3
                // DE LO CONTRARIO SI NO 1, ES 3 Y      - SE ENVIA 5
                if(opcionTipoSel == 1) {

                    // SI HAY MEDICION
                    Intent fotosActivacionActivity = new Intent(PreguntaFormatoGeneralActivacionActivity.this, PreguntaFormatoGeneralActivacionActivity.class);
                    fotosActivacionActivity.putExtra("OPCIONTITULO", "COMPETENCIA");
                    fotosActivacionActivity.putExtra("OPCIONACTUALLOGICA", opcionActualLogica);
                    fotosActivacionActivity.putExtra("OPCIONTIPOSEL", 3);
                    startActivity(fotosActivacionActivity);
                    finish();

                } else if(opcionTipoSel == 3) {

                    // SI HAY MEDICION
                    Intent fotosActivacionActivity = new Intent(PreguntaFormatoGeneralActivacionActivity.this, PreguntaFormatoGeneralActivacionActivity.class);
                    fotosActivacionActivity.putExtra("OPCIONTITULO", "COMPETENCIA");
                    fotosActivacionActivity.putExtra("OPCIONACTUALLOGICA", opcionActualLogica);
                    fotosActivacionActivity.putExtra("OPCIONTIPOSEL", 5);
                    startActivity(fotosActivacionActivity);
                    finish();

                } else if(opcionTipoSel == 5) {

//                    Sync sync = new Sync(PreguntaFormatoGeneralActivacionActivity.this, Const.ENVIAR_INFO);
//                    Progress.show(PreguntaFormatoGeneralActivacionActivity.this, "Sincronizador", "Enviando Informacion...", false);
//                    sync.start();
                    finish();
                }
            }

        } else {

            Toasty.error(PreguntaFormatoGeneralActivacionActivity.this, "La medición no se pudo almacenar.", Toast.LENGTH_LONG).show();
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

                PreguntaFormatoGeneralActivacionActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //ALERT
                        Alert.nutresaShow(PreguntaFormatoGeneralActivacionActivity.this, "INFORMACIÓN", "Información enviada de forma correcta.",
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

                PreguntaFormatoGeneralActivacionActivity.this.runOnUiThread(new Runnable() {

                    public void run() {

                        //ALERT
                        Alert.nutresaShow(PreguntaFormatoGeneralActivacionActivity.this, "ERROR", "No se pudo realizar el envio de información.",
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
