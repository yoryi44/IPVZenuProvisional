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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.Vector;

import businessObject.DataBaseBO;
import component.Alert;
import component.Util;
import config.Const;
import dataObject.Gramo;
import dataObject.Linea;
import dataObject.Main;
import dataObject.Marca;
import dataObject.Materia;
import dataObject.Producto;
import dataObject.Usuario;
import sharedPreferences.PreferencesCliente;

public class AgregarProcutoCompretenciaActivity extends AppCompatActivity {

    private Spinner spinnerMarcas;
    private Spinner spinnerLineas;
    private Spinner spinnerGramos;
    private Spinner spinnerMaterias;
    private EditText etPrecioProdCompetencia;

    String idCategoria = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_agregar_procuto_compretencia);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        setSupportActionBar(toolbar);

        // SE INICIALIZA EL COMPONENTE PRINCIPAL DE LA APLICACION
        Main.contexto = getApplicationContext();

        TextView tvTituloModulo = findViewById(R.id.tvTituloModulo);
        tvTituloModulo.setTypeface(Const.letraSemibold);
        tvTituloModulo.setText("AGREGAR PRODUCTO");

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

        cargarLineas();
        cargarMarcas();
        cargarGramos();
        cargarMaterias();
        etPrecioProdCompetencia = findViewById(R.id.etPrecioProdCompetencia);
    }

    private void cargarLineas(){

        spinnerLineas = findViewById(R.id.spLinea);
        Vector<Linea> listaLineas  = DataBaseBO.obtenerLineas(idCategoria);

        ArrayAdapter<Linea> adapter = new ArrayAdapter<Linea>(AgregarProcutoCompretenciaActivity.this,android.R.layout.simple_spinner_item,listaLineas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLineas.setAdapter(adapter);
    }

    private void cargarMarcas(){

        spinnerMarcas = findViewById(R.id.spMarcas);
        Vector<Marca> listaMarcas  = DataBaseBO.obtenerMarcas(idCategoria);

        ArrayAdapter<Marca> adapter = new ArrayAdapter<Marca>(AgregarProcutoCompretenciaActivity.this,android.R.layout.simple_spinner_item,listaMarcas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMarcas.setAdapter(adapter);
    }
    private void cargarGramos(){

        spinnerGramos = findViewById(R.id.spGramos);
        Vector<Gramo> listaGramos  = DataBaseBO.obtenerGramos(idCategoria);

        ArrayAdapter<Gramo> adapter = new ArrayAdapter<Gramo>(AgregarProcutoCompretenciaActivity.this,android.R.layout.simple_spinner_item,listaGramos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGramos.setAdapter(adapter);
    }
    private void cargarMaterias(){

        spinnerMaterias = findViewById(R.id.spMaterias);
        Vector<Materia> listaMaterias = DataBaseBO.obtenerMaterias(idCategoria);

        ArrayAdapter<Materia> adapter = new ArrayAdapter<Materia>(AgregarProcutoCompretenciaActivity.this,android.R.layout.simple_spinner_item,listaMaterias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMaterias.setAdapter(adapter);
    }

    public void on_ClickCancelarProductoComp(View view) {
        finish();
    }

    public void on_ClickGuardarProductoComp(View view) {

        int indexLinea =  spinnerLineas.getSelectedItemPosition();
        Linea linea = (Linea) spinnerLineas.getAdapter().getItem(indexLinea);

        int indexMarca =  spinnerMarcas.getSelectedItemPosition();
        Marca marca = (Marca) spinnerMarcas.getAdapter().getItem(indexMarca);

        int indexGramo =  spinnerGramos.getSelectedItemPosition();
        Gramo gramo = (Gramo) spinnerGramos.getAdapter().getItem(indexGramo);

        int indexMateria =  spinnerMaterias.getSelectedItemPosition();
        Materia materia = (Materia) spinnerMaterias.getAdapter().getItem(indexMateria);

        String precio = "";
        precio = etPrecioProdCompetencia.getText().toString();

        String codigoCliente = PreferencesCliente.obtenerCodigoClienteSeleccionado(getApplicationContext());
        Usuario usuario = Main.usuario;
        int tipoUsuario = DataBaseBO.obtenerTipoUsuario();
        String id = Util.obtenerId(Main.usuario.codigo);
        String fecha = Util.obtenerFechaActual();

        String codigoProducto= linea.codigo+""+materia.codigo+""+marca.codigo+""+gramo.codigo;
        String nombreProducto= linea.nombre+" - "+materia.nombre+" - "+marca.nombre+" - "+gramo.nombre;
        int precioEntero= !precio.equals("") ? Integer.parseInt(precio):0;

        boolean guardoProd = DataBaseBO.guardarProductoCompetencia(codigoProducto, nombreProducto,fecha);
        boolean guardoProducto = DataBaseBO.guardarProductosPrecioCompetencia(codigoCliente, usuario, tipoUsuario, id, fecha, codigoProducto, nombreProducto,precioEntero);

        if (guardoProducto && guardoProd){

            Producto productoSel = new Producto();
            productoSel.codigo = codigoProducto;
            productoSel.nombre = nombreProducto;
            productoSel.precioCliente = precioEntero;


            // SE PASA EL OBJETO A JSON
            Gson gsObject = new Gson();
            String stringObjet = gsObject.toJson(productoSel);

            // SE ENVIA LA RESPUESTA CON EL OBJERO EN JSON STRING
            Intent respuestaObjetoSel = new Intent();
            respuestaObjetoSel.putExtra("OBJETOSEL", stringObjet);
            setResult(Activity.RESULT_OK, respuestaObjetoSel);
            finish();
        }else{
            //ALERT
            Alert.nutresaShow(AgregarProcutoCompretenciaActivity.this, "INFORMACIÓN", "No se pudo guardar el producto.",
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

    @Override
    public void onBackPressed() {

        AgregarProcutoCompretenciaActivity.this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            AgregarProcutoCompretenciaActivity.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
