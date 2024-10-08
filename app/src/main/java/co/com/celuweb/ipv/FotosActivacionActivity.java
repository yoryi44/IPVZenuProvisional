package co.com.celuweb.ipv;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.util.ArrayList;

import businessObject.DataBaseBO;
import component.Alert;
import component.Util;
import config.Const;
import dataObject.Cliente;
import dataObject.Foto;
import dataObject.Main;
import dataObject.Usuario;
import es.dmoral.toasty.Toasty;
import sharedPreferences.PreferencesCliente;

public class FotosActivacionActivity extends AppCompatActivity {

    private Foto fotoSel;
    private int posFotoSel;
    private int anchoImg;
    private int altoImg;

    private String idMedicion;

    private String codigoCliente;
    private Usuario usuario;

    private TextView tvLabelRazonSocial;

    boolean estaGuardando = true;

    private String opcionTitulo;
    private int opcionActualLogica;
    private int opcionTipoSel;

    private final static String TAG = FotosActivacionActivity.class.getName();

    // ARRAYLIST DE OBJETOS FOTO
    ArrayList<Foto> listaDeFotosMedicion = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_fotos);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder(); StrictMode.setVmPolicy(builder.build());

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

        Main.fotoActual = null;
        Main.fotosGaleria.removeAllElements();
        Main.listaInfoFotos.removeAllElements();

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

            if (extras.containsKey("IDMEDICION"))
                idMedicion = extras.getString("IDMEDICION");
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

            Toasty.warning(FotosActivacionActivity.this, "Se ha alcanzado el limite de fotos almacenables para esta medición.", Toast.LENGTH_LONG).show();
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

                Toasty.warning(FotosActivacionActivity.this, "Se ha alcanzado el limite de fotos almacenables para esta medición.", Toast.LENGTH_LONG).show();
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

                        foto.id             = idMedicion;
                        foto.idFoto         = Util.obtenerId(Main.usuario.codigo);
                        foto.codigoCliente  = codigoCliente;
                        foto.codigoVendedor = Main.usuario.codigo;
                        foto.modulo         = 4;
                        foto.fecha          = Util.obtenerFechaActual();

                        if (DataBaseBO.guardarImagen(foto, byteArray)) {

                            Main.guardarFoto = false;
                            imgFoto = Util.resizedImage(anchoImg, altoImg);

                            if (imgFoto != null) {

                                Drawable imgGaleria = Util.resizedImage(30, 30);
                                Main.fotosGaleria.addElement(imgGaleria);
                                Main.listaInfoFotos.addElement(foto);

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

                Alert.nutresaShow(FotosActivacionActivity.this, "INFORMACIÓN", mensaje + ".",
                        "ACEPTAR",
                        null,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Alert.dialogo.cancel();
                                SetPhotoDefault();

                                ((FotosActivacionActivity.ImageAdapter)(((Gallery) findViewById(R.id.galleryFotos))).getAdapter()).notifyDataSetChanged();

                                fotoSel = null;
                                posFotoSel = -1;
                            }

                        }, null);

            } else {

                Toasty.warning(FotosActivacionActivity.this, "Debe capturar la imagen antes de guardarla.", Toast.LENGTH_LONG).show();
            }

            estaGuardando = true;
        }
    }

    public void on_ClickEliminarFoto(View view) {

        if (fotoSel != null) {

            //ALERT
            Alert.nutresaShow(FotosActivacionActivity.this, "INFORMACIÓN", "Esta seguro de Eliminar la foto?",
                    "ACEPTAR",
                    "CANCELAR",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Alert.dialogo.cancel();

                            if (DataBaseBO.borrarImagen(fotoSel.idFoto)) {

                                Main.fotosGaleria.removeElementAt(posFotoSel);
                                Main.listaInfoFotos.removeElementAt(posFotoSel);

                                Alert.nutresaShow(FotosActivacionActivity.this, "INFORMACIÓN", "Foto eliminada con éxito.",
                                        "ACEPTAR",
                                        null,
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                Alert.dialogo.cancel();
                                                SetPhotoDefault();

                                                ((FotosActivacionActivity.ImageAdapter)(((Gallery) findViewById(R.id.galleryFotos))).getAdapter()).notifyDataSetChanged();

                                                fotoSel = null;
                                                posFotoSel = -1;
                                            }

                                        }, null);

                            } else {

                                Toasty.error(FotosActivacionActivity.this, "Error eliminando la foto.", Toast.LENGTH_LONG).show();
                            }
                        }

                    }, new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            Alert.dialogo.cancel();
                        }
                    });

        } else {

            Toasty.info(FotosActivacionActivity.this, "Para eliminar, debe seleccionar una foto.", Toast.LENGTH_LONG).show();
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
        galleryFotos.setAdapter(new FotosActivacionActivity.ImageAdapter(this));
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

            Toasty.warning(FotosActivacionActivity.this, "No se pudo cargar la Foto.", Toast.LENGTH_LONG).show();
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

        if(opcionTipoSel == 6) {

            Intent productosFormatoGeneralActivacionActivity = new Intent(FotosActivacionActivity.this, ProductosFormatoGeneralActivacionActivity.class);
            productosFormatoGeneralActivacionActivity.putExtra("OPCIONACTUALLOGICA", opcionActualLogica);
            productosFormatoGeneralActivacionActivity.putExtra("OPCIONTIPOSEL", opcionTipoSel);

            if(opcionActualLogica == 1) {

                productosFormatoGeneralActivacionActivity.putExtra("OPCIONTITULO", "PROPIA");

            } else if(opcionActualLogica == 10) {

                productosFormatoGeneralActivacionActivity.putExtra("OPCIONTITULO", "COMPETENCIA");
            }

            // IDMEDICION PARA LA MEDICION GENERAL
            productosFormatoGeneralActivacionActivity.putExtra("IDMEDICION", idMedicion);

            // CODIGO COMPONENTE SELECCIONADO
            productosFormatoGeneralActivacionActivity.putExtra("CODIGOCOMPONENTESELECCIONADO", "");

            // NOMBRE COMPONENTE SELECCIONADO
            productosFormatoGeneralActivacionActivity.putExtra("NOMBRECOMPONENTESELECCIONADO", "");

            startActivityForResult(productosFormatoGeneralActivacionActivity, Const.RESP_TERMINARACTIVACION);

            Main.fotoActual = null;
            Main.fotosGaleria.removeAllElements();
            Main.listaInfoFotos.removeAllElements();

            finish();

        } else {

            Intent listaFormatoGeneralActivacionActivity = new Intent(FotosActivacionActivity.this, ListaFormatoGeneralActivacionActivity.class);
            listaFormatoGeneralActivacionActivity.putExtra("OPCIONACTUALLOGICA", opcionActualLogica);
            listaFormatoGeneralActivacionActivity.putExtra("OPCIONTIPOSEL", opcionTipoSel);

            if(opcionActualLogica == 1) {

                listaFormatoGeneralActivacionActivity.putExtra("OPCIONTITULO", "PROPIA");

            } else if(opcionActualLogica == 10) {

                listaFormatoGeneralActivacionActivity.putExtra("OPCIONTITULO", "COMPETENCIA");
            }

            // IDMEDICION PARA LA MEDICION GENERAL
            listaFormatoGeneralActivacionActivity.putExtra("IDMEDICION", idMedicion);

            startActivity(listaFormatoGeneralActivacionActivity);

            Main.fotoActual = null;
            Main.fotosGaleria.removeAllElements();
            Main.listaInfoFotos.removeAllElements();

            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            //ALERT
            Alert.nutresaShow(FotosActivacionActivity.this, "ALERTA", "Toda la información de la medición actual sera eliminada, desea continuar.",
                    "ACEPTAR",
                    "CANCELAR",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Alert.dialogo.cancel();

                            Main.fotoActual = null;
                            Main.fotosGaleria.removeAllElements();
                            Main.listaInfoFotos.removeAllElements();

                            DataBaseBO.eliminarFotosMedicionActual(idMedicion);

                            System.gc();
                            finish();
                        }

                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Alert.dialogo.cancel();
                        }
                    });
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {}
}
