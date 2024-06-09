package com.bypriyan.findro.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.bypriyan.findro.Constant;

public class preferenceManager {

    private final SharedPreferences sharedPreferences;
    public preferenceManager(Context context){
        sharedPreferences = context.getSharedPreferences(Constant.KEY_PREFERENCE_NAME, context.MODE_PRIVATE);

    }
    public void putBoolean(String key, Boolean Value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, Value);
        editor.apply();
    }

    public boolean getBoolean (String key){
        return sharedPreferences.getBoolean(key, false);
    }

    public void putString(String key, String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();

    }

    public String getString(String key){
        return sharedPreferences.getString(key, null);
    }

    public void clear(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

}
