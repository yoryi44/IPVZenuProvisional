package component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import co.com.celuweb.ipv.R;
import dataObject.ItemListViewActividadesCliente;

public class ListViewAdapterListaCategoriasCliente extends ArrayAdapter<ItemListViewActividadesCliente> {

    public ItemListViewActividadesCliente[] itemList;
    public Context context;
    public int[] colors;

    /**
     * CONSTRUCTOR DE LA CLASE
     * @param context
     * @param itemList
     */
    public ListViewAdapterListaCategoriasCliente(Context context, ItemListViewActividadesCliente[] itemList) {

        super(context, R.layout.list_item_lista_actividades_cliente, itemList);
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
        ListViewAdapterListaActividadesCliente.ViewHolder holder;

        if(item == null) {

            LayoutInflater inflater = LayoutInflater.from(getContext());
            item = inflater.inflate(R.layout.list_item_lista_actividades_cliente, null);

            holder = new ListViewAdapterListaActividadesCliente.ViewHolder();

            holder.ivIconoEstadoActividad = item.findViewById(R.id.ivIconoEstadoActividad);
            holder.lblDuracionActividad   = item.findViewById(R.id.lblDuracionActividad);
            holder.lblNombreActividad     = item.findViewById(R.id.lblNombreActividad);
            holder.ivIconoActividad       = item.findViewById(R.id.ivIconoActividad);

            item.setTag(holder);

        } else{

            holder =(ListViewAdapterListaActividadesCliente.ViewHolder) item.getTag();
        }

        holder.lblNombreActividad.setText(itemList[position].nombreTarea);
//        holder.lblDuracionActividad.setText(itemList[position].tiempoTarea);


        holder.ivIconoActividad.setImageResource(R.mipmap.iconohojavida);



//        if(itemList[position].tieneGestion == 0) {
//
//            holder.ivIconoEstadoActividad.setImageResource(R.mipmap.iconook);
//
//        } else if(itemList[position].tieneGestion == 1) {

            holder.ivIconoEstadoActividad.setImageResource(R.mipmap.iconook_gris);

//        } else {
//
//            holder.ivIconoEstadoActividad.setImageResource(R.mipmap.iconook_naranja);
//        }

        return(item);
    }

    @Override
    public ItemListViewActividadesCliente getItem(int position) {

        return itemList[position];
    }

    /**
     * VIEWHOLDER
     */
    static class ViewHolder {

        ImageView ivIconoEstadoActividad;
        TextView lblDuracionActividad;
        TextView  lblNombreActividad;
        ImageView ivIconoActividad;
    }
}
