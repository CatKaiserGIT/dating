package controllers;

import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gebruder.R;
import com.gebruder.utils.BundleBuilder;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

import static com.gebruder.utils.Utils.findFrontFacingCamera;
import static com.gebruder.utils.Utils.retrieveAvatars;

public class CameraController extends BaseController {

    private static final String KEY_AVATAR_INDEX = "avatar_index";

    @BindView(R.id.civ_top)
    CircleImageView civ_top;

    @BindView(R.id.pb_image_loading)
    ProgressBar pb_image_loading;

    @BindView(R.id.tv_camera)
    TextureView tv_camera;

    @BindView(R.id.btn_enable_camera)
    Button btn_enable_camera;

    private CameraDevice currentCamera;
    protected CaptureRequest captureRequest;
    protected CameraCaptureSession cameraCaptureSessions;
    protected CaptureRequest.Builder captureRequestBuilder;
    private Handler backgroundHandler;
    private HandlerThread backgroundThread;

    public CameraController(int avatarInt) {
        this(new BundleBuilder(new Bundle())
                .putInt(KEY_AVATAR_INDEX, avatarInt)
                .build());
    }

    public CameraController(@Nullable Bundle args) {
        super(args);
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);

        int index = getArgs().getInt(KEY_AVATAR_INDEX);
        if (index >= 0) {
            civ_top.setImageResource(retrieveAvatars()[index]);
        }
    }

    @Override
    protected int layoutId() {
        return R.layout.controller_camera;
    }

    @OnClick(R.id.btn_back)
    void back() {
        getRouter().handleBack();
    }

    @OnClick(R.id.btn_enable_camera)
    @SuppressWarnings("all")
    void enableCamera() {
        if (requestPermission()) {
            return;
        }
        try {
            String frontCamera = findFrontFacingCamera(getActivity());
            if (frontCamera != null) {
                CameraManager cm = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
                cm.openCamera(frontCamera, stateCallback, null);
            } else {
                Toast.makeText(getActivity(), "No front camera found", Toast.LENGTH_LONG).show();
            }
            btn_enable_camera.setVisibility(View.GONE);
        } catch (Exception e) {
        }
    }

    @Override
    protected void cameraEnabled() {
        enableCamera();
    }

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            //This is called when the camera is open
            Timber.d("Camera opened");
            currentCamera = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            currentCamera.close();
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            currentCamera.close();
            currentCamera = null;
        }
    };

    private void createCameraPreview() {
        try {
            SurfaceTexture texture = tv_camera.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(tv_camera.getWidth(), tv_camera.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = currentCamera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            currentCamera.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    //The camera is already closed
                    if (null == currentCamera) {
                        return;
                    }
                    // When the session is ready, we start displaying the preview.
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(getApplicationContext(), "Configuration change", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void updatePreview() {
        if (null == currentCamera) {
            Timber.e("updatePreview error, return");
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void startBackgroundThread() {
        backgroundThread = new HandlerThread("Camera Background");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        if (backgroundThread == null) return;
        backgroundThread.quitSafely();
        try {
            backgroundThread.join();
            backgroundThread = null;
            backgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResumed(@NonNull Activity activity) {
        startBackgroundThread();
        super.onActivityResumed(activity);
    }

    @Override
    protected void onActivityPaused(@NonNull Activity activity) {
        stopBackgroundThread();
        super.onActivityPaused(activity);
    }


}
