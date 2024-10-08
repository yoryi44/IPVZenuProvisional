package dataObject;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.util.Vector;

/**
 * Created by Cw Desarrollo on 21/09/2016.
 */
public class Main {

    public static Context contexto;
    public static int cantVendedores = 0;
    public static Usuario usuario = null;
    public static Vector<ItemListView> listaVendedores = new Vector<ItemListView>();
    public static Cliente cliente;

    public static Drawable fotoActual;
    public static boolean guardarFoto = false;

    public static Vector<Foto> listaInfoFotos = new Vector<Foto>();
    public static Vector<Drawable> fotosGaleria = new Vector<Drawable>();
    public static Vector<ModuloActivacion> listaExhibidores = new Vector<ModuloActivacion>();
    public static Vector<ModuloActivacion> listaExhibidoresCompetencia = new Vector<ModuloActivacion>();
    public static Marca marca=null;
    public static int totalCore = 0;
    public static String idCategoria = "";

    /**
    public static Vector<ItemListViewClientes> listaClientes = new Vector<ItemListViewClientes>();
    public static Vector<ItemListViewClientes> listaClientesCartera = new Vector<ItemListViewClientes>();
    public static String vendedoresSel = "";
    public static ItemListView vendedor;
    public static Cliente cliente2;
    public static Vector<ItemListViewCarteraCliente> listaCarteraCliente = new Vector<ItemListViewCarteraCliente>();
    public static Vector<ItemListViewListaProductosCore> listaproductosCore = new Vector<ItemListViewListaProductosCore>();


    public static long saldoCartera = 0;
    public static long saldoVencCartera = 0;
    public static int consecutivoCasos;
    public static boolean esCasoNuevo;
    public static Vector<ItemListViewListaCasos> listaCasosCliente = new Vector<ItemListViewListaCasos>();

    public static Comentario comntario;
     **/
}
