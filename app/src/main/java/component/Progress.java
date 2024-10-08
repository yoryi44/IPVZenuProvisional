package component;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.TextView;

import co.com.celuweb.ipv.R;
import config.Const;

/**
 * Created by Cw Desarrollo on 16/09/2016.
 */
public class Progress {

    private static ProgressDialog progressDialog;

    /**
     * Este metodo me permite abrir barra de progreso
     * @param activity
     * @param title
     * @param msg
     * @param cancelable
     */
    public static void show(Activity activity, String title, String msg, boolean cancelable) {

        progressDialog = new ProgressDialog(activity);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);

        ((TextView) progressDialog.findViewById(R.id.tvTitle)).setText(title.toUpperCase());
        ((TextView) progressDialog.findViewById(R.id.tvTitle)).setTypeface(Const.letraSemibold);
        ((TextView) progressDialog.findViewById(R.id.tvMsg)).setText(msg.toUpperCase());
        ((TextView) progressDialog.findViewById(R.id.tvMsg)).setTypeface(Const.letraRegular);

        progressDialog.setCancelable(cancelable);
    }

    /**
     * Este metodo me permite cerrar barra de progreso
     */
    public static void hide(){
        progressDialog.dismiss();
    }
}
