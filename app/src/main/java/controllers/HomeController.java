package controllers;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bluelinelabs.conductor.RouterTransaction;
import com.gebruder.R;

import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;

import static com.gebruder.utils.Utils.retrieveAvatars;

public class HomeController extends BaseController {

    @BindView(R.id.tv_online_users)
    TextView tv_online_users;

    @BindView(R.id.tv_page_title)
    TextView tv_page_title;

    @BindView(R.id.iv_photos_cycle)
    ImageView iv_photos_cycle;

    private int currentAvatarIndex = 0;


    public HomeController() {

    }

    @Override
    protected int layoutId() {
        return R.layout.controller_home;
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        tv_online_users.setText(String.format("ONLINE: %s", (500 + new Random().nextInt(1000))));

        currentAvatarIndex = new Random().nextInt(retrieveAvatars().length);
        setCurrentAvatar(currentAvatarIndex);
    }

    private void setCurrentAvatar(int index) {
        currentAvatarIndex = index;
        iv_photos_cycle.setImageResource(retrieveAvatars()[currentAvatarIndex]);
    }

    @OnClick(R.id.btn_cycle)
    void cycle() {
        setCurrentAvatar((currentAvatarIndex + 1 < retrieveAvatars().length ? currentAvatarIndex + 1 : 0));
    }

    @OnClick(R.id.btn_message)
    void message() {
        getRouter().pushController(RouterTransaction.with(new CameraController(currentAvatarIndex)));
    }

    @OnClick(R.id.btn_settings)
    void settings() {
        getRouter().pushController(RouterTransaction.with(new SettingsController()));
    }

    @OnClick(R.id.btn_start_searching)
    void search() {
        getRouter().pushController(RouterTransaction.with(new CameraController(-1)));
    }
}
