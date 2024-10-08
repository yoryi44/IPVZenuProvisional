package component;

import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Vector;

import co.com.celuweb.ipv.R;
import config.Const;
import dataObject.ItemListViewAgotados;
import dataObject.ProductoAgotado;

/**
 * Created by Cw Desarrollo on 21/09/2016.
 */
public class ListViewAdapterListaAgotados extends ArrayAdapter<ItemListViewAgotados> {

    public ItemListViewAgotados[] itemList;
    public Vector<ProductoAgotado> listaProductosAgotados;
    public Context context;
    public int[] colors;

    /**
     * CONSTRUCTOR DE LA CLASE
     * @param context
     * @param itemList
     * @param listaProductosAgotados
     */
    public ListViewAdapterListaAgotados(Context context, ItemListViewAgotados[] itemList, Vector<ProductoAgotado> listaProductosAgotados) {

        super(context, R.layout.list_item_lista_agotados, itemList);
        this.itemList = itemList;
        this.context = context;
        this.listaProductosAgotados = listaProductosAgotados;
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
            item = inflater.inflate(R.layout.list_item_lista_agotados, null);

            holder = new ViewHolder();

            holder.ivProductoAgotado             = item.findViewById(R.id.ivProductoAgotado);
            holder.tvNombreProducto              = item.findViewById(R.id.tvNombreProducto);
            holder.etCantidadAgotado             = item.findViewById(R.id.etCantidadAgotado);
            holder.ivBotonCopiarCantidadAgotados = item.findViewById(R.id.ivBotonCopiarCantidadAgotados);
            holder.llBordeCantidad               = item.findViewById(R.id.llBordeCantidad);
            holder.position                      = position;

            item.setTag(holder);

        } else {

            item = convertView;
            holder = (ViewHolder) item.getTag();

            if(holder.position != position) {

                holder.position = position;
                holder.etCantidadAgotado = item.findViewById(R.id.etCantidadAgotado);
            }
        }

        holder.tvNombreProducto.setText(itemList[position].nombre);
        holder.tvNombreProducto.setTypeface(Const.letraRegular);

        if(itemList[position].esModificado == true) {

            holder.ivBotonCopiarCantidadAgotados.setImageResource(R.mipmap.iconook);
        }

        holder.ivBotonCopiarCantidadAgotados.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(itemList[holder.position].esModificado == false) {

                    itemList[holder.position].esModificado = true;
                    listaProductosAgotados.elementAt(holder.position).cantidadAct = 1;
                    holder.ivBotonCopiarCantidadAgotados.setImageResource(R.mipmap.iconook);

                } else {

                    itemList[holder.position].esModificado = false;
                    listaProductosAgotados.elementAt(holder.position).cantidadAct = 0;
                    holder.ivBotonCopiarCantidadAgotados.setImageResource(R.mipmap.iconook_gris);
                }
            }
        });

        return(item);
    }

    @Override
    public ItemListViewAgotados getItem(int position) {

        return itemList[position];
    }

    /**
     * VIEWHOLDER
     */
    static class ViewHolder {

        ImageView ivProductoAgotado;
        TextView tvNombreProducto;
        EditText etCantidadAgotado;
        ImageView ivBotonCopiarCantidadAgotados;
        LinearLayout llBordeCantidad;
        int position;
    }
}
