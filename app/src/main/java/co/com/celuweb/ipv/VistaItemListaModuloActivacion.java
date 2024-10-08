package co.com.celuweb.ipv;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class VistaItemListaModuloActivacion extends LinearLayout {

    // IDENTIFICADOR VISTA
    View rootView;

    // COMPONENTES VISTA

    TextView tvNombreModulo;
    ImageView ivSi;
    ImageView ivNo;
    ImageView ivCamara;
    public int position;
    boolean si=true;
    boolean no=true;
    LinearLayout llFondoProducto;

    public VistaItemListaModuloActivacion(Context context) {

        super(context);
        init(context);
    }

    private void init(Context context) {

        // SE DEFINE LA INFORMACION QUE SE MUESTRA EN LA VISTA
        rootView = inflate(context, R.layout.list_item_activacion, this);

        // COMPONENTES DE LA VISTA

        tvNombreModulo              = findViewById(R.id.tvNombreModulo);
        ivSi = findViewById(R.id.ivSi);
        ivNo = findViewById(R.id.ivNo);
        ivCamara = findViewById(R.id.ivCamara);

        llFondoProducto               = findViewById(R.id.llFondoProducto);

    }
}
