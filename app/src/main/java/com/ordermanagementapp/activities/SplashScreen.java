package com.ordermanagementapp.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.ordermanagementapp.R;

public class SplashScreen extends AppCompatActivity {
    ImageView ivOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ivOrder     = findViewById(R.id.ivOrder);

        Animation animation = AnimationUtils.loadAnimation(this,R.anim.mytransition);
        ivOrder.startAnimation(animation);

        int SPLASH_TIME_OUT = 3000;
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                Intent intent   = new Intent(SplashScreen.this,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);

            }
        }, SPLASH_TIME_OUT);
    }
}
