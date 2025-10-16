package com.example.pennywise;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 4000;

    private ImageView moneyIcon;
    private TextView appName, tagline;
    private Animation bounce, fadeIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initializeViews();
        startAnimations();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                navigateToLogin();
            }
        }, SPLASH_DELAY);
    }

    private void initializeViews() {
        moneyIcon = findViewById(R.id.splash_money_icon);
        appName = findViewById(R.id.splash_app_name);
        tagline = findViewById(R.id.splash_tagline);

        bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
    }

    private void startAnimations() {
        moneyIcon.startAnimation(bounce);
        moneyIcon.setAlpha(1.0f);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                appName.startAnimation(fadeIn);
            }
        }, 800);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tagline.startAnimation(fadeIn);
                tagline.setAlpha(1.0f);
            }
        }, 1600);
    }

    private void navigateToLogin() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}