package com.gebruder.utils;

import android.content.Context;
import android.content.SharedPreferences;

import timber.log.Timber;

public class Shared {

    private static final Shared instance = new Shared();
    private SharedPreferences shared;

    private static final String KEY_HD = "hd";
    private static final String KEY_MALE = "male";
    private static final String KEY_FEMALE = "female";

    public static Shared getInstance() {
        return instance;
    }

    public void init(Context context) {
        shared = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    public void saveHD(boolean isHD) {
        shared.edit().putBoolean(KEY_HD, isHD).apply();
    }

    public boolean isHD() {
        return shared.getBoolean(KEY_HD, false);
    }

    public boolean isMaleShown() {
        return shared.getBoolean(KEY_MALE, true);
    }

    public boolean isFemaleShown() {
        return shared.getBoolean(KEY_FEMALE, true);
    }

    public void showMale(boolean show) {
        Timber.d("showMale: %s", show);
        shared.edit().putBoolean(KEY_MALE, show).apply();
    }

    public void showFemale(boolean show) {
        Timber.d("showFemale: %s", show);
        shared.edit().putBoolean(KEY_FEMALE, show).apply();
    }
}
