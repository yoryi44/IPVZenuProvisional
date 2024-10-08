package co.com.celuweb.ipv;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class VistaItemListaProductosCompetenciaPrecio extends LinearLayout {

    // IDENTIFICADOR VISTA
    View rootView;

    // COMPONENTES VISTA
    ImageView ivProductoAgotado;
    TextView tvNombreProducto;
    EditText etCantidadAgotado;
    ImageView ivBotonCopiarCantidadAgotados;
    LinearLayout llBordeCantidad;
    public int position;
    LinearLayout llFondoProducto;

    public VistaItemListaProductosCompetenciaPrecio(Context context) {

        super(context);
        init(context);
    }

    private void init(Context context) {

        // SE DEFINE LA INFORMACION QUE SE MUESTRA EN LA VISTA
        rootView = inflate(context, R.layout.list_item_lista_productos_competencia, this);

        // COMPONENTES DE LA VISTA
        ivProductoAgotado             = findViewById(R.id.ivProductoAgotado);
        tvNombreProducto              = findViewById(R.id.tvNombreProducto);
        etCantidadAgotado             = findViewById(R.id.etCantidadAgotado);
        ivBotonCopiarCantidadAgotados = findViewById(R.id.ivBotonCopiarCantidadAgotados);
        llBordeCantidad               = findViewById(R.id.llBordeCantidad);
        llFondoProducto               = findViewById(R.id.llFondoProducto);

        llFondoProducto.setLongClickable(true);
    }
}
