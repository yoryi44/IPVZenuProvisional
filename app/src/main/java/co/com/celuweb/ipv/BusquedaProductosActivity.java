package co.com.celuweb.ipv;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.Vector;

import businessObject.DataBaseBO;
import component.ListViewAdapterListaClientes;
import component.ListViewAdapterListaProductos;
import config.Const;
import dataObject.ItemListViewProductos;
import dataObject.Main;
import dataObject.Producto;
import sharedPreferences.PreferencesCliente;

public class BusquedaProductosActivity extends AppCompatActivity {

    private EditText etOpcionBusquedaProducto;
    private Vector<Producto> listaProductos;
    private ListViewAdapterListaProductos adapter;
    private ListView lvListaProductos;

    private int tipoBusqueda = 0;
    private int tipoProductoComp = 0;
    private boolean mostrarOpcionCompetencia = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_busqueda_productos);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        setSupportActionBar(toolbar);

        // SE INICIALIZA EL COMPONENTE PRINCIPAL DE LA APLICACION
        Main.contexto = getApplicationContext();

        TextView tvTituloModulo = findViewById(R.id.tvTituloModulo);
        tvTituloModulo.setTypeface(Const.letraSemibold);
        tvTituloModulo.setText("BUSCAR PRODUCTOS");

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            if (extras.containsKey("OPCIONACTUALLOGICA"))
                tipoBusqueda = extras.getInt("OPCIONACTUALLOGICA");

            if(extras.containsKey("TIPOPRODUCTOCOMP")) {

                tipoProductoComp = 1;
            }

            if(extras.containsKey("MOSTRAROPCIONCOMPETENCIA")) {

                mostrarOpcionCompetencia = true;
            }
        }

        configuracionBuscadorProductos();
        setListinerListView();

        if(tipoProductoComp == 1 || tipoBusqueda == 10) {

            ImageButton btnAgregarProductoComp = findViewById(R.id.btnAgregarProductoComp);
            btnAgregarProductoComp.setVisibility(View.VISIBLE);
        }
    }

    private void configuracionBuscadorProductos() {

        etOpcionBusquedaProducto = findViewById(R.id.etOpcionBusquedaProducto);
        etOpcionBusquedaProducto.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

                String parametroBusqueda = s.toString();
                buscarClientes(parametroBusqueda);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
        });
    }

    private void buscarClientes(String parametroBusqueda) {

        lvListaProductos = findViewById(R.id.lvListaProductos);
        final Vector<ItemListViewProductos> listaItems = new Vector<>();
        listaProductos = DataBaseBO.listaProductos(listaItems, parametroBusqueda, tipoBusqueda);

        ItemListViewProductos[] items;

        if (listaItems.size() > 0) {

            items = new ItemListViewProductos[listaItems.size()];
            listaItems.copyInto(items);

        } else {

            items = new ItemListViewProductos[] {};

            if (listaProductos != null)
                listaProductos.removeAllElements();
        }

        adapter = new ListViewAdapterListaProductos(BusquedaProductosActivity.this, items);
        adapter.notifyDataSetChanged();
        lvListaProductos.setAdapter(adapter);
    }

    private void setListinerListView() {

        final ListView lvListaProductos = (ListView)findViewById(R.id.lvListaProductos);
        lvListaProductos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Producto productoSel = listaProductos.elementAt(position);

                // SE PASA EL OBJETO A JSON
                Gson gsObject = new Gson();
                String stringObjet = gsObject.toJson(productoSel);

                // SE ENVIA LA RESPUESTA CON EL OBJERO EN JSON STRING
                Intent respuestaObjetoSel = new Intent();
                respuestaObjetoSel.putExtra("OBJETOSEL", stringObjet);
                setResult(Activity.RESULT_OK, respuestaObjetoSel);
                finish();
            }
        });
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

    public void on_ClickAgregarProductoCompNuevo(View view) {

        Intent agregarProducto = new Intent(BusquedaProductosActivity.this, CrearProductoCompetenciaActivity.class);
        startActivityForResult(agregarProducto, Const.AGREGAR_PRODUCTO_COMPETENCIA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Const.AGREGAR_PRODUCTO_COMPETENCIA) {

            if (resultCode == Activity.RESULT_OK) {

                String objetoSeleccionadoAgregar = data.getStringExtra("OBJETOPRODUCTOCOMP");

                // SE ENVIA LA RESPUESTA CON EL OBJERO EN JSON STRING
                Intent respuestaObjetoSel = new Intent();
                respuestaObjetoSel.putExtra("OBJETOSEL", objetoSeleccionadoAgregar);
                setResult(Activity.RESULT_OK, respuestaObjetoSel);
                finish();
            }
        }
    }
}
