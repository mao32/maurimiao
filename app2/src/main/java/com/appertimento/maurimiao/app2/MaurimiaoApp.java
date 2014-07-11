package com.appertimento.maurimiao.app2;

import android.app.Application;
import android.content.Context;

/**
 * Created by mcolombo on 29/04/14.
 */
public class MaurimiaoApp extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }
}
