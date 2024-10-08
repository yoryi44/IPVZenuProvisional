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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import businessObject.DataBaseBO;
import component.Util;
import config.Const;
import dataObject.Cliente;
import dataObject.ExhibidorEncabezado;
import dataObject.Main;
import dataObject.Usuario;
import sharedPreferences.PreferencesCliente;

public class TemperaturaActivity extends AppCompatActivity {

    private EditText etCantidadGrados;
    private TextView tvIndicadorGrados;

    private TextView tvPreguntaParte2;
    private TextView tvPreguntaParte1;

    private TextView tvMensaje1;
    private TextView tvMensaje2;
    private TextView tvMensaje3;

    private TextView tvCantidadTemperaturaAnterior;

    private TextView tvLabelRazonSocial;

    private String idExhibidor;
    private int exhibidorRegistroHoy;

    private ImageView ivImagenExhibidor;

    private String codigoCliente;
    private Usuario usuario;
    private String idCategoria="";

    private final static String TAG = TemperaturaActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_temperatura);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        setSupportActionBar(toolbar);

        // SE INICIALIZA EL COMPONENTE PRINCIPAL DE LA APLICACION
        Main.contexto = getApplicationContext();

        TextView tvTituloModulo = findViewById(R.id.tvTituloModulo);
        tvTituloModulo.setTypeface(Const.letraSemibold);
        tvTituloModulo.setText("EXHIBICIÓN DE PRODUCTOS");

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        cargarInformacion();
        setComponentesVista();
        cargarClienteSel();
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


            if (extras.containsKey("IDCATEGORIA"))
                idCategoria = extras.getString("IDCATEGORIA");
        }
    }

    private void setComponentesVista() {

        tvLabelRazonSocial = findViewById(R.id.tvLabelRazonSocial);
        tvLabelRazonSocial.setTypeface(Const.letraSemibold);

        etCantidadGrados = findViewById(R.id.etCantidadGrados);
        etCantidadGrados.setTypeface(Const.letraSemibold);

        tvIndicadorGrados = findViewById(R.id.tvIndicadorGrados);
        tvIndicadorGrados.setTypeface(Const.letraSemibold);

        tvPreguntaParte2 = findViewById(R.id.tvPreguntaParte2);
        tvPreguntaParte2.setTypeface(Const.letraSemibold);

        tvPreguntaParte1 = findViewById(R.id.tvPreguntaParte1);
        tvPreguntaParte1.setTypeface(Const.letraSemibold);

        tvMensaje1 = findViewById(R.id.tvMensaje1);
        tvMensaje1.setTypeface(Const.letraRegular);

        tvMensaje2 = findViewById(R.id.tvMensaje2);
        tvMensaje2.setTypeface(Const.letraRegular);

        tvMensaje3 = findViewById(R.id.tvMensaje3);
        tvMensaje3.setTypeface(Const.letraRegular);

        ivImagenExhibidor = findViewById(R.id.ivImagenExhibidor);

        // TODO: AGREGAR FOTO DE LA MEDICION
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
    public void onBackPressed() {}

    public void on_ClickContinuarExhibidor(View view) {

        // SE DETERMINA SI HAY UNA TEMPERATURA ASIGNADA, SI LA HAY SE ALMACENA EN EL ENCABEZADO
        // Y SE PONE LA BANDERA DE 1 EN LA MISMA TABLA INDICANDO QUE HAY MEDICION DE ESE TIPO
        // DE LO CONTRARIO SE PONE CERO LA BANDERA Y NADA EN LA TEMPERATURA DE LA MEDICION
        int bandera = 0;
        String cantidad = "";
        String cantidadGradosFinal = etCantidadGrados.getText().toString();
        int cantidadGrados = Util.toInt(cantidadGradosFinal);

        if(cantidadGrados > 0) {

            if(exhibidorRegistroHoy == 1) {

                // ES UN REGISTRO DE HOY
                // SI HAY MEDICION
                // 1. SE OBTIENE EL REGISTO DEL ENCABEZADO ACTUAL Y SE INSERTA DE NUEVO EN TEMP
                ExhibidorEncabezado exhibidorEncabezadoHistorico = DataBaseBO.obtenerExhibidorEncabezado2_ACTUAL(idExhibidor);
                bandera = 1;
                cantidad = String.valueOf(cantidadGrados);
                exhibidorEncabezadoHistorico.fechaMovil = Util.obtenerFechaActual();

                // SE ELIMINA EL ENCABEZADO EXISTENTE PARA REEMPLAZRLO POR EL MAS ACTUALIZADO
                DataBaseBO.eliminarExhibidorEncabeza(idExhibidor);

                DataBaseBO.actualizarMedicionExhibidorActual_Temperatura(bandera, idExhibidor, cantidad, exhibidorEncabezadoHistorico,idCategoria);

            } else {

                // ES UN REGISTRO HISTORICO
                // 1. SE OBTIENE EL REGISTO DEL HISTORICO
                ExhibidorEncabezado exhibidorEncabezadoHistorico = DataBaseBO.obtenerExhibidorEncabezado2(idExhibidor);
                exhibidorEncabezadoHistorico.isTemperatura = "1";
                exhibidorEncabezadoHistorico.valorTemperatura = String.valueOf(cantidadGrados);
                exhibidorEncabezadoHistorico.fechaMovil = Util.obtenerFechaActual();

                // 2. SE ALMACENA CON LA INFORMACION DE LA FOTO ACTUAL
                DataBaseBO.insertarMedicionExhibidorActualizada_Temperatura(exhibidorEncabezadoHistorico,idCategoria);

                // SE ACTUALIZA BANDERA DE "exhibidorRegistroHoy" POR QUE CON AL LOGICA DE LA TEMPERATURA EL REGISTRO YA SE HIZO HOY
                exhibidorRegistroHoy = 1;
            }

        } else {

            if(exhibidorRegistroHoy == 1) {

                // ES UN REGISTRO DE HOY
                // NO HAY MEDICION
                // 1. SE OBTIENE EL REGISTO DEL ENCABEZADO ACTUAL Y SE INSERTA DE NUEVO EN TEMP
                ExhibidorEncabezado exhibidorEncabezadoHistorico = DataBaseBO.obtenerExhibidorEncabezado2_ACTUAL(idExhibidor);
                exhibidorEncabezadoHistorico.fechaMovil = Util.obtenerFechaActual();

                // SE ELIMINA EL ENCABEZADO EXISTENTE PARA REEMPLAZRLO POR EL MAS ACTUALIZADO
                DataBaseBO.eliminarExhibidorEncabeza(idExhibidor);

                DataBaseBO.actualizarMedicionExhibidorActual_Temperatura(bandera, idExhibidor, cantidad, exhibidorEncabezadoHistorico,idCategoria);

            } else {

                // ES UN REGISTRO HISTORICO
                // 1. SE OBTIENE EL REGISTO DEL HISTORICO
                ExhibidorEncabezado exhibidorEncabezadoHistorico = DataBaseBO.obtenerExhibidorEncabezado2(idExhibidor);
                exhibidorEncabezadoHistorico.isTemperatura = "0";
                exhibidorEncabezadoHistorico.valorTemperatura = "";
                exhibidorEncabezadoHistorico.fechaMovil = Util.obtenerFechaActual();

                // 2. SE ALMACENA CON LA INFORMACION DE LA FOTO ACTUAL
                DataBaseBO.insertarMedicionExhibidorActualizada_Temperatura(exhibidorEncabezadoHistorico,idCategoria);

                // SE ACTUALIZA BANDERA DE "exhibidorRegistroHoy" POR QUE CON AL LOGICA DE LA TEMPERATURA EL REGISTRO YA SE HIZO HOY
                exhibidorRegistroHoy = 1;
            }
        }

        Intent moduloProductosExhibidor = new Intent(TemperaturaActivity.this, ProductosExhibidorActivity.class);
        moduloProductosExhibidor.putExtra("IDEXHIBIDOR", idExhibidor);
        moduloProductosExhibidor.putExtra("EXHIBIDORREGISTROHOY", exhibidorRegistroHoy);
        moduloProductosExhibidor.putExtra("IDCATEGORIA", idCategoria);
        startActivity(moduloProductosExhibidor);
        finish();
    }

    public void on_ClickCopiarValorAnterior(View view) {

        // SE DETERMINA SI HAY UN VALOR ASIGNADO ANTERIOR
        tvCantidadTemperaturaAnterior = findViewById(R.id.tvCantidadTemperaturaAnterior);

        String valorConGrados = (tvCantidadTemperaturaAnterior.getText().toString());
        int index = valorConGrados.indexOf("º");
        int valorAnterior = Util.toInt(valorConGrados.substring(0, index));

        if(valorAnterior > 0) {

            etCantidadGrados.setText(String.valueOf(valorAnterior));
        }
    }
}
