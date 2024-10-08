package dataObject;

public class Producto {

    public float Iva;
    public String codigo;
    public String nombre;
    public int cantidadAnt;
    public int cantidadAntHoy;
    public int cantidadAct;
    public int cantAgotado;
    public boolean esModificado;
    public int estaSeleccionado;
    public int precioCliente;

    // CAMPOS PARA DESCUENTO
    public float Descuento;
    public float ValorDesc = 0;
    public String Marca;
    public String Linea;
    public String Sublinea;
    public String Categoria;
    public String Subcategoria;
    public String GM4;
    public String UnidadMedida;
    public String Portafolio;
    public float PrecioFinal;

    public float icui;

    public float ibua;

    public boolean seVende = true;
    public boolean agotado = false;
    public boolean recienAgregado=false;
    public float precioCalculado;
}
