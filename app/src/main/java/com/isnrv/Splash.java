package com.isnrv;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
/**
 * This class shows a loading animation at the start of the application
 * @author Yasir
 *
 */
public class Splash extends Activity {
	private final String TAG = "Splash";
    protected boolean _active = true;
    protected int _splashTime = 2000;
    boolean isRunning = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);              
        isRunning = true;
        StartAnimations();
        Thread splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (_active && (waited < _splashTime)) {
                        sleep(100);
                        if (_active) {
                            waited += 100;
                        }
                    }
                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    finish();
                    if(isRunning)
                        startActivity(new Intent(getApplicationContext(), Main.class));
                }
            }
        };
        splashTread.start();
        
        Intent intent = new Intent(getApplicationContext(), ScheduledService.class);
        if(PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_NO_CREATE) == null){
        	//start ScheduledService for prayers
        	Log.d(TAG, "Start ScheduledService");
            startService(intent);
        }
        
        
        
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            _active = false;
        }
        return true;
    }

    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.slow_fade_in);
        anim.reset();
        /*LinearLayout l = (LinearLayout) findViewById(R.id.splash_layout);
        l.clearAnimation();
        l.startAnimation(anim);*/

        //anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        //anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.splash_logo);
        iv.clearAnimation();
        iv.startAnimation(anim);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }


}