package dataObject;

public class Exhibidores {

    public String codigoCliente;
    public String codigoUsuario;
    public String id;
    public String nombre;
    public String ancho;
    public String alto;
    public String codTipoExhibidor;
    public String codUbicacion;
    public String fechaMovil;
    public boolean estaGestionado;
    public String codigoTipoExhibidor;
    public String codigoUbicacionExhibidor;
    public int registroHoy;
    public String finalizado;


    @Override
    public String toString() {
        return nombre;
    }
}
