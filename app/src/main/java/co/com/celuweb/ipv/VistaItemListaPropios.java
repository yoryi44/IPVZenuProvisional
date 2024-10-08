package co.com.celuweb.ipv;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class VistaItemListaPropios extends LinearLayout {

    // IDENTIFICADOR VISTA
    View rootView;

    // COMPONENTES VISTA
    ImageView ivProductoAgotado;
    TextView tvNombreProducto;
    EditText etCantidadAgotado;
    TextView tvCalculoPorcentaje;
    public int position;

    public VistaItemListaPropios(Context context) {

        super(context);
        init(context);
    }

    private void init(Context context) {

        // SE DEFINE LA INFORMACION QUE SE MUESTRA EN LA VISTA
        rootView = inflate(context, R.layout.list_item_lista_productos_propios_exhibidor, this);

        // COMPONENTES DE LA VISTA
        ivProductoAgotado   = findViewById(R.id.ivProductoAgotado);
        tvNombreProducto    = findViewById(R.id.tvNombreProducto);
        etCantidadAgotado   = findViewById(R.id.etCantidadAgotado);
        tvCalculoPorcentaje = findViewById(R.id.tvCalculoPorcentaje);
    }
}
