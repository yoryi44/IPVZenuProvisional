package component;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import co.com.celuweb.ipv.AgotadosActivity;
import co.com.celuweb.ipv.R;
import config.Const;
import dataObject.ItemListViewClientes;
import dataObject.ItemListViewComponenteActivacion;
import sharedPreferences.PreferencesOpcionSelOtra;

/**
 * Created by Cw Desarrollo on 21/09/2016.
 */
public class ListViewAdapterListaComponentesActivacion extends ArrayAdapter<ItemListViewComponenteActivacion> {

    public ItemListViewComponenteActivacion[] itemList;
    public Context context;
    public int[] colors;

    /**
     * CONSTRUCTOR DE LA CLASE
     * @param context
     * @param itemList
     */
    public ListViewAdapterListaComponentesActivacion(Context context, ItemListViewComponenteActivacion[] itemList) {

        super(context, R.layout.list_item_lista_componente_activacion, itemList);
        this.itemList = itemList;
        this.context = context;
        colors = new int[] { R.color.colorPrimary, R.color.colorPrimaryDark };
    }

    /**
     * VISUALIZACION DE LA LISTA
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public View getView(final int position, final View convertView, ViewGroup parent) {

        View item = convertView;
        ViewHolder holder;

        if(item == null) {

            LayoutInflater inflater = LayoutInflater.from(getContext());
            item = inflater.inflate(R.layout.list_item_lista_componente_activacion, null);

            holder = new ViewHolder();

            holder.ivProductoComponente = item.findViewById(R.id.ivProductoComponente);
            holder.tvNombreComponente   = item.findViewById(R.id.tvNombreComponente);
            holder.ivBotonComponente    = item.findViewById(R.id.ivBotonComponente);
            holder.llBotonComentario    = item.findViewById(R.id.llBotonComentario);
            holder.position             = position;

            item.setTag(holder);

        } else{

            item = convertView;
            holder = (ViewHolder) item.getTag();

            if(holder.position != position) {

                holder.position = position;
            }
        }

        holder.tvNombreComponente.setText(itemList[position].descripcion);

        if(itemList[position].seleccionado == 0) {

            holder.ivBotonComponente.setImageResource(R.mipmap.iconook_gris);

        } else {

            holder.ivBotonComponente.setImageResource(R.mipmap.iconook);
        }

        if((itemList[position].descripcion).equals("Otra") && itemList[position].seleccionado == 1) {

            holder.llBotonComentario.setVisibility(View.VISIBLE);

        } else {

            holder.llBotonComentario.setVisibility(View.GONE);
        }

        holder.llBotonComentario.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // SE MUESTRA EL DIALOGO PARA AGREGAR UNA OBSERVACION A LA OPCION OTRO O OTRA
                final Dialog dialogo = new Dialog(context);
                dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogo.setContentView(R.layout.observacionapp);

                TextView tvTitle = (TextView) dialogo.findViewById(R.id.tvTitle);
                tvTitle.setText("INFORMACIÓN");
                tvTitle.setTypeface(Const.letraSemibold);

                final EditText etObservacion = dialogo.findViewById(R.id.etObservacion);
                etObservacion.setTypeface(Const.letraSemibold);

                Button btnSi = (Button) dialogo.findViewById(R.id.btnSi);
                btnSi.setText("AGREGAR");
                btnSi.setTypeface(Const.letraRegular);

                Button btnNo = (Button) dialogo.findViewById(R.id.btnNo);
                btnNo.setTypeface(Const.letraRegular);
                btnNo.setText("CANCELAR");

                ((Button) dialogo.findViewById(R.id.btnSi)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String observacion = etObservacion.getText().toString();

                        if(observacion.length() > 0) {

                            PreferencesOpcionSelOtra.guardarObservacion(context, observacion);
                            dialogo.cancel();
                        }
                    }
                });
                ((Button) dialogo.findViewById(R.id.btnNo)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogo.cancel();
                    }
                });

                dialogo.setCancelable(true);
                dialogo.show();
            }
        });

        return(item);
    }

    @Override
    public ItemListViewComponenteActivacion getItem(int position) {

        return itemList[position];
    }

    /**
     * VIEWHOLDER
     */
    static class ViewHolder {

        ImageView ivProductoComponente;
        TextView tvNombreComponente;
        ImageView ivBotonComponente;
        LinearLayout llBotonComentario;
        int position;
    }
}
