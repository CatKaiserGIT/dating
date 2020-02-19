package controllers;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.bluelinelabs.conductor.Controller;

import butterknife.ButterKnife;
import butterknife.Unbinder;

abstract class BaseController extends Controller {
    protected static final int REQUEST_CAMERA_PERMISSION = 1302;
    private Unbinder unbinder;

    public BaseController() {
    }

    public BaseController(@Nullable Bundle args) {
        super(args);
    }

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(layoutId(), container, false);

        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // close the app
                Toast.makeText(getApplicationContext(), "You need to enable camera permission", Toast.LENGTH_LONG).show();
                return;
            }else{
                cameraEnabled();
                return;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected boolean requestPermission(){
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            return true;
        }
        return false;
    }

    protected void cameraEnabled(){

    }

    protected abstract int layoutId();

    @Override
    protected void onDestroyView(@NonNull View view) {
        super.onDestroyView(view);
        if (unbinder != null) {
            unbinder.unbind();
            unbinder = null;
        }
    }
}
