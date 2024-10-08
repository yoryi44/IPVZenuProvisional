package component;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Vector;

import co.com.celuweb.ipv.R;
import config.Const;
import dataObject.Exhibidores;
import dataObject.ItemListViewExhibidores;

/**
 * Created by Cw Desarrollo on 21/09/2016.
 */
public class ListViewAdapterListaExhibidores extends ArrayAdapter<ItemListViewExhibidores> {

    public ItemListViewExhibidores[] itemList;
    public Vector<Exhibidores> listaExhibidores;
    public boolean recarga;
    public Context context;
    public int[] colors;

    /**
     * CONSTRUCTOR DE LA CLASE
     * @param context
     * @param itemList
     * @param listaExhibidores
     * @param recarga
     */
    public ListViewAdapterListaExhibidores(Context context, ItemListViewExhibidores[] itemList, Vector<Exhibidores> listaExhibidores, boolean recarga) {

        super(context, R.layout.list_item_lista_exhibidores, itemList);
        this.itemList = itemList;
        this.context = context;
        this.recarga = recarga;
        this.listaExhibidores = listaExhibidores;
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
            item = inflater.inflate(R.layout.list_item_lista_exhibidores, null);

            holder = new ViewHolder();

            holder.tvNombreExhibidor                  = (TextView) item.findViewById(R.id.tvNombreExhibidor);
            holder.ivBotonExhibicionMedida            = (ImageView) item.findViewById(R.id.ivBotonExhibicionMedida);
            holder.ivBotonModificarExhibicion         = (ImageView) item.findViewById(R.id.ivBotonModificarExhibicion);
            holder.ivBotonEliminarExhibicion          = (ImageView) item.findViewById(R.id.ivBotonEliminarExhibicion);

            holder.llComponenteEstadoExhibicion       = (LinearLayout) item.findViewById(R.id.llComponenteEstadoExhibicion);
            holder.llComponenteModificacionExhibicion = (LinearLayout) item.findViewById(R.id.llComponenteModificacionExhibicion);

            holder.position                           = position;

            item.setTag(holder);

        } else {

            item = convertView;
            holder = (ViewHolder) item.getTag();

            if(holder.position != position) {

                holder.position = position;
            }
        }

        holder.tvNombreExhibidor.setText(itemList[position].nombre);
        holder.tvNombreExhibidor.setTypeface(Const.letraRegular);

        if(recarga) {

            // SE MUESTRAN LAS OPCIONES DE EDICION
            holder.llComponenteModificacionExhibicion.setVisibility(View.VISIBLE);

            // SE OCULTA LA OPCION DE ESTADO
            holder.llComponenteEstadoExhibicion.setVisibility(View.GONE);

        } else {

            // SE MUESTRAN LAS OPCIONES DE EDICION
            holder.llComponenteModificacionExhibicion.setVisibility(View.GONE);

            // SE OCULTA LA OPCION DE ESTADO
            holder.llComponenteEstadoExhibicion.setVisibility(View.VISIBLE);
        }

        // MODIFICA EXHIBIDOR
        holder.ivBotonModificarExhibicion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("MODIFICAREXHIBIDOR");
                broadcastIntent.putExtra("POSEXHIBIDOR", "" + holder.position);
                broadcastIntent.putExtra("IDEXHIBIDOR", "" + itemList[position].id);
                context.sendBroadcast(broadcastIntent);
            }
        });

        // ELIMINAR EXHIBIDOR
        holder.ivBotonEliminarExhibicion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("ELIMINAREXHIBIDOR");
                broadcastIntent.putExtra("POSEXHIBIDOR", "" + position);
                broadcastIntent.putExtra("IDEXHIBIDOR", "" + itemList[position].id);
                context.sendBroadcast(broadcastIntent);
            }
        });

        if(itemList[position].estaGestionado) {

            holder.ivBotonExhibicionMedida.setImageResource(R.mipmap.iconook);

        } else  {

            holder.ivBotonExhibicionMedida.setImageResource(R.mipmap.iconook_gris);
        }

        return(item);
    }

    @Override
    public ItemListViewExhibidores getItem(int position) {

        return itemList[position];
    }

    /**
     * VIEWHOLDER
     */
    static class ViewHolder {

        TextView tvNombreExhibidor;
        ImageView ivBotonExhibicionMedida;
        ImageView ivBotonModificarExhibicion;
        ImageView ivBotonEliminarExhibicion;

        LinearLayout llComponenteEstadoExhibicion;
        LinearLayout llComponenteModificacionExhibicion;

        int position;
    }
}
