package co.com.celuweb.ipv;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        // start thread
        background.start();
    }

    Thread background = new Thread() {

        public void run() {

            try {

                // Thread will sleep for 5 seconds
                sleep(5 * 1000);

                // After 5 seconds redirect to another intent
                Intent i = new Intent(getBaseContext(), LogInActivity.class);
                startActivity(i);

                //Remove activity
                finish();

            } catch (Exception e) {}
        }
    };
}
