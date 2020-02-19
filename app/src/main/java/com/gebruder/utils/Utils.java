package com.gebruder.utils;


import android.content.Context;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;

import timber.log.Timber;

import static com.gebruder.Constants.female_avatars;
import static com.gebruder.Constants.male_avatars;

public class Utils {

    public static String findFrontFacingCamera(Context context) {

        CameraManager cm = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String id : cm.getCameraIdList()) {
                CameraCharacteristics cc = cm.getCameraCharacteristics(id);
                if (cc.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
                    return id;
                }
            }
        } catch (Exception e) {
            Timber.d("No camera found");
        }
        return null;
    }

    public static int[] retrieveAvatars() {
        int[] array = null;
        if (Shared.getInstance().isMaleShown()) {
            Timber.d("male shown");
            array = male_avatars;
        }
        if (Shared.getInstance().isFemaleShown()) {
            Timber.d("female shown");
            if (array != null) {
                int[] newArr = new int[female_avatars.length + male_avatars.length];
                for (int i = 0; i < female_avatars.length; i++) {
                    newArr[i * 2] = female_avatars[i];
                    newArr[i * 2 + 1] = male_avatars[i];
                }
                array = newArr;
            } else {
                array = female_avatars;
            }
        }
        return array;
    }
}
