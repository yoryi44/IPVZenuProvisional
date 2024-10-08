package config;

/**
 * Created by Cw Desarrollo on 16/09/2016.
 */
public interface Synchronizer {

    public void respSync(boolean ok, String respuestaServer, String msg, int codeRequest);
}
