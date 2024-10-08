package component;

import android.content.Context;
import android.content.Intent;
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
import dataObject.ItemListViewProductosPropios;
import dataObject.Producto;

/**
 * Created by Cw Desarrollo on 21/09/2016.
 */
public class ListViewAdapterListaPropiosExhibidor extends ArrayAdapter<ItemListViewProductosPropios> {

    public ItemListViewProductosPropios[] itemList;
    public Vector<Producto> listaProductosPropios;
    public Context context;
    public int[] colors;

    /**
     * CONSTRUCTOR DE LA CLASE
     * @param context
     * @param itemList
     * @param listaProductosPropios
     */
    public ListViewAdapterListaPropiosExhibidor(Context context, ItemListViewProductosPropios[] itemList, Vector<Producto> listaProductosPropios) {

        super(context, R.layout.list_item_lista_productos_propios_exhibidor, itemList);
        this.itemList = itemList;
        this.context = context;
        this.listaProductosPropios = listaProductosPropios;
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
            item = inflater.inflate(R.layout.list_item_lista_productos_propios_exhibidor, null);

            holder = new ViewHolder();

            holder.ivProductoAgotado   = (ImageView) item.findViewById(R.id.ivProductoAgotado);
            holder.tvNombreProducto    = (TextView) item.findViewById(R.id.tvNombreProducto);
            holder.etCantidadAgotado   = (EditText) item.findViewById(R.id.etCantidadAgotado);
            holder.tvCalculoPorcentaje = (TextView) item.findViewById(R.id.tvCalculoPorcentaje);
            holder.llBordeCantidad     = (LinearLayout) item.findViewById(R.id.llBordeCantidad);
            holder.cantidadCaras       = 0;
            holder.position            = position;

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

        // 1. SE DETERMINA SI EL PRODUCTO ESTA MODIFICADO
        if(itemList[position].esModificado == true) {

            // PRIMA EL VALOR QUE TENGA EL CAMPO "cantidadAct"
            if(itemList[position].cantidadAct > 0) {

                holder.etCantidadAgotado.setText(String.valueOf(itemList[position].cantidadAct));

            } else {

                holder.etCantidadAgotado.setText(String.valueOf(itemList[position].cantidadAnt));
            }

        } else {

            holder.etCantidadAgotado.setText(String.valueOf(itemList[position].cantidadAnt));
        }

        holder.etCantidadAgotado.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(charSequence.length() > 0) {

                    listaProductosPropios.elementAt(holder.position).cantidadAct = Util.toInt(String.valueOf(charSequence));
                    listaProductosPropios.elementAt(holder.position).esModificado = true;

                    itemList[holder.position].esModificado = true;
                    itemList[holder.position].cantidadAct = Util.toInt(String.valueOf(charSequence));

                } else {

                    listaProductosPropios.elementAt(holder.position).cantidadAct = -1;
                    listaProductosPropios.elementAt(holder.position).esModificado = false;

                    itemList[holder.position].esModificado = false;
                    itemList[holder.position].cantidadAct = -1;
                }

                // SE ENVIA EL BROADCAST LUEGO DEL CAMBIO EN EL ITEM PARA REALIZAR RECALCULO DE DATOS
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("CARGARCALCULOSVISTA");
                context.sendBroadcast(broadcastIntent);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        return(item);
    }

    @Override
    public ItemListViewProductosPropios getItem(int position) {

        return itemList[position];
    }

    /**
     * VIEWHOLDER
     */
    static class ViewHolder {

        ImageView ivProductoAgotado;
        TextView tvNombreProducto;
        EditText etCantidadAgotado;
        TextView tvCalculoPorcentaje;
        LinearLayout llBordeCantidad;
        int cantidadCaras;
        int position;
    }
}
