package component;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import co.com.celuweb.ipv.R;

import config.Const;

/**
 * Created by Cw Desarrollo on 16/09/2016.
 */
public class Alert {

    public static Dialog dialogo;

    /**
     * DIALOG PARA ALERTAS SENCILLAS CON UN SOLO BOTON
     * @param context
     * @param title
     * @param msg
     * @param neutral
     */
    public static void showDefault(Context context, String title, String msg, String neutral) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(msg);
        alertDialogBuilder.setNeutralButton(neutral == null ? "Aceptar" : neutral, null);
        alertDialogBuilder.show();
    }

    /**
     * DIALOG PARA ALERTAS CON METODOS EN LOS BOTONES
     * @param context
     * @param title
     * @param message
     * @param positivo
     * @param negative
     * @param onClickListener
     * @param onClickListenerNeg
     */
    public static void showDialog(Context context, String title, String message,
                                  String positivo, String negative,
                                  DialogInterface.OnClickListener onClickListener,
                                  DialogInterface.OnClickListener onClickListenerNeg) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setPositiveButton(positivo == null ? "Aceptar" : positivo, onClickListener);
        dialog.setNegativeButton(negative == null ? "Cancelar" : negative, onClickListenerNeg);
        dialog.show();
    }

    /**
     *
     * @param activity
     * @param title
     * @param msg
     * @param positive
     * @param negative
     * @param onClickListener
     * @param onClickListenerNeg
     */
    public static void nutresaShow(Activity activity, String title, String msg, String positive, String negative,
                                   View.OnClickListener onClickListener,
                                   View.OnClickListener onClickListenerNeg){

        dialogo = new Dialog(activity);
        dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogo.setContentView(R.layout.mensajeapp);

        TextView tvTitle = (TextView) dialogo.findViewById(R.id.tvTitle);
        tvTitle.setText(title);
        tvTitle.setTypeface(Const.letraSemibold);

        TextView tvMsg = (TextView) dialogo.findViewById(R.id.tvMsg);
        tvMsg.setText(msg);
        tvMsg.setTypeface(Const.letraRegular);

        if(title.equals("ERROR") || title.equals("ALERTA")) {

            tvTitle.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark));
            tvMsg.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark));
        }

        Button btnSi = (Button) dialogo.findViewById(R.id.btnSi);
        btnSi.setText(positive);
        btnSi.setTypeface(Const.letraRegular);

        Button btnNo = (Button) dialogo.findViewById(R.id.btnNo);
        btnNo.setTypeface(Const.letraRegular);

        if(negative == null){

            btnNo.setVisibility(View.GONE);

        } else {

            btnNo.setText(negative);
        }

        ((Button) dialogo.findViewById(R.id.btnSi)).setOnClickListener(onClickListener);
        ((Button) dialogo.findViewById(R.id.btnNo)).setOnClickListener(onClickListenerNeg);

        dialogo.setCancelable(true);
        dialogo.show();
    }

    /**
     *
     * @param activity
     * @param title
     * @param msg
     * @param positive
     * @param negative
     * @param onClickListener
     * @param onClickListenerNeg
     * @param cancelable
     */
    public static void nutresaShow(Activity activity, String title, String msg, String positive, String negative,
                                   View.OnClickListener onClickListener,
                                   View.OnClickListener onClickListenerNeg,Boolean cancelable){

        dialogo = new Dialog(activity);
        dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogo.setContentView(R.layout.mensajeapp);

        TextView tvTitle = (TextView) dialogo.findViewById(R.id.tvTitle);
        tvTitle.setText(title);
        tvTitle.setTypeface(Const.letraSemibold);

        TextView tvMsg = (TextView) dialogo.findViewById(R.id.tvMsg);
        tvMsg.setText(msg);
        tvMsg.setTypeface(Const.letraRegular);

        if(title.equals("ERROR") || title.equals("ALERTA")) {

            tvTitle.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark));
            tvMsg.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark));
        }

        Button btnSi = (Button) dialogo.findViewById(R.id.btnSi);
        btnSi.setText(positive);
        btnSi.setTypeface(Const.letraRegular);

        Button btnNo = (Button) dialogo.findViewById(R.id.btnNo);
        btnNo.setTypeface(Const.letraRegular);

        if(negative == null){

            btnNo.setVisibility(View.GONE);

        } else {

            btnNo.setText(negative);
        }

        ((Button) dialogo.findViewById(R.id.btnSi)).setOnClickListener(onClickListener);
        ((Button) dialogo.findViewById(R.id.btnNo)).setOnClickListener(onClickListenerNeg);

        dialogo.setCancelable(cancelable);
        dialogo.show();
    }
}
