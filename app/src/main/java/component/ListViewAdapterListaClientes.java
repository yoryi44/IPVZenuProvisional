package component;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Vector;

import co.com.celuweb.ipv.OpcionesClienteActivity;
import co.com.celuweb.ipv.R;
import config.Const;
import dataObject.Cliente;
import dataObject.ItemListViewClientes;
import dataObject.Main;
import sharedPreferences.PreferencesCliente;

/**
 * Created by Cw Desarrollo on 21/09/2016.
 */
public class ListViewAdapterListaClientes extends RecyclerView.Adapter<ListViewAdapterListaClientes.ViewHolderClientes>{

    public Vector<Cliente>  listaClientes;
    public Context context;
    public int[] colors;

    /**
     * CONSTRUCTOR DE LA CLASE
     * @param context
     * @param listaClientes
     */
    public ListViewAdapterListaClientes(Context context, Vector<Cliente> listaClientes) {

//        super(context, R.layout.list_item_lista_clientes, itemList);
        this.listaClientes = listaClientes;
        this.context = context;
        colors = new int[] { R.color.colorPrimary, R.color.colorPrimaryDark };
    }



//    /**
//     * VISUALIZACION DE LA LISTA
//     * @param position
//     * @param convertView
//     * @param parent
//     * @return
//     */
//    public View getView(final int position, View convertView, ViewGroup parent) {
//
//        View item = convertView;
//        ViewHolder holder;
//
//        if(item == null) {
//
//            LayoutInflater inflater = LayoutInflater.from(getContext());
//            item = inflater.inflate(R.layout.list_item_lista_clientes, null);
//
//            holder = new ViewHolder();
//            holder.lblCodigoNombreListaClientes = (TextView)item.findViewById(R.id.lblCodigoNombreListaClientes);
//            holder.lblDireccionListaClientes = (TextView)item.findViewById(R.id.lblDireccionListaClientes);
//            holder.ivUsuarioListaClientes = (ImageView) item.findViewById(R.id.ivUsuarioListaClientes);
//
//            item.setTag(holder);
//
//        } else{
//
//            holder =(ViewHolder) item.getTag();
//        }
//
//        holder.lblCodigoNombreListaClientes.setText(itemList[position].codigo);
//        holder.lblDireccionListaClientes.setText(itemList[position].direccion);
//
//        if(itemList[position].gestion) {
//
//            holder.ivUsuarioListaClientes.setImageResource(R.mipmap.iconook);
//        }
//
//        return(item);
//    }

    @NonNull
    @Override
    public ViewHolderClientes onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_lista_clientes, parent, false);
        return new ListViewAdapterListaClientes.ViewHolderClientes(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderClientes holder, int position) {

        Cliente item = listaClientes.get(position);
        ((ListViewAdapterListaClientes.ViewHolderClientes) holder).bind(item, position);
    }



    public class ViewHolderClientes extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView lblCodigoNombreListaClientes;
        TextView lblDireccionListaClientes;
        ImageView ivUsuarioListaClientes;
        Cliente item;


        public ViewHolderClientes(View itemView) {
            super(itemView);

            lblCodigoNombreListaClientes = itemView.findViewById(R.id.lblCodigoNombreListaClientes);
            lblDireccionListaClientes = itemView.findViewById(R.id.lblDireccionListaClientes);
            ivUsuarioListaClientes = itemView.findViewById(R.id.ivUsuarioListaClientes);
            itemView.setOnClickListener(this);
        }

        void bind(Cliente item, int position){

            this.item = item;

            lblCodigoNombreListaClientes.setText(item.codigo+" - "+item.nombre+"-"+item.razonSocial);
            lblDireccionListaClientes.setText(item.direccion);

            if(item.tieneGestion) {

                ivUsuarioListaClientes.setImageResource(R.mipmap.iconook);
            }
        }

        @Override
        public void onClick(View v) {

            //EVITAR EVENTO DOBLE CLICK
            v.setEnabled(false);

            String codigoClienteSel = item.codigo;
                PreferencesCliente.guardarCodigoCliente(context.getApplicationContext(), codigoClienteSel);
                Main.cliente = item;

                Intent opcionesClienteActivity = new Intent(context, OpcionesClienteActivity.class);
                context.startActivity(opcionesClienteActivity);
        }


//        @Override
//        public void onClick(View v) {
//
//            getCiudadCallback.getCiudad(ciudad);
//
//        }
    }
//    @Override
//    public ItemListViewClientes getItem(int position) {
//
//        return itemList[position];
//    }

    /**
     * VIEWHOLDER
     */
    static class ViewHolder {

        TextView lblCodigoNombreListaClientes;
        TextView lblDireccionListaClientes;
        ImageView ivUsuarioListaClientes;
    }

    @Override
    public int getItemCount() {
        return listaClientes.size();
    }
}
