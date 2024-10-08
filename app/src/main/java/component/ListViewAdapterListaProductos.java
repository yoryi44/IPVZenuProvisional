package component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import co.com.celuweb.ipv.R;
import config.Const;
import dataObject.ItemListViewAgotados;
import dataObject.ItemListViewProductos;

/**
 * Created by Cw Desarrollo on 21/09/2016.
 */
public class ListViewAdapterListaProductos extends ArrayAdapter<ItemListViewProductos> {

    public ItemListViewProductos[] itemList;
    public Context context;
    public int[] colors;

    /**
     * CONSTRUCTOR DE LA CLASE
     * @param context
     * @param itemList
     */
    public ListViewAdapterListaProductos(Context context, ItemListViewProductos[] itemList) {

        super(context, R.layout.list_item_lista_productos, itemList);
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
        final ViewHolder holder;

        if(item == null) {

            LayoutInflater inflater = LayoutInflater.from(getContext());
            item = inflater.inflate(R.layout.list_item_lista_productos, null);

            holder = new ViewHolder();

            holder.ivProductoAgotado = (ImageView) item.findViewById(R.id.ivProductoAgotado);
            holder.tvNombreProducto  = (TextView) item.findViewById(R.id.tvNombreProducto);

            item.setTag(holder);

        } else {

            holder = (ViewHolder) item.getTag();
        }

        holder.tvNombreProducto.setText(itemList[position].nombre);
        holder.tvNombreProducto.setTypeface(Const.letraRegular);

        return(item);
    }

    @Override
    public ItemListViewProductos getItem(int position) {

        return itemList[position];
    }

    /**
     * VIEWHOLDER
     */
    static class ViewHolder {

        ImageView ivProductoAgotado;
        TextView tvNombreProducto;
    }
}
