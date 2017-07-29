package com.wmd.demo.scaleringview;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ScaleRingView radiationcontrolview;
    private  float preValue = 0;
    private  float alpha;
    private Handler mH=new Handler();
    private Runnable mR=new Runnable() {
        @Override
        public void run() {
            mH.postDelayed(mR,1800);
            showParatemer(String.valueOf(new Random().nextInt(250)),radiationcontrolview);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        radiationcontrolview = (ScaleRingView) findViewById(R.id.radiationcontrolview);
        radiationcontrolview.setRadiationUnitText("cm/Lg");
        mH.post(mR);
    }

    @Override
    protected void onResume() {
        super.onResume();



    }

    public  void showParatemer(String radiation_value,
                               final ScaleRingView radiationcontrolview
    ) {
        final float radiationValue = (float) Integer.parseInt(radiation_value) / 100;
        radiationcontrolview.setDetectRadiationValue(radiationValue);
        if ((radiationValue >= 0 && radiationValue < radiationcontrolview.STAGE_1_VALUE
                && preValue >= 0 && preValue < radiationcontrolview.STAGE_1_VALUE) ||
                (radiationValue >= radiationcontrolview.STAGE_1_VALUE &&
                        radiationValue < radiationcontrolview.STAGE_2_VALUE
                        && preValue >= radiationcontrolview.STAGE_1_VALUE &&
                        preValue < radiationcontrolview.STAGE_2_VALUE) ||
                (radiationValue > radiationcontrolview.STAGE_2_VALUE &&
                        preValue > radiationcontrolview.STAGE_2_VALUE)) {
            alpha = 1.0f;
        } else {
            alpha = 0.095f;
        }
    }
}
