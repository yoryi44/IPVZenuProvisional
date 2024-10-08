package component;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import co.com.celuweb.ipv.R;
import config.Const;

public class DialogoObservacion {

    public static Dialog dialogo;

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
    public static void nutresaShow(Context activity, String title, String msg, String positive, String negative,
                                   View.OnClickListener onClickListener,
                                   View.OnClickListener onClickListenerNeg){

        dialogo = new Dialog(activity);
        dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogo.setContentView(R.layout.observacionapp);

        TextView tvTitle = (TextView) dialogo.findViewById(R.id.tvTitle);
        tvTitle.setText(title);
        tvTitle.setTypeface(Const.letraSemibold);

        EditText etObservacion = dialogo.findViewById(R.id.etObservacion);
        etObservacion.setTypeface(Const.letraSemibold);

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
}
