package component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import co.com.celuweb.ipv.R;
import dataObject.ItemListViewComponenteActivacion;
import dataObject.ItemListViewMedicionActivacion;

/**
 * Created by Cw Desarrollo on 21/09/2016.
 */
public class ListViewAdapterListaActivacionTerminada extends ArrayAdapter<ItemListViewMedicionActivacion> {

    public ItemListViewMedicionActivacion[] itemList;
    public Context context;
    public int[] colors;

    /**
     * CONSTRUCTOR DE LA CLASE
     * @param context
     * @param itemList
     */
    public ListViewAdapterListaActivacionTerminada(Context context, ItemListViewMedicionActivacion[] itemList) {

        super(context, R.layout.list_item_lista_activacion_terminada, itemList);
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        View item = convertView;
        ViewHolder holder;

        if(item == null) {

            LayoutInflater inflater = LayoutInflater.from(getContext());
            item = inflater.inflate(R.layout.list_item_lista_activacion_terminada, null);

            holder = new ViewHolder();

            holder.ivProductoComponente       = item.findViewById(R.id.ivProductoComponente);
            holder.tvNombreComponente         = item.findViewById(R.id.tvNombreComponente);
            holder.tvNombreProductoComponente = item.findViewById(R.id.tvNombreProductoComponente);
            holder.ivBotonComponente          = item.findViewById(R.id.ivBotonComponente);
            holder.position                   = position;

            item.setTag(holder);

        } else{

            item = convertView;
            holder = (ViewHolder) item.getTag();

            if(holder.position != position) {

                holder.position = position;
            }
        }

        holder.tvNombreComponente.setText(itemList[position].descripcion);
        holder.tvNombreProductoComponente.setText(itemList[position].nombreProducto);

        if(itemList[position].estaSeleccionado == 0) {

            holder.ivBotonComponente.setImageResource(R.mipmap.iconook_gris);

        } else {

            holder.ivBotonComponente.setImageResource(R.mipmap.iconook);
        }

        return(item);
    }

    @Override
    public ItemListViewMedicionActivacion getItem(int position) {

        return itemList[position];
    }

    /**
     * VIEWHOLDER
     */
    static class ViewHolder {

        ImageView ivProductoComponente;
        TextView tvNombreComponente;
        TextView tvNombreProductoComponente;
        ImageView ivBotonComponente;
        int position;
    }
}
