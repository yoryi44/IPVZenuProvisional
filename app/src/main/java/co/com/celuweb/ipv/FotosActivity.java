package co.com.celuweb.ipv;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;

import businessObject.DataBaseBO;
import component.Alert;
import component.Util;
import config.Const;
import dataObject.Cliente;
import dataObject.ExhibidorEncabezado;
import dataObject.Foto;
import dataObject.Main;
import dataObject.Usuario;
import es.dmoral.toasty.Toasty;
import sharedPreferences.PreferencesCliente;

public class FotosActivity extends AppCompatActivity {

    private Foto fotoSel;
    private int posFotoSel;
    private int anchoImg;
    private int altoImg;

    private String idExhibidor;
    private int exhibidorRegistroHoy;

    private String codigoCliente;
    private Usuario usuario;

    private TextView tvLabelRazonSocial;

    boolean estaGuardando = true;
    boolean activacion = false;
    boolean esCompetencia = false;
    boolean esExhibidor = false;
    private String idCategoria="";
    int posicion;

    private final static String TAG = FotosActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_fotos);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder(); StrictMode.setVmPolicy(builder.build());

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("FOTOS EXHIBICIÓN");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        setSupportActionBar(toolbar);

        // SE INICIALIZA EL COMPONENTE PRINCIPAL DE LA APLICACION
        Main.contexto = getApplicationContext();

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        setComponentesVista();
        cargarClienteSel();
        cargarInformacion();
        inicializar();
    }

    private void setComponentesVista() {

        tvLabelRazonSocial = findViewById(R.id.tvLabelRazonSocial);
        tvLabelRazonSocial.setTypeface(Const.letraSemibold);
    }

    private void cargarClienteSel() {

        if(Main.cliente == null) {

            Cliente cliente = DataBaseBO.obtenerClienteSeleccionado(PreferencesCliente.obtenerCodigoClienteSeleccionado(getApplicationContext()));
            Main.cliente = cliente;
        }

        tvLabelRazonSocial = findViewById(R.id.tvLabelRazonSocial);
        tvLabelRazonSocial.setText(String.valueOf(Main.cliente.razonSocial));
    }

    public void cargarInformacion() {

        codigoCliente = PreferencesCliente.obtenerCodigoClienteSeleccionado(getApplicationContext());
        usuario = Main.usuario;

        if(Main.cliente == null) {

            Cliente cliente = DataBaseBO.obtenerClienteSeleccionado(PreferencesCliente.obtenerCodigoClienteSeleccionado(getApplicationContext()));
            Main.cliente = cliente;
        }

        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            if (extras.containsKey("IDEXHIBIDOR"))
                idExhibidor = extras.getString("IDEXHIBIDOR");

            if (extras.containsKey("EXHIBIDORREGISTROHOY"))
                exhibidorRegistroHoy = extras.getInt("EXHIBIDORREGISTROHOY");

            if (extras.containsKey("ACTIVACION"))
                activacion = extras.getBoolean("ACTIVACION");

            if (extras.containsKey("POSICION"))
                posicion = extras.getInt("POSICION");

            if (extras.containsKey("COMPETENCIA"))
                esCompetencia = extras.getBoolean("COMPETENCIA");

            if (extras.containsKey("ESEXHIBIDOR"))
                esExhibidor = extras.getBoolean("ESEXHIBIDOR");


            if (extras.containsKey("IDCATEGORIA"))
                idCategoria = extras.getString("IDCATEGORIA");

        }

        if (activacion){
            findViewById(R.id.btnContinuar).setVisibility(View.GONE);
            findViewById(R.id.btnTerminarActivacion).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.btnContinuar).setVisibility(View.VISIBLE);
            findViewById(R.id.btnTerminarActivacion).setVisibility(View.GONE);
        }




    }

    public void inicializar() {

        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int ancho = display.getWidth();
        int alto = display.getHeight();

        anchoImg = (ancho * 100) / 240;
        altoImg = (alto * 130) / 320;

        IncializarCatalogo();

        if (Main.fotoActual != null) {

            (findViewById(R.id.imageFoto)).setBackground(Main.fotoActual);

        } else {

            SetPhotoDefault();
        }
    }

    public void on_ClickTomarFoto(View view) {

        if (Main.listaInfoFotos.size() >= 3) {

            Toasty.warning(FotosActivity.this, "Se ha alcanzado el limite de fotos almacenables para esta medición.", Toast.LENGTH_LONG).show();
            return;
        }

        fotoSel = null;
        posFotoSel = -1;

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String filePath = Util.dirApp().getPath() + "/foto.jpg";

        Uri output = Uri.fromFile(new File(filePath));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
        startActivityForResult(intent, Const.RESP_TOMAR_FOTO);
    }

    public void on_ClickGuardarFoto(View view) {

        if (estaGuardando == true) {

            estaGuardando = false;
            if (Main.listaInfoFotos.size() >= 3) {

                Toasty.warning(FotosActivity.this, "Se ha alcanzado el limite de fotos almacenables para esta medición.", Toast.LENGTH_LONG).show();
                return;
            }

            if (Main.guardarFoto) {

                fotoSel = null;
                posFotoSel = -1;
                boolean tipoMensaje = false;

                String mensaje;
                Drawable imgFoto = Util.resizedImage(320, 480);

                if (imgFoto != null) {

                    Bitmap bitmap = ((BitmapDrawable)imgFoto).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                    byte[] byteArray = stream.toByteArray();

                    if (byteArray != null && byteArray.length > 0) {

                        Foto foto = new Foto();

                        foto.id             = idExhibidor;
                        foto.idFoto         = Util.obtenerId(Main.usuario.codigo);
                        foto.codigoCliente  = codigoCliente;
                        foto.codigoVendedor = Main.usuario.codigo;
                        if (activacion){
                            foto.modulo         = 4;
                        }else{
                            foto.modulo         = 1;
                        }

                        foto.fecha          = Util.obtenerFechaActual();

                        if (DataBaseBO.guardarImagen(foto, byteArray)) {

                            Main.guardarFoto = false;
                            imgFoto = Util.resizedImage(anchoImg, altoImg);

                            if (imgFoto != null) {

                                Drawable imgGaleria = Util.resizedImage(30, 30);
                                Main.fotosGaleria.addElement(imgGaleria);
                                Main.listaInfoFotos.addElement(foto);
                                if(!esExhibidor){
                                    if (esCompetencia){
                                        Main.listaExhibidoresCompetencia.elementAt(posicion).foto=1;
                                    }else{
                                        Main.listaExhibidores.elementAt(posicion).foto=1;
                                    }
                                }

                                mensaje = "Foto Guardada con Exito";
                                Main.fotoActual = null;
                                System.gc();

                            } else {

                                mensaje = "Foto Guardada con Exito, Error Visualizando la Img.";
                                tipoMensaje = true;
                            }

                        } else {

                            mensaje = "Error guardando la Imagen: " + DataBaseBO.mensaje;
                            tipoMensaje = false;
                        }

                    } else {

                        mensaje = "Error procesando la Imagen 2";
                        tipoMensaje = false;
                    }

                } else {

                    mensaje = "Error procesando la Imagen 1";
                    tipoMensaje = false;
                }

                //ALERT
                String tipo = "";

                if(tipoMensaje) {

                    tipo = "INFORMACIÓN";

                } else {

                    tipo = "ERROR";
                }

                Alert.nutresaShow(FotosActivity.this, "INFORMACIÓN", mensaje + ".",
                        "ACEPTAR",
                        null,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Alert.dialogo.cancel();
                                SetPhotoDefault();

                                ((ImageAdapter)(((Gallery) findViewById(R.id.galleryFotos))).getAdapter()).notifyDataSetChanged();

                                fotoSel = null;
                                posFotoSel = -1;
                            }

                        }, null);

            } else {

                Toasty.warning(FotosActivity.this, "Debe capturar la imagen antes de guardarla.", Toast.LENGTH_LONG).show();
            }

            estaGuardando = true;
        }
    }

    public void on_ClickEliminarFoto(View view) {

        if (fotoSel != null) {

            //ALERT
            Alert.nutresaShow(FotosActivity.this, "INFORMACIÓN", "Esta seguro de Eliminar la foto?",
                    "ACEPTAR",
                    "CANCELAR",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Alert.dialogo.cancel();

                            if (DataBaseBO.borrarImagen(fotoSel.idFoto)) {

                                Main.fotosGaleria.removeElementAt(posFotoSel);
                                Main.listaInfoFotos.removeElementAt(posFotoSel);

                                Alert.nutresaShow(FotosActivity.this, "INFORMACIÓN", "Foto eliminada con éxito.",
                                        "ACEPTAR",
                                        null,
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                Alert.dialogo.cancel();
                                                SetPhotoDefault();

                                                ((ImageAdapter)(((Gallery) findViewById(R.id.galleryFotos))).getAdapter()).notifyDataSetChanged();

                                                fotoSel = null;
                                                posFotoSel = -1;
                                            }

                                        }, null);

                            } else {

                                Toasty.error(FotosActivity.this, "Error eliminando la foto.", Toast.LENGTH_LONG).show();
                            }
                        }

                    }, new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            Alert.dialogo.cancel();
                        }
                    });

        } else {

            Toasty.info(FotosActivity.this, "Para eliminar, debe seleccionar una foto.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Const.RESP_TOMAR_FOTO && resultCode == RESULT_OK) {

            Main.fotoActual = Util.resizedImage(anchoImg, altoImg);

            if (Main.fotoActual != null) {

                Main.guardarFoto = true;
                ImageView imgFoto = findViewById(R.id.imageFoto);
                imgFoto.setBackground(Main.fotoActual);
            }

//            BorrarFotoCapturada();
        }
    }

    public void BorrarFotoCapturada() {

        String[] projection = { MediaStore.Images.ImageColumns.SIZE,
                MediaStore.Images.ImageColumns.DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATA,
                BaseColumns._ID, };

        Cursor cursor = null;
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        try {

            if (uri != null) {

                cursor = getContentResolver().query(uri, projection, null, null, null);
            }

            if (cursor != null && cursor.moveToLast()) {

                ContentResolver contentResolver = getContentResolver();
                int rows = contentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, BaseColumns._ID + "=" + cursor.getString(3), null);
            }

        } finally {

            if (cursor != null)
                cursor.close();
        }
    }

    public void IncializarCatalogo() {

        Gallery galleryFotos = findViewById(R.id.galleryFotos);
        galleryFotos.setAdapter(new ImageAdapter(this));
        galleryFotos.setSpacing(6);

        galleryFotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                posFotoSel = position;
                fotoSel = Main.listaInfoFotos.elementAt(position);
                CargarFotoSeleccionada();
            }
        });
    }

    public void CargarFotoSeleccionada() {

        Main.guardarFoto = false;
        byte[] image = DataBaseBO.cargarImagen(fotoSel.idFoto);

        if (image != null && image.length > 0) {

            Main.fotoActual = Util.resizedImage(image, anchoImg, altoImg);
            ImageView imgFoto = findViewById(R.id.imageFoto);
            imgFoto.setBackground(Main.fotoActual);

        } else {

            Toasty.warning(FotosActivity.this, "No se pudo cargar la Foto.", Toast.LENGTH_LONG).show();
        }
    }

    public void SetPhotoDefault() {

        Drawable fotoVacia = getResources().getDrawable(R.mipmap.fotovacia);
        Drawable img = Util.resizedImage(fotoVacia, anchoImg, altoImg);

        if (img != null)
            ((ImageView) findViewById(R.id.imageFoto)).setBackground(img);
    }

    public class ImageAdapter extends BaseAdapter {

        private Context context;
        int galleryItemBackground;

        public ImageAdapter(Context context) {

            this.context = context;
            TypedArray typedArray = obtainStyledAttributes(R.styleable.GalleryFotos);
            galleryItemBackground = typedArray.getResourceId(R.styleable.GalleryFotos_android_galleryItemBackground, 0);
            typedArray.recycle();
        }

        public int getCount() {

            return Main.fotosGaleria.size();
        }

        public Object getItem(int position) {

            return position;
        }

        public long getItemId(int position) {

            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            ImageView i = new ImageView(context);
            i.setImageDrawable(Main.fotosGaleria.elementAt(position));
            i.setScaleType(ImageView.ScaleType.FIT_XY);
            i.setBackgroundResource(galleryItemBackground);
            return i;
        }
    }

    public void on_ClickContinuarExhibidor(View view) {

        // SE REALIZA LA VERIFICACION SI HAY FOTOS PARA LA MEDICION ACTUAL
        int bandera = 0;
        int cantidadFotosMedicionActual = DataBaseBO.hayFotosMedicionActual(idExhibidor);

        if(cantidadFotosMedicionActual > 0) {

            // SE DETERMINA SEGUN LA BANDERA "exhibidorRegistroHoy" A CUAL TABLA DEBE IR LA LOGICA
            if(exhibidorRegistroHoy == 1) {

                // ES UN REGISTRO DE HOY
                // HAY FOTOS
                // 1. SE OBTIENE EL REGISTO DEL ENCABEZADO ACTUAL Y SE INSERTA DE NUEVO EN TEMP
                ExhibidorEncabezado exhibidorEncabezadoHistorico = DataBaseBO.obtenerExhibidorEncabezado_ACTUAL(idExhibidor);
                bandera = 1;
                exhibidorEncabezadoHistorico.fechaMovil = Util.obtenerFechaActual();

                // SE ELIMINA EL ENCABEZADO EXISTENTE PARA REEMPLAZRLO POR EL MAS ACTUALIZADO
                DataBaseBO.eliminarExhibidorEncabeza(idExhibidor);

                DataBaseBO.actualizarMedicionExhibidorActual_Fotos(bandera, idExhibidor, exhibidorEncabezadoHistorico,idCategoria);

            } else {

                // ES UN REGISTRO HISTORICO
                // 1. SE OBTIENE EL REGISTO DEL HISTORICO
                ExhibidorEncabezado exhibidorEncabezadoHistorico = DataBaseBO.obtenerExhibidorEncabezado(idExhibidor);
                exhibidorEncabezadoHistorico.isFoto = "1";
                exhibidorEncabezadoHistorico.fechaMovil = Util.obtenerFechaActual();

                // 2. SE ALMACENA CON LA INFORMACION DE LA FOTO ACTUAL
                DataBaseBO.insertarMedicionExhibidorActualizada_Fotos(exhibidorEncabezadoHistorico,idCategoria);

                // SE ACTUALIZA BANDERA DE "exhibidorRegistroHoy" POR QUE CON AL LOGICA DE LA FOTO EL REGISTRO YA SE HIZO HOY
                exhibidorRegistroHoy = 1;
            }

        } else {

            // SE DETERMINA SEGUN LA BANDERA "exhibidorRegistroHoy" A CUAL TABLA DEBE IR LA LOGICA
            if(exhibidorRegistroHoy == 1) {

                // ES UN REGISTRO DE HOY
                // NO HAY FOTOS
                // 1. SE OBTIENE EL REGISTO DEL ENCABEZADO ACTUAL Y SE INSERTA DE NUEVO EN TEMP
                ExhibidorEncabezado exhibidorEncabezadoHistorico = DataBaseBO.obtenerExhibidorEncabezado_ACTUAL(idExhibidor);
                bandera = 0;
                exhibidorEncabezadoHistorico.fechaMovil = Util.obtenerFechaActual();

                // SE ELIMINA EL ENCABEZADO EXISTENTE PARA REEMPLAZRLO POR EL MAS ACTUALIZADO
                DataBaseBO.eliminarExhibidorEncabeza(idExhibidor);

                DataBaseBO.actualizarMedicionExhibidorActual_Fotos(bandera, idExhibidor, exhibidorEncabezadoHistorico,idCategoria);

            } else {

                // ES UN REGISTRO HISTORICO
                // 1. SE OBTIENE EL REGISTO DEL HISTORICO
                ExhibidorEncabezado exhibidorEncabezadoHistorico = DataBaseBO.obtenerExhibidorEncabezado(idExhibidor);
                exhibidorEncabezadoHistorico.isFoto = "0";
                exhibidorEncabezadoHistorico.fechaMovil = Util.obtenerFechaActual();

                // 2. SE ALMACENA CON LA INFORMACION DE LA FOTO ACTUAL
                DataBaseBO.insertarMedicionExhibidorActualizada_Fotos(exhibidorEncabezadoHistorico,idCategoria);

                // SE ACTUALIZA BANDERA DE "exhibidorRegistroHoy" POR QUE CON AL LOGICA DE LA FOTO EL REGISTRO YA SE HIZO HOY
                exhibidorRegistroHoy = 1;
            }
        }

        // SE DETERMINA EL CANAL DEL CLIENTE PARA SABER SI DEBE O NO MEDIR TEMPERATURA
        boolean debeTenerMedicionTemperatura = DataBaseBO.debeTenerMedicionTemperatura(Main.cliente.canal);

        if(debeTenerMedicionTemperatura) {

            Intent moduloTemperaturaExhibidor = new Intent(FotosActivity.this, TemperaturaActivity.class);
            moduloTemperaturaExhibidor.putExtra("IDEXHIBIDOR", idExhibidor);
            moduloTemperaturaExhibidor.putExtra("EXHIBIDORREGISTROHOY", exhibidorRegistroHoy);
            moduloTemperaturaExhibidor.putExtra("IDCATEGORIA", idCategoria);
            startActivity(moduloTemperaturaExhibidor);

        } else {

//            // 1. SE OBTIENE EL REGISTO DEL ENCABEZADO ACTUAL Y SE INSERTA DE NUEVO EN TEMP
//            ExhibidorEncabezado exhibidorEncabezadoHistorico = DataBaseBO.obtenerExhibidorEncabezado_ACTUAL_NOTEMPERATURA(idExhibidor);
//            exhibidorEncabezadoHistorico.fechaMovil = Util.obtenerFechaActual();
//
//            // SE ELIMINA EL ENCABEZADO EXISTENTE PARA REEMPLAZRLO POR EL MAS ACTUALIZADO
//            DataBaseBO.eliminarExhibidorEncabeza(idExhibidor);
//
//            DataBaseBO.actualizarMedicionExhibidorActual_Temperatura(0, idExhibidor, "", exhibidorEncabezadoHistorico);
//
//            // SE CARGA DIRECTAMENTE EL MODULO DE PRODUCTOS PARA LA MEDICION
//            Intent moduloProductosExhibidor = new Intent(FotosActivity.this, ProductosExhibidorActivity.class);
//            moduloProductosExhibidor.putExtra("IDEXHIBIDOR", idExhibidor);
//            moduloProductosExhibidor.putExtra("EXHIBIDORREGISTROHOY", exhibidorRegistroHoy);
//            startActivity(moduloProductosExhibidor);
        }

        // SE LIMPIAN LOS COMPONENTES DE LA FOTO
        Main.fotoActual = null;
        Main.fotosGaleria.removeAllElements();
        Main.listaInfoFotos.removeAllElements();
        System.gc();

        setResult(Activity.RESULT_OK);
        finish();
    }

    public void on_ClickTerminarMedicionFotos(View view) {
        // SE LIMPIAN LOS COMPONENTES DE LA FOTO
        Main.fotoActual = null;
        Main.fotosGaleria.removeAllElements();
        Main.listaInfoFotos.removeAllElements();
        System.gc();

        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            Main.fotoActual = null;
            Main.fotosGaleria.removeAllElements();
            Main.listaInfoFotos.removeAllElements();

            // TODO: AGREGAR MENSAJE DE ALERTA PARA BORRAR FOTOS Y CREAR METODO DE BORRADO

            System.gc();

            setResult(Activity.RESULT_OK);
            finish();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {}
}
