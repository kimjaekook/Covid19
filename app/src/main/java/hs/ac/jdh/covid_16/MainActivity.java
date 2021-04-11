package hs.ac.jdh.covid_16;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tvTitle;
        tvTitle = (TextView) findViewById(R.id.tvTitle);

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
        tvTitle.startAnimation(animation);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);

            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }
        return super.onTouchEvent(event);
    }
}