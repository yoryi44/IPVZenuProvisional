package co.com.celuweb.ipv;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class VistaItemListaProductosAgotados extends LinearLayout {

    // IDENTIFICADOR VISTA
    View rootView;

    // COMPONENTES VISTA
    ImageView ivProductoAgotado;
    TextView tvNombreProducto;
    ImageView ivBotonCopiarCantidadAgotados;
    LinearLayout llFondoProducto;
    boolean vendido = true;
    public int position;

    public VistaItemListaProductosAgotados(Context context) {

        super(context);
        init(context);
    }

    private void init(Context context) {

        // SE DEFINE LA INFORMACION QUE SE MUESTRA EN LA VISTA
        rootView = inflate(context, R.layout.list_item_lista_agotados, this);

        // COMPONENTES DE LA VISTA

        ivProductoAgotado             = findViewById(R.id.ivProductoAgotado);
        tvNombreProducto              = findViewById(R.id.tvNombreProducto);
        ivBotonCopiarCantidadAgotados = findViewById(R.id.ivBotonCopiarCantidadAgotados);
        llFondoProducto               = findViewById(R.id.llFondoProducto);
    }
}
