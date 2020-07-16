package pushy.fastech.pk.splashscreen;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.material.components.R;
import com.material.components.activity.login.LoginSimpleGreen;

public class SplashScreen extends AppCompatActivity {

    private  static int SPALSH_TIME_OUT = 4000;
    private ImageView logo;
    private Animation anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        // Floating Animation
        anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.floating_up);
        anim.setDuration(500);
        logo = findViewById(R.id.splash_logo);
        logo.setAnimation(anim);

        // Timer to exit screen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, LoginSimpleGreen.class);
                startActivity(intent);
                finish();
            }
        }, SPALSH_TIME_OUT);
    }
}
