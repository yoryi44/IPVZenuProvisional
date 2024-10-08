package businessObject;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import component.Util;
import dataObject.ActivacionCompetencia;
import dataObject.ActividadesCliente;
import dataObject.AdicionalesExhibidor;
import dataObject.Cliente;
import dataObject.ComponenteActivacion;
import dataObject.ComponenteActivacionTerminado;
import dataObject.ExhibidorEncabezado;
import dataObject.Exhibidores;
import dataObject.Foto;
import dataObject.Gramo;
import dataObject.ItemListView;
import dataObject.ItemListViewActividadesCliente;
import dataObject.ItemListViewAgotados;
import dataObject.ItemListViewClientes;
import dataObject.ItemListViewComponenteActivacion;
import dataObject.ItemListViewExhibidores;
import dataObject.ItemListViewMedicionActivacion;
import dataObject.ItemListViewProductos;
import dataObject.Linea;
import dataObject.Main;
import dataObject.Marca;
import dataObject.Materia;
import dataObject.ModuloActivacion;
import dataObject.ObjetoActivacion;
import dataObject.Producto;
import dataObject.ProductoAgotado;
import dataObject.TipoExhibidor;
import dataObject.UbicacionExhibidor;
import dataObject.Usuario;
import dataObject.Vendedor;

public class DataBaseBO {

    public static final String TAG = "DataBaseBO";
    private static File dbFile;
    public static String mensaje;
    public static String msg = "No Existe Base de datos";
    public static String idDetalleMedicion= "";


    public static boolean crearConfigDB() {

        SQLiteDatabase db = null;
        String config = "";

        try {

            File dbFile = new File(Util.dirApp(), "Config.db");
            db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);

            config = "CREATE TABLE IF NOT EXISTS Vendedor(codigo varchar(10), nombre varchar(30), seleccionado int)";
            db.execSQL(config);

            config = "CREATE TABLE IF NOT EXISTS Usuario(codigo varchar(10), nombre varchar(30), codigoRegional varchar(2))";
            db.execSQL(config);
            return true;

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "CrearConfigDB -> " + mensaje, e);
            return false;

        } finally {

            if (db != null)
                db.close();
        }
    }

    public static void guardarVendedoresSincronizador(Vector<ItemListView> listVendedores) {

        ContentValues values;
        SQLiteDatabase dbCongig = null;

        try {

            dbFile = new File(Util.dirApp(), "Config.db");
            dbCongig = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            dbCongig.execSQL("DELETE FROM Vendedor");

            /*******************************************
             * Se almacenan los vendedores sincronizados
             *******************************************/
            values = new ContentValues();

            for (int i = 0; i < listVendedores.size(); i++) {

                values.put("codigo", listVendedores.elementAt(i).codigo);
                values.put("nombre", listVendedores.elementAt(i).nombre);
                values.put("seleccionado", listVendedores.elementAt(i).seleccionado);

                dbCongig.insertOrThrow("Vendedor", null, values);
            }

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("DataBaseBO", "RegistrarProductoPedidos: " + mensaje, e);

        } finally {

            if (dbCongig != null)
                dbCongig.close();
        }
    }

    public static void guardarUsuarioSincronizador(Usuario usuario) {

        ContentValues values;
        SQLiteDatabase dbCongig = null;

        try {

            dbFile = new File(Util.dirApp(), "Config.db");
            dbCongig = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            dbCongig.execSQL("DELETE FROM Usuario");

            /*******************************************
             ******* Se almacenan el Usuario sincronizado
             *******************************************/
            values = new ContentValues();

            values.put("codigo", usuario.codigo);
            values.put("nombre", usuario.nombre);
            values.put("codigoRegional", "0");

            dbCongig.insertOrThrow("Usuario", null, values);

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("DataBaseBO", "Almacenar Usuario: " + mensaje, e);

        } finally {

            if (dbCongig != null)
                dbCongig.close();
        }
    }

    public static int getDiaNumeroSemana() {

        SQLiteDatabase db = null;
        int dia = 0;

        try {

            File dbFile = new File(Util.dirApp(), "Config.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT strftime('%w', datetime(CURRENT_TIMESTAMP, 'localtime')) as dia";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                dia = cursor.getInt(cursor.getColumnIndex("dia"));
            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();
        }

        if (dia == 0)
            dia = 7;

        dia--;

        return dia;
    }

    public static boolean hayInformacionXEnviar() {

        mensaje = "";
        SQLiteDatabase db = null;

        boolean hayInfoPendiente = false;
        Vector<String> tableNames = new Vector<String>();

        try {

            File dbFile = new File(Util.dirApp(), "Temp.db");

            if (dbFile.exists()) {

                db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
                String query = "SELECT tbl_name FROM sqlite_master WHERE tbl_name <> 'android_metadata' AND [type] = 'table'";
                Cursor cursor = db.rawQuery(query, null);

                if (cursor.moveToFirst()) {

                    do {

                        String tableName = cursor.getString(cursor.getColumnIndex("tbl_name"));
                        tableNames.addElement(tableName);

                    } while (cursor.moveToNext());
                }

                if (cursor != null)
                    cursor.close();

                for (String tableName : tableNames) {

                    query = "SELECT COUNT(*) AS total FROM " + tableName;
                    cursor = db.rawQuery(query, null);

                    if (cursor.moveToFirst()) {

                        int total = cursor.getInt(cursor.getColumnIndex("total"));

                        if (total > 0) {

                            hayInfoPendiente = true;
                            break;
                        }
                    }

                    if (cursor != null)
                        cursor.close();
                }

                if (cursor != null)
                    cursor.close();

            } else {

                Log.e(TAG, "hayInformacionXEnviar" + msg);
            }

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "hayInformacionXEnviar" + mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return hayInfoPendiente;
    }

    public static Vector<ItemListView> obtenerListaDeVendedores() {

        SQLiteDatabase dbConfig = null;
        Vector<ItemListView> listaVendedores = new Vector<ItemListView>();

        try {

            File dbFile = new File(Util.dirApp(), "Config.db");
            dbConfig = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT codigo, nombre, seleccionado FROM Vendedor";

            Log.i("Consulta Vendedores", query);
            Cursor cursor = dbConfig.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                do {

                    ItemListView item = new ItemListView();

                    item.codigo = cursor.getString(cursor.getColumnIndex("codigo"));
                    item.nombre = cursor.getString(cursor.getColumnIndex("nombre"));
                    item.seleccionado = cursor.getInt(cursor.getColumnIndex("seleccionado"));

                    listaVendedores.addElement(item);

                } while (cursor.moveToNext());
            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("DataBaseBO", mensaje, e);

        } finally {

            if (dbConfig != null)
                dbConfig.close();
        }

        return listaVendedores;
    }

    public static Vector<Vendedor> obtenerListaDeVendedoresSincronizacion() {

        SQLiteDatabase db = null;
        Vector<Vendedor> listaVendedores = new Vector<>();

        try {

            File dbFile = new File(Util.dirApp(), "Config.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT codigo Codigo, nombre Nombre, seleccionado Seleccionado FROM Vendedor";
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                do {

                    Vendedor vendedor = new Vendedor();

                    vendedor.codigoVendedor = cursor.getString(cursor.getColumnIndex("Codigo"));
                    vendedor.nombreVendedor = cursor.getString(cursor.getColumnIndex("Nombre"));
                    vendedor.seleccionado = cursor.getInt(cursor.getColumnIndex("Seleccionado"));

                    listaVendedores.addElement(vendedor);

                } while (cursor.moveToNext());
            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("DataBaseBO", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return listaVendedores;
    }

    public static Vector<Cliente> listaClientesRutero(Vector<ItemListViewClientes> listaItems,
                                                      String diaSemanaSel,
                                                      String parametroBusqueda,
                                                      boolean visitados) {

        mensaje = "";
        Cliente cliente;
        SQLiteDatabase db = null;
        String sql = "";
        boolean gestion = false;

        Vector<Cliente> listaClientes = new Vector<Cliente>();

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            if (parametroBusqueda.length() > 0) {

                if (visitados) {

                    gestion = true;

                    sql = "SELECT distinct Rutero.Codigo as codigo, Nombre, cast (\" + condicionOrden[ 0 ] + \" as integer) as orden, " +
                            "Direccion, Telefono, Nit, Razonsocial, Ciudad, CodigoAmarre, Canal, Subcanal, Bloqueado, TipoCredito, " +
                            "Territorio, Potencial AS Potencial, Valor as Valor, Necesidad as Necesidad, Actividad, enlace1 as enlace, " +
                            "Cupo, condicionpago, Zonaventas, ZonaTransporte, email, ciudad2, GrupoPrecios, linea as linea, Barrio Barrio, " +
                            "cantidadGestion cantidadGestion, Vendedor1 VendedorCliente, Agencia Agencia " +
                            "FROM Clientes " +
                            "INNER JOIN Rutero ON Clientes.codigo = Rutero.codigo " +
                            "WHERE Rutero.DIAVISITA = '" + diaSemanaSel + "' " +
                            "AND (Clientes.Codigo LIKE '%" + parametroBusqueda + "%' OR Razonsocial LIKE '%" + parametroBusqueda +
                            "%' OR Nombre LIKE '%" + parametroBusqueda + "%' OR Nit LIKE '%" + parametroBusqueda + "%') " +
                            "AND Clientes.Codigo IN (SELECT CodigoCliente FROM IPV_ClientesGestionados)";

                } else {

                    gestion = false;

                    sql = "SELECT distinct Rutero.Codigo as codigo, Nombre, cast (\" + condicionOrden[ 0 ] + \" as integer) as orden, " +
                            "Direccion, Telefono, Nit, Razonsocial, Ciudad, CodigoAmarre, Canal, Subcanal, Bloqueado, TipoCredito, " +
                            "Territorio, Potencial AS Potencial, Valor as Valor, Necesidad as Necesidad, Actividad, enlace1 as enlace, " +
                            "Cupo, condicionpago, Zonaventas, ZonaTransporte, email, ciudad2, GrupoPrecios, linea as linea, Barrio Barrio, " +
                            "cantidadGestion cantidadGestion, Vendedor1 VendedorCliente, Agencia Agencia " +
                            "FROM Clientes " +
                            "INNER JOIN Rutero ON Clientes.codigo = Rutero.codigo " +
                            "WHERE Rutero.DIAVISITA = '" + diaSemanaSel + "' " +
                            "AND (Clientes.Codigo LIKE '%" + parametroBusqueda + "%' OR Razonsocial LIKE '%" + parametroBusqueda +
                            "%' OR Nombre LIKE '%" + parametroBusqueda + "%' OR Nit LIKE '%" + parametroBusqueda + "%') " +
                            "AND Clientes.Codigo NOT IN (SELECT CodigoCliente FROM IPV_ClientesGestionados)";
                }

            } else {

                if (visitados) {

                    gestion = true;

                    sql = "SELECT distinct Rutero.Codigo as codigo, Nombre, cast (\" + condicionOrden[ 0 ] + \" as integer) as orden, " +
                            "Direccion, Telefono, Nit, Razonsocial, Ciudad, CodigoAmarre, Canal, Subcanal, Bloqueado, TipoCredito, " +
                            "Territorio, Potencial AS Potencial, Valor as Valor, Necesidad as Necesidad, Actividad, enlace1 as enlace, " +
                            "Cupo, condicionpago, Zonaventas, ZonaTransporte, email, ciudad2, GrupoPrecios, linea as linea, Barrio Barrio, " +
                            "cantidadGestion cantidadGestion, Vendedor1 VendedorCliente, Agencia Agencia " +
                            "FROM Clientes " +
                            "INNER JOIN Rutero ON Clientes.codigo = Rutero.codigo " +
                            "WHERE Rutero.DIAVISITA = '" + diaSemanaSel + "' " +
                            "AND (Clientes.Codigo LIKE '%" + parametroBusqueda + "%' OR Razonsocial LIKE '%" + parametroBusqueda +
                            "%' OR Nombre LIKE '%" + parametroBusqueda + "%' OR Nit LIKE '%" + parametroBusqueda + "%') " +
                            "AND Clientes.Codigo IN (SELECT CodigoCliente FROM IPV_ClientesGestionados)";

                } else {

                    gestion = false;

                    sql = "SELECT distinct Rutero.Codigo as codigo, Nombre, cast (\" + condicionOrden[ 0 ] + \" as integer) as orden, " +
                            "Direccion, Telefono, Nit, Razonsocial, Ciudad, CodigoAmarre, Canal, Subcanal, Bloqueado, TipoCredito, " +
                            "Territorio, Potencial AS Potencial, Valor as Valor, Necesidad as Necesidad, Actividad, enlace1 as enlace, " +
                            "Cupo, condicionpago, Zonaventas, ZonaTransporte, email, ciudad2, GrupoPrecios, linea as linea, Barrio Barrio, " +
                            "cantidadGestion cantidadGestion, Vendedor1 VendedorCliente, Agencia Agencia " +
                            "FROM Clientes " +
                            "INNER JOIN Rutero ON Clientes.codigo = Rutero.codigo " +
                            "WHERE Rutero.DIAVISITA = '" + diaSemanaSel + "' " +
                            "AND (Clientes.Codigo LIKE '%" + parametroBusqueda + "%' OR Razonsocial LIKE '%" + parametroBusqueda +
                            "%' OR Nombre LIKE '%" + parametroBusqueda + "%' OR Nit LIKE '%" + parametroBusqueda + "%') " +
                            "AND Clientes.Codigo NOT IN (SELECT CodigoCliente FROM IPV_ClientesGestionados)";
                }
            }

            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst()) {

                do {

                    cliente = new Cliente();

                    cliente.codigo = cursor.getString(cursor.getColumnIndex("codigo")).trim();
                    cliente.nombre = cursor.getString(cursor.getColumnIndex("Nombre"));
                    cliente.ordenVisita = cursor.getInt(cursor.getColumnIndex("orden"));
                    cliente.direccion = cursor.getString(cursor.getColumnIndex("Direccion"));
                    cliente.telefono = cursor.getString(cursor.getColumnIndex("Telefono"));
                    cliente.nit = cursor.getString(cursor.getColumnIndex("Nit")).trim();
                    cliente.razonSocial = cursor.getString(cursor.getColumnIndex("Razonsocial")).trim();
                    cliente.ciudad = cursor.getString(cursor.getColumnIndex("Ciudad"));
                    cliente.listaPrecio = cursor.getString(cursor.getColumnIndex("CodigoAmarre"));
                    cliente.canal = cursor.getString(cursor.getColumnIndex("Canal"));
                    cliente.cupo = cursor.getFloat(cursor.getColumnIndex("Cupo"));
                    cliente.subCanal = cursor.getString(cursor.getColumnIndex("Subcanal"));
                    cliente.bloqueado = cursor.getString(cursor.getColumnIndex("Bloqueado")).trim();
                    cliente.tipoCredito = cursor.getString(cursor.getColumnIndex("TipoCredito"));
                    cliente.territorio = cursor.getString(cursor.getColumnIndex("Territorio"));
                    cliente.GC2 = cursor.getString(cursor.getColumnIndex("Potencial"));
                    cliente.GC3 = cursor.getString(cursor.getColumnIndex("Valor"));
                    cliente.GC4 = cursor.getString(cursor.getColumnIndex("Necesidad"));
                    cliente.actividad = cursor.getString(cursor.getColumnIndex("Actividad"));
                    cliente.enlace = cursor.getString(cursor.getColumnIndex("enlace"));
                    cliente.condPago = cursor.getString(cursor.getColumnIndex("condicionpago"));
                    cliente.zonaVentas = cursor.getString(cursor.getColumnIndex("Zonaventas"));
                    cliente.zonaTransporte = cursor.getString(cursor.getColumnIndex("ZonaTransporte"));
                    cliente.email = cursor.getString(cursor.getColumnIndex("email"));
                    cliente.codDane = cursor.getString(cursor.getColumnIndex("ciudad2"));
                    cliente.grupoPrecio = cursor.getString(cursor.getColumnIndex("GrupoPrecios"));
                    cliente.linea = cursor.getString(cursor.getColumnIndex("linea"));
                    cliente.barrio = cursor.getString(cursor.getColumnIndex("Barrio"));
                    cliente.cantidadGes = cursor.getInt(cursor.getColumnIndex("cantidadGestion"));
                    cliente.vendedorCliente = cursor.getString(cursor.getColumnIndex("VendedorCliente"));
                    cliente.agencia = cursor.getString(cursor.getColumnIndex("Agencia"));
                    cliente.tieneGestion = gestion;

                    cliente.extra_ruta = 0; //Los Clientes del Rutero no son Extra Ruta

//                    ItemListViewClientes item = new ItemListViewClientes();
//
//                    item.codigo = cliente.codigo;
//                    item.codigoCliente = item.codigo;
//                    item.codigo += " - " + cliente.razonSocial;
//                    item.direccion = cliente.direccion;
//                    item.gestion = cliente.tieneGestion;
//
//                    listaItems.add(item);
                    listaClientes.addElement(cliente);


                } while (cursor.moveToNext());

                mensaje = "Rutero Cargado Correctamente";

            } else {

                mensaje = "Consulta sin Resultados";
            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("ListaClientesRutero", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return listaClientes;
    }

    public static void eliminarUsuario() {

        SQLiteDatabase db = null;
        String config = "";

        try {

            File dbFile = new File(Util.dirApp(), "Config.db");
            db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);

            config = "DELETE FROM Usuario";
            db.execSQL(config);


        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "ELIMINAR USUARIO -> " + mensaje, e);


        } finally {

            if (db != null)
                db.close();
        }
    }

    public static Usuario obtenerUsuario() {

        SQLiteDatabase dbConfig = null;
        Usuario usuario = null;
        String query = "";

        try {

            File dbFile = new File(Util.dirApp(), "Config.db");
            dbConfig = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            query = "SELECT codigo codigo, nombre nombre FROM Usuario";

            Log.i("Consulta Usuario", query);
            Cursor cursor = dbConfig.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                do {

                    usuario = new Usuario();

                    usuario.codigo = cursor.getString(cursor.getColumnIndex("codigo"));
                    usuario.nombre = cursor.getString(cursor.getColumnIndex("nombre"));


                } while (cursor.moveToNext());
            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("DataBaseBO", mensaje, e);

        } finally {

            if (dbConfig != null)
                dbConfig.close();
        }

        return usuario;
    }

    public static boolean borrarInfoTemp() {

        SQLiteDatabase dbTemp = null;

        try {

            File dbFile = new File(Util.dirApp(), "Temp.db");

            if (dbFile.exists()) {

                dbTemp = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

                Vector<String> tableNames = new Vector<String>();
                String query = "SELECT tbl_name FROM sqlite_master";
                Cursor cursor = dbTemp.rawQuery(query, null);

                if (cursor.moveToFirst()) {

                    do {

                        String tableName = cursor.getString(cursor.getColumnIndex("tbl_name"));

                        if (tableName.equals("android_metadata"))
                            continue;

                        tableNames.addElement(tableName);

                    } while (cursor.moveToNext());
                }

                if (cursor != null)
                    cursor.close();

                for (String tableName : tableNames) {

                    query = "DELETE FROM " + tableName;
                    dbTemp.execSQL(query);
                }
            }

            return true;

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "BorrarInfoTemp: " + mensaje, e);
            return false;

        } finally {

            if (dbTemp != null)
                dbTemp.close();
        }
    }

    public static String obtenerVersionApp() {

        String version = "";
        SQLiteDatabase db = null;

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT Version AS Version FROM Version";
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                version = cursor.getString(cursor.getColumnIndex("Version"));
            }

            Log.i("ObtenerVersionApp", "version = " + version);

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("ObtenerVersionApp", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }
        return version;
    }

    public static Cliente obtenerClienteSeleccionado(String codigoCliente) {

        mensaje = "";
        Cliente cliente = new Cliente();
        SQLiteDatabase db = null;
        String sql = "";

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            sql = "SELECT distinct Rutero.Codigo as codigo, Nombre, cast (\" + condicionOrden[ 0 ] + \" as integer) as orden, " +
                    "Direccion, Telefono, Nit, Razonsocial, Ciudad, CodigoAmarre, Canal, Subcanal, Bloqueado, TipoCredito, " +
                    "Territorio, Potencial AS Potencial, Valor as Valor, Necesidad as Necesidad, Actividad, enlace1 as enlace, " +
                    "Cupo, condicionpago, Zonaventas, ZonaTransporte, email, ciudad2, GrupoPrecios, linea as linea, Barrio Barrio, " +
                    "cantidadGestion cantidadGestion, Vendedor1 VendedorCliente " +
                    "FROM Clientes " +
                    "INNER JOIN Rutero ON Clientes.codigo = Rutero.codigo " +
                    "WHERE Rutero.Codigo = '" + codigoCliente + "'";


            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst()) {

                cliente.codigo = cursor.getString(cursor.getColumnIndex("codigo")).trim();
                cliente.nombre = cursor.getString(cursor.getColumnIndex("Nombre"));
                cliente.ordenVisita = cursor.getInt(cursor.getColumnIndex("orden"));
                cliente.direccion = cursor.getString(cursor.getColumnIndex("Direccion"));
                cliente.telefono = cursor.getString(cursor.getColumnIndex("Telefono"));
                cliente.nit = cursor.getString(cursor.getColumnIndex("Nit")).trim();
                cliente.razonSocial = cursor.getString(cursor.getColumnIndex("Razonsocial")).trim();
                cliente.ciudad = cursor.getString(cursor.getColumnIndex("Ciudad"));
                cliente.listaPrecio = cursor.getString(cursor.getColumnIndex("CodigoAmarre"));
                cliente.canal = cursor.getString(cursor.getColumnIndex("Canal"));
                cliente.cupo = cursor.getFloat(cursor.getColumnIndex("Cupo"));
                cliente.subCanal = cursor.getString(cursor.getColumnIndex("Subcanal"));
                cliente.bloqueado = cursor.getString(cursor.getColumnIndex("Bloqueado")).trim();
                cliente.tipoCredito = cursor.getString(cursor.getColumnIndex("TipoCredito"));
                cliente.territorio = cursor.getString(cursor.getColumnIndex("Territorio"));
                cliente.GC2 = cursor.getString(cursor.getColumnIndex("Potencial"));
                cliente.GC3 = cursor.getString(cursor.getColumnIndex("Valor"));
                cliente.GC4 = cursor.getString(cursor.getColumnIndex("Necesidad"));
                cliente.actividad = cursor.getString(cursor.getColumnIndex("Actividad"));
                cliente.enlace = cursor.getString(cursor.getColumnIndex("enlace"));
                cliente.condPago = cursor.getString(cursor.getColumnIndex("condicionpago"));
                cliente.zonaVentas = cursor.getString(cursor.getColumnIndex("Zonaventas"));
                cliente.zonaTransporte = cursor.getString(cursor.getColumnIndex("ZonaTransporte"));
                cliente.email = cursor.getString(cursor.getColumnIndex("email"));
                cliente.codDane = cursor.getString(cursor.getColumnIndex("ciudad2"));
                cliente.grupoPrecio = cursor.getString(cursor.getColumnIndex("GrupoPrecios"));
                cliente.linea = cursor.getString(cursor.getColumnIndex("linea"));
                cliente.extra_ruta = 0; //Los Clientes del Rutero no son Extra Ruta
                cliente.barrio = cursor.getString(cursor.getColumnIndex("Barrio"));
                cliente.cantidadGes = cursor.getInt(cursor.getColumnIndex("cantidadGestion"));
                cliente.vendedorCliente = cursor.getString(cursor.getColumnIndex("VendedorCliente"));

            } else {

                mensaje = "Consulta sin Resultados";
            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("ListaClientesRutero", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return cliente;
    }

    public static Vector<ActividadesCliente> obtenerListaActividadesCliente(Vector<ItemListViewActividadesCliente> listaItems, String canal, String codigoCliente, String idCategoria) {

        mensaje = "";
        ActividadesCliente actividadesCliente;
        SQLiteDatabase db = null;
        String sql = "";

        Cursor cursorTabla = null;

        Vector<ActividadesCliente> listaActividadesClientes = new Vector<ActividadesCliente>();

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            sql = "SELECT t.Canal Canal, t.Orden Orden, t.NombreTarea NombreTarea, t.TiempoTarea TiempoTarea " +
                    "FROM IPV_TareasCanal t inner join IPV_CategoriaTareas c ON (t.CategoriaTarea = c.CategoriaTarea) " +
                    "WHERE Canal = '" + canal + "' " +
                    "AND t.CategoriaTarea = '"+idCategoria+"' " +
                    "ORDER BY Orden";

            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst()) {

                do {

                    actividadesCliente = new ActividadesCliente();
                    ItemListViewActividadesCliente item = new ItemListViewActividadesCliente();

                    actividadesCliente.canal = cursor.getString(cursor.getColumnIndex("Canal"));
                    actividadesCliente.nombreTarea = cursor.getString(cursor.getColumnIndex("NombreTarea"));
                    actividadesCliente.tiempoTarea = cursor.getString(cursor.getColumnIndex("TiempoTarea"));
                    actividadesCliente.orden = String.valueOf(cursor.getInt(cursor.getColumnIndex("Orden")));

                    item.canal = actividadesCliente.canal;
                    item.nombreTarea = actividadesCliente.nombreTarea;
                    item.tiempoTarea = actividadesCliente.tiempoTarea;
                    item.orden = actividadesCliente.orden;

                    // SE DETERMINA EL NOMBRE DE LA TABLA DE CADA TAREA A FIN DE
                    // SABER SI TIENE O NO MEDICIONES REALIZADAS DE ESE TIPO DE TAREA
                    String nombreTabla = "";
                    int valorProOCom = -1;
                    int cantidadRegistro = 0;
                    int tipoIconoColor = 0;

                    if ((actividadesCliente.nombreTarea).equals("DISTRIBUCIÓN Y AGOTADOS")) {

                        nombreTabla = "IPV_med_agotados";

                    } else if ((actividadesCliente.nombreTarea).equals("EXHIBICIÓN DE PRODUCTOS")) {

                        nombreTabla = "IPV_med_exhibidor_encabezado";

                    } else if ((actividadesCliente.nombreTarea).equals("PRECIO DE PRODUCTOS")) {

                        nombreTabla = "IPV_med_precios";

                    } else if ((actividadesCliente.nombreTarea).equals("ACTIVACIÓN COMERCIAL PROPIA")) {

                        nombreTabla = "IPV_med_activacion";
                        valorProOCom = 0;

                    } else if ((actividadesCliente.nombreTarea).equals("ACTIVACIÓN COMERCIAL COMPETENCIA")) {

                        nombreTabla = "IPV_med_activacion";
                        valorProOCom = 1;
                    } else if ((actividadesCliente.nombreTarea).equals("PRECIOS Y DISPONIBILIDAD")) {

                        nombreTabla = "IPV_med_precios";

                    } else if ((actividadesCliente.nombreTarea).equals("ACTIVACIÓN")) {

                        nombreTabla = "IPV_med_activacion";
                        valorProOCom = 1;
                    }

                    // SE CREA LA CONSULTA
                    String sqlTablaCantidad = "";
                    String condicionCount= idCategoria==null?"":" AND CategoriaTarea = '"+idCategoria+"' ";

                    if (valorProOCom == -1) {

                        sqlTablaCantidad = "SELECT COUNT(*) Cantidad FROM " + nombreTabla + " WHERE CodigoCliente = '" + codigoCliente + "' "+condicionCount;

                    } else {

                        if (valorProOCom == 0) {

                            // PROPIO
                            sqlTablaCantidad = "SELECT COUNT(*) Cantidad FROM " + nombreTabla + " WHERE CodigoCliente = '" + codigoCliente + "' AND Propio = 1 "+condicionCount;

                        } else {

                            // COMPETENCIA
                            sqlTablaCantidad = "SELECT COUNT(*) Cantidad FROM " + nombreTabla + " WHERE CodigoCliente = '" + codigoCliente + "' AND Competencia = 1 "+condicionCount;
                        }
                    }

                    cursorTabla = db.rawQuery(sqlTablaCantidad, null);

                    if (cursorTabla.moveToFirst()) {

                        cantidadRegistro = cursorTabla.getInt(cursorTabla.getColumnIndex("Cantidad"));
                    }

                    if (cursorTabla != null)
                        cursorTabla.close();

                    // 0 VERDE
                    // 1 GRIS
                    // 2 NARANJA

                    if (valorProOCom == -1) {

                        if (cantidadRegistro > 0) {

                            tipoIconoColor = 0; // VERDE

                        } else {

                            tipoIconoColor = 1; // GRIS
                        }

                    } else {

                        if (cantidadRegistro == 3) {

                            tipoIconoColor = 0; // Verde

                        } else if (cantidadRegistro > 0 && cantidadRegistro < 3) {

                            tipoIconoColor = 2; // Naranja

                        } else if (cantidadRegistro == 0) {

                            tipoIconoColor = 1; // GRIS
                        }
                    }

                    actividadesCliente.tieneGestion = tipoIconoColor;
                    item.tieneGestion = tipoIconoColor;

                    listaItems.add(item);
                    listaActividadesClientes.addElement(actividadesCliente);

                } while (cursor.moveToNext());

                mensaje = "Actividades Cargadas Correctamente";

            } else {

                mensaje = "Consulta sin Resultados";
            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("ListaClientesRutero", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return listaActividadesClientes;
    }

    @SuppressLint("LongLogTag")
    public static Vector<ProductoAgotado> obtenerListaProductosAgotados(String codigo) {

        mensaje = "";
        ProductoAgotado productoAgotado;
        SQLiteDatabase db = null;
        String query = "";
        int cantidadAnt = 1;

        Vector<ProductoAgotado> listaProductosAgotados = new Vector<ProductoAgotado>();

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            query = "SELECT DISTINCT Codigo Codigo, Nombre NombreProducto, " +
                    "CASE WHEN mah.Cantidad IS NULL THEN 0 ELSE mah.Cantidad END CantidadAnt, " +
                    "CASE WHEN ma.Cantidad IS NULL THEN 0 ELSE ma.Cantidad END CantidadAct " +
                    "FROM Core2 co " +
                    "INNER JOIN Productos pro ON co.materia = pro.codigo " +
                    "LEFT JOIN IPV_med_hist_agotados mah ON (co.Materia = mah.CodigoProducto AND '" + codigo + "' = mah.CodigoCliente) " +
                    "LEFT JOIN IPV_med_agotados ma ON (co.Materia = ma.CodigoProducto AND '" + codigo + "' = ma.CodigoCliente) " +
                    "ORDER BY pro.marcad Desc";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                do {

                    productoAgotado = new ProductoAgotado();
                    ItemListViewAgotados item = new ItemListViewAgotados();

                    productoAgotado.codigo = cursor.getString(cursor.getColumnIndex("Codigo"));
                    productoAgotado.nombre = cursor.getString(cursor.getColumnIndex("NombreProducto"));
                    productoAgotado.cantidadAnt = cursor.getInt(cursor.getColumnIndex("CantidadAnt"));
                    productoAgotado.cantidadAct = cursor.getInt(cursor.getColumnIndex("CantidadAct"));
                    productoAgotado.cantidadAnt = 0;
                    productoAgotado.cantidadAct = 0;
                    productoAgotado.esModificado = false;
                    productoAgotado.seVende = true;


                    item.codigo = productoAgotado.codigo;
                    item.nombre = productoAgotado.nombre;
                    item.cantidadAnt = productoAgotado.cantidadAnt;
                    item.cantidadAct = productoAgotado.cantidadAct;
                    item.esModificado = productoAgotado.esModificado;
                    item.seVende = true;

                    listaProductosAgotados.addElement(productoAgotado);

                } while (cursor.moveToNext());

                mensaje = "Productos Agotados Cargados Correctamente";

            } else {

                mensaje = "Consulta sin Resultados";
            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("obtenerListaProductosAgotados", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return listaProductosAgotados;
    }

    public static int obtenerTipoUsuario() {

        int tipoUsuario = 3;
        SQLiteDatabase db = null;

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT tipoUsuario tipoUsuario FROM Vendedor";
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                tipoUsuario = cursor.getInt(cursor.getColumnIndex("tipoUsuario"));
            }

            Log.i("ObtenerTipoUsuario", "tipo = " + tipoUsuario);

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("ObtenerTipoUsuario", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return tipoUsuario;
    }


    public static Vector<ActividadesCliente> obtenerCategorias(Vector<ItemListViewActividadesCliente> listaItems) {

        int tipoUsuario = 3;
        SQLiteDatabase db = null;
        ActividadesCliente actividadesCliente = null;
        Vector<ActividadesCliente> listaActividadesClientes = new Vector<ActividadesCliente>();

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "select distinct c.Nombre as nombre, c.CategoriaTarea as CategoriaTarea from IPV_TareasCanal t inner join IPV_CategoriaTareas c ON (t.CategoriaTarea = c.CategoriaTarea) " +
                    "where Canal='" + Main.cliente.canal + "' " +
                    "order by c.orden asc";
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                do {

                    actividadesCliente = new ActividadesCliente();
                    ItemListViewActividadesCliente item = new ItemListViewActividadesCliente();

                    actividadesCliente.nombreTarea = cursor.getString(cursor.getColumnIndex("nombre"));
                    actividadesCliente.codigo = cursor.getString(cursor.getColumnIndex("CategoriaTarea"));

//                    item.canal = actividadesCliente.canal;
                    item.nombreTarea = actividadesCliente.nombreTarea;
//                    item.tiempoTarea = actividadesCliente.tiempoTarea;
//                    item.orden = actividadesCliente.orden;


//                    actividadesCliente.tieneGestion = tipoIconoColor;
//                    item.tieneGestion = tipoIconoColor;

                    listaItems.add(item);
                    listaActividadesClientes.addElement(actividadesCliente);

                } while (cursor.moveToNext());
            }

            Log.i("ObtenerTipoUsuario", "tipo = " + tipoUsuario);

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("ObtenerTipoUsuario", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return listaActividadesClientes;
    }


    public static Vector<AdicionalesExhibidor> obtenerListaAdicionales(Vector<ItemListViewActividadesCliente> listaItems) {


        SQLiteDatabase db = null;
        AdicionalesExhibidor adicionalesExhibidor = null;
        Vector<AdicionalesExhibidor> listaAdicionalesExhibidor = new Vector<AdicionalesExhibidor>();

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "select IDEncabezado as IDEncabezado,IDDetalle as IDDetalle,CodigoUsuario as CodigoUsuario, " +
                    "codigoExhibicion as codigoExhibicion, nombreExhibicion as nombreExhibicion, codigoEstado as codigoEstado, nombreEstado as nombreEstado  from  IPV_med_DetalleActivacion " +
                    "where CodigoUsuario='" + Main.usuario.codigo + "' " +
                    "and codigocliente='" + Main.cliente.codigo + "'";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                do {

                    adicionalesExhibidor = new AdicionalesExhibidor();
                    ItemListViewActividadesCliente item = new ItemListViewActividadesCliente();

                    adicionalesExhibidor.idExhibidor = cursor.getString(cursor.getColumnIndex("IDEncabezado"));
                    adicionalesExhibidor.id = cursor.getString(cursor.getColumnIndex("IDDetalle"));
                    adicionalesExhibidor.usuario = cursor.getString(cursor.getColumnIndex("CodigoUsuario"));
                    adicionalesExhibidor.codexhibidores = cursor.getString(cursor.getColumnIndex("codigoExhibicion"));
                    adicionalesExhibidor.exhibidores = cursor.getString(cursor.getColumnIndex("nombreExhibicion"));
                    adicionalesExhibidor.codestado = cursor.getString(cursor.getColumnIndex("codigoEstado"));
                    adicionalesExhibidor.estado = cursor.getString(cursor.getColumnIndex("nombreEstado"));

                    item.nombreTarea = adicionalesExhibidor.exhibidores + " - " + adicionalesExhibidor.estado;


                    listaItems.add(item);
                    listaAdicionalesExhibidor.addElement(adicionalesExhibidor);

                } while (cursor.moveToNext());
            }

            Log.i("ObtenerTipoUsuario", "R");

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("ObtenerTipoUsuario", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return listaAdicionalesExhibidor;
    }


    public static Vector<Marca> obtenerMarcas(String idCategoria) {

        int tipoUsuario = 3;
        SQLiteDatabase db = null;
        Marca marca = null;
        Vector<Marca> listaMarcas = new Vector<Marca>();

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "select c.codigoMarca as codigoMarca, m.nombre as nombre  from IPV_Config_MarcasCompetencia c inner join marcascompetencia m ON (c.codigoMarca = m.id) " +
                    "where c.canal ='" + Main.cliente.canal + "' " +
                    "and c.ofiventas='" + Main.cliente.agencia + "' " +
                    "and m.CategoriaTarea='"+idCategoria+"'"+
                    "order by m.nombre";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                do {

                    marca = new Marca();
                    marca.codigo = cursor.getString(cursor.getColumnIndex("codigoMarca"));
                    marca.nombre = cursor.getString(cursor.getColumnIndex("nombre"));

                    listaMarcas.addElement(marca);

                } while (cursor.moveToNext());
            }

            Log.i("ObtenerTipoUsuario", "tipo = " + tipoUsuario);

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("ObtenerTipoUsuario", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return listaMarcas;
    }

    public static Vector<Linea> obtenerLineas(String idCategoria) {

        SQLiteDatabase db = null;
        Linea linea = null;
        Vector<Linea> listaMarcas = new Vector<Linea>();

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "select c.codigo as codigo, c.nombre as nombre from IPV_Config_LineasCompetencia c " +
                    "where CategoriaTarea='"+idCategoria+"'"+
                    "order by c.nombre";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                do {

                    linea = new Linea();
                    linea.codigo = cursor.getString(cursor.getColumnIndex("codigo"));
                    linea.nombre = cursor.getString(cursor.getColumnIndex("nombre"));

                    listaMarcas.addElement(linea);

                } while (cursor.moveToNext());
            }

            Log.i("ObtenerTipoUsuario", "tipo = ");

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("ObtenerTipoUsuario", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return listaMarcas;
    }

    public static Vector<Materia> obtenerMaterias(String idCategoria) {

        int tipoUsuario = 3;
        SQLiteDatabase db = null;
        Materia materia = null;
        Vector<Materia> listaMarcas = new Vector<Materia>();

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "select c.codigo as codigo, c.nombre as nombre from IPV_Config_MateriaPrimaCompetencia c " +
                    "where CategoriaTarea='"+idCategoria+"'"+
                    "order by c.nombre";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                do {

                    materia = new Materia();
                    materia.codigo = cursor.getString(cursor.getColumnIndex("codigo"));
                    materia.nombre = cursor.getString(cursor.getColumnIndex("nombre"));

                    listaMarcas.addElement(materia);

                } while (cursor.moveToNext());
            }

            Log.i("ObtenerTipoUsuario", "tipo = " + tipoUsuario);

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("ObtenerTipoUsuario", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return listaMarcas;
    }


    public static Vector<Gramo> obtenerGramos(String idCategoria) {

        int tipoUsuario = 3;
        SQLiteDatabase db = null;
        Gramo gramo = null;
        Vector<Gramo> listaMarcas = new Vector<Gramo>();

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "select c.codigo as codigo, c.nombre as nombre from IPV_Config_GramosCompetencia c " +
                    "where CategoriaTarea='"+idCategoria+"'"+
                    "order by c.nombre";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                do {

                    gramo = new Gramo();
                    gramo.codigo = cursor.getString(cursor.getColumnIndex("codigo"));
                    gramo.nombre = cursor.getString(cursor.getColumnIndex("nombre"));

                    listaMarcas.addElement(gramo);

                } while (cursor.moveToNext());
            }

            Log.i("ObtenerTipoUsuario", "tipo = " + tipoUsuario);

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("ObtenerTipoUsuario", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return listaMarcas;
    }


    public static Vector<Exhibidores> obtenerTipoExhibidores() {

        int tipoUsuario = 3;
        SQLiteDatabase db = null;
        Exhibidores exhibidores = null;
        Vector<Exhibidores> listaMarcas = new Vector<Exhibidores>();

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT codigo as codigo, descripcion as  descripcion FROM  Exhibidores where codCanal = '" + Main.cliente.canal + "' " +
                    "order by descripcion";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                do {

                    exhibidores = new Exhibidores();
                    exhibidores.id = cursor.getString(cursor.getColumnIndex("codigo"));
                    exhibidores.nombre = cursor.getString(cursor.getColumnIndex("descripcion"));

                    listaMarcas.addElement(exhibidores);

                } while (cursor.moveToNext());
            }

            Log.i("ObtenerTipoUsuario", "tipo = " + tipoUsuario);

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("ObtenerTipoUsuario", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return listaMarcas;
    }

    public static Vector<Exhibidores> obtenerEstadosExhibidores() {

        int tipoUsuario = 3;
        SQLiteDatabase db = null;
        Exhibidores exhibidores = null;
        Vector<Exhibidores> listaMarcas = new Vector<Exhibidores>();

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT codigo as codigo, nombre as nombre FROM ExhibidoresEstado " +
                    "order by nombre";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                do {

                    exhibidores = new Exhibidores();
                    exhibidores.id = cursor.getString(cursor.getColumnIndex("codigo"));
                    exhibidores.nombre = cursor.getString(cursor.getColumnIndex("nombre"));

                    listaMarcas.addElement(exhibidores);

                } while (cursor.moveToNext());
            }

            Log.i("ObtenerTipoUsuario", "tipo = " + tipoUsuario);

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("ObtenerTipoUsuario", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return listaMarcas;
    }

    public static String getDescripcionCondicionPago(String condicionPago) {

        String cp = "";

        SQLiteDatabase db = null;

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null,
                    SQLiteDatabase.OPEN_READWRITE);


            String query = "SELECT Codigo, Descripcion FROM CondicionesPago where Codigo = '" + condicionPago + "'";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                cp = cursor.getString(cursor.getColumnIndex("Codigo"));
                cp += "-" + cursor.getString(cursor.getColumnIndex("Descripcion"));
            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();
        }

        return cp;
    }

    public static Vector<Producto> listaProductos(Vector<ItemListViewProductos> listaItems, String parametroBusqueda, int tipoBusqueda) {

        mensaje = "";
        Producto producto;
        SQLiteDatabase db = null;
        String query = "";

        Vector<Producto> listaProductos = new Vector<Producto>();

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            if (tipoBusqueda == 0) {

                query = "SELECT DISTINCT Codigo Codigo, Nombre NombreProducto, " +
                        "precio PrecioCliente, " +
                        "Iva IvaProducto, " +
                        "marca Marca, " +
                        "linea Linea, " +
                        "sublinea Sublinea, " +
                        "categoria Categoria, " +
                        "subcategoria Subcategoria, " +
                        "gm4 GM4, " +
                        "unidadMedida UnidadMedida, " +
                        "portafolio Portafolio " +
                        "FROM Productos " +
                        "WHERE Codigo NOT IN (SELECT materia FROM Core WHERE tabla = 'ZCOR' GROUP BY materia) " +
                        "AND (Codigo LIKE '%" + parametroBusqueda + "%' OR Nombre LIKE '%" + parametroBusqueda + "%' OR EAN LIKE '%" + parametroBusqueda + "%') "+
                        "AND Productos.LineaProduccion = '"+Main.idCategoria+"' ";

            } else {

                if (tipoBusqueda == 1) {         // PROPIO

                    query = "SELECT DISTINCT Codigo Codigo, Nombre NombreProducto FROM Productos " +
                            "WHERE " +
                            // Codigo NOT IN (SELECT materia FROM Core GROUP BY materia) " + AND
                            "(Codigo LIKE '%" + parametroBusqueda + "%' OR Nombre LIKE '%" + parametroBusqueda + "%' OR EAN LIKE '%" + parametroBusqueda + "%') " +
                            "AND Productos.LineaProduccion = '"+Main.idCategoria+"' ";

                } else if (tipoBusqueda == 10) { // COMPETENCIA

                    query = "SELECT DISTINCT codProdCompetencia Codigo, desProdCompetencia NombreProducto FROM ProductosCompetencia " +
                            "WHERE (codProdCompetencia LIKE '%" + parametroBusqueda + "%' OR desProdCompetencia LIKE '%" + parametroBusqueda + "%')";
                }
            }

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                do {

                    producto = new Producto();
                    ItemListViewProductos item = new ItemListViewProductos();

                    producto.codigo = cursor.getString(cursor.getColumnIndex("Codigo"));
                    producto.nombre = cursor.getString(cursor.getColumnIndex("NombreProducto"));
                    if (tipoBusqueda == 0) {
                        producto.precioCliente = cursor.getInt(cursor.getColumnIndex("PrecioCliente"));
                        producto.Iva = cursor.getFloat(cursor.getColumnIndex("IvaProducto"));
                        producto.Marca = cursor.getString(cursor.getColumnIndex("Marca"));
                        producto.Linea = cursor.getString(cursor.getColumnIndex("Linea"));
                        producto.Sublinea = cursor.getString(cursor.getColumnIndex("Sublinea"));
                        producto.Categoria = cursor.getString(cursor.getColumnIndex("Categoria"));
                        producto.Subcategoria = cursor.getString(cursor.getColumnIndex("Subcategoria"));
                        producto.GM4 = cursor.getString(cursor.getColumnIndex("GM4"));
                        producto.UnidadMedida = cursor.getString(cursor.getColumnIndex("UnidadMedida"));
                        producto.Portafolio = cursor.getString(cursor.getColumnIndex("Portafolio"));

                    }
                    producto.cantidadAnt = 0;

                    item.codigo = producto.codigo;
                    item.nombre = producto.nombre;
                    item.cantidadAnt = producto.cantidadAnt;

                    listaItems.add(item);
                    listaProductos.addElement(producto);


                } while (cursor.moveToNext());

                mensaje = "Productos Agotados Cargados Correctamente";

            } else {

                mensaje = "Consulta sin Resultados";
            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("obtenerListaProductoss", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return listaProductos;
    }

    public static boolean guardarProductosAgotados(String codigoCliente, Usuario usuario, int tipoUsuario, String id, String fecha, Vector<ProductoAgotado> listaProductosAgotados) {

        SQLiteDatabase db = null;
        SQLiteDatabase dbTemp = null;

        try {

            dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            File filePedido = new File(Util.dirApp(), "Temp.db");
            dbTemp = SQLiteDatabase.openDatabase(filePedido.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            /****************************************************************
             * Se almacenan los productos agotados de la medicion por cliente
             ****************************************************************/
            for (int i = 0; i < listaProductosAgotados.size(); i++) {

                if (listaProductosAgotados.elementAt(i).cantidadAct >= 0 && listaProductosAgotados.elementAt(i).seVende) {

                    ContentValues values = new ContentValues();

                    values.put("CodigoCliente", codigoCliente);
                    values.put("CodigoUsuario", usuario.codigo);
                    values.put("NombreUsuario", usuario.nombre);
                    values.put("TipoUsuario", tipoUsuario);
                    values.put("ID", id);
                    values.put("CodigoProducto", listaProductosAgotados.elementAt(i).codigo);
                    values.put("NombreProducto", listaProductosAgotados.elementAt(i).nombre);
                    values.put("Cantidad", listaProductosAgotados.elementAt(i).cantidadAct);
                    values.put("Core", 1);
                    values.put("Propio", 1);
                    values.put("Competencia", 0);
                    values.put("FechaMovil", fecha);

                    db.insertOrThrow("IPV_med_agotados", null, values);
                    dbTemp.insertOrThrow("IPV_med_agotados", null, values);
                }
            }

            return true;

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("DataBaseBO", "RegistrarProductoPedidos: " + mensaje, e);
            return false;

        } finally {

            if (db != null)
                db.close();

            if (dbTemp != null)
                dbTemp.close();

            validarAgregarGestionCliente(codigoCliente);
        }
    }


    public static boolean guardarProductosPreciosDistribucionAgotados(String codigoCliente, Usuario usuario, int tipoUsuario, String id, String fecha, Vector<Producto> listaProductosAgotados,String idCategoria) {

        SQLiteDatabase db = null;
        SQLiteDatabase dbTemp = null;

        try {

            dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            File filePedido = new File(Util.dirApp(), "Temp.db");
            dbTemp = SQLiteDatabase.openDatabase(filePedido.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            /****************************************************************
             * Se almacenan los productos agotados de la medicion por cliente
             ****************************************************************/
            for (int i = 0; i < listaProductosAgotados.size(); i++) {

                if (listaProductosAgotados.elementAt(i).cantAgotado == 1 || listaProductosAgotados.elementAt(i).cantAgotado == -1) {

                    ContentValues values = new ContentValues();

                    values.put("CodigoCliente", codigoCliente);
                    values.put("CodigoUsuario", usuario.codigo);
                    values.put("NombreUsuario", usuario.nombre);
                    values.put("TipoUsuario", tipoUsuario);
                    values.put("ID", id);
                    values.put("CodigoProducto", listaProductosAgotados.elementAt(i).codigo);
                    values.put("NombreProducto", listaProductosAgotados.elementAt(i).nombre);
                    values.put("Cantidad", listaProductosAgotados.elementAt(i).cantAgotado);
                    values.put("Core", 1);
                    values.put("Propio", 1);
                    values.put("Competencia", 0);
                    values.put("FechaMovil", fecha);
                    values.put("CategoriaTarea", idCategoria);

                    db.insertOrThrow("IPV_med_agotados", null, values);
                    dbTemp.insertOrThrow("IPV_med_agotados", null, values);
                }
            }

            return true;

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("DataBaseBO", "RegistrarProductoPedidos: " + mensaje, e);
            return false;

        } finally {

            if (db != null)
                db.close();

            if (dbTemp != null)
                dbTemp.close();

            validarAgregarGestionCliente(codigoCliente);
        }
    }

    private static void validarAgregarGestionCliente(String codigoCliente) {

        // SE DETERMINA POR MEDIO DE UNA SUMA DE ACTIVIDADES SI EL CLIENTE QUEDA MARCADO COMO VISITADO
        SQLiteDatabase db = null;
        String sql = "";
        Cursor cursorTabla = null;
        int cantidadRegistro = 0;

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


//            // 1.0 "DISTRIBUCIÓN Y AGOTADOS"
//            sql = "SELECT COUNT(*) Cantidad FROM IPV_med_agotados WHERE CodigoCliente = '" + codigoCliente + "'";
//
//            cursorTabla = db.rawQuery(sql, null);
//
//            if (cursorTabla.moveToFirst()) {
//
//                int cantidad = cursorTabla.getInt(cursorTabla.getColumnIndex("Cantidad"));
//
//                if(cantidad > 0) {
//
//                    cantidadRegistro += 1;
//                }
//            }


            // 2.0 EXHIBICIÓN DE PRODUCTOS"
            sql = "SELECT COUNT(*) Cantidad FROM IPV_med_exhibidor_encabezado WHERE CodigoCliente = '" + codigoCliente + "'";

            cursorTabla = db.rawQuery(sql, null);

            if (cursorTabla.moveToFirst()) {

                int cantidad = cursorTabla.getInt(cursorTabla.getColumnIndex("Cantidad"));

                if (cantidad > 0) {

                    cantidadRegistro += 1;
                }
            }


            // 3.0 "PRECIO DE PRODUCTOS"
            sql = "SELECT COUNT(*) Cantidad FROM IPV_med_precios WHERE CodigoCliente = '" + codigoCliente + "'";

            cursorTabla = db.rawQuery(sql, null);

            if (cursorTabla.moveToFirst()) {

                int cantidad = cursorTabla.getInt(cursorTabla.getColumnIndex("Cantidad"));

                if (cantidad > 0) {

                    cantidadRegistro += 1;
                }
            }


            // 4.0 "ACTIVACIÓN COMERCIAL PROPIA"
            sql = "SELECT COUNT(*) Cantidad FROM IPV_med_activacion WHERE CodigoCliente = '" + codigoCliente + "' AND Propio = 1";

            cursorTabla = db.rawQuery(sql, null);

            if (cursorTabla.moveToFirst()) {

                int cantidad = cursorTabla.getInt(cursorTabla.getColumnIndex("Cantidad"));

                if (cantidad > 0) {

                    cantidadRegistro += 1;
                }
            }


//            // 5.0 "ACTIVACIÓN COMERCIAL COMPETENCIA"
//            sql = "SELECT COUNT(*) Cantidad FROM IPV_med_activacion WHERE CodigoCliente = '" + codigoCliente + "' AND Competencia = 1";
//
//            cursorTabla = db.rawQuery(sql, null);
//
//            if (cursorTabla.moveToFirst()) {
//
//                int cantidad = cursorTabla.getInt(cursorTabla.getColumnIndex("Cantidad"));
//
//                if(cantidad > 0) {
//
//                    cantidadRegistro += 1;
//                }
//            }

            if (cantidadRegistro >= 3) {

                ContentValues valuesMarcaGestion = new ContentValues();
                valuesMarcaGestion.put("CodigoCliente", codigoCliente);
                db.insertOrThrow("IPV_ClientesGestionados", null, valuesMarcaGestion);
            }

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("CantidadActividades", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }
    }

    public static Vector<Exhibidores> obtenerListaExhibidores(Vector<ItemListViewExhibidores> listaItems, String codigoCliente) {

        mensaje = "";
        Exhibidores exhibidor;
        SQLiteDatabase db = null;
        String query = "";

        Vector<Exhibidores> listaExhibidores = new Vector<Exhibidores>();

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            query = "SELECT CodigoCliente CodigoCliente, CodigoUsuario CodigoUsuario, ID ID, Nombre Nombre, " +
                    "Ancho Ancho, Alto Alto, codTipoExhibidor codTipoExhibidor, codUbicacion codUbicacion, FechaMovil FechaMovil, " +
                    "CASE WHEN strftime('%Y-%m-%d', FechaMovil) = strftime('%Y-%m-%d', datetime(CURRENT_TIMESTAMP, 'localtime')) THEN 1 ELSE 0 END AS RegistroHoy, " +
                    "CASE WHEN Finalizado IS NULL THEN 0 ELSE Finalizado END AS Finalizado " +
                    "FROM IPV_med_exhibidor_cliente " +
                    "WHERE CodigoCliente = '" + codigoCliente + "'";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                do {

                    exhibidor = new Exhibidores();
                    ItemListViewExhibidores item = new ItemListViewExhibidores();

                    exhibidor.codigoCliente = cursor.getString(cursor.getColumnIndex("CodigoCliente"));
                    exhibidor.codigoUsuario = cursor.getString(cursor.getColumnIndex("CodigoUsuario"));
                    exhibidor.id = cursor.getString(cursor.getColumnIndex("ID"));
                    exhibidor.nombre = cursor.getString(cursor.getColumnIndex("Nombre"));
                    exhibidor.ancho = cursor.getString(cursor.getColumnIndex("Ancho"));
                    exhibidor.alto = cursor.getString(cursor.getColumnIndex("Alto"));
                    exhibidor.codTipoExhibidor = cursor.getString(cursor.getColumnIndex("codTipoExhibidor"));
                    exhibidor.codUbicacion = cursor.getString(cursor.getColumnIndex("codUbicacion"));
                    exhibidor.fechaMovil = cursor.getString(cursor.getColumnIndex("FechaMovil"));
                    exhibidor.registroHoy = cursor.getInt(cursor.getColumnIndex("RegistroHoy"));

                    int finalizado = cursor.getInt(cursor.getColumnIndex("Finalizado"));
                    exhibidor.estaGestionado = (finalizado == 1) ? true : false;

                    item.codigoCliente = exhibidor.codigoCliente;
                    item.codigoUsuario = exhibidor.codigoUsuario;
                    item.id = exhibidor.id;
                    item.nombre = exhibidor.nombre;
                    item.ancho = exhibidor.ancho;
                    item.alto = exhibidor.alto;
                    item.codTipoExhibidor = exhibidor.codTipoExhibidor;
                    item.codUbicacion = exhibidor.codUbicacion;
                    item.fechaMovil = exhibidor.fechaMovil;
                    item.registroHoy = exhibidor.registroHoy;
                    item.estaGestionado = exhibidor.estaGestionado;

                    listaItems.add(item);
                    listaExhibidores.addElement(exhibidor);

                } while (cursor.moveToNext());

                mensaje = "Productos Agotados Cargados Correctamente";

            } else {

                mensaje = "Consulta sin Resultados";
            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("obtenerListaExhibidores", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return listaExhibidores;
    }


    public static Exhibidores obtenerExhibidorMarca(String codigoCliente) {

        mensaje = "";
        Exhibidores exhibidor = null;
        SQLiteDatabase db = null;
        String query = "";

        Vector<Exhibidores> listaExhibidores = new Vector<Exhibidores>();

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            query = "SELECT DISTINCT CodigoCliente CodigoCliente, CodigoUsuario CodigoUsuario, ID ID, Nombre Nombre, " +
                    "Ancho Ancho, Alto Alto, codTipoExhibidor codTipoExhibidor, codUbicacion codUbicacion, FechaMovil FechaMovil, " +
                    "CASE WHEN strftime('%Y-%m-%d', FechaMovil) = strftime('%Y-%m-%d', datetime(CURRENT_TIMESTAMP, 'localtime')) THEN 1 ELSE 0 END AS RegistroHoy, " +
                    "CASE WHEN Finalizado IS NULL THEN 0 ELSE Finalizado END AS Finalizado " +
                    "FROM IPV_med_exhibidor_cliente " +
                    "WHERE CodigoCliente = '" + codigoCliente + "'";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                exhibidor = new Exhibidores();

                exhibidor.codigoCliente = cursor.getString(cursor.getColumnIndex("CodigoCliente"));
                exhibidor.codigoUsuario = cursor.getString(cursor.getColumnIndex("CodigoUsuario"));
                exhibidor.id = cursor.getString(cursor.getColumnIndex("ID"));
                exhibidor.nombre = cursor.getString(cursor.getColumnIndex("Nombre"));
                exhibidor.ancho = cursor.getString(cursor.getColumnIndex("Ancho"));
                exhibidor.alto = cursor.getString(cursor.getColumnIndex("Alto"));
                exhibidor.codTipoExhibidor = cursor.getString(cursor.getColumnIndex("codTipoExhibidor"));
                exhibidor.codUbicacion = cursor.getString(cursor.getColumnIndex("codUbicacion"));
                exhibidor.fechaMovil = cursor.getString(cursor.getColumnIndex("FechaMovil"));
                exhibidor.registroHoy = cursor.getInt(cursor.getColumnIndex("RegistroHoy"));

                int finalizado = cursor.getInt(cursor.getColumnIndex("Finalizado"));
                exhibidor.estaGestionado = (finalizado == 1) ? true : false;

                listaExhibidores.addElement(exhibidor);


                mensaje = "Productos Agotados Cargados Correctamente";

            } else {

                mensaje = "Consulta sin Resultados";
            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("obtenerListaExhibidores", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return exhibidor;
    }

    @SuppressLint("LongLogTag")
    public static Vector<Producto> obtenerListaProductosPropios(String codigo,String idCategoria) {

        mensaje = "";
        Producto productoPropio;
        SQLiteDatabase db = null;
        String query = "";

        Vector<Producto> listaProductosPropios = new Vector<Producto>();

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            query = "SELECT DISTINCT pro.Codigo Codigo, pro.Nombre NombreProducto, " +
                    "CASE WHEN mp.Precio IS NULL THEN 0 ELSE mp.Precio END CantidadAnt, " +
                    "CASE WHEN mpa.Precio IS NULL THEN 0 ELSE mpa.Precio END CantidadAntHoy, " +
                    "ptmp.Precio PrecioCliente, pro.Iva IvaProducto, " +
                    "pro.marca Marca, " +
                    "pro.linea Linea, " +
                    "pro.sublinea Sublinea, " +
                    "pro.categoria Categoria, " +
                    "pro.subcategoria Subcategoria, " +
                    "pro.gm4 GM4, " +
                    "pro.unidadMedida UnidadMedida, " +
                    "pro.portafolio Portafolio,pro.ICUI as icui, pro.IBUA as ibua " +
                    "FROM Core2 co " +
                    "INNER JOIN Productos pro ON co.materia = pro.codigo " +
                    "LEFT JOIN IPV_med_hist_precios mp ON (co.Materia = mp.CodigoProducto AND '" + codigo + "' = mp.CodigoCliente) " +
                    "LEFT JOIN IPV_med_precios mpa ON (co.Materia = mpa.CodigoProducto AND '" + codigo + "' = mpa.CodigoCliente) " +
                    "LEFT JOIN ProductosTmp ptmp ON co.Materia = ptmp.codigo " +
                    "WHERE pro.LineaProduccion = '"+Main.idCategoria+"' "+
                    "and pro.LineaProduccion = '"+idCategoria+"' "+
                    "ORDER BY pro.marcad ASC";


            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                do {

                    productoPropio = new Producto();

                    productoPropio.codigo = cursor.getString(cursor.getColumnIndex("Codigo"));
                    productoPropio.nombre = cursor.getString(cursor.getColumnIndex("NombreProducto"));
                    productoPropio.cantidadAnt = cursor.getInt(cursor.getColumnIndex("CantidadAnt"));
                    productoPropio.cantidadAntHoy = cursor.getInt(cursor.getColumnIndex("CantidadAntHoy"));
                    productoPropio.precioCliente = cursor.getInt(cursor.getColumnIndex("PrecioCliente"));
                    productoPropio.Iva = cursor.getFloat(cursor.getColumnIndex("IvaProducto"));
                    productoPropio.icui = cursor.getFloat(cursor.getColumnIndex("icui"));
                    productoPropio.ibua = cursor.getFloat(cursor.getColumnIndex("ibua"));
                    productoPropio.Marca = cursor.getString(cursor.getColumnIndex("Marca"));
                    productoPropio.Linea = cursor.getString(cursor.getColumnIndex("Linea"));
                    productoPropio.Sublinea = cursor.getString(cursor.getColumnIndex("Sublinea"));
                    productoPropio.Categoria = cursor.getString(cursor.getColumnIndex("Categoria"));
                    productoPropio.Subcategoria = cursor.getString(cursor.getColumnIndex("Subcategoria"));
                    productoPropio.GM4 = cursor.getString(cursor.getColumnIndex("GM4"));
                    productoPropio.UnidadMedida = cursor.getString(cursor.getColumnIndex("UnidadMedida"));
                    productoPropio.Portafolio = cursor.getString(cursor.getColumnIndex("Portafolio"));
                    productoPropio.cantidadAct = -1;
                    productoPropio.esModificado = false;

                    float[] descs;
                    descs = DataBaseBO.calcularDescuentosInventarioProducto(productoPropio, false);
                    productoPropio.Descuento = descs[0];
                    productoPropio.ValorDesc = descs[1];

                    productoPropio.precioCalculado = getPrecioProducto(productoPropio.codigo);

                    if(productoPropio.icui > 0)
                        productoPropio.PrecioFinal = (((productoPropio.precioCalculado * 1) - productoPropio.ValorDesc) * (1 + (productoPropio.Iva / 100))) + (((productoPropio.precioCalculado * 1) - productoPropio.ValorDesc) * ((productoPropio.icui / 100)));
                    else if(productoPropio.ibua > 0)
                        productoPropio.PrecioFinal = (((productoPropio.precioCalculado * 1) - productoPropio.ValorDesc) * (1 + (productoPropio.Iva / 100))) + productoPropio.ibua;
                    else
                        productoPropio.PrecioFinal = ((productoPropio.precioCalculado * 1) - productoPropio.ValorDesc) * (1 + (productoPropio.Iva / 100));

                    // SE AGREGA AJUSTE A LA LOGICA DE PRECIO PARA INCLUIR LA MEDICION DE PRECIO ACTUAL
                    if (productoPropio.cantidadAntHoy > 0) {

                        productoPropio.cantidadAnt = productoPropio.cantidadAntHoy;
                    }

                    listaProductosPropios.addElement(productoPropio);

                } while (cursor.moveToNext());

                mensaje = "Productos Agotados Cargados Correctamente";

            } else {

                mensaje = "Consulta sin Resultados";
            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("obtenerListaProductosPropios", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return listaProductosPropios;
    }

    @SuppressLint("LongLogTag")
    public static Vector<Producto> obtenerListaProductosCompetencia(String codigo, String agencia, String GC4, String idCategoria) {

        mensaje = "";
        Producto productoCompetencia;
        SQLiteDatabase db = null;
        String query = "";
        String canalCliente = Main.cliente.canal;

        Vector<Producto> listaProductosCompetencia = new Vector<Producto>();

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            query = "SELECT DISTINCT codProdCompetencia Codigo, desProdCompetencia NombreProducto, " +
                    "CASE WHEN mp.Precio IS NULL THEN 0 ELSE mp.Precio END CantidadAnt, " +
                    "CASE WHEN mpa.Precio IS NULL THEN 0 ELSE mpa.Precio END CantidadAntHoy " +
                    "FROM ProductosCompetencia po " +
                    "INNER JOIN IPV_Config_ProductosCompetencia icpc ON po.codProdCompetencia = icpc.CodigoProducto " +
                    "LEFT JOIN IPV_med_hist_precios mp ON (po.codProdCompetencia = mp.CodigoProducto AND '" + codigo + "' = mp.CodigoCliente) " +
                    "LEFT JOIN IPV_med_precios mpa ON (po.codProdCompetencia = mpa.CodigoProducto AND '" + codigo + "' = mpa.CodigoCliente ) " +
                    "WHERE icpc.Canal = '" + canalCliente + "' AND icpc.ofiventas = '" + agencia + "' AND icpc.Grupoclientes4 = '" + GC4 + "' " +
//                    "AND mpa.CodigoCliente='"+Main.cliente.codigo+"' "+
//                    "and po.CategoriaTarea = '02' "+
                    "and po.CategoriaTarea = '"+idCategoria+"' "+
                    "ORDER BY CodMarca ASC";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                do {

                    productoCompetencia = new Producto();

                    productoCompetencia.codigo = cursor.getString(cursor.getColumnIndex("Codigo"));
                    productoCompetencia.nombre = cursor.getString(cursor.getColumnIndex("NombreProducto"));
                    productoCompetencia.cantidadAnt = cursor.getInt(cursor.getColumnIndex("CantidadAnt"));
                    productoCompetencia.cantidadAntHoy = cursor.getInt(cursor.getColumnIndex("CantidadAntHoy"));
                    productoCompetencia.cantidadAct = -1;
                    productoCompetencia.esModificado = false;

                    // SE AGREGA AJUSTE A LA LOGICA DE PRECIO PARA INCLUIR LA MEDICION DE PRECIO ACTUAL
                    if (productoCompetencia.cantidadAntHoy > 0) {

                        productoCompetencia.cantidadAnt = productoCompetencia.cantidadAntHoy;
                    }

                    listaProductosCompetencia.addElement(productoCompetencia);

                } while (cursor.moveToNext());

                mensaje = "Productos Agotados Cargados Correctamente";

            } else {

                mensaje = "Consulta sin Resultados";
            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("obtenerListaProductosCompetencia", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return listaProductosCompetencia;
    }


    @SuppressLint("LongLogTag")
    public static Vector<ModuloActivacion> obtenerListaModulosActivacion(boolean propio,String idCategoria ) {

        mensaje = "";
        ModuloActivacion moduloActivacion;
        SQLiteDatabase db = null;
        String query = "";

        Vector<ModuloActivacion> listaModulos = new Vector<ModuloActivacion>();

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            query = "SELECT codigo as codigo, nombre as nombre, orden as orden, propio as propio, competencia as competencia " +
                    "FROM IPV_Config_ModuloActivacion "+
                    "where CategoriaTarea='"+idCategoria+"' ";

            if (propio) {
                query += "and propio=1 ";
            } else {
                query += "and competencia=1 ";
            }
            query += "ORDER BY orden ASC";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                do {

                    moduloActivacion = new ModuloActivacion();

                    moduloActivacion.codigo = cursor.getString(cursor.getColumnIndex("codigo"));
                    moduloActivacion.nombre = cursor.getString(cursor.getColumnIndex("nombre"));
                    moduloActivacion.orden = cursor.getInt(cursor.getColumnIndex("orden"));
                    moduloActivacion.propio = cursor.getInt(cursor.getColumnIndex("propio"));
                    moduloActivacion.competencia = cursor.getInt(cursor.getColumnIndex("competencia"));

                    listaModulos.addElement(moduloActivacion);

                } while (cursor.moveToNext());

                mensaje = "Productos Agotados Cargados Correctamente";

            } else {

                mensaje = "Consulta sin Resultados";
            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("obtenerListaProductosCompetencia", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return listaModulos;
    }

    @SuppressLint("LongLogTag")
    public static Vector<ModuloActivacion> obtenerListaActivacionPropios(boolean propio) {

        mensaje = "";
        ModuloActivacion moduloActivacion;
        SQLiteDatabase db = null;
        String query = "";

        Vector<ModuloActivacion> listaModulos = new Vector<ModuloActivacion>();

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            query = "SELECT a.ID as id, ma.codigo as codigo, ma.nombre as nombre, ma.orden as orden, ma.propio as propio, " +
                    "ma.competencia as competencia, a.Valor1 as respuesta " +
                    "FROM IPV_Config_ModuloActivacion ma " +
                    "inner join IPV_med_activacion a on(ma.codigo=a.TipoOpcion and a.Propio=1 and  a.CodigoCliente='" + Main.cliente.codigo + "') ";

            if (propio) {
                query += "WHERE ma.propio=1 ";
            } else {
                query += "WHERE ma.competencia=1 ";
            }
            query += "ORDER BY orden ASC";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                do {

                    moduloActivacion = new ModuloActivacion();

                    moduloActivacion.id = cursor.getString(cursor.getColumnIndex("id"));
                    moduloActivacion.codigo = cursor.getString(cursor.getColumnIndex("codigo"));
                    moduloActivacion.nombre = cursor.getString(cursor.getColumnIndex("nombre"));
                    moduloActivacion.orden = cursor.getInt(cursor.getColumnIndex("orden"));
                    moduloActivacion.propio = cursor.getInt(cursor.getColumnIndex("propio"));
                    moduloActivacion.competencia = cursor.getInt(cursor.getColumnIndex("competencia"));
                    moduloActivacion.respuesta = cursor.getString(cursor.getColumnIndex("respuesta"));

                    listaModulos.addElement(moduloActivacion);

                } while (cursor.moveToNext());

                mensaje = "Productos Agotados Cargados Correctamente";

            } else {

                mensaje = "Consulta sin Resultados";
            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("obtenerListaProductosCompetencia", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return listaModulos;
    }

    public static boolean guardarActivaciones(String codigoCliente,
                                              Usuario usuario,
                                              int tipoUsuario,
                                              String id,
                                              String fecha,
                                              Vector<ModuloActivacion> listaPropios,
                                              Vector<ActivacionCompetencia> listaCompetencia,
                                              String idCategoria) {

        SQLiteDatabase db = null;
        SQLiteDatabase dbTemp = null;

        try {

            dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            File filePedido = new File(Util.dirApp(), "Temp.db");
            dbTemp = SQLiteDatabase.openDatabase(filePedido.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            /****************************************************************
             * Se almacenan los productos agotados de la medicion por cliente
             ****************************************************************/
            for (int i = 0; i < listaPropios.size(); i++) {

                ContentValues values = new ContentValues();

                values.put("CodigoCliente", codigoCliente);
                values.put("CodigoUsuario", usuario.codigo);
                values.put("NombreUsuario", usuario.nombre);
                values.put("TipoUsuario", tipoUsuario);
                values.put("ID", listaPropios.get(i).id);
                values.put("TipoOpcion", listaPropios.get(i).codigo);
                values.put("Nombre1", listaPropios.get(i).nombre);
                values.put("Valor1", listaPropios.get(i).respuesta.equals("") ? "NO" : listaPropios.get(i).respuesta);
                values.put("CodigoProducto", "");
                values.put("NombreProducto", "");
                values.put("Core", "");
                values.put("Propio", 1);
                values.put("Competencia", 0);
                values.put("FechaMovil", fecha);
                values.put("CategoriaTarea", idCategoria);


                db.insertOrThrow("IPV_med_activacion", null, values);
                dbTemp.insertOrThrow("IPV_med_activacion", null, values);
            }

            for (int i = 0; i < listaCompetencia.size(); i++) {

                String marca = listaCompetencia.get(i).idMarca;
                Vector<ModuloActivacion> listaCompe = listaCompetencia.get(i).listaModulosCompetencia;

                for (int j = 0; j < listaCompe.size(); j++) {

                    ContentValues values = new ContentValues();

                    values.put("CodigoCliente", codigoCliente);
                    values.put("CodigoUsuario", usuario.codigo);
                    values.put("NombreUsuario", usuario.nombre);
                    values.put("TipoUsuario", tipoUsuario);
                    values.put("ID", listaCompe.get(j).id);
                    values.put("TipoOpcion", listaCompe.get(j).codigo);
                    values.put("Nombre1", listaCompe.get(j).nombre);
                    values.put("Valor1", listaCompe.get(j).respuesta.equals("") ? "NO" : listaCompe.get(j).respuesta);
                    values.put("CodigoProducto", marca);
                    values.put("NombreProducto", "");
                    values.put("Core", "");
                    values.put("Propio", 0);
                    values.put("Competencia", 1);
                    values.put("FechaMovil", fecha);
                    values.put("CategoriaTarea", idCategoria);

                    db.insertOrThrow("IPV_med_activacion", null, values);
                    dbTemp.insertOrThrow("IPV_med_activacion", null, values);
                }
            }

            /**
             // SE ALMACENA UN REGISTRO DE MARCA PARA EL CLIENTE GESTIONADO
             ContentValues valuesMarcaGestion = new ContentValues();
             valuesMarcaGestion.put("CodigoCliente", codigoCliente);
             db.insertOrThrow("IPV_ClientesGestionados", null, valuesMarcaGestion);
             sdsd:
             **/

            return true;

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("DataBaseBO", "RegistrarProductoPedidos: " + mensaje, e);
            return false;

        } finally {

            if (db != null)
                db.close();

            if (dbTemp != null)
                dbTemp.close();

            validarAgregarGestionCliente(codigoCliente);
        }
    }


    public static boolean guardarDetalleExhibidor(String idEncabezado, String id, Usuario usuario, Exhibidores exhibidor, Exhibidores estado,
                                                  String fecha) {

        SQLiteDatabase db = null;
        SQLiteDatabase dbTemp = null;

        try {

            dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            File filePedido = new File(Util.dirApp(), "Temp.db");
            dbTemp = SQLiteDatabase.openDatabase(filePedido.getPath(), null, SQLiteDatabase.OPEN_READWRITE);


            ContentValues values = new ContentValues();

            values.put("IDEncabezado", idEncabezado);
            values.put("IDDetalle", id);
            values.put("CodigoUsuario", usuario.codigo);
            values.put("codigoExhibicion", exhibidor.id);
            values.put("nombreExhibicion", exhibidor.nombre);
            values.put("codigoEstado", estado.id);
            values.put("nombreEstado", estado.nombre);
            values.put("codigocliente", Main.cliente.codigo);
            values.put("FechaMovil", fecha);


            db.insertOrThrow("IPV_med_DetalleActivacion", null, values);
            dbTemp.insertOrThrow("IPV_med_DetalleActivacion", null, values);


            return true;

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("DataBaseBO", "RegistrarProductoPedidos: " + mensaje, e);
            return false;

        } finally {

            if (db != null)
                db.close();

            if (dbTemp != null)
                dbTemp.close();

        }
    }


    public static boolean guardarProductosPrecios(String codigoCliente,
                                                  Usuario usuario,
                                                  int tipoUsuario,
                                                  String id,
                                                  String fecha,
                                                  Vector<Producto> listaProductosPropios,
                                                  Vector<Producto> listaProductosCompetencia,
                                                  String idCategoria) {

        SQLiteDatabase db = null;
        SQLiteDatabase dbTemp = null;

        try {

            dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            File filePedido = new File(Util.dirApp(), "Temp.db");
            dbTemp = SQLiteDatabase.openDatabase(filePedido.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            /****************************************************************
             * Se almacenan los productos agotados de la medicion por cliente
             ****************************************************************/
            for (int i = 0; i < listaProductosPropios.size(); i++) {

                ContentValues values = new ContentValues();

                values.put("CodigoCliente", codigoCliente);
                values.put("CodigoUsuario", usuario.codigo);
                values.put("NombreUsuario", usuario.nombre);
                values.put("TipoUsuario", tipoUsuario);
                values.put("ID", id);
                values.put("CodigoProducto", listaProductosPropios.elementAt(i).codigo);
                values.put("NombreProducto", listaProductosPropios.elementAt(i).nombre);
                values.put("Precio", listaProductosPropios.elementAt(i).cantidadAct);
                //values.put("PrecioV",        listaProductosPropios.elementAt(i).precioCliente);
                String aux=Util.numeroSinDecimal(String.valueOf(listaProductosPropios.elementAt(i).PrecioFinal));
                values.put("PrecioV",aux);
                values.put("Core", 1);
                values.put("Propio", 1);
                values.put("Competencia", 0);
                values.put("FechaMovil", fecha);
                values.put("CategoriaTarea", idCategoria);

                db.insertOrThrow("IPV_med_precios", null, values);
                dbTemp.insertOrThrow("IPV_med_precios", null, values);
            }

            for (int i = 0; i < listaProductosCompetencia.size(); i++) {

                ContentValues values = new ContentValues();

                values.put("CodigoCliente", codigoCliente);
                values.put("CodigoUsuario", usuario.codigo);
                values.put("NombreUsuario", usuario.nombre);
                values.put("TipoUsuario", tipoUsuario);
                values.put("ID", id);
                values.put("CodigoProducto", listaProductosCompetencia.elementAt(i).codigo);
                values.put("NombreProducto", listaProductosCompetencia.elementAt(i).nombre);
                values.put("Precio", listaProductosCompetencia.elementAt(i).cantidadAct);
                values.put("Core", 0);
                values.put("Propio", 0);
                values.put("Competencia", 1);
                values.put("FechaMovil", fecha);
                values.put("CategoriaTarea", idCategoria);

                db.insertOrThrow("IPV_med_precios", null, values);
                dbTemp.insertOrThrow("IPV_med_precios", null, values);
            }

            /**
             // SE ALMACENA UN REGISTRO DE MARCA PARA EL CLIENTE GESTIONADO
             ContentValues valuesMarcaGestion = new ContentValues();
             valuesMarcaGestion.put("CodigoCliente", codigoCliente);
             db.insertOrThrow("IPV_ClientesGestionados", null, valuesMarcaGestion);
             sdsd:
             **/

            return true;

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("DataBaseBO", "RegistrarProductoPedidos: " + mensaje, e);
            return false;

        } finally {

            if (db != null)
                db.close();

            if (dbTemp != null)
                dbTemp.close();

            validarAgregarGestionCliente(codigoCliente);
        }
    }

    public static Vector<TipoExhibidor> obtenerListaTipoExhibidor(Vector<String> listaItems, String canal) {

        String query = "";
        SQLiteDatabase db = null;
        Vector<TipoExhibidor> listaTipoExhibidor = new Vector<>();

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            query = "SELECT codigo Codigo, descripcion Descripcion FROM Exhibidores WHERE codCanal = '" + canal + "'";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                do {

                    TipoExhibidor tipoExhibidor = new TipoExhibidor();

                    tipoExhibidor.codigoTipo = cursor.getString(cursor.getColumnIndex("Codigo"));
                    tipoExhibidor.descripcionTipo = cursor.getString(cursor.getColumnIndex("Descripcion"));

                    listaItems.addElement(tipoExhibidor.descripcionTipo);
                    listaTipoExhibidor.addElement(tipoExhibidor);

                } while (cursor.moveToNext());
            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "listaEstadosEntrega -> " + mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return listaTipoExhibidor;
    }

    public static Vector<UbicacionExhibidor> obtenerListaUbicacionExhibidor(Vector<String> listaItems) {

        String query = "";
        SQLiteDatabase db = null;
        Vector<UbicacionExhibidor> listaUbicacionExhibidor = new Vector<>();

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            query = "SELECT codUbicacion Codigo, descripcion Descripcion FROM IPV_UbicacionExhibidores";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                do {

                    UbicacionExhibidor ubicacionExhibidor = new UbicacionExhibidor();

                    ubicacionExhibidor.codigoUbicacion = cursor.getString(cursor.getColumnIndex("Codigo"));
                    ubicacionExhibidor.descripcionUbicacion = cursor.getString(cursor.getColumnIndex("Descripcion"));

                    listaItems.addElement(ubicacionExhibidor.descripcionUbicacion);
                    listaUbicacionExhibidor.addElement(ubicacionExhibidor);

                } while (cursor.moveToNext());
            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "listaEstadosEntrega -> " + mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return listaUbicacionExhibidor;
    }

    public static boolean guardarExhibidorNuevo(String codigoCliente, Usuario usuario, String id, String fecha, Exhibidores exhibidorCreado, int tipoUsuario,String idCategoria) {

        SQLiteDatabase db = null;
        SQLiteDatabase dbTemp = null;

        try {

            dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            File filePedido = new File(Util.dirApp(), "Temp.db");
            dbTemp = SQLiteDatabase.openDatabase(filePedido.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            /****************************************************************
             * Se almacenan los productos agotados de la medicion por cliente
             ****************************************************************/
            ContentValues valuesExhibidor = new ContentValues();

            valuesExhibidor.put("CodigoCliente", codigoCliente);
            valuesExhibidor.put("CodigoUsuario", usuario.codigo);
            valuesExhibidor.put("ID", id);
            valuesExhibidor.put("Nombre", exhibidorCreado.nombre);
            valuesExhibidor.put("Ancho", exhibidorCreado.ancho);
            valuesExhibidor.put("Alto", exhibidorCreado.alto);
            valuesExhibidor.put("codTipoExhibidor", exhibidorCreado.codigoTipoExhibidor);
            valuesExhibidor.put("codUbicacion", exhibidorCreado.codigoUbicacionExhibidor);
            valuesExhibidor.put("FechaMovil", fecha);

            db.insertOrThrow("IPV_med_exhibidor_cliente", null, valuesExhibidor);
            dbTemp.insertOrThrow("IPV_med_exhibidor_cliente", null, valuesExhibidor);

            /****************************************************** Encabezado */
            ContentValues valuesExhibidorEncabezado = new ContentValues();

            valuesExhibidorEncabezado.put("CodigoCliente", codigoCliente);
            valuesExhibidorEncabezado.put("CodigoUsuario", usuario.codigo);
            valuesExhibidorEncabezado.put("NombreUsuario", usuario.nombre);
            valuesExhibidorEncabezado.put("TipoUsuario", tipoUsuario);
            valuesExhibidorEncabezado.put("ID", id);
            valuesExhibidorEncabezado.put("IDExhibidorCliente", id);
            valuesExhibidorEncabezado.put("FechaMovil", fecha);
            valuesExhibidorEncabezado.put("CategoriaTarea", idCategoria);

            db.insertOrThrow("IPV_med_exhibidor_encabezado", null, valuesExhibidorEncabezado);
            dbTemp.insertOrThrow("IPV_med_exhibidor_encabezado", null, valuesExhibidorEncabezado);

            return true;

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("DataBaseBO", "RegistrarExhibidorNuevo: " + mensaje, e);
            return false;

        } finally {

            if (db != null)
                db.close();

            if (dbTemp != null)
                dbTemp.close();

            validarAgregarGestionCliente(codigoCliente);
        }
    }

    public static void eliminarExhibidor(String idExhibidor) {

        SQLiteDatabase db = null;
        SQLiteDatabase dbTemp = null;

        try {

            dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            File filePedido = new File(Util.dirApp(), "Temp.db");
            dbTemp = SQLiteDatabase.openDatabase(filePedido.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "DELETE FROM IPV_med_exhibidor_cliente WHERE ID = '" + idExhibidor + "'";
            String queryEnc = "DELETE FROM IPV_med_exhibidor_encabezado WHERE ID = '" + idExhibidor + "'";
            db.execSQL(query);
            db.execSQL(queryEnc);
            dbTemp.execSQL(query);
            dbTemp.execSQL(queryEnc);

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "Eliminar Exhibidor: " + mensaje, e);

        } finally {

            if (db != null)
                db.close();

            if (dbTemp != null)
                dbTemp.close();
        }
    }

    public static void eliminarExhibidorCompleto(String idExhibidor, String codigoCliente, String fecha, String codigoUsuario) {

        SQLiteDatabase db = null;
        SQLiteDatabase dbTemp = null;

        try {

            dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            File filePedido = new File(Util.dirApp(), "Temp.db");
            dbTemp = SQLiteDatabase.openDatabase(filePedido.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "DELETE FROM IPV_med_exhibidor_cliente WHERE ID = '" + idExhibidor + "'";
            String queryEnc = "DELETE FROM IPV_med_exhibidor_encabezado WHERE ID = '" + idExhibidor + "'";
            String queryDet = "DELETE FROM IPV_med_exhibidor_detalle WHERE ID = '" + idExhibidor + "'";
            db.execSQL(query);
            db.execSQL(queryEnc);
            db.execSQL(queryDet);
            dbTemp.execSQL(query);
            dbTemp.execSQL(queryEnc);
            dbTemp.execSQL(queryDet);

            // SE INSERTA EL REGISTRO DE ELIMINACION EN LA TABLA DE BORADO DE LOS EXHIBIDORES
            ContentValues valuesMarcaExhibidorEliminado = new ContentValues();

            valuesMarcaExhibidorEliminado.put("CodigoCliente", codigoCliente);
            valuesMarcaExhibidorEliminado.put("CodigoUsuario", codigoUsuario);
            valuesMarcaExhibidorEliminado.put("ID", String.valueOf(idExhibidor));
            valuesMarcaExhibidorEliminado.put("FechaMovil", fecha);

            db.insertOrThrow("IPV_med_exhibidor_cliente_eliminados", null, valuesMarcaExhibidorEliminado);
            dbTemp.insertOrThrow("IPV_med_exhibidor_cliente_eliminados", null, valuesMarcaExhibidorEliminado);

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "Eliminar Exhibidor: " + mensaje, e);

        } finally {

            if (db != null)
                db.close();

            if (dbTemp != null)
                dbTemp.close();
        }
    }

    public static Exhibidores obtenerExhibidor(String id) {

        mensaje = "";
        Exhibidores exhibidor = new Exhibidores();
        SQLiteDatabase db = null;

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT CodigoCliente CodigoCliente, CodigoUsuario CodigoUsuario, ID ID, Nombre Nombre, " +
                    "Ancho Ancho, Alto Alto, codTipoExhibidor codTipoExhibidor, codUbicacion codUbicacion, FechaMovil FechaMovil " +
                    "FROM IPV_med_exhibidor_cliente " +
                    "WHERE ID = '" + id + "'";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                exhibidor.codigoCliente = cursor.getString(cursor.getColumnIndex("CodigoCliente"));
                exhibidor.codigoUsuario = cursor.getString(cursor.getColumnIndex("CodigoUsuario"));
                exhibidor.id = cursor.getString(cursor.getColumnIndex("ID"));
                exhibidor.nombre = cursor.getString(cursor.getColumnIndex("Nombre"));
                exhibidor.ancho = cursor.getString(cursor.getColumnIndex("Ancho"));
                exhibidor.alto = cursor.getString(cursor.getColumnIndex("Alto"));
                exhibidor.codTipoExhibidor = cursor.getString(cursor.getColumnIndex("codTipoExhibidor"));
                exhibidor.codUbicacion = cursor.getString(cursor.getColumnIndex("codUbicacion"));
                exhibidor.fechaMovil = cursor.getString(cursor.getColumnIndex("FechaMovil"));
            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("obtenerExhibidor", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return exhibidor;
    }

    public static boolean guardarImagen(Foto foto, byte[] image) {

        SQLiteDatabase db = null;
        SQLiteDatabase dbTemp = null;

        try {

            dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            File filePedido = new File(Util.dirApp(), "Temp.db");
            dbTemp = SQLiteDatabase.openDatabase(filePedido.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            ContentValues values = new ContentValues();

            values.put("id", foto.id);
            values.put("Imagen", image);
            values.put("CodCliente", foto.codigoCliente);
            values.put("CodVendedor", foto.codigoVendedor);
            values.put("Modulo", foto.modulo);
            values.put("fecha", foto.fecha);
            values.put("IdFoto", foto.idFoto);

            db.insertOrThrow("Fotos", null, values);
            dbTemp.insertOrThrow("Fotos", null, values);

            return true;

        } catch (Exception e) {

            mensaje = e.getMessage();
            return false;

        } finally {

            if (db != null)
                db.close();

            if (dbTemp != null)
                dbTemp.close();
        }
    }

    @SuppressLint("LongLogTag")
    public static boolean borrarImagen(String idImagen) {

        SQLiteDatabase db = null;
        SQLiteDatabase dbTemp = null;

        try {

            dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            File filePedido = new File(Util.dirApp(), "Temp.db");
            dbTemp = SQLiteDatabase.openDatabase(filePedido.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("DELETE FROM Fotos WHERE IdFoto = '" + idImagen + "'");
            dbTemp.execSQL("DELETE FROM Fotos WHERE IdFoto = '" + idImagen + "'");
            db.execSQL("VACUUM");
            dbTemp.execSQL("VACUUM");

            mensaje = "Imagen borrada con exito";
            return true;

        } catch (Exception e) {

            mensaje = "Error cargando Imagen: " + e.getMessage();
            Log.e("DataBaseBO - BorrarImagen", mensaje, e);
            return false;

        } finally {

            if (db != null)
                db.close();

            if (dbTemp != null)
                dbTemp.close();
        }
    }

    public static byte[] cargarImagen(String idImagen) {

        mensaje = "";
        byte[] image = null;

        SQLiteDatabase db = null;

        try {

            File dbFile = new File(Util.dirApp(), "Temp.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            Cursor cursor = db.query("Fotos",
                    new String[]{"Imagen"},
                    "IdFoto = '" + idImagen + "'",
                    null,
                    null,
                    null,
                    null);

            if (cursor.moveToFirst()) {

                image = cursor.getBlob(cursor.getColumnIndex("Imagen"));
                mensaje = "Imagen cargada correctamente";
            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = "Error cargando Imagen: " + e.getMessage();

        } finally {

            if (db != null)
                db.close();
        }

        return image;
    }

    //************************************************** CORE **************************************************//
    //**********************************************************************************************************//
    //**********************************************************************************************************//
    //**********************************************************************************************************//
    public static void portafolio_core_3(String vendedor, String distrito,
                                         String codCliente, String tipologia, String codDane, String Gc2,
                                         String Gc3, String Gc4) {

        String sql = "";

        SQLiteDatabase db = null;

        String secuencia = "0";
        String condicionCore = ""; // Productos Core
        String condicionInno = ""; // Productos Innovacion
        String condicionComp = ""; // Productos Complementarios
        int regsCore, prioridad;

        Main.totalCore = 0;

        try {

            //Se registra el Log de la consulta SQl Registrada
            ArrayList<String> querys = new ArrayList<String>();

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null,
                    SQLiteDatabase.OPEN_READWRITE);

            sql = "select max(secuencia) as secuencia1 ,count(1) as cont ,prioridad from core  WHERE ("
                    + " (cliente='" + codCliente + "')"
                    + " or (tipologia='" + tipologia + "' and ciudad='" + codDane + "')"
                    + " or (tipologia='" + tipologia + "' and distrito='" + distrito + "' and vendedor='" + vendedor + "')"
                    + " or (tipologia='" + tipologia + "' and distrito='" + distrito + "' and secuencia='778')"
                    + " or (tipologia='" + tipologia + "'  and secuencia='754')"
                    + " or (grupoclientes2='" + Gc2 + "' and grupoclientes3='" + Gc3 + "' and grupoclientes4='" + Gc4 + "')"
                    + " or (grupoclientes3='" + Gc3 + "' and grupoclientes4='" + Gc4 + "' and secuencia='787')"
                    + " or (grupoclientes4='" + Gc4 + "' and grupoclientes2='" + Gc2 + "' and secuencia='785')"
                    + " or (grupoclientes4='" + Gc4 + "' and grupoclientes2='" + Gc2 + "' and secuencia='828')"
                    + " or (grupoclientes4='" + Gc4 + "' and secuencia='784')"
                    + " or secuencia='702') "
                    + " and tabla='ZCOR' group by secuencia,prioridad order by prioridad";

            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst()) {

                secuencia = cursor.getString(cursor.getColumnIndex("secuencia1"));
                regsCore = cursor.getInt(cursor.getColumnIndex("cont"));
                Main.totalCore = regsCore;
                prioridad = cursor.getInt(cursor.getColumnIndex("prioridad"));
            }

            switch (Integer.parseInt(secuencia)) {

                case 701:
                    condicionCore = "(tabla='ZCOR' and core.secuencia = '"
                            + secuencia + "' and cliente = '" + codCliente
                            + "') ";
                    break;

                case 702:
                    condicionCore = "(tabla='ZCOR' and core.secuencia = '"
                            + secuencia + "') ";
                    break;

                case 784:
                    condicionCore = "(tabla='ZCOR' and core.secuencia = '"
                            + secuencia + "' and grupoclientes4 = '" + Gc4
                            + "') ";
                    break;

                case 785:
                    condicionCore = "(tabla='ZCOR' and core.secuencia = '"
                            + secuencia + "' and grupoclientes4 = '" + Gc4
                            + "' and  grupoclientes2 = '" + Gc2 + "' ) ";
                    break;

                case 828:
                    condicionCore = "(tabla='ZCOR' and core.secuencia = '"
                            + secuencia + "' and grupoclientes4 = '" + Gc4
                            + "' and  grupoclientes2 = '" + Gc2 + "' ) ";
                    break;

                case 786:
                    condicionCore = "(tabla='ZCOR' and core.secuencia = '"
                            + secuencia + "' and grupoclientes4 = '" + Gc4
                            + "' and  grupoclientes2 = '" + Gc2
                            + "' and  grupoclientes3 = '" + Gc3 + "' )";
                    break;

                case 787:
                    condicionCore = "(tabla='ZCOR' and core.secuencia = '"
                            + secuencia + "' and grupoclientes4 = '" + Gc4
                            + "' and  grupoclientes3 = '" + Gc3 + "' ) ";
                    break;

                case 907:
                    condicionCore = "(tabla='ZCOR' and core.secuencia = '"
                            + secuencia + "' and tipologia = '" + tipologia
                            + "' and ciudad = '" + codDane + "') ";
                    break;

                default:
                    condicionCore = "(tabla='ZCOR' and core.secuencia = '"
                            + secuencia + "' and tipologia = '" + tipologia
                            + "') ";
                    break;

            }

            secuencia = "0";

            sql = "select max(secuencia) as secuencia1, count(1) as cont, prioridad from core  WHERE ( "
                    + "(cliente = '" + codCliente + "') "
                    + "or (tipologia = '" + tipologia + "' and ciudad = '" + codDane + "') "
                    + "or (tipologia = '" + tipologia + "' and distrito = '" + distrito + "' and vendedor = '" + vendedor + "') "
                    + "or (tipologia = '" + tipologia + "' and distrito = '" + distrito + "' and secuencia = '778') "
                    + "or (tipologia = '" + tipologia + "'  and secuencia = '754') "
                    + "or (grupoclientes2 = '" + Gc2 + "' and grupoclientes3 = '" + Gc3 + "' and grupoclientes4 = '" + Gc4 + "') "
                    + "or (grupoclientes3 = '" + Gc3 + "' and grupoclientes4 = '" + Gc4 + "' and secuencia = '787') "
                    + "or (grupoclientes4 = '" + Gc4 + "' and grupoclientes2 = '" + Gc3 + "' and secuencia = '785') "
                    + "or (grupoclientes4 = '" + Gc4 + "' and grupoclientes2 = '" + Gc3 + "' and secuencia = '828') "
                    + "or (grupoclientes4 = '" + Gc4 + "' and secuencia = '784') " + ") "
                    + "and tabla='ZINN' group by secuencia, prioridad order by prioridad";

            cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst()) {

                secuencia = cursor.getString(cursor.getColumnIndex("secuencia1"));
                regsCore = cursor.getInt(cursor.getColumnIndex("cont"));
                prioridad = cursor.getInt(cursor.getColumnIndex("prioridad"));
            }

            querys.add(sql);

            switch (Integer.parseInt(secuencia)) {

                case 701:
                    condicionInno = "(tabla='ZINN' and core.secuencia = '"
                            + secuencia + "' and cliente = '" + codCliente
                            + "') ";
                    break;

                case 784:
                    condicionInno = "(tabla='ZINN' and core.secuencia = '"
                            + secuencia + "' and grupoclientes4 = '" + Gc4
                            + "') ";
                    break;

                case 785:
                    condicionInno = "(tabla='ZINN' and core.secuencia = '"
                            + secuencia + "' and grupoclientes4 = '" + Gc4
                            + "' and  grupoclientes2 = '" + Gc2 + "' ) ";
                    break;

                case 828:
                    condicionInno = "(tabla='ZINN' and core.secuencia = '"
                            + secuencia + "' and grupoclientes4 = '" + Gc4
                            + "' and  grupoclientes2 = '" + Gc2 + "' ) ";
                    break;

                case 786:
                    condicionInno = "(tabla='ZINN' and core.secuencia = '"
                            + secuencia + "' and grupoclientes4 = '" + Gc4
                            + "' and  grupoclientes2 = '" + Gc2
                            + "' and  grupoclientes3 = '" + Gc3 + "' ) ";
                    break;

                case 787:
                    condicionInno = "(tabla='ZINN' and core.secuencia = '"
                            + secuencia + "' and grupoclientes4 ='" + Gc4
                            + "'  and  grupoclientes3 ='" + Gc3 + "' ) ";
                    break;

                case 903:
                    condicionInno = "(tabla='ZINN' and core.secuencia = '"
                            + secuencia + "' and tipologia = '" + tipologia
                            + "' and ciudad = '" + codDane + "') ";
                    break;

                default:
                    condicionInno = "(tabla='ZINN' and core.secuencia = '"
                            + secuencia + "' and tipologia = '" + tipologia
                            + "') ";
                    break;

            }

            sql = "select max(secuencia) as secuencia1, count(1) as cont, prioridad from core  WHERE ( "
                    + "(cliente = '" + codCliente + "') "
                    + "or (tipologia = '" + tipologia + "' and ciudad = '" + codDane + "') "
                    + "or (tipologia = '" + tipologia + "' and distrito = '" + distrito + "' and vendedor = '" + vendedor + "') "
                    + "or (tipologia = '" + tipologia + "' and distrito = '" + distrito + "' and secuencia = '778') "
                    + "or (tipologia = '" + tipologia + "'  and secuencia = '754') "
                    + "or (grupoclientes2 = '" + Gc2 + "' and grupoclientes3 = '" + Gc3 + "' and grupoclientes4 = '" + Gc4 + "') "
                    + "or (grupoclientes3 = '" + Gc3 + "' and grupoclientes4 = '" + Gc4 + "' and secuencia = '787') "
                    + "or (grupoclientes4 = '" + Gc4 + "' and grupoclientes2 = '" + Gc2 + "' and secuencia = '785') "
                    + "or (grupoclientes4 = '" + Gc4 + "' and grupoclientes2 = '" + Gc2 + "' and secuencia = '828') "
                    + "or (grupoclientes4 = '" + Gc4 + "' and secuencia = '784') " + ") "
                    + "and tabla='ZCOM' group by secuencia, prioridad order by prioridad";

            cursor = db.rawQuery(sql, null);
            querys.add(sql);

            secuencia = "0";

            if (cursor.moveToFirst()) {

                secuencia = cursor.getString(cursor.getColumnIndex("secuencia1"));
                regsCore = cursor.getInt(cursor.getColumnIndex("cont"));
                prioridad = cursor.getInt(cursor.getColumnIndex("prioridad"));
            }

            switch (Integer.parseInt(secuencia)) {

                case 701:
                    condicionComp = "(tabla='ZCOM' and core.secuencia = '"
                            + secuencia + "' and cliente = '" + codCliente
                            + "') ";
                    break;

                case 784:
                    condicionComp = "(tabla='ZCOM' and core.secuencia = '"
                            + secuencia + "' and grupoclientes4 = '" + Gc4
                            + "') ";
                    break;

                case 785:
                    condicionComp = "(tabla='ZCOM' and core.secuencia = '"
                            + secuencia + "' and grupoclientes4 = '" + Gc4
                            + "' and  grupoclientes2 = '" + Gc2 + "' ) ";
                    break;

                case 828:
                    condicionComp = "(tabla='ZCOM' and core.secuencia = '"
                            + secuencia + "' and grupoclientes4 = '" + Gc4
                            + "' and  grupoclientes2 = '" + Gc2 + "' ) ";
                    break;

                case 786:
                    condicionComp = "(tabla='ZCOM' and core.secuencia = '"
                            + secuencia + "' and grupoclientes4 = '" + Gc4
                            + "' and  grupoclientes2 = '" + Gc2
                            + "' and  grupoclientes3 = '" + Gc3 + "' ) ";
                    break;

                case 787:
                    condicionComp = "(tabla='ZCOM' and core.secuencia = '"
                            + secuencia + "' and grupoclientes4 = '" + Gc4
                            + "' and  grupoclientes3 ='" + Gc3 + "' ) ";
                    break;

                case 903:
                    condicionComp = "(tabla='ZCOM' and core.secuencia = '"
                            + secuencia + "' and tipologia = '" + tipologia
                            + "' and ciudad = '" + codDane + "') ";
                    break;

                default:
                    condicionComp = "(tabla='ZCOM' and core.secuencia = '"
                            + secuencia + "' and tipologia = '" + tipologia
                            + "') ";
                    break;
            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();
        }

        portafolio_core_2(condicionCore, condicionInno, condicionComp);
    }

    public static void portafolio_core_2(String condicionCore,
                                         String condicionInno,
                                         String condicionComp) {

        SQLiteDatabase db = null;
        String enlace = "";
        String sql;

        try {

            //Se registra el Log de la consulta SQl Registrada
            ArrayList<String> querys = new ArrayList<String>();

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            sql = "delete from core2";
            db.execSQL(sql);
            querys.add(sql);

            if (enlace.equals("")) {

                sql = "update productos set core='002'";
                db.execSQL(sql);

            } else {

                sql = "update productos set core='000'";
                db.execSQL(sql);
            }

            querys.add(sql);

            sql = "update productos set core = '002' where portafolio  in ( select portafolio from enlaces WHERE enlace = '" + enlace + "') or  portafolio = '999' ";
            db.execSQL(sql);
            querys.add(sql);

            if (!condicionCore.equals("")) {

                sql = "insert into core2 ";
                sql += "select distinct materia, '004', ordenamiento, ordenprod from core where " + condicionCore;
                db.execSQL(sql);
                querys.add(sql);

                sql = " select count(1) as total from core where " + condicionCore;
                querys.add(sql);

                Cursor cursor = db.rawQuery(sql, null);

                if (cursor.moveToFirst()) {

                    Main.totalCore = cursor.getInt(cursor.getColumnIndex("total"));
                }

                if (cursor != null)
                    cursor.close();
            }

            if (!condicionInno.equals("")) {

                sql = "insert into core2 ";
                sql += "select distinct materia, '003', ordenamiento, ordenprod from core where " + condicionInno;
                db.execSQL(sql);
                querys.add(sql);
            }

            if (!condicionComp.equals("")) {

                sql = "insert into core2 ";
                sql += "select distinct  materia, '002', ordenamiento, ordenprod from core  where " + condicionComp;
                db.execSQL(sql);
                querys.add(sql);
            }

            sql = "Delete from core2 where materia in (select codigocore from corealterno) and core='004'";
            db.execSQL(sql);

            sql = "insert into core2 " + "select codigo,'004',ordenamiento,ordenprod from core inner join corealterno on core.materia=corealterno.codigocore where  " + condicionCore;
            db.execSQL(sql);

            sql = "insert into core2 ";
            sql += "SELECT distinct codigo, '002', 3, 0  FROM productos  LEFT JOIN core2 ON productos.codigo = core2.materia WHERE core2.materia IS NULL AND productos.CORE = '002'";
            db.execSQL(sql);
            querys.add(sql);

            sql = "Delete from Core2 WHere Materia IN (select codigo from productos where cen_ext2=1)";
            db.execSQL(sql);
            querys.add(sql);

            sql = "DELETE FROM core2 WHERE Materia IN (	SELECT Codigo FROM Productos WHERE core='000' ) ";
            db.execSQL(sql);
            querys.add(sql);

        } catch (Exception e) {

            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();
        }

        // SE AGREGA LA NUEVA LOGICA PARA EL PORTAFOLIO CORE CON LA NUEVA TABLA "IPV_Config_ProductosCore"
        portafolioCoreFinal();
    }

    private static void portafolioCoreFinal() {

        Cliente clienteInformacionCore = new Cliente();
        clienteInformacionCore.canal = Main.cliente.canal;
        clienteInformacionCore.agencia = Main.cliente.agencia;
        clienteInformacionCore.GC2 = Main.cliente.GC2;
        clienteInformacionCore.GC4 = Main.cliente.GC4;

        SQLiteDatabase db = null;

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT c2.Materia Materia, c2.core core, c2.Ordenamiento Ordenamiento, c2.Ordenprod Ordenprod " +
                    "FROM IPV_Config_ProductosCore icpc " +
                    "INNER JOIN Core2 c2 ON icpc.CodigoProducto = c2.Materia " +
                    "WHERE icpc.Canal = '" + clienteInformacionCore.canal + "' AND icpc.ofiventas = '" + clienteInformacionCore.agencia + "' " +
                    "AND icpc.Grupoclientes2 = '" + clienteInformacionCore.GC2 + "' AND icpc.Grupoclientes4 = '" + clienteInformacionCore.GC4 + "'";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                // SE ELIMINAN LOS REGISTROS ANTERIORES DE CORE2
                db.execSQL("DELETE FROM Core2");

                do {

                    ContentValues core2 = new ContentValues();

                    core2.put("Materia", cursor.getString(cursor.getColumnIndex("Materia")));
                    core2.put("core", cursor.getString(cursor.getColumnIndex("core")));
                    core2.put("Ordenamiento", cursor.getInt(cursor.getColumnIndex("Ordenamiento")));
                    core2.put("Ordenprod", cursor.getInt(cursor.getColumnIndex("Ordenprod")));

                    db.insertOrThrow("Core2", null, core2);

                } while (cursor.moveToNext());

            } else {

                // SE ELIMINAN LOS REGISTROS ANTERIORES DE CORE2
                db.execSQL("DELETE FROM Core2");
            }

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("TablaFinalCore2", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }
    }
    //**********************************************************************************************************//
    //**********************************************************************************************************//
    //**********************************************************************************************************//

    public static int hayFotosMedicionActual(String idExhibidor) {

        int cantidadFotos = 0;
        SQLiteDatabase db = null;

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT COUNT(*) CantidadFotos FROM Fotos WHERE id = '" + idExhibidor + "'";
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                cantidadFotos = cursor.getInt(cursor.getColumnIndex("CantidadFotos"));
            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("ObtenerCantidadFotos", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return cantidadFotos;
    }

    public static void actualizarMedicionExhibidorActual_Fotos(int bandera, String idExhibidor, ExhibidorEncabezado exhibidorEncabezadoHistorico,String idCategoria) {

        SQLiteDatabase db = null;
        SQLiteDatabase dbTemp = null;

        try {

            dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            File filePedido = new File(Util.dirApp(), "Temp.db");
            dbTemp = SQLiteDatabase.openDatabase(filePedido.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("UPDATE IPV_med_exhibidor_encabezado SET isFoto = '" + bandera + "' WHERE ID = '" + idExhibidor + "'");

            /****************************************************** Encabezado */
            ContentValues valuesExhibidorEncabezado = new ContentValues();

            valuesExhibidorEncabezado.put("CodigoCliente", exhibidorEncabezadoHistorico.codigoCliente);
            valuesExhibidorEncabezado.put("CodigoUsuario", exhibidorEncabezadoHistorico.codigoUsuario);
            valuesExhibidorEncabezado.put("NombreUsuario", exhibidorEncabezadoHistorico.nombreUsuario);
            valuesExhibidorEncabezado.put("TipoUsuario", exhibidorEncabezadoHistorico.tipoUsuario);
            valuesExhibidorEncabezado.put("ID", exhibidorEncabezadoHistorico.id);
            valuesExhibidorEncabezado.put("IDExhibidorCliente", exhibidorEncabezadoHistorico.idExhibidorCliente);
            valuesExhibidorEncabezado.put("FechaMovil", exhibidorEncabezadoHistorico.fechaMovil);
            valuesExhibidorEncabezado.put("isFoto", bandera);
            valuesExhibidorEncabezado.put("CategoriaTarea", idCategoria);
            valuesExhibidorEncabezado.put("IDDetalle", idDetalleMedicion);

            dbTemp.insertOrThrow("IPV_med_exhibidor_encabezado", null, valuesExhibidorEncabezado);

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("DataBaseBO", "RegistrarProductoPedidos: " + mensaje, e);

        } finally {

            if (db != null)
                db.close();

            if (dbTemp != null)
                dbTemp.close();
        }
    }

    public static void actualizarMedicionExhibidorActual_Temperatura(int bandera, String idExhibidor, String cantidad, ExhibidorEncabezado exhibidorEncabezadoHistorico,String idCategoria) {

        SQLiteDatabase db = null;
        SQLiteDatabase dbTemp = null;

        try {

            dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            File filePedido = new File(Util.dirApp(), "Temp.db");
            dbTemp = SQLiteDatabase.openDatabase(filePedido.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("UPDATE IPV_med_exhibidor_encabezado SET isTemperatura = '" + bandera + "', ValorTemperatura = '" + cantidad + "' WHERE ID = '" + idExhibidor + "'");

            /****************************************************** Encabezado */
            ContentValues valuesExhibidorEncabezado = new ContentValues();

            valuesExhibidorEncabezado.put("CodigoCliente", exhibidorEncabezadoHistorico.codigoCliente);
            valuesExhibidorEncabezado.put("CodigoUsuario", exhibidorEncabezadoHistorico.codigoUsuario);
            valuesExhibidorEncabezado.put("NombreUsuario", exhibidorEncabezadoHistorico.nombreUsuario);
            valuesExhibidorEncabezado.put("TipoUsuario", exhibidorEncabezadoHistorico.tipoUsuario);
            valuesExhibidorEncabezado.put("ID", exhibidorEncabezadoHistorico.id);
            valuesExhibidorEncabezado.put("IDExhibidorCliente", exhibidorEncabezadoHistorico.idExhibidorCliente);
            valuesExhibidorEncabezado.put("FechaMovil", exhibidorEncabezadoHistorico.fechaMovil);
            valuesExhibidorEncabezado.put("isFoto", exhibidorEncabezadoHistorico.isFoto);
            valuesExhibidorEncabezado.put("isTemperatura", bandera);
            valuesExhibidorEncabezado.put("ValorTemperatura", cantidad);
            valuesExhibidorEncabezado.put("CategoriaTarea", idCategoria);
            valuesExhibidorEncabezado.put("IDDetalle", idDetalleMedicion);

            dbTemp.insertOrThrow("IPV_med_exhibidor_encabezado", null, valuesExhibidorEncabezado);

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("DataBaseBO", "RegistrarProductoPedidos: " + mensaje, e);

        } finally {

            if (db != null)
                db.close();

            if (dbTemp != null)
                dbTemp.close();
        }
    }

    @SuppressLint("LongLogTag")
    public static Vector<Producto> obtenerListaProductosPropiosExhibidor(String idExhibidor, int tipoConsulta, Cliente cliente, String idCategoria) {

        mensaje = "";
        Producto productoPropio;
        SQLiteDatabase db = null;
        String query = "";

        Vector<Producto> listaProductosPropios = new Vector<Producto>();

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            if (tipoConsulta == 0) {

                query = "SELECT cm.codigoMarca CodigoMarca, Descripcion Descripcion, " +
                        "CASE WHEN med.NroCaras IS NULL THEN 0 ELSE med.NroCaras END CantidadAnt " +
                        "FROM IPV_Config_MarcasPropias cm " +
                        "INNER JOIN Marcas m ON cm.codigoMarca = m.Codigo " +
                        "LEFT JOIN IPV_med_exhibidor_detalle med ON (m.Codigo = med.CodigoMarca AND med.ID = '" + idExhibidor + "') " +
                        "WHERE canal = '" + cliente.canal + "' " +
                        "AND ofiventas = '" + cliente.agencia +"' "+
                        "AND m.CategoriaTarea = '" + idCategoria+ "' " +
                        "GROUP BY cm.codigoMarca";

            } else if (tipoConsulta == 1) {

                query = "SELECT cm.codigoMarca CodigoMarca, Descripcion Descripcion, " +
                        "CASE WHEN med.NroCaras IS NULL THEN 0 ELSE med.NroCaras END CantidadAnt " +
                        "FROM IPV_Config_MarcasPropias cm " +
                        "INNER JOIN Marcas m ON cm.codigoMarca = m.Codigo " +
                        "LEFT JOIN IPV_med_hist_exhibidor_detalle med ON (m.Codigo = med.CodigoMarca AND med.ID = '" + idExhibidor + "') " +
                        "WHERE canal = '" + cliente.canal + "' AND ofiventas = '" + cliente.agencia + "' " +
                        "AND m.CategoriaTarea = '" + idCategoria+ "' " +
                        "GROUP BY cm.codigoMarca";

            } else if (tipoConsulta == 2) {

                query = "SELECT " +
                        "codigoMarca CodigoMarca, Descripcion Descripcion, " +
                        "0 CantidadAnt " +
                        "FROM IPV_Config_MarcasPropias cm " +
                        "INNER JOIN Marcas m ON cm.codigoMarca = m.Codigo " +
                        "WHERE canal = '" + cliente.canal + "' AND ofiventas = '" + cliente.agencia + "' " +
                        "AND m.CategoriaTarea = '" + idCategoria+ "' " +
                        "GROUP BY cm.codigoMarca";
            }

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                do {

                    productoPropio = new Producto();

                    productoPropio.codigo = cursor.getString(cursor.getColumnIndex("CodigoMarca"));
                    productoPropio.nombre = cursor.getString(cursor.getColumnIndex("Descripcion"));

                    if (tipoConsulta == 0 || tipoConsulta == 1) {

                        // SE CARGA EL VALOR DE CANTIDAD ANTERIOR POR QUE TIENE VALOR ASIGNADO
                        productoPropio.cantidadAnt = cursor.getInt(cursor.getColumnIndex("CantidadAnt"));
                        productoPropio.cantidadAct = productoPropio.cantidadAnt;
                        productoPropio.esModificado = true;

                    } else {

                        productoPropio.cantidadAct = 0;
                        productoPropio.esModificado = false;
                    }

                    listaProductosPropios.addElement(productoPropio);

                } while (cursor.moveToNext());

                mensaje = "Productos Agotados Cargados Correctamente";

            } else {

                mensaje = "Consulta sin Resultados";
            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("obtenerListaProductosPropios", mensaje, e);

        } finally {

            if (db != null)
                db.close();

            /**
             if (listaProductosPropios.size() > 0) {

             Producto productoPropioOtro = new Producto();

             productoPropioOtro.codigo = "9999";
             productoPropioOtro.nombre = "Otras Propios";
             productoPropioOtro.cantidadAnt = 0;
             productoPropioOtro.cantidadAct = 0;
             productoPropioOtro.esModificado = false;

             listaProductosPropios.addElement(productoPropioOtro);
             }
             **/
        }

        return listaProductosPropios;
    }

    @SuppressLint("LongLogTag")
    public static Vector<Producto> obtenerListaProductosCompetenciaExhibidor(String idExhibidor, int tipoConsulta, Cliente cliente,String idCategoria) {

        mensaje = "";
        Producto productoCompetencia;
        SQLiteDatabase db = null;
        String query = "";
        String canalCliente = Main.cliente.canal;

        Vector<Producto> listaProductosCompetencia = new Vector<Producto>();

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            if (tipoConsulta == 0) {

                query = "SELECT cm.codigoMarca CodigoMarca, nombre Descripcion, " +
                        "CASE WHEN med.NroCaras IS NULL THEN 0 ELSE med.NroCaras END CantidadAnt " +
                        "FROM IPV_Config_MarcasCompetencia cm " +
                        "INNER JOIN MarcasCompetencia m ON cm.codigoMarca = m.id " +
                        "LEFT JOIN IPV_med_exhibidor_detalle med ON (m.id = med.CodigoMarca AND med.ID = '" + idExhibidor + "' AND med.competencia = '1') " +
                        "WHERE canal = '" + cliente.canal + "' AND ofiventas = '" + cliente.agencia + "'" +
                        "AND m.CategoriaTarea = '" + idCategoria+ "' " +
                        "GROUP BY cm.codigoMarca";

            } else if (tipoConsulta == 1) {

                query = "SELECT cm.codigoMarca CodigoMarca, nombre Descripcion, " +
                        "CASE WHEN med.NroCaras IS NULL THEN 0 ELSE med.NroCaras END CantidadAnt " +
                        "FROM IPV_Config_MarcasCompetencia cm " +
                        "INNER JOIN MarcasCompetencia m ON cm.codigoMarca = m.id " +
                        "LEFT JOIN IPV_med_hist_exhibidor_detalle med ON (m.id = med.CodigoMarca AND med.ID = '" + idExhibidor + "' AND med.competencia = '1') " +
                        "WHERE canal = '" + cliente.canal + "' AND ofiventas = '" + cliente.agencia + "'" +
                        "AND m.CategoriaTarea = '" + idCategoria+ "' " +
                        "GROUP BY cm.codigoMarca";

            } else if (tipoConsulta == 2) {

                query = "SELECT " +
                        "codigoMarca CodigoMarca, nombre Descripcion, " +
                        "0 CantidadAnt " +
                        "FROM IPV_Config_MarcasCompetencia cm " +
                        "INNER JOIN MarcasCompetencia m ON cm.codigoMarca = m.id " +
                        "WHERE canal = '" + cliente.canal + "' AND ofiventas = '" + cliente.agencia + "' " +
                        "AND m.CategoriaTarea = '" + idCategoria+ "' " +
                        "GROUP BY cm.codigoMarca";
            }

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                do {

                    productoCompetencia = new Producto();

                    productoCompetencia.codigo = cursor.getString(cursor.getColumnIndex("CodigoMarca"));
                    productoCompetencia.nombre = cursor.getString(cursor.getColumnIndex("Descripcion"));

                    if (tipoConsulta == 0 || tipoConsulta == 1) {

                        // SE CARGA EL VALOR DE CANTIDAD ANTERIOR POR QUE TIENE VALOR ASIGNADO
                        productoCompetencia.cantidadAnt = cursor.getInt(cursor.getColumnIndex("CantidadAnt"));
                        productoCompetencia.cantidadAct = productoCompetencia.cantidadAnt;
                        productoCompetencia.esModificado = true;

                    } else {

                        productoCompetencia.cantidadAct = 0;
                        productoCompetencia.esModificado = false;
                    }

                    listaProductosCompetencia.addElement(productoCompetencia);

                } while (cursor.moveToNext());

                mensaje = "Productos Agotados Cargados Correctamente";

            } else {

                mensaje = "Consulta sin Resultados";
            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("obtenerListaProductosCompetencia", mensaje, e);

        } finally {

            if (db != null)
                db.close();

            if (listaProductosCompetencia.size() > 0) {

                Producto productoCompetenciaOtro = new Producto();

                productoCompetenciaOtro.codigo = "8888";
                productoCompetenciaOtro.nombre = "Otras Competencia";
                productoCompetenciaOtro.cantidadAnt = 0;
                productoCompetenciaOtro.cantidadAct = 0;
                productoCompetenciaOtro.esModificado = false;

                listaProductosCompetencia.addElement(productoCompetenciaOtro);
            }
        }

        return listaProductosCompetencia;
    }

    public static boolean guardarProductosExhibidor(String id, String idDetalle, String fecha, Vector<Producto> listaProductosPropios, Vector<Producto> listaProductosCompetencia, String codigo, int totalCaras) {

        SQLiteDatabase db = null;
        SQLiteDatabase dbTemp = null;

        try {

            dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            File filePedido = new File(Util.dirApp(), "Temp.db");
            dbTemp = SQLiteDatabase.openDatabase(filePedido.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            /****************************************************************
             * Se almacenan los productos exhibidor de la medicion por cliente
             ****************************************************************/
            for (int i = 0; i < listaProductosPropios.size(); i++) {

                ContentValues valuesExhibidor = new ContentValues();

                valuesExhibidor.put("ID", id);
                valuesExhibidor.put("IDDetalle", idDetalle);
                valuesExhibidor.put("CodigoMarca", listaProductosPropios.elementAt(i).codigo);
                valuesExhibidor.put("NombreMarca", listaProductosPropios.elementAt(i).nombre);
                valuesExhibidor.put("CodigoProducto", "");
                valuesExhibidor.put("NombreProducto", "");
                valuesExhibidor.put("NroCaras", listaProductosPropios.elementAt(i).cantidadAct);
                if (totalCaras != 0) {
                    valuesExhibidor.put("PorcentajeCaras", "" + Math.round((listaProductosPropios.elementAt(i).cantidadAct * 100) / totalCaras));
                } else {
                    valuesExhibidor.put("PorcentajeCaras", "" + 0);
                }
                valuesExhibidor.put("Core", "0");
                valuesExhibidor.put("Propio", "1");
                valuesExhibidor.put("Competencia", "0");
                valuesExhibidor.put("FechaMovil", fecha);

                db.insertOrThrow("IPV_med_exhibidor_detalle", null, valuesExhibidor);
                dbTemp.insertOrThrow("IPV_med_exhibidor_detalle", null, valuesExhibidor);
            }

            for (int i = 0; i < listaProductosCompetencia.size(); i++) {

                ContentValues valuesExhibidor = new ContentValues();

                valuesExhibidor.put("ID", id);
                valuesExhibidor.put("IDDetalle", idDetalle);
                valuesExhibidor.put("CodigoMarca", listaProductosCompetencia.elementAt(i).codigo);
                valuesExhibidor.put("NombreMarca", listaProductosCompetencia.elementAt(i).nombre);
                valuesExhibidor.put("CodigoProducto", "");
                valuesExhibidor.put("NombreProducto", "");
                valuesExhibidor.put("NroCaras", listaProductosCompetencia.elementAt(i).cantidadAct);
                if (totalCaras != 0) {
                    valuesExhibidor.put("PorcentajeCaras", "" + Math.round((listaProductosCompetencia.elementAt(i).cantidadAct * 100) / totalCaras));
                } else {
                    valuesExhibidor.put("PorcentajeCaras", "" + 0);
                }
                valuesExhibidor.put("Core", "0");
                valuesExhibidor.put("Propio", "0");
                valuesExhibidor.put("Competencia", "1");
                valuesExhibidor.put("FechaMovil", fecha);

                db.insertOrThrow("IPV_med_exhibidor_detalle", null, valuesExhibidor);
                dbTemp.insertOrThrow("IPV_med_exhibidor_detalle", null, valuesExhibidor);
            }

            return true;

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("DataBaseBO", "RegistrarProductoExhibidor: " + mensaje, e);
            return false;

        } finally {

            if (db != null)
                db.close();

            if (dbTemp != null)
                dbTemp.close();

            validarAgregarGestionCliente(codigo);
        }
    }

    @SuppressLint("LongLogTag")
    public static Vector<ComponenteActivacion> obtenerListaComponenteActivacion(Vector<ItemListViewComponenteActivacion> listaItems,
                                                                                String codigo,
                                                                                int opcionActualLogica,
                                                                                int opcionTipoSel) {

        mensaje = "";
        ComponenteActivacion componenteActivacion;
        SQLiteDatabase db = null;
        String query = "";

        Vector<ComponenteActivacion> listaComponentesActivacion = new Vector<ComponenteActivacion>();

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            if (opcionTipoSel <= 2) { // PROMOCION

                query = "SELECT codTipo Codigo, descripcion Descripcion FROM IPV_TipoPromocion";

            } else {                 // MATERIAL POP

                query = "SELECT codTipo Codigo, descripcion Descripcion FROM IPV_TipoMaterialPOP";
            }

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                do {

                    componenteActivacion = new ComponenteActivacion();
                    ItemListViewComponenteActivacion item = new ItemListViewComponenteActivacion();

                    componenteActivacion.codigo = cursor.getString(cursor.getColumnIndex("Codigo"));
                    componenteActivacion.descripcion = cursor.getString(cursor.getColumnIndex("Descripcion"));
                    componenteActivacion.seleccionado = 0;

                    item.codigo = componenteActivacion.codigo;
                    item.descripcion = componenteActivacion.descripcion;
                    item.seleccionado = componenteActivacion.seleccionado;

                    listaItems.add(item);
                    listaComponentesActivacion.addElement(componenteActivacion);


                } while (cursor.moveToNext());

                mensaje = "Activacion Cargada Correctamente";

            } else {

                mensaje = "Consulta sin Resultados";
            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("obtenerListaComponentesActivacion", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return listaComponentesActivacion;
    }

    public static boolean almacenarRegistroActivacion(ArrayList<ObjetoActivacion> listaRespuestaActivacion, String codigoCliente) {

        SQLiteDatabase db = null;
        SQLiteDatabase dbTemp = null;

        try {

            dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            File filePedido = new File(Util.dirApp(), "Temp.db");
            dbTemp = SQLiteDatabase.openDatabase(filePedido.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            /****************************************************************
             ******** Se almacenan los registros de la medicion de ACTIVACION
             ****************************************************************/
            for (int i = 0; i < listaRespuestaActivacion.size(); i++) {

                ContentValues contentValuesActivacion = new ContentValues();

                contentValuesActivacion.put("CodigoCliente", listaRespuestaActivacion.get(i).codigoCliente);
                contentValuesActivacion.put("CodigoUsuario", listaRespuestaActivacion.get(i).codigoUsuario);
                contentValuesActivacion.put("NombreUsuario", listaRespuestaActivacion.get(i).nombreUsuario);
                contentValuesActivacion.put("TipoUsuario", listaRespuestaActivacion.get(i).tipoUsuario);
                contentValuesActivacion.put("ID", listaRespuestaActivacion.get(i).id);
                contentValuesActivacion.put("TipoOpcion", listaRespuestaActivacion.get(i).tipoOpcion);
                contentValuesActivacion.put("Valor1", listaRespuestaActivacion.get(i).valor1);
                contentValuesActivacion.put("CodigoProducto", listaRespuestaActivacion.get(i).codigoProducto);
                contentValuesActivacion.put("NombreProducto", listaRespuestaActivacion.get(i).nombreProducto);
                contentValuesActivacion.put("Core", listaRespuestaActivacion.get(i).core);
                contentValuesActivacion.put("Propio", listaRespuestaActivacion.get(i).propio);
                contentValuesActivacion.put("Competencia", listaRespuestaActivacion.get(i).competencia);
                contentValuesActivacion.put("FechaMovil", listaRespuestaActivacion.get(i).fechaMovil);
                contentValuesActivacion.put("Nombre1", listaRespuestaActivacion.get(i).nombre1);

                db.insertOrThrow("IPV_med_activacion", null, contentValuesActivacion);
                dbTemp.insertOrThrow("IPV_med_activacion", null, contentValuesActivacion);
            }

            return true;

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("DataBaseBO", "RegistrarProductoExhibidor: " + mensaje, e);
            return false;

        } finally {

            if (db != null)
                db.close();

            if (dbTemp != null)
                dbTemp.close();

            validarAgregarGestionCliente(codigoCliente);
        }
    }

    @SuppressLint("LongLogTag")
    public static void eliminarFotosMedicionActual(String idMedicion) {

        SQLiteDatabase db = null;
        SQLiteDatabase dbTemp = null;

        try {

            dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            File filePedido = new File(Util.dirApp(), "Temp.db");
            dbTemp = SQLiteDatabase.openDatabase(filePedido.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("DELETE FROM Fotos WHERE id = '" + idMedicion + "'");
            dbTemp.execSQL("DELETE FROM Fotos WHERE id = '" + idMedicion + "'");
            db.execSQL("VACUUM");
            dbTemp.execSQL("VACUUM");

            mensaje = "Imagen borrada con exito";

        } catch (Exception e) {

            mensaje = "Error cargando Imagen: " + e.getMessage();
            Log.e("DataBaseBO - BorrarImagen", mensaje, e);

        } finally {

            if (db != null)
                db.close();

            if (dbTemp != null)
                dbTemp.close();
        }
    }

    public static Vector<ComponenteActivacionTerminado> obtenerListaMedicionActivacionTerminada(Vector<ItemListViewMedicionActivacion> listaItems, String codigo, int opcionActualLogica, int opcionTipoSel) {

        mensaje = "";
        ComponenteActivacionTerminado componenteActivacionTerminado;
        SQLiteDatabase db = null;
        int parametroConsulta = 0;
        String sql = "";

        Vector<ComponenteActivacionTerminado> listaComponentesActivacion = new Vector<ComponenteActivacionTerminado>();

        if (opcionActualLogica == 1) {

            // PROPIO
            parametroConsulta = 1;

        } else {

            // COMPETENCIA
            parametroConsulta = 0;
        }

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            sql = "SELECT Nombre1 Descripcion, Valor1 Codigo, NombreProducto, ID ID FROM IPV_med_activacion " +
                    "WHERE CodigoCliente = '" + codigo + "' " +
                    "AND Valor1 != 'SI' AND Propio = " + parametroConsulta + " AND TipoOpcion = " + opcionTipoSel + "  " +
                    "GROUP BY ID";

            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst()) {

                do {

                    componenteActivacionTerminado = new ComponenteActivacionTerminado();
                    ItemListViewMedicionActivacion item = new ItemListViewMedicionActivacion();

                    componenteActivacionTerminado.codigo = cursor.getString(cursor.getColumnIndex("Codigo"));
                    componenteActivacionTerminado.descripcion = cursor.getString(cursor.getColumnIndex("Descripcion"));
                    componenteActivacionTerminado.nombreProducto = cursor.getString(cursor.getColumnIndex("NombreProducto"));
                    componenteActivacionTerminado.id = cursor.getString(cursor.getColumnIndex("ID"));
                    componenteActivacionTerminado.seleccionado = 1;

                    item.codigo = componenteActivacionTerminado.codigo;
                    item.descripcion = componenteActivacionTerminado.descripcion;
                    item.nombreProducto = componenteActivacionTerminado.nombreProducto;
                    item.estaSeleccionado = componenteActivacionTerminado.seleccionado;

                    listaItems.add(item);
                    listaComponentesActivacion.addElement(componenteActivacionTerminado);


                } while (cursor.moveToNext());

                mensaje = "Actividades Cargadas Correctamente";

            } else {

                mensaje = "Consulta sin Resultados";
            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("ListaClientesRutero", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return listaComponentesActivacion;
    }

    public static String obtenerIdMedicionAnteriorAgotados(String codigo) {

        String idMedicion = "";

        SQLiteDatabase db = null;

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT ID ID FROM IPV_med_agotados WHERE CodigoCliente = '" + codigo + "' " +
                    "GROUP BY ID";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                idMedicion = cursor.getString(cursor.getColumnIndex("ID"));
            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();
        }

        return idMedicion;
    }

    public static void eliminarMedicionAgotadosClienteAnterior(String idMedidcionAnterior) {

        SQLiteDatabase db = null;

        try {

            dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "DELETE FROM IPV_med_agotados WHERE ID = '" + idMedidcionAnterior + "'";
            db.execSQL(query);

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "Eliminar Medicion Agotados: " + mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }
    }

    @SuppressLint("LongLogTag")
    public static boolean obtenerInformacionPrecargaFotosExhibidor(String idExhibidorSel, int exhibidorRegistroHoy) {

        boolean entraFotosPrecarga = false;
        SQLiteDatabase db = null;
        String nombreTablaBusqueda;

        if (exhibidorRegistroHoy == 1) {

            nombreTablaBusqueda = "IPV_med_exhibidor_encabezado";

        } else {

            nombreTablaBusqueda = "IPV_med_hist_exhibidor_encabezado";
        }

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT CASE WHEN isFoto IS NULL THEN '0' ELSE isFoto END AS IsFoto " +
                    "FROM " + nombreTablaBusqueda + " " +
                    "WHERE IDExhibidorCliente = '" + idExhibidorSel + "'";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                String enteroIsFoto = cursor.getString(cursor.getColumnIndex("IsFoto"));
                entraFotosPrecarga = (enteroIsFoto.equals("0")) ? true : false;
            }

            Log.i("InformacionPrecargaFotosExhibidor", "" + entraFotosPrecarga);

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("InformacionPrecargaFotosExhibidor", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return entraFotosPrecarga;
    }

    @SuppressLint("LongLogTag")
    public static boolean obtenerInformacionPrecargaTemperaturaExhibidor(String idExhibidorSel, int exhibidorRegistroHoy) {

        boolean entraTemperaturaPrecarga = false;
        SQLiteDatabase db = null;
        String nombreTablaBusqueda;

        if (exhibidorRegistroHoy == 1) {

            nombreTablaBusqueda = "IPV_med_exhibidor_encabezado";

        } else {

            nombreTablaBusqueda = "IPV_med_hist_exhibidor_encabezado";
        }

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT CASE WHEN isTemperatura IS NULL THEN '0' ELSE isTemperatura END AS IsTemperatura " +
                    "FROM " + nombreTablaBusqueda + " " +
                    "WHERE IDExhibidorCliente = '" + idExhibidorSel + "'";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                String enteroIsTemperatura = cursor.getString(cursor.getColumnIndex("IsTemperatura"));
                entraTemperaturaPrecarga = (enteroIsTemperatura.equals("0")) ? true : false;
            }

            Log.i("InformacionPrecargaTemperaturaExhibidor", "" + entraTemperaturaPrecarga);

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("InformacionPrecargaTemperaturaExhibidor", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return entraTemperaturaPrecarga;
    }

    @SuppressLint("LongLogTag")
    public static ExhibidorEncabezado obtenerExhibidorEncabezado(String idExhibidor) {

        mensaje = "";
        ExhibidorEncabezado exhibidorEncabezado = new ExhibidorEncabezado();
        SQLiteDatabase db = null;
        String query = "";

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            query = "SELECT CodigoCliente CodigoCliente, CodigoUsuario CodigoUsuario, NombreUsuario NombreUsuario, " +
                    "TipoUsuario TipoUsuario, ID ID, IDExhibidorCliente IDExhibidorCliente " +
                    "FROM IPV_med_hist_exhibidor_encabezado " +
                    "WHERE IDExhibidorCliente = '" + idExhibidor + "'";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                exhibidorEncabezado = new ExhibidorEncabezado();

                exhibidorEncabezado.codigoCliente = cursor.getString(cursor.getColumnIndex("CodigoCliente"));
                exhibidorEncabezado.codigoUsuario = cursor.getString(cursor.getColumnIndex("CodigoUsuario"));
                exhibidorEncabezado.nombreUsuario = cursor.getString(cursor.getColumnIndex("NombreUsuario"));
                exhibidorEncabezado.tipoUsuario = cursor.getInt(cursor.getColumnIndex("TipoUsuario"));
                exhibidorEncabezado.id = cursor.getString(cursor.getColumnIndex("ID"));
                exhibidorEncabezado.idExhibidorCliente = cursor.getString(cursor.getColumnIndex("IDExhibidorCliente"));

            } else {

                mensaje = "Consulta sin Resultados";
            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("obtenerExhibidorEncabezado", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return exhibidorEncabezado;
    }

    @SuppressLint("LongLogTag")
    public static ExhibidorEncabezado obtenerExhibidorEncabezado_ACTUAL(String idExhibidor) {

        mensaje = "";
        ExhibidorEncabezado exhibidorEncabezado = new ExhibidorEncabezado();
        SQLiteDatabase db = null;
        String query = "";

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            query = "SELECT CodigoCliente CodigoCliente, CodigoUsuario CodigoUsuario, NombreUsuario NombreUsuario, " +
                    "TipoUsuario TipoUsuario, ID ID, IDExhibidorCliente IDExhibidorCliente " +
                    "FROM IPV_med_exhibidor_encabezado " +
                    "WHERE IDExhibidorCliente = '" + idExhibidor + "'";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                exhibidorEncabezado = new ExhibidorEncabezado();

                exhibidorEncabezado.codigoCliente = cursor.getString(cursor.getColumnIndex("CodigoCliente"));
                exhibidorEncabezado.codigoUsuario = cursor.getString(cursor.getColumnIndex("CodigoUsuario"));
                exhibidorEncabezado.nombreUsuario = cursor.getString(cursor.getColumnIndex("NombreUsuario"));
                exhibidorEncabezado.tipoUsuario = cursor.getInt(cursor.getColumnIndex("TipoUsuario"));
                exhibidorEncabezado.id = cursor.getString(cursor.getColumnIndex("ID"));
                exhibidorEncabezado.idExhibidorCliente = cursor.getString(cursor.getColumnIndex("IDExhibidorCliente"));

            } else {

                mensaje = "Consulta sin Resultados";
            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("obtenerExhibidorEncabezado", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return exhibidorEncabezado;
    }

    @SuppressLint("LongLogTag")
    public static ExhibidorEncabezado obtenerExhibidorEncabezado_ACTUAL_NOTEMPERATURA(String idExhibidor) {

        mensaje = "";
        ExhibidorEncabezado exhibidorEncabezado = new ExhibidorEncabezado();
        SQLiteDatabase db = null;
        String query = "";

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            query = "SELECT CodigoCliente CodigoCliente, CodigoUsuario CodigoUsuario, NombreUsuario NombreUsuario, " +
                    "TipoUsuario TipoUsuario, ID ID, IDExhibidorCliente IDExhibidorCliente, isFoto IsFoto " +
                    "FROM IPV_med_exhibidor_encabezado " +
                    "WHERE IDExhibidorCliente = '" + idExhibidor + "'";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                exhibidorEncabezado = new ExhibidorEncabezado();

                exhibidorEncabezado.codigoCliente = cursor.getString(cursor.getColumnIndex("CodigoCliente"));
                exhibidorEncabezado.codigoUsuario = cursor.getString(cursor.getColumnIndex("CodigoUsuario"));
                exhibidorEncabezado.nombreUsuario = cursor.getString(cursor.getColumnIndex("NombreUsuario"));
                exhibidorEncabezado.tipoUsuario = cursor.getInt(cursor.getColumnIndex("TipoUsuario"));
                exhibidorEncabezado.id = cursor.getString(cursor.getColumnIndex("ID"));
                exhibidorEncabezado.idExhibidorCliente = cursor.getString(cursor.getColumnIndex("IDExhibidorCliente"));
                exhibidorEncabezado.isFoto = cursor.getString(cursor.getColumnIndex("IsFoto"));

            } else {

                mensaje = "Consulta sin Resultados";
            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("obtenerExhibidorEncabezado", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return exhibidorEncabezado;
    }

    public static void insertarMedicionExhibidorActualizada_Fotos(ExhibidorEncabezado exhibidorEncabezadoHistorico,String idCategoria) {

        SQLiteDatabase db = null;
        SQLiteDatabase dbTemp = null;

        try {

            dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            File filePedido = new File(Util.dirApp(), "Temp.db");
            dbTemp = SQLiteDatabase.openDatabase(filePedido.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            /****************************************************** Encabezado */
            ContentValues valuesExhibidorEncabezado = new ContentValues();

            valuesExhibidorEncabezado.put("CodigoCliente", exhibidorEncabezadoHistorico.codigoCliente);
            valuesExhibidorEncabezado.put("CodigoUsuario", exhibidorEncabezadoHistorico.codigoUsuario);
            valuesExhibidorEncabezado.put("NombreUsuario", exhibidorEncabezadoHistorico.nombreUsuario);
            valuesExhibidorEncabezado.put("TipoUsuario", exhibidorEncabezadoHistorico.tipoUsuario);
            valuesExhibidorEncabezado.put("ID", exhibidorEncabezadoHistorico.id);
            valuesExhibidorEncabezado.put("IDExhibidorCliente", exhibidorEncabezadoHistorico.idExhibidorCliente);
            valuesExhibidorEncabezado.put("FechaMovil", exhibidorEncabezadoHistorico.fechaMovil);
            valuesExhibidorEncabezado.put("isFoto", exhibidorEncabezadoHistorico.isFoto);
            valuesExhibidorEncabezado.put("CategoriaTarea", idCategoria);
            valuesExhibidorEncabezado.put("IDDetalle", idDetalleMedicion);


            db.insertOrThrow("IPV_med_exhibidor_encabezado", null, valuesExhibidorEncabezado);
            dbTemp.insertOrThrow("IPV_med_exhibidor_encabezado", null, valuesExhibidorEncabezado);

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("DataBaseBO", "RegistrarExhibidorNuevo: " + mensaje, e);

        } finally {

            if (db != null)
                db.close();

            if (dbTemp != null)
                dbTemp.close();
        }
    }

    public static boolean debeTenerMedicionTemperatura(String canal) {

        SQLiteDatabase db = null;
        int cantidad = 0;

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT COUNT(*) Cantidad FROM IPV_med_exhibidor_opciontemperatura WHERE Canal = '" + canal + "'";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                cantidad = cursor.getInt(cursor.getColumnIndex("Cantidad"));
            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();
        }

        return cantidad > 0;
    }

    @SuppressLint("LongLogTag")
    public static ExhibidorEncabezado obtenerExhibidorEncabezado2(String idExhibidor) {

        mensaje = "";
        ExhibidorEncabezado exhibidorEncabezado = new ExhibidorEncabezado();
        SQLiteDatabase db = null;
        String query = "";

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            query = "SELECT CodigoCliente CodigoCliente, CodigoUsuario CodigoUsuario, NombreUsuario NombreUsuario, " +
                    "TipoUsuario TipoUsuario, ID ID, IDExhibidorCliente IDExhibidorCliente, isFoto IsFoto " +
                    "FROM IPV_med_hist_exhibidor_encabezado " +
                    "WHERE IDExhibidorCliente = '" + idExhibidor + "'";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                exhibidorEncabezado = new ExhibidorEncabezado();

                exhibidorEncabezado.codigoCliente = cursor.getString(cursor.getColumnIndex("CodigoCliente"));
                exhibidorEncabezado.codigoUsuario = cursor.getString(cursor.getColumnIndex("CodigoUsuario"));
                exhibidorEncabezado.nombreUsuario = cursor.getString(cursor.getColumnIndex("NombreUsuario"));
                exhibidorEncabezado.tipoUsuario = cursor.getInt(cursor.getColumnIndex("TipoUsuario"));
                exhibidorEncabezado.id = cursor.getString(cursor.getColumnIndex("ID"));
                exhibidorEncabezado.idExhibidorCliente = cursor.getString(cursor.getColumnIndex("IDExhibidorCliente"));
                exhibidorEncabezado.isFoto = cursor.getString(cursor.getColumnIndex("IsFoto"));

            } else {

                mensaje = "Consulta sin Resultados";
            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("obtenerExhibidorEncabezado", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return exhibidorEncabezado;
    }

    @SuppressLint("LongLogTag")
    public static ExhibidorEncabezado obtenerExhibidorEncabezado2_ACTUAL(String idExhibidor) {

        mensaje = "";
        ExhibidorEncabezado exhibidorEncabezado = new ExhibidorEncabezado();
        SQLiteDatabase db = null;
        String query = "";

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            query = "SELECT CodigoCliente CodigoCliente, CodigoUsuario CodigoUsuario, NombreUsuario NombreUsuario, " +
                    "TipoUsuario TipoUsuario, ID ID, IDExhibidorCliente IDExhibidorCliente, isFoto IsFoto " +
                    "FROM IPV_med_exhibidor_encabezado " +
                    "WHERE IDExhibidorCliente = '" + idExhibidor + "'";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                exhibidorEncabezado = new ExhibidorEncabezado();

                exhibidorEncabezado.codigoCliente = cursor.getString(cursor.getColumnIndex("CodigoCliente"));
                exhibidorEncabezado.codigoUsuario = cursor.getString(cursor.getColumnIndex("CodigoUsuario"));
                exhibidorEncabezado.nombreUsuario = cursor.getString(cursor.getColumnIndex("NombreUsuario"));
                exhibidorEncabezado.tipoUsuario = cursor.getInt(cursor.getColumnIndex("TipoUsuario"));
                exhibidorEncabezado.id = cursor.getString(cursor.getColumnIndex("ID"));
                exhibidorEncabezado.idExhibidorCliente = cursor.getString(cursor.getColumnIndex("IDExhibidorCliente"));
                exhibidorEncabezado.isFoto = cursor.getString(cursor.getColumnIndex("IsFoto"));

            } else {

                mensaje = "Consulta sin Resultados";
            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("obtenerExhibidorEncabezado", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return exhibidorEncabezado;
    }

    public static void insertarMedicionExhibidorActualizada_Temperatura(ExhibidorEncabezado exhibidorEncabezadoHistorico,String idCategoria) {

        SQLiteDatabase db = null;
        SQLiteDatabase dbTemp = null;

        try {

            dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            File filePedido = new File(Util.dirApp(), "Temp.db");
            dbTemp = SQLiteDatabase.openDatabase(filePedido.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            /****************************************************** Encabezado */
            ContentValues valuesExhibidorEncabezado = new ContentValues();

            valuesExhibidorEncabezado.put("CodigoCliente", exhibidorEncabezadoHistorico.codigoCliente);
            valuesExhibidorEncabezado.put("CodigoUsuario", exhibidorEncabezadoHistorico.codigoUsuario);
            valuesExhibidorEncabezado.put("NombreUsuario", exhibidorEncabezadoHistorico.nombreUsuario);
            valuesExhibidorEncabezado.put("TipoUsuario", exhibidorEncabezadoHistorico.tipoUsuario);
            valuesExhibidorEncabezado.put("ID", exhibidorEncabezadoHistorico.id);
            valuesExhibidorEncabezado.put("IDExhibidorCliente", exhibidorEncabezadoHistorico.idExhibidorCliente);
            valuesExhibidorEncabezado.put("FechaMovil", exhibidorEncabezadoHistorico.fechaMovil);
            valuesExhibidorEncabezado.put("isFoto", exhibidorEncabezadoHistorico.isFoto);
            valuesExhibidorEncabezado.put("isTemperatura", exhibidorEncabezadoHistorico.isTemperatura);
            valuesExhibidorEncabezado.put("ValorTemperatura", exhibidorEncabezadoHistorico.valorTemperatura);
            valuesExhibidorEncabezado.put("CategoriaTarea", idCategoria);
            valuesExhibidorEncabezado.put("IDDetalle", idDetalleMedicion);

            db.insertOrThrow("IPV_med_exhibidor_encabezado", null, valuesExhibidorEncabezado);
            dbTemp.insertOrThrow("IPV_med_exhibidor_encabezado", null, valuesExhibidorEncabezado);

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("DataBaseBO", "RegistrarExhibidorNuevo: " + mensaje, e);

        } finally {

            if (db != null)
                db.close();

            if (dbTemp != null)
                dbTemp.close();
        }
    }

    public static boolean hayMedicionesExhibidorParaElDia(String idExhibidor) {

        SQLiteDatabase db = null;
        int cantidad = 0;

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT COUNT(*) Cantidad FROM IPV_med_exhibidor_detalle WHERE ID = '" + idExhibidor + "'";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                cantidad = cursor.getInt(cursor.getColumnIndex("Cantidad"));
            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();
        }

        return cantidad > 0;
    }

    public static boolean hayMedicionesExhibidorHistoricas(String idExhibidor) {

        SQLiteDatabase db = null;
        int cantidad = 0;

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT COUNT(*) Cantidad FROM IPV_med_hist_exhibidor_detalle WHERE ID = '" + idExhibidor + "'";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                cantidad = cursor.getInt(cursor.getColumnIndex("Cantidad"));
            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();
        }

        return cantidad > 0;
    }

    @SuppressLint("LongLogTag")
    public static ExhibidorEncabezado obtenerExhibidorEncabezado3(String idExhibidor) {

        mensaje = "";
        ExhibidorEncabezado exhibidorEncabezado = new ExhibidorEncabezado();
        SQLiteDatabase db = null;
        String query = "";

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            query = "SELECT CodigoCliente CodigoCliente, CodigoUsuario CodigoUsuario, NombreUsuario NombreUsuario, " +
                    "TipoUsuario TipoUsuario, ID ID, IDExhibidorCliente IDExhibidorCliente, isFoto IsFoto, " +
                    "isTemperatura IsTemperatura, ValorTemperatura ValorTemperatura " +
                    "FROM IPV_med_hist_exhibidor_encabezado " +
                    "WHERE IDExhibidorCliente = '" + idExhibidor + "'";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                exhibidorEncabezado = new ExhibidorEncabezado();

                exhibidorEncabezado.codigoCliente = cursor.getString(cursor.getColumnIndex("CodigoCliente"));
                exhibidorEncabezado.codigoUsuario = cursor.getString(cursor.getColumnIndex("CodigoUsuario"));
                exhibidorEncabezado.nombreUsuario = cursor.getString(cursor.getColumnIndex("NombreUsuario"));
                exhibidorEncabezado.tipoUsuario = cursor.getInt(cursor.getColumnIndex("TipoUsuario"));
                exhibidorEncabezado.id = cursor.getString(cursor.getColumnIndex("ID"));
                exhibidorEncabezado.idExhibidorCliente = cursor.getString(cursor.getColumnIndex("IDExhibidorCliente"));
                exhibidorEncabezado.isFoto = cursor.getString(cursor.getColumnIndex("IsFoto"));
                exhibidorEncabezado.isTemperatura = cursor.getString(cursor.getColumnIndex("IsTemperatura"));
                exhibidorEncabezado.valorTemperatura = cursor.getString(cursor.getColumnIndex("ValorTemperatura"));

            } else {

                mensaje = "Consulta sin Resultados";
            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("obtenerExhibidorEncabezado", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return exhibidorEncabezado;
    }

    @SuppressLint("LongLogTag")
    public static ExhibidorEncabezado obtenerExhibidorEncabezado3_ACTUAL(String idExhibidor) {

        mensaje = "";
        ExhibidorEncabezado exhibidorEncabezado = new ExhibidorEncabezado();
        SQLiteDatabase db = null;
        String query = "";

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            query = "SELECT CodigoCliente CodigoCliente, CodigoUsuario CodigoUsuario, NombreUsuario NombreUsuario, " +
                    "TipoUsuario TipoUsuario, ID ID, IDExhibidorCliente IDExhibidorCliente, isFoto IsFoto, " +
                    "isTemperatura IsTemperatura, ValorTemperatura ValorTemperatura " +
                    "FROM IPV_med_exhibidor_encabezado " +
                    "WHERE IDExhibidorCliente = '" + idExhibidor + "'";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                exhibidorEncabezado = new ExhibidorEncabezado();

                exhibidorEncabezado.codigoCliente = cursor.getString(cursor.getColumnIndex("CodigoCliente"));
                exhibidorEncabezado.codigoUsuario = cursor.getString(cursor.getColumnIndex("CodigoUsuario"));
                exhibidorEncabezado.nombreUsuario = cursor.getString(cursor.getColumnIndex("NombreUsuario"));
                exhibidorEncabezado.tipoUsuario = cursor.getInt(cursor.getColumnIndex("TipoUsuario"));
                exhibidorEncabezado.id = cursor.getString(cursor.getColumnIndex("ID"));
                exhibidorEncabezado.idExhibidorCliente = cursor.getString(cursor.getColumnIndex("IDExhibidorCliente"));
                exhibidorEncabezado.isFoto = cursor.getString(cursor.getColumnIndex("IsFoto"));
                exhibidorEncabezado.isTemperatura = cursor.getString(cursor.getColumnIndex("IsTemperatura"));
                exhibidorEncabezado.valorTemperatura = cursor.getString(cursor.getColumnIndex("ValorTemperatura"));

            } else {

                mensaje = "Consulta sin Resultados";
            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("obtenerExhibidorEncabezado", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return exhibidorEncabezado;
    }

    public static void actualizarMedicionExhibidorActualizada_Productos(int cantidadCarasPropias, int cantidadCarasCompetencia, int cantidadPorcentajePropias, int cantidadPorcentajeCompetencia, String idExhibidor, ExhibidorEncabezado exhibidorEncabezadoHistorico,String idCategoria, String idDetalle) {

        SQLiteDatabase db = null;
        SQLiteDatabase dbTemp = null;

        try {

            dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            File filePedido = new File(Util.dirApp(), "Temp.db");
            dbTemp = SQLiteDatabase.openDatabase(filePedido.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("UPDATE IPV_med_exhibidor_encabezado SET TotalPropios = '" + cantidadCarasPropias + "', TotalCompetencia = '" + cantidadCarasCompetencia + "', PorcentajePropios = '" + cantidadPorcentajePropias + "', PorcentajeCompetencia = '" + cantidadPorcentajeCompetencia + "' WHERE ID = '" + idExhibidor + "'");

            /****************************************************** Encabezado */
            ContentValues valuesExhibidorEncabezado = new ContentValues();

            valuesExhibidorEncabezado.put("CodigoCliente", exhibidorEncabezadoHistorico.codigoCliente);
            valuesExhibidorEncabezado.put("CodigoUsuario", exhibidorEncabezadoHistorico.codigoUsuario);
            valuesExhibidorEncabezado.put("NombreUsuario", exhibidorEncabezadoHistorico.nombreUsuario);
            valuesExhibidorEncabezado.put("TipoUsuario", exhibidorEncabezadoHistorico.tipoUsuario);
            valuesExhibidorEncabezado.put("ID", exhibidorEncabezadoHistorico.id);
            valuesExhibidorEncabezado.put("IDExhibidorCliente", exhibidorEncabezadoHistorico.idExhibidorCliente);
            valuesExhibidorEncabezado.put("FechaMovil", exhibidorEncabezadoHistorico.fechaMovil);
            valuesExhibidorEncabezado.put("isFoto", exhibidorEncabezadoHistorico.isFoto);
            valuesExhibidorEncabezado.put("isTemperatura", exhibidorEncabezadoHistorico.isTemperatura);
            valuesExhibidorEncabezado.put("ValorTemperatura", exhibidorEncabezadoHistorico.valorTemperatura);
            valuesExhibidorEncabezado.put("TotalPropios", "" + cantidadCarasPropias);
            valuesExhibidorEncabezado.put("TotalCompetencia", "" + cantidadCarasCompetencia);
            valuesExhibidorEncabezado.put("PorcentajePropios", "" + cantidadPorcentajePropias);
            valuesExhibidorEncabezado.put("PorcentajeCompetencia", "" + cantidadPorcentajeCompetencia);
            valuesExhibidorEncabezado.put("CategoriaTarea", idCategoria);
            valuesExhibidorEncabezado.put("IDDetalle", idDetalle);

            dbTemp.insertOrThrow("IPV_med_exhibidor_encabezado", null, valuesExhibidorEncabezado);

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("DataBaseBO", "RegistrarProductoPedidos: " + mensaje, e);

        } finally {

            if (db != null)
                db.close();

            if (dbTemp != null)
                dbTemp.close();
        }

    }

    public static void insertarMedicionExhibidorActualizada_Productos(ExhibidorEncabezado exhibidorEncabezadoHistorico,String idCategoria,  String idDetalle) {

        SQLiteDatabase db = null;
        SQLiteDatabase dbTemp = null;

        try {

            dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            File filePedido = new File(Util.dirApp(), "Temp.db");
            dbTemp = SQLiteDatabase.openDatabase(filePedido.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
            idDetalleMedicion = idDetalle;
            /****************************************************** Encabezado */
            ContentValues valuesExhibidorEncabezado = new ContentValues();

            valuesExhibidorEncabezado.put("CodigoCliente", exhibidorEncabezadoHistorico.codigoCliente);
            valuesExhibidorEncabezado.put("CodigoUsuario", exhibidorEncabezadoHistorico.codigoUsuario);
            valuesExhibidorEncabezado.put("NombreUsuario", exhibidorEncabezadoHistorico.nombreUsuario);
            valuesExhibidorEncabezado.put("TipoUsuario", exhibidorEncabezadoHistorico.tipoUsuario);
            valuesExhibidorEncabezado.put("ID", exhibidorEncabezadoHistorico.id);
            valuesExhibidorEncabezado.put("IDExhibidorCliente", exhibidorEncabezadoHistorico.idExhibidorCliente);
            valuesExhibidorEncabezado.put("FechaMovil", exhibidorEncabezadoHistorico.fechaMovil);
            valuesExhibidorEncabezado.put("isFoto", exhibidorEncabezadoHistorico.isFoto);
            valuesExhibidorEncabezado.put("isTemperatura", exhibidorEncabezadoHistorico.isTemperatura);
            valuesExhibidorEncabezado.put("ValorTemperatura", exhibidorEncabezadoHistorico.valorTemperatura);
            valuesExhibidorEncabezado.put("TotalPropios", exhibidorEncabezadoHistorico.totalPropios);
            valuesExhibidorEncabezado.put("TotalCompetencia", exhibidorEncabezadoHistorico.totalCompetencia);
            valuesExhibidorEncabezado.put("PorcentajePropios", exhibidorEncabezadoHistorico.porcentajePropios);
            valuesExhibidorEncabezado.put("PorcentajeCompetencia", exhibidorEncabezadoHistorico.porcentajeCompetencia);
            valuesExhibidorEncabezado.put("CategoriaTarea", idCategoria);
            valuesExhibidorEncabezado.put("IDDetalle", idDetalle);


            db.insertOrThrow("IPV_med_exhibidor_encabezado", null, valuesExhibidorEncabezado);
            dbTemp.insertOrThrow("IPV_med_exhibidor_encabezado", null, valuesExhibidorEncabezado);


        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("DataBaseBO", "RegistrarExhibidorNuevo: " + mensaje, e);

        } finally {

            if (db != null)
                db.close();

            if (dbTemp != null)
                dbTemp.close();
        }
    }

    public static void eliminarDetallesExhibidor(String idExhibidor) {

        SQLiteDatabase db = null;
        SQLiteDatabase dbTemp = null;

        try {

            dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            File filePedido = new File(Util.dirApp(), "Temp.db");
            dbTemp = SQLiteDatabase.openDatabase(filePedido.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "DELETE FROM IPV_med_exhibidor_detalle WHERE ID = '" + idExhibidor + "'";
            db.execSQL(query);
            dbTemp.execSQL(query);

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "Eliminar Exhibidor: " + mensaje, e);

        } finally {

            if (db != null)
                db.close();

            if (dbTemp != null)
                dbTemp.close();
        }
    }

    public static void actualizarExhibidorCliente(Exhibidores exhibidorActual) {

        SQLiteDatabase db = null;
        SQLiteDatabase dbTemp = null;

        try {

            dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            File filePedido = new File(Util.dirApp(), "Temp.db");
            dbTemp = SQLiteDatabase.openDatabase(filePedido.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("UPDATE IPV_med_exhibidor_cliente SET Finalizado = '1' WHERE ID = '" + exhibidorActual.id + "'");

            ContentValues valuesExhibidor = new ContentValues();

            valuesExhibidor.put("CodigoCliente", exhibidorActual.codigoCliente);
            valuesExhibidor.put("CodigoUsuario", exhibidorActual.codigoUsuario);
            valuesExhibidor.put("ID", exhibidorActual.id);
            valuesExhibidor.put("Nombre", exhibidorActual.nombre);
            valuesExhibidor.put("Ancho", exhibidorActual.ancho);
            valuesExhibidor.put("Alto", exhibidorActual.alto);
            valuesExhibidor.put("codTipoExhibidor", exhibidorActual.codTipoExhibidor);
            valuesExhibidor.put("codUbicacion", exhibidorActual.codUbicacion);
            valuesExhibidor.put("FechaMovil", exhibidorActual.fechaMovil);
            valuesExhibidor.put("Finalizado", 1);

            dbTemp.insertOrThrow("IPV_med_exhibidor_cliente", null, valuesExhibidor);

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("DataBaseBO", "RegistrarExhibidorNuevo: " + mensaje, e);

        } finally {

            if (db != null)
                db.close();

            if (dbTemp != null)
                dbTemp.close();
        }
    }

    public static void eliminarExhibidorCliente(String idExhibidor) {

        SQLiteDatabase dbTemp = null;

        try {

            File filePedido = new File(Util.dirApp(), "Temp.db");
            dbTemp = SQLiteDatabase.openDatabase(filePedido.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "DELETE FROM IPV_med_exhibidor_cliente WHERE ID = '" + idExhibidor + "'";
            dbTemp.execSQL(query);

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "Eliminar Exhibidor: " + mensaje, e);

        } finally {

            if (dbTemp != null)
                dbTemp.close();
        }
    }

    public static void eliminarExhibidorEncabeza(String idExhibidor) {

        SQLiteDatabase db = null;
        SQLiteDatabase dbTemp = null;

        try {

            File filePedido = new File(Util.dirApp(), "Temp.db");
            dbTemp = SQLiteDatabase.openDatabase(filePedido.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String queryEnc = "DELETE FROM IPV_med_exhibidor_encabezado WHERE ID = '" + idExhibidor + "'";
            dbTemp.execSQL(queryEnc);

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "Eliminar Exhibidor: " + mensaje, e);

        } finally {

            if (db != null)
                db.close();

            if (dbTemp != null)
                dbTemp.close();
        }
    }

    public static boolean estaExhibidoresClienteCompleto(String codigoCliente) {

        SQLiteDatabase db = null;
        int cantidadMediciones = 0;
        int cantidadMedicionesCompleta = 0;

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT COUNT(*) Cantidad, " +
                    "CASE WHEN SUM(Finalizado) IS NULL THEN 0 ELSE SUM(Finalizado) END AS CantidadFinalizado " +
                    "FROM IPV_med_exhibidor_cliente WHERE CodigoCliente = '" + codigoCliente + "'";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                cantidadMediciones = cursor.getInt(cursor.getColumnIndex("Cantidad"));
                cantidadMedicionesCompleta = cursor.getInt(cursor.getColumnIndex("CantidadFinalizado"));
            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();
        }

        return (cantidadMediciones == cantidadMedicionesCompleta);
    }

    public static boolean hayInformacionXEnviarExhibidores() {

        mensaje = "";
        SQLiteDatabase db = null;

        boolean hayInfoPendiente = false;
        Vector<String> tableNames = new Vector<String>();

        try {

            File dbFile = new File(Util.dirApp(), "Temp.db");

            if (dbFile.exists()) {

                db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
                String query = "SELECT tbl_name FROM sqlite_master WHERE tbl_name <> 'android_metadata' AND [type] = 'table' " +
                        "AND tbl_name IN ('IPV_med_exhibidor_cliente', 'IPV_med_exhibidor_encabezado', 'IPV_med_exhibidor_detalle')";

                Cursor cursor = db.rawQuery(query, null);

                if (cursor.moveToFirst()) {

                    do {

                        String tableName = cursor.getString(cursor.getColumnIndex("tbl_name"));
                        tableNames.addElement(tableName);

                    } while (cursor.moveToNext());
                }

                if (cursor != null)
                    cursor.close();

                for (String tableName : tableNames) {

                    query = "SELECT COUNT(*) AS total FROM " + tableName;
                    cursor = db.rawQuery(query, null);

                    if (cursor.moveToFirst()) {

                        int total = cursor.getInt(cursor.getColumnIndex("total"));

                        if (total > 0) {

                            hayInfoPendiente = true;
                            break;
                        }
                    }

                    if (cursor != null)
                        cursor.close();
                }

                if (cursor != null)
                    cursor.close();

            } else {

                Log.e(TAG, "hayInformacionXEnviar" + msg);
            }

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "hayInformacionXEnviar" + mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return hayInfoPendiente;
    }

    public static void actualizarTablaVendedoresASincronizar(Vector<Vendedor> listaVendedoresSincronizacion) {

        SQLiteDatabase dbConfig = null;

        try {

            dbFile = new File(Util.dirApp(), "Config.db");
            dbConfig = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            for (int i = 0; i < listaVendedoresSincronizacion.size(); i++) {

                String codigoVendedor = listaVendedoresSincronizacion.elementAt(i).codigoVendedor;

                if (listaVendedoresSincronizacion.elementAt(i).estadoActual == true) {

                    dbConfig.execSQL("UPDATE Vendedor SET seleccionado = '" + 1 + "' WHERE codigo = '" + codigoVendedor + "'");

                } else {

                    dbConfig.execSQL("UPDATE Vendedor SET seleccionado = '" + 0 + "' WHERE codigo = '" + codigoVendedor + "'");
                }
            }
        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("DataBaseBO", "ActualizarVendedoresSel: " + mensaje, e);

        } finally {

            if (dbConfig != null)
                dbConfig.close();
        }
    }

    public static void organizarTmpProductos() {

        SQLiteDatabase db = null;
        String sql = "";

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("DROP TABLE IF EXISTS ProductosTmp");

            sql = "CREATE TABLE ProductosTmp "
                    + "(codigo varchar(8), nombre varchar(40), precio int, iva float, saldo int, "
                    + "unidadmedida varchar(30), linead varchar(13), core varchar(10), ordenamiento varchar(10), "
                    + "ordenprod varchar(10), sublinea varchar(10), marca varchar(10), portafolio varchar(10), "
                    + "categoria varchar(10), subCategoria varchar(10), grupo varchar(10), GM4 varchar(10), "
                    + "bodega varchar(10), agrupacion varchar(10), lineaproduccion varchar(10), marcad varchar(10), "
                    + "prodCore varchar(10), cen_ext2 varchar(10), ean varchar(15), peso float, unidades varchar(40), itf varchar(14), "
                    + "Indice integer PRIMARY KEY AUTOINCREMENT)";

            db.execSQL(sql);
            sql = " CREATE INDEX idx1 ON ProductosTmp(nombre, lineaproduccion, linead, marcad)";
            db.execSQL(sql);
            sql = " CREATE INDEX idx2 ON ProductosTmp(nombre, linead, marcad)";
            db.execSQL(sql);
            sql = " CREATE INDEX idx3 ON ProductosTmp(nombre, marcad)";
            db.execSQL(sql);
            sql = " CREATE INDEX idx4 ON ProductosTmp(nombre)";
            db.execSQL(sql);
            sql = " CREATE INDEX idx5 ON ProductosTmp(codigo, lineaproduccion, linead, marcad)";
            db.execSQL(sql);
            sql = " CREATE INDEX idx6 ON ProductosTmp(codigo, linead, marcad)";
            db.execSQL(sql);
            sql = " CREATE INDEX idx7 ON ProductosTmp(codigo, marcad)";
            db.execSQL(sql);
            sql = " CREATE INDEX idx8 ON ProductosTmp(codigo)";

            db.execSQL(sql);

            // Bodega
            sql = "insert into ProductosTmp ( codigo, nombre, precio, iva, saldo, unidadmedida, linead, core, ordenamiento, ordenprod, "
                    + "sublinea, marca, portafolio, categoria, subCategoria, grupo, GM4, bodega, agrupacion, lineaproduccion, marcad, prodCore, "
                    + "cen_ext2, ean, peso, unidades, itf )"
                    + " SELECT Productos.codigo, Productos.nombre, Productos.precio, Productos.iva, "
                    + "CASE WHEN Productos.Saldo IS NULL THEN 0 ELSE Saldo END AS Saldo, Productos.unidadmedida, Productos.linead, Core2.core as core, Core2.ordenamiento, Core2.ordenprod, "
                    + "Productos.sublinea, Productos.marca, Productos.portafolio, Productos.categoria, Productos.subCategoria, Productos.grupo, Productos.GM4, Productos.bodega, "
                    + "Productos.agrupacion, Productos.lineaproduccion, Productos.marcad, Productos.core, Productos.cen_ext2, Productos.ean, Productos.peso, Productos.unidades, Productos.itf "
                    + "FROM Productos "
                    + "INNER JOIN Core2 on Core2.materia = Productos.codigo "
                    + "order by Productos.codigo";

            db.execSQL(sql);

        } catch (Exception e) {

            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();
        }
    }

    public static void descuentosTmp(String distrito,
                                     String codCliente,
                                     String canal,
                                     String subCanal,
                                     String tipologia,
                                     String grupoPrecio,
                                     String Gc2,
                                     String Gc3,
                                     String Gc4,
                                     String codigoCP) {

        String sql = "";
        double descuento = 0;
        int j = 0;
        double perDescuento = 0;
        double precio = 0;
        double iva = 0;
        double precioInicial = 0;

        SQLiteDatabase db = null;

        try {

            //Se registra el Log de la consulta SQl Registrada
            ArrayList<String> querys = new ArrayList<String>();

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null,
                    SQLiteDatabase.OPEN_READWRITE);

            // PRECIOS
            sql = "delete from tmpprecios";
            db.execSQL(sql);
            querys.add(sql);

            sql = "insert into tmpprecios ";
            sql = sql + " select materia,prioridad,precio from precios where cliente='" + codCliente + "'";
            sql = sql + " OR subcanal='" + subCanal + "'";
            sql = sql + " OR canal='" + canal + "'";
            sql = sql + " OR distrito='" + distrito + "'";
            sql = sql + " OR tipologia='" + tipologia + "'";
            sql = sql + " OR grupoprecios='" + grupoPrecio + "'";
            sql = sql + " OR secuencia='703'";
            sql = sql + " OR secuencia='702'";

            db.execSQL(sql);
            querys.add(sql);

            sql = "insert into tmpprecios ";
            sql = sql + " select materia,prioridad,precio from precios where (grupoclientes4='" + Gc4
                    + "' and grupoclientes2 is null and grupoclientes3 is null)";
            sql = sql + " OR (grupoclientes4='" + Gc4 + "' and grupoclientes2='" + Gc2
                    + "'  and grupoclientes3 is null)";
            sql = sql + " OR (grupoclientes4='" + Gc4 + "' and grupoclientes2='" + Gc2 + "' and grupoclientes3='" + Gc3
                    + "')";

            // DESCUENTOS
            sql = "delete from tmpdescuentos";
            db.execSQL(sql);
            querys.add(sql);

            sql = "insert into tmpdescuentos ";
            sql = sql
                    + "select TABLA,ORDENTABLA,SECUENCIA,PRIORIDAD,CANAL,MATERIAL,CATEGORIA,subcategoria,portafolio,GM4,sublinea,LINEA,MARCA,DESCUENTO,CANTIDAD,VALOR from descuentos where (tipologia='"
                    + tipologia + "' and distrito is null and canal is null)";
            sql = sql + " OR grupoprecio='" + grupoPrecio + "'";
            sql = sql + " OR cliente='" + codCliente + "'";
            sql = sql + " OR (subcanal='" + subCanal + "' and canal is null)";
            sql = sql + " OR (linea='" + Main.cliente.linea
                    + "' and cliente is null)";

            db.execSQL(sql);
            querys.add(sql);

            sql = "insert into tmpdescuentos ";
            sql = sql
                    + " select TABLA,ORDENTABLA,SECUENCIA,PRIORIDAD,CANAL,MATERIAL,CATEGORIA,subcategoria,portafolio,GM4,sublinea,LINEA,MARCA,DESCUENTO,CANTIDAD,VALOR from descuentos  where (canal='"
                    + canal + "' and subcanal is null and tipologia is null)";
            sql = sql + " OR (canal='" + canal + "' and subcanal='" + subCanal
                    + "')";
            sql = sql + " OR (canal='" + canal + "' and tipologia='"
                    + tipologia + "')";
            sql = sql + " OR (distrito='" + distrito
                    + "' and tipologia is null)";
            sql = sql + " OR (distrito='" + distrito + "' and tipologia='"
                    + tipologia + "')";
            sql = sql + " OR (condicionpago='" + codigoCP
                    + "' and tipologia is null and subcanal is null)";
            sql = sql + " OR (condicionpago='" + codigoCP + "' and tipologia='"
                    + tipologia + "')";
            sql = sql + " OR (condicionpago='" + codigoCP + "' and subcanal='"
                    + subCanal + "')";
            sql = sql + " OR secuencia='702'";
            sql = sql + " OR secuencia='703'";

            db.execSQL(sql);
            querys.add(sql);

            sql = "insert into tmpdescuentos ";
            sql = sql
                    + " select TABLA,ORDENTABLA,SECUENCIA,PRIORIDAD,CANAL,MATERIAL,CATEGORIA,subcategoria,portafolio,GM4,sublinea,LINEA,MARCA,DESCUENTO,CANTIDAD,VALOR from descuentos  where (grupoclientes4='"
                    + Gc4
                    + "' and grupoclientes2 is null and grupoclientes3 is null)";
            sql = sql + " OR (grupoclientes4='" + Gc4
                    + "' and grupoclientes2='" + Gc2
                    + "' and grupoclientes3 is null)";
            sql = sql + " OR (grupoclientes4='" + Gc4
                    + "' and grupoclientes2='" + Gc2 + "' and grupoclientes3='"
                    + Gc3 + "')";

            db.execSQL(sql);
            querys.add(sql);

            sql = "DELETE from tmpdescuentos where canal<>'" + canal
                    + "' and ifnull(canal,'')<>''";

            db.execSQL(sql);
            querys.add(sql);

        } catch (Exception e) {

            mensaje = e.getMessage();

        } finally {

            if (db != null)
                db.close();
        }
    }

    public static float[] calcularDescuentosInventarioProducto(Producto producto, boolean actualizarDescuento) {

        SQLiteDatabase db = null;
        String sql = "";

        float precio = 0, precioConIva = 0, descuento = 0, perDescuento = 0, precioInicial = 0, factor = 0;
        String condicion = "", condiciones = "";

        int inventario;

        String tabla, secuencia;
        int prioridad, ordentabla;
        float tDescuento;

        float _precio = 0, _descPer = 0, _descuento = 0, _precioIva = 0, _existencia = 0;

        Cursor cursor;

        try {

            precio = producto.precioCliente;
            precioConIva = precio + ((precio * producto.Iva) / 100);

            precioInicial = precio;

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null,
                    SQLiteDatabase.OPEN_READWRITE);

            sql = "select tabla, secuencia, prioridad, descuento, ordentabla from tmpdescuentos where ( ( marca = '"
                    + producto.Marca
                    + "' AND LINEA IS NULL ) OR ( linea = '"
                    + producto.Linea
                    + "' AND MARCA IS NULL ) OR  material = '"
                    + producto.codigo
                    + "' OR categoria = '"
                    + producto.Categoria
                    + "' OR subcategoria='"
                    + producto.Subcategoria
                    + "' OR portafolio = '"
                    + producto.Portafolio
                    + "' or GM4 = '"
                    + producto.GM4
                    + "' or sublinea = '"
                    + producto.Sublinea
                    + "' ) and CANTIDAD IS NULL AND VALOR IS NULL";
            sql += " union ";
            sql += " select tabla, secuencia, prioridad, descuento, ordentabla from tmpdescuentos where  secuencia = '742' and CANTIDAD IS NULL AND VALOR IS NULL";
            sql += " union ";
            sql += " select tabla, secuencia, prioridad, descuento, ordentabla from tmpdescuentos where  marca = '"
                    + producto.Marca
                    + "' and linea = '"
                    + producto.Linea
                    + "' and CANTIDAD IS NULL AND VALOR IS NULL  ";
            sql += " union ";
            sql += " select tabla, secuencia, prioridad, descuento, ordentabla from tmpdescuentos where MATERIAL IS NULL AND CATEGORIA IS NULL AND SUBCATEGORIA IS NULL AND PORTAFOLIO IS NULL AND GM4 IS NULL AND SUBLINEA IS NULL AND LINEA IS NULL AND MARCA IS NULL and CANTIDAD IS NULL AND VALOR IS NULL ";
            sql += "order by ordentabla, prioridad";

            cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst()) {

                do {

                    tabla = cursor.getString(cursor.getColumnIndex("tabla"));
                    secuencia = cursor.getString(cursor.getColumnIndex("secuencia"));
                    prioridad = cursor.getInt(cursor.getColumnIndex("prioridad"));
                    tDescuento = cursor.getFloat(cursor.getColumnIndex("descuento"));
                    ordentabla = cursor.getInt(cursor.getColumnIndex("ordentabla"));

                    if (!condicion.equals(tabla)) {

                        if (tabla.equals("ZBA3") || tabla.equals("ZPVA") || tabla.equals("ZBAA") || tabla.equals("ZPVB")) {

                            perDescuento = (tDescuento * 100) / precio;
                            precio = precio - tDescuento;

                        } else {

                            perDescuento = tDescuento;
                            descuento = (precio * perDescuento) / 100;
                            precio = precio - descuento;
                        }

                        condicion = tabla;

                        if (condiciones.equals("")) {

                            condiciones = tabla + "-" + secuencia + "," + tDescuento + "%";

                        } else {

                            condiciones += "|" + tabla + "-" + secuencia + "," + tDescuento + "%";
                        }

                    }

                } while (cursor.moveToNext());

                if (cursor != null)
                    cursor.close();

                if (precioInicial > 0) {

                    float cien = 100;

                    perDescuento = cien - ((precio / precioInicial) * cien);
                    descuento = ((precioInicial * perDescuento) / 100);

                } else {

                    descuento = 0;
                }

                sql = "Select * from UNIDADES where CODIGO = '" + producto.codigo + "' AND UM = '" + producto.UnidadMedida + "' ";

                factor = 1;

                cursor = db.rawQuery(sql, null);

                if (cursor.moveToFirst()) {

                    do {

                        factor = cursor.getFloat(cursor.getColumnIndex("Factor"));

                    } while (cursor.moveToNext());
                }

                if (cursor != null)
                    cursor.close();

                _precio = precioInicial * factor;

                _descPer = perDescuento;
                _descuento = descuento * factor;
                _precioIva = (_precio - _descuento)
                        * (1 + (producto.Iva / 100));

                inventario = 0;

                _existencia = inventario;

                if (actualizarDescuento) {

                    sql = "update detalle " + "set precio = " + _precio
                            + " , descuentoRenglon = " + _descuento + " "
                            + "where codigoRef = '" + producto.codigo
                            + "' and NumDoc = '";

                    db.execSQL(sql);
                }
            }
        } catch (Exception e) {
        } finally {

            if (db != null) {

                db.close();
            }
        }

        float datos[] = new float[4];

        datos[0] = _descPer;
        datos[1] = _descuento;
        datos[2] = _existencia;
        datos[3] = _precioIva;

        return datos;
    }

    public static void eliminarMedicionGeneral(ComponenteActivacionTerminado medicion) {

        SQLiteDatabase db = null;
        SQLiteDatabase dbTemp = null;

        try {

            dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            File filePedido = new File(Util.dirApp(), "Temp.db");
            dbTemp = SQLiteDatabase.openDatabase(filePedido.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            // SE DEBE REALIZAR LA ELIMINACION EN TEMP Y DATABASE
            // TABLAS A BORRAR "Fotos" - "IPV_med_activacion"
            String queryFotos = "DELETE FROM Fotos WHERE id = '" + medicion.id + "'";
            String queryMedicion = "DELETE FROM IPV_med_activacion WHERE ID = '" + medicion.id + "'";

            db.execSQL(queryFotos);
            db.execSQL(queryMedicion);
            dbTemp.execSQL(queryFotos);
            dbTemp.execSQL(queryMedicion);

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("DataBaseBO", "RegistrarProductoPedidos: " + mensaje, e);

        } finally {

            if (db != null)
                db.close();

            if (dbTemp != null)
                dbTemp.close();
        }
    }

    public static boolean sePuedeBorrarMedicion(ComponenteActivacionTerminado medicion) {

        SQLiteDatabase dbTemp = null;
        boolean respuesta = false;

        try {

            File filePedido = new File(Util.dirApp(), "Temp.db");
            dbTemp = SQLiteDatabase.openDatabase(filePedido.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "SELECT * FROM IPV_med_activacion WHERE ID = '" + medicion.id + "'";

            Cursor cursor = dbTemp.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                respuesta = true;
            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "Eliminar Exhibidor: " + mensaje, e);

        } finally {

            if (dbTemp != null)
                dbTemp.close();
        }

        return respuesta;
    }


    public static boolean guardarProductosPrecioCompetencia(String codigoCliente,
                                                            Usuario usuario,
                                                            int tipoUsuario,
                                                            String id,
                                                            String fecha, String codigoProducto, String nombreProducto, int precio) {

        SQLiteDatabase db = null;
        SQLiteDatabase dbTemp = null;

        try {

            dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            File filePedido = new File(Util.dirApp(), "Temp.db");
            dbTemp = SQLiteDatabase.openDatabase(filePedido.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            ContentValues values = new ContentValues();

            values.put("CodigoCliente", codigoCliente);
            values.put("CodigoUsuario", usuario.codigo);
            values.put("NombreUsuario", usuario.nombre);
            values.put("TipoUsuario", tipoUsuario);
            values.put("ID", id);
            values.put("CodigoProducto", codigoProducto);
            values.put("NombreProducto", nombreProducto);
            values.put("Precio", precio);
            values.put("PrecioV", precio);
            values.put("Core", 1);
            values.put("Propio", 0);
            values.put("Competencia", 1);
            values.put("FechaMovil", fecha);

            db.insertOrThrow("IPV_med_precios", null, values);
            dbTemp.insertOrThrow("IPV_med_precios", null, values);

            return true;

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("DataBaseBO", "RegistrarProductoPedidos: " + mensaje, e);
            return false;

        } finally {

            if (db != null)
                db.close();

            if (dbTemp != null)
                dbTemp.close();

            validarAgregarGestionCliente(codigoCliente);
        }
    }


    public static boolean guardarProductoCompetencia(String codigoProducto, String nombreProducto, String fecha) {

        SQLiteDatabase db = null;
        SQLiteDatabase dbTemp = null;

        try {

            dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

//            File filePedido = new File(Util.dirApp(), "Temp.db");
//            dbTemp = SQLiteDatabase.openDatabase(filePedido.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            ContentValues values = new ContentValues();

            values.put("codProdCompetencia", codigoProducto);
            values.put("desProdCompetencia", nombreProducto);


            db.insertOrThrow("ProductosCompetencia", null, values);

            values = new ContentValues();

            values.put("codigoProducto", codigoProducto);
            values.put("canal", Main.cliente.canal);
            values.put("ofiventas", Main.cliente.agencia);
            values.put("Grupoclientes4", Main.cliente.GC4);
            values.put("fechaRegistro", fecha);

            db.insertOrThrow("IPV_Config_ProductosCompetencia", null, values);
//            dbTemp.insertOrThrow("ProductosCompetencia", null, values);

            return true;

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("DataBaseBO", "RegistrarProductoPedidos: " + mensaje, e);
            return false;

        } finally {

            if (db != null)
                db.close();

            if (dbTemp != null)
                dbTemp.close();
        }
    }

    public static boolean eliminarProductoCompetencia(String codProducto) {

        SQLiteDatabase db = null;
        SQLiteDatabase dbTemp = null;
        boolean result = false;
        try {

            dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            File filePedido = new File(Util.dirApp(), "Temp.db");
            dbTemp = SQLiteDatabase.openDatabase(filePedido.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "DELETE FROM ProductosCompetencia WHERE codProdCompetencia = '" + codProducto + "'";
            String query2 = "DELETE FROM IPV_Config_ProductosCompetencia WHERE codigoProducto = '" + codProducto + "'";
            String query3 = "DELETE FROM IPV_med_precios WHERE CodigoProducto = '" + codProducto + "'";

            db.execSQL(query);
            db.execSQL(query2);
            db.execSQL(query3);

//            dbTemp.execSQL(query);
//            dbTemp.execSQL(query2);
            dbTemp.execSQL(query3);

            result = true;
        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e(TAG, "Eliminar Exhibidor: " + mensaje, e);
            return result;
        } finally {

            if (db != null)
                db.close();

            if (dbTemp != null)
                dbTemp.close();

            return result;
        }
    }


    public static Vector<ActividadesCliente> validarVisita(Vector<ItemListViewActividadesCliente> listaItems, String canal, String codigoCliente) {

        mensaje = "";
        ActividadesCliente actividadesCliente;
        SQLiteDatabase db = null;
        String sql = "";

        Cursor cursorTabla = null;

        Vector<ActividadesCliente> listaActividadesClientes = new Vector<ActividadesCliente>();

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            sql = "SELECT t.Canal Canal, t.Orden Orden, t.NombreTarea NombreTarea, t.TiempoTarea TiempoTarea " +
                    "FROM IPV_TareasCanal t inner join IPV_CategoriaTareas c ON (t.CategoriaTarea = c.CategoriaTarea) " +
                    "WHERE Canal = '" + canal + "' " +
                    "ORDER BY Orden";

            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst()) {

                do {

                    actividadesCliente = new ActividadesCliente();
                    ItemListViewActividadesCliente item = new ItemListViewActividadesCliente();

                    actividadesCliente.canal = cursor.getString(cursor.getColumnIndex("Canal"));
                    actividadesCliente.nombreTarea = cursor.getString(cursor.getColumnIndex("NombreTarea"));
                    actividadesCliente.tiempoTarea = cursor.getString(cursor.getColumnIndex("TiempoTarea"));
                    actividadesCliente.orden = String.valueOf(cursor.getInt(cursor.getColumnIndex("Orden")));

                    item.canal = actividadesCliente.canal;
                    item.nombreTarea = actividadesCliente.nombreTarea;
                    item.tiempoTarea = actividadesCliente.tiempoTarea;
                    item.orden = actividadesCliente.orden;

                    // SE DETERMINA EL NOMBRE DE LA TABLA DE CADA TAREA A FIN DE
                    // SABER SI TIENE O NO MEDICIONES REALIZADAS DE ESE TIPO DE TAREA
                    String nombreTabla = "";
                    int valorProOCom = -1;
                    int cantidadRegistro = 0;
                    int tipoIconoColor = 0;

                    if ((actividadesCliente.nombreTarea).equals("DISTRIBUCIÓN Y AGOTADOS")) {

                        nombreTabla = "IPV_med_agotados";

                    } else if ((actividadesCliente.nombreTarea).equals("EXHIBICIÓN DE PRODUCTOS")) {

                        nombreTabla = "IPV_med_exhibidor_encabezado";

                    } else if ((actividadesCliente.nombreTarea).equals("PRECIO DE PRODUCTOS")) {

                        nombreTabla = "IPV_med_precios";

                    } else if ((actividadesCliente.nombreTarea).equals("ACTIVACIÓN COMERCIAL PROPIA")) {

                        nombreTabla = "IPV_med_activacion";
                        valorProOCom = 0;

                    } else if ((actividadesCliente.nombreTarea).equals("ACTIVACIÓN COMERCIAL COMPETENCIA")) {

                        nombreTabla = "IPV_med_activacion";
                        valorProOCom = 1;
                    } else if ((actividadesCliente.nombreTarea).equals("PRECIOS Y DISPONIBILIDAD")) {

                        nombreTabla = "IPV_med_precios";
                    } else if ((actividadesCliente.nombreTarea).equals("ACTIVACIÓN")) {

                        nombreTabla = "IPV_med_activacion";
                        valorProOCom = 1;
                    }

                    // SE CREA LA CONSULTA
                    String sqlTablaCantidad = "";

                    if (valorProOCom == -1) {

                        sqlTablaCantidad = "SELECT COUNT(*) Cantidad FROM " + nombreTabla + " WHERE CodigoCliente = '" + codigoCliente + "'";

                    } else {

                        if (valorProOCom == 0) {

                            // PROPIO
                            sqlTablaCantidad = "SELECT COUNT(*) Cantidad FROM " + nombreTabla + " WHERE CodigoCliente = '" + codigoCliente + "' AND Propio = 1";

                        } else {

                            // COMPETENCIA
                            sqlTablaCantidad = "SELECT COUNT(*) Cantidad FROM " + nombreTabla + " WHERE CodigoCliente = '" + codigoCliente + "' AND Competencia = 1";
                        }
                    }

                    cursorTabla = db.rawQuery(sqlTablaCantidad, null);

                    if (cursorTabla.moveToFirst()) {

                        cantidadRegistro = cursorTabla.getInt(cursorTabla.getColumnIndex("Cantidad"));
                    }

                    if (cursorTabla != null)
                        cursorTabla.close();

                    // 0 VERDE
                    // 1 GRIS
                    // 2 NARANJA

                    if (valorProOCom == -1) {

                        if (cantidadRegistro > 0) {

                            tipoIconoColor = 0; // VERDE

                        } else {

                            tipoIconoColor = 1; // GRIS
                        }

                    } else {

                        if (cantidadRegistro == 3) {

                            tipoIconoColor = 0; // Verde

                        } else if (cantidadRegistro > 0 && cantidadRegistro < 3) {

                            tipoIconoColor = 2; // Naranja

                        } else if (cantidadRegistro == 0) {

                            tipoIconoColor = 1; // GRIS
                        }
                    }

                    actividadesCliente.tieneGestion = tipoIconoColor;
                    item.tieneGestion = tipoIconoColor;

                    listaItems.add(item);
                    listaActividadesClientes.addElement(actividadesCliente);

                } while (cursor.moveToNext());

                mensaje = "Actividades Cargadas Correctamente";

            } else {

                mensaje = "Consulta sin Resultados";
            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("ListaClientesRutero", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return listaActividadesClientes;
    }

    public static float getPrecioProducto(String codProducto) {

        mensaje = "";
        float precio = 0;
        SQLiteDatabase db = null;

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String query = "Select Precio AS Precio From TmpPrecios WHERE Codigo = '" + codProducto + "' ORDER BY Prioridad";

            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {

                precio = cursor.getFloat(cursor.getColumnIndex("Precio"));
                mensaje = "Cargo el producto correctamente";

            } else {

                query = "SELECT  Precio AS Precio From Productos where codigo = '" + codProducto + "'";

                Cursor cursor2 = db.rawQuery(query, null);

                if (cursor2.moveToFirst()) {

                    precio = cursor2.getFloat(cursor2.getColumnIndex("Precio"));
                }

                if (cursor2 != null)
                    cursor2.close();

                mensaje = "Consulta sin resultados";
            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("ProductoXCodigo", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return precio;
    }


    public static Vector<ActividadesCliente> obtenerListaGestionada( String canal, String codigoCliente, String idCategoria) {

        mensaje = "";
        ActividadesCliente actividadesCliente;
        SQLiteDatabase db = null;
        String sql = "";

        Cursor cursorTabla = null;

        Vector<ActividadesCliente> listaActividadesClientes = new Vector<ActividadesCliente>();

        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            String condicion= idCategoria==null?"":" AND t.CategoriaTarea = '"+idCategoria+"' ";


            sql = "SELECT distinct t.NombreTarea NombreTarea " +
                    "FROM IPV_TareasCanal t inner join IPV_CategoriaTareas c ON (t.CategoriaTarea = c.CategoriaTarea) " +
                    "WHERE Canal = '" + canal + "' " +
                     condicion  +
                    "ORDER BY t.Orden";

            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst()) {

                do {

                    actividadesCliente = new ActividadesCliente();

                    actividadesCliente.nombreTarea = cursor.getString(cursor.getColumnIndex("NombreTarea"));


                    // SE DETERMINA EL NOMBRE DE LA TABLA DE CADA TAREA A FIN DE
                    // SABER SI TIENE O NO MEDICIONES REALIZADAS DE ESE TIPO DE TAREA
                    String nombreTabla = "";
                    int valorProOCom = -1;
                    int cantidadRegistro = 0;
                    int tipoIconoColor = 0;

                    if ((actividadesCliente.nombreTarea).equalsIgnoreCase("DISTRIBUCIÓN Y AGOTADOS")) {

                        nombreTabla = "IPV_med_agotados";

                    } else if ((actividadesCliente.nombreTarea).equalsIgnoreCase("EXHIBICIÓN DE PRODUCTOS")) {

                        nombreTabla = "IPV_med_exhibidor_encabezado";

                    } else if ((actividadesCliente.nombreTarea).equalsIgnoreCase("PRECIO DE PRODUCTOS")) {

                        nombreTabla = "IPV_med_precios";

                    } else if ((actividadesCliente.nombreTarea).equalsIgnoreCase("ACTIVACIÓN COMERCIAL PROPIA")) {

                        nombreTabla = "IPV_med_activacion";
                        valorProOCom = 0;

                    } else if ((actividadesCliente.nombreTarea).equalsIgnoreCase("ACTIVACIÓN COMERCIAL COMPETENCIA")) {

                        nombreTabla = "IPV_med_activacion";
                        valorProOCom = 1;
                    } else if ((actividadesCliente.nombreTarea).equalsIgnoreCase("PRECIOS Y DISPONIBILIDAD")) {

                        nombreTabla = "IPV_med_precios";

                    } else if ((actividadesCliente.nombreTarea).equalsIgnoreCase("ACTIVACIÓN")) {

                        nombreTabla = "IPV_med_activacion";
                        valorProOCom = 1;
                    }

                    // SE CREA LA CONSULTA
                    String sqlTablaCantidad = "";
                    String condicionCount= idCategoria==null?"":" AND CategoriaTarea = '"+idCategoria+"' ";

                    if (valorProOCom == -1) {

                        sqlTablaCantidad = "SELECT COUNT(*) Cantidad FROM " + nombreTabla + " WHERE CodigoCliente = '" + codigoCliente + "' "+condicionCount;

                    } else {

                        if (valorProOCom == 0) {

                            // PROPIO
                            sqlTablaCantidad = "SELECT COUNT(*) Cantidad FROM " + nombreTabla + " WHERE CodigoCliente = '" + codigoCliente + "' AND Propio = 1 "+condicionCount;

                        } else {

                            // COMPETENCIA
                            sqlTablaCantidad = "SELECT COUNT(*) Cantidad FROM " + nombreTabla + " WHERE CodigoCliente = '" + codigoCliente + "' AND Competencia = 1 "+condicionCount;
                        }
                    }

                    cursorTabla = db.rawQuery(sqlTablaCantidad, null);

                    if (cursorTabla.moveToFirst()) {

                        cantidadRegistro = cursorTabla.getInt(cursorTabla.getColumnIndex("Cantidad"));
                    }

                    if (cursorTabla != null)
                        cursorTabla.close();

                    // 0 VERDE
                    // 1 GRIS
                    // 2 NARANJA

                    if (valorProOCom == -1) {

                        if (cantidadRegistro > 0) {

                            tipoIconoColor = 0; // VERDE

                        } else {

                            tipoIconoColor = 1; // GRIS
                        }

                    } else {

                        if (cantidadRegistro == 3) {

                            tipoIconoColor = 0; // Verde

                        } else if (cantidadRegistro > 0 && cantidadRegistro < 3) {

                            tipoIconoColor = 2; // Naranja

                        } else if (cantidadRegistro == 0) {

                            tipoIconoColor = 1; // GRIS
                        }
                    }

                    actividadesCliente.tieneGestion = tipoIconoColor;

                    listaActividadesClientes.addElement(actividadesCliente);

                } while (cursor.moveToNext());

                mensaje = "Actividades Cargadas Correctamente";

            } else {

                mensaje = "Consulta sin Resultados";
            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("ListaClientesRutero", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return listaActividadesClientes;
    }


    public static Vector<Cliente> getClientessyncronizar() {

        mensaje = "";
        Cliente cliente;
        SQLiteDatabase db = null;

        String sql = "";

        Vector<Cliente> listaClientes= new Vector<Cliente>();



        try {

            File dbFile = new File(Util.dirApp(), "DataBase.db");
            db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

            sql = "select distinct c.Codigo as Codigo, c.Canal as Canal " +
                    "from clientes  c " +
                    "where c.Codigo not in (select CodigoCliente from IPV_ClientesGestionados)";

            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst()) {

                do {

                    cliente = new Cliente();

                    cliente.codigo = cursor.getString(cursor.getColumnIndex("Codigo"));
                    cliente.canal = cursor.getString(cursor.getColumnIndex("Canal"));


                    listaClientes.addElement(cliente);


                } while (cursor.moveToNext());

                mensaje = "Actividades Cargadas Correctamente";

            } else {

                mensaje = "Consulta sin Resultados";
            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {

            mensaje = e.getMessage();
            Log.e("ListaClientesRutero", mensaje, e);

        } finally {

            if (db != null)
                db.close();
        }

        return listaClientes;
    }



}