package co.com.celuweb.ipv;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class VistaItemListaVendedoresSincronizar extends LinearLayout {

    // IDENTIFICADOR VISTA
    View rootView;

    // COMPONENTES VISTA
    ImageView ivBotonVendedorSincronizar;
    TextView tvNombreVendedor;
    public int position;

    public VistaItemListaVendedoresSincronizar(Context context) {

        super(context);
        init(context);
    }

    private void init(Context context) {

        // SE DEFINE LA INFORMACION QUE SE MUESTRA EN LA VISTA
        rootView = inflate(context, R.layout.list_item_vendedor, this);

        // COMPONENTES DE LA VISTA
        ivBotonVendedorSincronizar = findViewById(R.id.ivBotonVendedorSincronizar);
        tvNombreVendedor  = findViewById(R.id.tvNombreVendedor);
    }
}
