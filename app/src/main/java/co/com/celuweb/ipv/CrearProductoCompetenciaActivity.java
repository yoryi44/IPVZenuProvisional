package co.com.celuweb.ipv;

import android.app.Activity;
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
import android.widget.TextView;

import com.google.gson.Gson;

import component.Alert;
import component.Util;
import config.Const;
import dataObject.Main;
import dataObject.Producto;

public class CrearProductoCompetenciaActivity extends AppCompatActivity {

    private TextView tvNombreProdutoCompetencia;
    private EditText etNombreProdutoCompetencia;

    private TextView tvNumeroCarasProdutoCompetencia;
    private EditText etNumeroCarasProdutoCompetencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_crear_producto_competencia);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        setSupportActionBar(toolbar);

        // SE INICIALIZA EL COMPONENTE PRINCIPAL DE LA APLICACION
        Main.contexto = getApplicationContext();

        TextView tvTituloModulo = findViewById(R.id.tvTituloModulo);
        tvTituloModulo.setTypeface(Const.letraSemibold);
        tvTituloModulo.setText("CREAR PRODUCTO COMPETENCIA");

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        setComponentesVista();
    }

    private void setComponentesVista() {

        tvNombreProdutoCompetencia = findViewById(R.id.tvNombreProdutoCompetencia);
        tvNombreProdutoCompetencia.setTypeface(Const.letraSemibold);

        etNombreProdutoCompetencia = findViewById(R.id.etNombreProdutoCompetencia);
        etNombreProdutoCompetencia.setTypeface(Const.letraRegular);

        /**
        tvNumeroCarasProdutoCompetencia = findViewById(R.id.tvNumeroCarasProdutoCompetencia);
        tvNumeroCarasProdutoCompetencia.setTypeface(Const.letraSemibold);

        etNumeroCarasProdutoCompetencia = findViewById(R.id.etNumeroCarasProdutoCompetencia);
        etNumeroCarasProdutoCompetencia.setTypeface(Const.letraRegular);
         **/
    }

    public void on_ClickAgregarProductoCompetencia(View view) {

        // SE VALIDA LA INFORMACION DEL PRODUCTO CREADO
        etNombreProdutoCompetencia = findViewById(R.id.etNombreProdutoCompetencia);
        String nombreProducto = etNombreProdutoCompetencia.getText().toString();

        /**
        etNumeroCarasProdutoCompetencia = findViewById(R.id.etNumeroCarasProdutoCompetencia);
        String cantidadCaras = etNumeroCarasProdutoCompetencia.getText().toString();
         **/

        if(nombreProducto.equals("")) { //  || cantidadCaras.equals("")

            //ALERT
            Alert.nutresaShow(CrearProductoCompetenciaActivity.this, "INFORMACIÓN", "Falta información para la creación del producto.",
                    "OK",
                    null,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Alert.dialogo.cancel();
                        }

                    }, null);

        } else {

            Producto productoCompetenciaCreado = new Producto();
            productoCompetenciaCreado.codigo = Util.obtenerIdProducto();
            productoCompetenciaCreado.cantidadAct = 0; // Util.toInt(cantidadCaras)
            productoCompetenciaCreado.cantidadAnt = 0;
            productoCompetenciaCreado.esModificado = true;
            productoCompetenciaCreado.nombre = nombreProducto;

            // SE PASA EL OBJETO A JSON
            Gson gsObject = new Gson();
            String stringObjet = gsObject.toJson(productoCompetenciaCreado);

            // SE ENVIA LA RESPUESTA CON EL OBJERO EN JSON STRING
            Intent respuestaObjetoSel = new Intent();
            respuestaObjetoSel.putExtra("OBJETOPRODUCTOCOMP", stringObjet);
            setResult(Activity.RESULT_OK, respuestaObjetoSel);
            finish();
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
}



