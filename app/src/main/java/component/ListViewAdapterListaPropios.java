package component;

import android.content.Context;
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
public class ListViewAdapterListaPropios extends ArrayAdapter<ItemListViewProductosPropios> {

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
    public ListViewAdapterListaPropios(Context context, ItemListViewProductosPropios[] itemList, Vector<Producto> listaProductosPropios) {

        super(context, R.layout.list_item_lista_productos_propios, itemList);
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
            item = inflater.inflate(R.layout.list_item_lista_productos_propios, null);

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

            if(itemList[position].cantidadAct >= 0) {

                holder.etCantidadAgotado.setText(String.valueOf(itemList[position].cantidadAct));
            }

        } else {

            holder.etCantidadAgotado.setText("");

            // NO ESTA MODIFICADO Y NO TIENE VALOR
            if(itemList[position].cantidadAnt > 0) {

                String valorHint = String.valueOf(itemList[position].cantidadAnt);
                holder.etCantidadAgotado.setHint(valorHint);
            }
        }

        holder.ivBotonCopiarCantidadAgotados.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if ((holder.etCantidadAgotado.getHint()) != null) {

                    String valorHint = String.valueOf(holder.etCantidadAgotado.getHint());
                    holder.etCantidadAgotado.setText(valorHint);

                    if(!valorHint.equals("")) {

                        itemList[holder.position].esModificado = true;
                        itemList[holder.position].cantidadAct = Util.toInt(valorHint);

                        listaProductosPropios.elementAt(holder.position).cantidadAct = Util.toInt(valorHint);
                        listaProductosPropios.elementAt(holder.position).esModificado = true;

                        int valorHintEntero = Util.toInt(valorHint);

                        if(valorHintEntero >= 0 && valorHintEntero < 1000) {

                            // SE HACE EL CAMBIO DE ICONO Y EL COLOR DEL BORDE DEL EDITTEXT
                            holder.ivBotonCopiarCantidadAgotados.setImageResource(R.mipmap.iconook_naranja);
                            holder.llBordeCantidad.setBackground(context.getResources().getDrawable(R.drawable.edittext_rounded_orange));

                        } else {

                            // SE HACE EL CAMBIO DE ICONO Y EL COLOR DEL BORDE DEL EDITTEXT
                            holder.ivBotonCopiarCantidadAgotados.setImageResource(R.mipmap.iconook);
                            holder.llBordeCantidad.setBackground(context.getResources().getDrawable(R.drawable.edittext_rounded_green));
                        }
                    }
                }
            }
        });

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

                    int valorIngresado = Util.toInt(String.valueOf(charSequence));

                    if(valorIngresado >= 0 && valorIngresado < 1000) {

                        // SE HACE EL CAMBIO DE ICONO Y EL COLOR DEL BORDE DEL EDITTEXT
                        holder.ivBotonCopiarCantidadAgotados.setImageResource(R.mipmap.iconook_naranja);
                        holder.llBordeCantidad.setBackground(context.getResources().getDrawable(R.drawable.edittext_rounded_orange));

                    } else {

                        // SE HACE EL CAMBIO DE ICONO Y EL COLOR DEL BORDE DEL EDITTEXT
                        holder.ivBotonCopiarCantidadAgotados.setImageResource(R.mipmap.iconook);
                        holder.llBordeCantidad.setBackground(context.getResources().getDrawable(R.drawable.edittext_rounded_green));
                    }

                } else {

                    listaProductosPropios.elementAt(holder.position).cantidadAct = -1;
                    listaProductosPropios.elementAt(holder.position).esModificado = false;

                    itemList[holder.position].esModificado = false;
                    itemList[holder.position].cantidadAct = -1;

                    // SE HACE EL CAMBIO DE ICONO Y EL COLOR DEL BORDE DEL EDITTEXT
                    holder.ivBotonCopiarCantidadAgotados.setImageResource(R.mipmap.iconook_gris);
                    holder.llBordeCantidad.setBackground(context.getResources().getDrawable(R.drawable.edittext_rounded));
                }
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
        ImageView ivBotonCopiarCantidadAgotados;
        LinearLayout llBordeCantidad;
        int position;
    }
}
