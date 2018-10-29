package xyz.pickles.empire;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {

    private static Context context;
    private static String globalToken;
    private static String globalAddress;

    public static String getToken(){
        return globalToken;
    }

    public void setToken(String str){
        globalToken = str;
    }

    public static String getAddress(){
        return globalAddress;
    }

    public void setAddress(String address){
        globalAddress = address;
    }

    public void onCreate(){
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public static Context getContext(){
        return MyApplication.context;
    }
}