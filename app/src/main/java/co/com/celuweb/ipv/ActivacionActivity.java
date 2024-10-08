package co.com.celuweb.ipv;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Vector;

import businessObject.DataBaseBO;
import component.Alert;
import component.Util;
import config.Const;
import dataObject.ActivacionCompetencia;
import dataObject.Cliente;
import dataObject.Main;
import dataObject.Marca;
import dataObject.ModuloActivacion;
import dataObject.Usuario;
import sharedPreferences.PreferencesCliente;

public class ActivacionActivity extends AppCompatActivity {

    private FrameLayout flInformacionProductosPropiosPrecios;
    private FrameLayout flInformacionProductosCompetenciaPrecios;
    private TextView tvLabelRazonSocial;
    private Vector<ModuloActivacion> listaModulosPropios;
    private Vector<ModuloActivacion> listaModulosCompetencia;
    private Vector<ActivacionCompetencia> listaActivacionesCompetencia;
    private LinearLayout llListaModulosActivacion;
    private Spinner spinner;

    String idCategoria = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_activacion);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        setSupportActionBar(toolbar);

        // SE INICIALIZA EL COMPONENTE PRINCIPAL DE LA APLICACION
        Main.contexto = getApplicationContext();

        TextView tvTituloModulo = findViewById(R.id.tvTituloModulo);
        tvTituloModulo.setTypeface(Const.letraSemibold);
        tvTituloModulo.setText("ACTIVACION");

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
        setComponentesVista();
        // SE CAMBIA LA VISTA PARA QUE TENGA LOGICA DE TAB
        configurarTabsInformacionPrecioProductos();
        cargarListaModulosCompetencia(false,null);
        cargarListaModulosPropios(false);
        cargarMarcas();
        listaActivacionesCompetencia =  new Vector<ActivacionCompetencia>();
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


    private void cargarMarcas(){
        Main.marca = null;
        spinner = findViewById(R.id.spMarcas);
        Vector<Marca> listaMarcas  = DataBaseBO.obtenerMarcas(idCategoria);

        ArrayAdapter<Marca> adapter = new ArrayAdapter<Marca>(ActivacionActivity.this,android.R.layout.simple_spinner_item,listaMarcas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                Marca marca = (Marca) spinner.getAdapter().getItem(position);


                if (Main.marca==null){
                    Main.marca = marca;
                }
                int pos=existeRegistro(Main.marca);
//                ActivacionCompetencia existe=existeRegistro(Main.marca);
                if(pos!=-1){
                    listaActivacionesCompetencia.get(pos).listaModulosCompetencia=Main.listaExhibidoresCompetencia;
                    int posActual= existeRegistro(marca);
                    boolean valor= (posActual!=-1) ? true:false;
                    int posFinal = (posActual==-1) ? 0:posActual;
                    cargarListaModulosCompetencia(valor,listaActivacionesCompetencia.get(posFinal).listaModulosCompetencia);
                }else{
                    ActivacionCompetencia activacionCompetencia= new ActivacionCompetencia();
                    activacionCompetencia.idMarca= Main.marca==null ? marca.codigo: Main.marca.codigo;
                    activacionCompetencia.listaModulosCompetencia = Main.listaExhibidoresCompetencia;

                    if(verificarVacios(Main.listaExhibidoresCompetencia)){
                        listaActivacionesCompetencia.add(activacionCompetencia);

                        int posActual= existeRegistro(marca);
                        boolean valor= (posActual!=-1) ? true:false;
                        int posFinal = (posActual==-1) ? 0:posActual;
                        cargarListaModulosCompetencia(valor,listaActivacionesCompetencia.get(posFinal).listaModulosCompetencia);
                    }

                }


//                if(existe==null){
//
//                    ActivacionCompetencia activacionCompetencia= new ActivacionCompetencia();
//                    activacionCompetencia.idMarca= Main.marca==null ? marca.codigo: Main.marca.codigo;
//                    activacionCompetencia.listaModulosCompetencia = Main.listaExhibidoresCompetencia;
//
//                    listaActivacionesCompetencia.add(activacionCompetencia);
//                    cargarListaModulosCompetencia(false,null);
//
//                }else{
//                    cargarListaModulosCompetencia(true,existe);
//                }
                Main.marca= marca;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) { }
        });
    }
    public int existeRegistro(Marca marca){
        for(int i=0; i< listaActivacionesCompetencia.size(); i++){
            if (listaActivacionesCompetencia.get(i).idMarca.equals(marca.codigo)){
                return i;
            }
        }
        return -1;
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
                    flInformacionProductosPropiosPrecios = findViewById(R.id.flInformacionModulosPropios);
                    flInformacionProductosPropiosPrecios.setVisibility(View.VISIBLE);

                    // 2. SE OCULTA LA VISTA DE PRODUCTOS COMPETENCIA
                    flInformacionProductosCompetenciaPrecios = findViewById(R.id.flInformacionModulosCompetencia);
                    flInformacionProductosCompetenciaPrecios.setVisibility(View.GONE);

                } else {

                    // 1. SE OCULTA LA VISTA DE PRODUCTOS PROPIOS
                    flInformacionProductosPropiosPrecios = findViewById(R.id.flInformacionModulosPropios);
                    flInformacionProductosPropiosPrecios.setVisibility(View.GONE);

                    // 2. SE MUESTRA LA VISTA DE PRODUCTOS COMPETENCIA
                    flInformacionProductosCompetenciaPrecios = findViewById(R.id.flInformacionModulosCompetencia);
                    flInformacionProductosCompetenciaPrecios.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }



    private void cargarListaModulosCompetencia(boolean modificado, Vector<ModuloActivacion> listaModulosComp) {

        if(!modificado){
            listaModulosCompetencia = DataBaseBO.obtenerListaModulosActivacion(false,idCategoria);
            Main.listaExhibidoresCompetencia = listaModulosCompetencia;
        }else{
            Main.listaExhibidoresCompetencia = listaModulosComp;
//            Main.listaExhibidoresCompetencia = activacion.listaModulosCompetencia;
        }

        llListaModulosActivacion = findViewById(R.id.llListaModulosActivacionCompetencia);
        llListaModulosActivacion.removeAllViews();



        for(int pos = 0; pos < Main.listaExhibidoresCompetencia.size(); pos++) {

            // AGREGA AL LINEARLAYOUT UNA VISTA POR CADA ITEM DE LA LISTA
            final VistaItemListaModuloActivacion vProductoCompetencia = new VistaItemListaModuloActivacion(this);
            vProductoCompetencia.position = pos;


            if(Main.listaExhibidoresCompetencia.elementAt(vProductoCompetencia.position).id==null){
                Main.listaExhibidoresCompetencia.elementAt(vProductoCompetencia.position).id = Util.obtenerId(Main.usuario.codigo);
            }

            vProductoCompetencia.tvNombreModulo.setText(String.valueOf(Main.listaExhibidoresCompetencia.elementAt(pos).nombre));


            if (Main.listaExhibidoresCompetencia.elementAt(vProductoCompetencia.position).foto==1) {
                // SE HACE EL CAMBIO DE ICONO
                vProductoCompetencia.ivCamara.setImageResource(R.mipmap.cam_ok);
            } else {
                // SE HACE EL CAMBIO DE ICONO
                vProductoCompetencia.ivCamara.setImageResource(R.mipmap.cam);
            }

            if (Main.listaExhibidoresCompetencia.elementAt(vProductoCompetencia.position).respuesta.equals("SI")) {
                // SE HACE EL CAMBIO DE ICONO
                vProductoCompetencia.ivSi.setImageResource(R.mipmap.iconook);
            } else {
                // SE HACE EL CAMBIO DE ICONO
                vProductoCompetencia.ivSi.setImageResource(R.mipmap.iconook_gris);
            }

            if (Main.listaExhibidoresCompetencia.elementAt(vProductoCompetencia.position).respuesta.equals("NO")) {
                // SE HACE EL CAMBIO DE ICONO
                vProductoCompetencia.ivNo.setImageResource(R.mipmap.icon_no_rojo);
            } else {
                // SE HACE EL CAMBIO DE ICONO
                vProductoCompetencia.ivNo.setImageResource(R.mipmap.icon_no);
            }

            vProductoCompetencia.ivSi.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    if (vProductoCompetencia.si) {
                        // SE HACE EL CAMBIO DE ICONO
                        vProductoCompetencia.si = false;
                        vProductoCompetencia.ivSi.setImageResource(R.mipmap.iconook);

                        vProductoCompetencia.no = true;
                        vProductoCompetencia.ivNo.setImageResource(R.mipmap.icon_no);

                        Main.listaExhibidoresCompetencia.elementAt(vProductoCompetencia.position).respuesta = "SI";
                    } else {
                        // SE HACE EL CAMBIO DE ICONO
                        vProductoCompetencia.si = true;
                        vProductoCompetencia.ivSi.setImageResource(R.mipmap.iconook_gris);
                    }

                }
            });

            vProductoCompetencia.ivNo.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    if (vProductoCompetencia.no) {
                        // SE HACE EL CAMBIO DE ICONO
                        vProductoCompetencia.no = false;
                        vProductoCompetencia.ivNo.setImageResource(R.mipmap.icon_no_rojo);

                        vProductoCompetencia.si = true;
                        vProductoCompetencia.ivSi.setImageResource(R.mipmap.iconook_gris);

                        Main.listaExhibidoresCompetencia.elementAt(vProductoCompetencia.position).respuesta = "NO";
                    } else {
                        // SE HACE EL CAMBIO DE ICONO
                        vProductoCompetencia.no = true;
                        vProductoCompetencia.ivNo.setImageResource(R.mipmap.icon_no);
                    }

                }
            });

            vProductoCompetencia.ivCamara.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    Intent moduloFotosExhibidor = new Intent(ActivacionActivity.this, FotosActivity.class);
                    moduloFotosExhibidor.putExtra("IDEXHIBIDOR", Main.listaExhibidoresCompetencia.elementAt(vProductoCompetencia.position).id);
                    moduloFotosExhibidor.putExtra("EXHIBIDORREGISTROHOY", 0 );
                    moduloFotosExhibidor.putExtra("ACTIVACION", true);
                    moduloFotosExhibidor.putExtra("POSICION", vProductoCompetencia.position);
                    moduloFotosExhibidor.putExtra("COMPETENCIA", true);
                    moduloFotosExhibidor.putExtra("ESEXHIBIDOR", false);
                    startActivityForResult(moduloFotosExhibidor, Const.FOTO);

                }
            });
            llListaModulosActivacion.addView(vProductoCompetencia);

        }
    }
    private void cargarListaModulosPropios(boolean modificado) {

        if(!modificado){
//            listaModulosPropios = DataBaseBO.obtenerListaActivacionPropios(true);
//            if(listaModulosPropios.size()==0){
            listaModulosPropios = DataBaseBO.obtenerListaModulosActivacion(true,idCategoria);
//            }
            Main.listaExhibidores = listaModulosPropios;
        }

        llListaModulosActivacion = findViewById(R.id.llListaModulosActivacionPropios);
        llListaModulosActivacion.removeAllViews();

        for(int pos = 0; pos < Main.listaExhibidores.size(); pos++) {

            // AGREGA AL LINEARLAYOUT UNA VISTA POR CADA ITEM DE LA LISTA



            final VistaItemListaModuloActivacion vProductoCompetencia = new VistaItemListaModuloActivacion(this);
            vProductoCompetencia.position = pos;

            if( Main.listaExhibidores.elementAt(vProductoCompetencia.position).id==null){
                Main.listaExhibidores.elementAt(vProductoCompetencia.position).id = Util.obtenerId(Main.usuario.codigo);
            }




            vProductoCompetencia.tvNombreModulo.setText(String.valueOf(Main.listaExhibidores.elementAt(pos).nombre));



            if (Main.listaExhibidores.elementAt(vProductoCompetencia.position).foto==1) {
                // SE HACE EL CAMBIO DE ICONO
                vProductoCompetencia.ivCamara.setImageResource(R.mipmap.cam_ok);
            } else {
                // SE HACE EL CAMBIO DE ICONO
                vProductoCompetencia.ivCamara.setImageResource(R.mipmap.cam);
            }

            if (Main.listaExhibidores.elementAt(vProductoCompetencia.position).respuesta.equals("SI")) {
                // SE HACE EL CAMBIO DE ICONO
                vProductoCompetencia.ivSi.setImageResource(R.mipmap.iconook);
            } else {
                // SE HACE EL CAMBIO DE ICONO
                vProductoCompetencia.ivSi.setImageResource(R.mipmap.iconook_gris);
            }

            if (Main.listaExhibidores.elementAt(vProductoCompetencia.position).respuesta.equals("NO")) {
                // SE HACE EL CAMBIO DE ICONO
                vProductoCompetencia.ivNo.setImageResource(R.mipmap.icon_no_rojo);
            } else {
                // SE HACE EL CAMBIO DE ICONO
                vProductoCompetencia.ivNo.setImageResource(R.mipmap.icon_no);
            }


            vProductoCompetencia.ivSi.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    if (vProductoCompetencia.si) {
                        // SE HACE EL CAMBIO DE ICONO
                        vProductoCompetencia.si = false;
                        vProductoCompetencia.ivSi.setImageResource(R.mipmap.iconook);

                        vProductoCompetencia.no = true;
                        vProductoCompetencia.ivNo.setImageResource(R.mipmap.icon_no);

                        Main.listaExhibidores.elementAt(vProductoCompetencia.position).respuesta = "SI";
                    } else {
                        // SE HACE EL CAMBIO DE ICONO
                        vProductoCompetencia.si = true;
                        vProductoCompetencia.ivSi.setImageResource(R.mipmap.iconook_gris);
                    }

                }
            });

            vProductoCompetencia.ivNo.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    if (vProductoCompetencia.no) {
                        // SE HACE EL CAMBIO DE ICONO
                        vProductoCompetencia.no = false;
                        vProductoCompetencia.ivNo.setImageResource(R.mipmap.icon_no_rojo);

                        vProductoCompetencia.si = true;
                        vProductoCompetencia.ivSi.setImageResource(R.mipmap.iconook_gris);

                        Main.listaExhibidores.elementAt(vProductoCompetencia.position).respuesta = "NO";
                    } else {
                        // SE HACE EL CAMBIO DE ICONO
                        vProductoCompetencia.no = true;
                        vProductoCompetencia.ivNo.setImageResource(R.mipmap.icon_no);
                    }


                }
            });

            vProductoCompetencia.ivCamara.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    Intent moduloFotosExhibidor = new Intent(ActivacionActivity.this, FotosActivity.class);
                    moduloFotosExhibidor.putExtra("IDEXHIBIDOR", Main.listaExhibidores.elementAt(vProductoCompetencia.position).id);
                    moduloFotosExhibidor.putExtra("EXHIBIDORREGISTROHOY", 0 );
                    moduloFotosExhibidor.putExtra("ACTIVACION", true);
                    moduloFotosExhibidor.putExtra("POSICION", vProductoCompetencia.position);
                    moduloFotosExhibidor.putExtra("COMPETENCIA", false);
                    moduloFotosExhibidor.putExtra("ESEXHIBIDOR", false);
                    startActivityForResult(moduloFotosExhibidor, Const.FOTO);

                }
            });

            vProductoCompetencia.llFondoProducto.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    Intent moduloFotosExhibidor = new Intent(ActivacionActivity.this, ExhibicionesAdicionalesActivity.class);
                    moduloFotosExhibidor.putExtra("IDEXHIBIDOR", Main.listaExhibidores.elementAt(vProductoCompetencia.position).id );
                    moduloFotosExhibidor.putExtra("POSICION", vProductoCompetencia.position);
                    startActivity(moduloFotosExhibidor);

                }
            });
            if(Main.listaExhibidores.elementAt(pos).codigo.equals("6")){
                vProductoCompetencia.ivCamara.setVisibility(View.INVISIBLE);
            }


            llListaModulosActivacion.addView(vProductoCompetencia);

        }
    }



    private void terminarMedicion() {

        String codigoCliente = PreferencesCliente.obtenerCodigoClienteSeleccionado(getApplicationContext());
        Usuario usuario = Main.usuario;
        int tipoUsuario = DataBaseBO.obtenerTipoUsuario();
        String id = Util.obtenerId(Main.usuario.codigo);
        String fecha = Util.obtenerFechaActual();

        int indexMarca =  spinner.getSelectedItemPosition();
        Marca marca = (Marca) spinner.getAdapter().getItem(indexMarca);

        int pos=existeRegistro(marca);
//                ActivacionCompetencia existe=existeRegistro(Main.marca);
        if(pos!=-1){
            listaActivacionesCompetencia.get(pos).listaModulosCompetencia=Main.listaExhibidoresCompetencia;
        }else{
            ActivacionCompetencia activacionCompetencia= new ActivacionCompetencia();
            activacionCompetencia.idMarca=  marca.codigo;
            activacionCompetencia.listaModulosCompetencia = Main.listaExhibidoresCompetencia;
            listaActivacionesCompetencia.add(activacionCompetencia);
        }

        boolean respuestasPropios=validarCheck(listaModulosPropios);
        boolean respuestasCompetencia=validarCheckCompetencia(listaActivacionesCompetencia);
        if(respuestasPropios && respuestasCompetencia) {
            boolean guardo = DataBaseBO.guardarActivaciones(codigoCliente, usuario, tipoUsuario, id, fecha, listaModulosPropios, listaActivacionesCompetencia,idCategoria);

            if(guardo) {

                Alert.nutresaShow(ActivacionActivity.this, "INFORMACÓN", "La informacion se almaceno de forma exitosa.",
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
                Alert.nutresaShow(ActivacionActivity.this, "ERROR", "No se pudo guardar la medición de forma correcta.",
                        "OK", null,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Alert.dialogo.cancel();
                            }

                        }, null);
            }
        }else{
            //ALERT
            Alert.nutresaShow(ActivacionActivity.this, "ERROR", "Existen activaciones sin marcar",
                    "OK", null,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Alert.dialogo.cancel();
                        }

                    }, null);
        }


    }

    public boolean validarCheck(Vector<ModuloActivacion> listaModulosPropios){

        for (ModuloActivacion m:listaModulosPropios){
            if (m.respuesta.equals(""))
                return false;
        }
        return true;
    }

    public boolean validarCheckCompetencia(Vector<ActivacionCompetencia> listaModulosCompe){

        for (int i=0; i<listaModulosCompe.size();i++){
            for (int j=0; j<listaModulosCompe.get(i).listaModulosCompetencia.size();j++){
                if(listaModulosCompe.get(i).listaModulosCompetencia.get(j).respuesta.equals("")){
                    return false;
                }
            }
        }
        return true;
    }

    public boolean verificarVacios(Vector<ModuloActivacion> listaModulosPropios){

        for (ModuloActivacion m:listaModulosPropios){
            if (!m.respuesta.equals(""))
                return true;
        }
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Const.FOTO) {

//            if (resultCode == Activity.RESULT_OK) {
                cargarListaModulosPropios(true);
                cargarListaModulosCompetencia(true,Main.listaExhibidoresCompetencia);
//            }
        }
    }

    public void on_ClickGuardar(View view) {
        terminarMedicion();
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

        ActivacionActivity.this.finish();
    }
}
