package controllers;

import android.Manifest;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.app.ActivityCompat;

import com.gebruder.R;
import com.gebruder.utils.Shared;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class SettingsController extends BaseController {

    @BindView(R.id.s_permission)
    Switch s_permission;

    @BindView(R.id.s_hd)
    Switch s_hd;

    @BindView(R.id.cb_male)
    AppCompatCheckBox cb_male;

    @BindView(R.id.cb_female)
    AppCompatCheckBox cb_female;

    @Override
    protected int layoutId() {
        return R.layout.controller_settings;
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            s_permission.setChecked(false);
        } else {
            cameraEnabled();
        }
        s_hd.setChecked(Shared.getInstance().isHD());

        cb_female.setChecked(Shared.getInstance().isFemaleShown());
        cb_male.setChecked(Shared.getInstance().isMaleShown());
    }

    @OnClick(R.id.btn_back)
    void onBack() {
        getRouter().handleBack();
    }

    @OnClick(R.id.s_permission)
    void requestCamera() {
        requestPermission();
    }

    @OnClick(R.id.s_hd)
    void onHd() {
        Shared.getInstance().saveHD(s_hd.isChecked());
    }

    @Override
    protected void cameraEnabled() {
        s_permission.setChecked(true);
        s_permission.setEnabled(false);
    }

    @OnCheckedChanged(R.id.cb_female)
    void onFemale(){
        Shared.getInstance().showFemale(cb_female.isChecked());
        if(!Shared.getInstance().isFemaleShown() && !Shared.getInstance().isMaleShown()){
            cb_male.setChecked(true);
        }
    }

    @OnCheckedChanged(R.id.cb_male)
    void onMale(){
        Shared.getInstance().showMale(cb_male.isChecked());
        if(!Shared.getInstance().isFemaleShown() && !Shared.getInstance().isMaleShown()){
            cb_female.setChecked(true);
        }
    }
}
