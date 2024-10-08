package co.com.celuweb.ipv;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class VistaItemListaProductosPropiosPrecio extends LinearLayout {

    // IDENTIFICADOR VISTA
    View rootView;

    // COMPONENTES VISTA
    ImageView ivProductoAgotado;
    TextView tvNombreProducto;
    TextView tvPrecioProducto;
    EditText etCantidadAgotado;
    ImageView ivBotonCopiarCantidadAgotados;
    LinearLayout llBordeCantidad;
    LinearLayout llFondoProducto;

    Button ivNoVenta;
    Button ivAgotado;
    public int position;
    boolean vendido = true;

    public VistaItemListaProductosPropiosPrecio(Context context) {

        super(context);
        init(context);
    }

    private void init(Context context) {

        // SE DEFINE LA INFORMACION QUE SE MUESTRA EN LA VISTA
        rootView = inflate(context, R.layout.list_item_lista_productos_propios, this);

        // COMPONENTES DE LA VISTA
        ivProductoAgotado             = findViewById(R.id.ivProductoAgotado);
        tvNombreProducto              = findViewById(R.id.tvNombreProducto);
        tvPrecioProducto              = findViewById(R.id.tvPrecioProducto);
        etCantidadAgotado             = findViewById(R.id.etCantidadAgotado);
        ivBotonCopiarCantidadAgotados = findViewById(R.id.ivBotonCopiarCantidadAgotados);
        llBordeCantidad               = findViewById(R.id.llBordeCantidad);
        ivNoVenta                     = findViewById(R.id.ivNoVenta);
        ivAgotado                     = findViewById(R.id.ivAgotado);
        llFondoProducto               = findViewById(R.id.llFondoProducto);

        ImageView ivProductoAgotado;
    }
}
