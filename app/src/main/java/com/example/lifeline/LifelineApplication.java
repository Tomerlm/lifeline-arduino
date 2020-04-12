package com.example.lifeline;

import android.app.Application;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class LifelineApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/MontserratRegular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
    }
}
