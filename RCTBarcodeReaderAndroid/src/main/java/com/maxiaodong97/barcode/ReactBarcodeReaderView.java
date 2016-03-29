package com.maxiaodong97.barcode;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.hardware.Camera;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;

import com.maxiaodong97.barcode.ui.camera.CameraSource;
import com.maxiaodong97.barcode.ui.camera.CameraSourcePreview;
import com.maxiaodong97.barcode.ui.camera.GraphicOverlay;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReactBarcodeReaderView extends CameraSourcePreview {
    // intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;
    private CameraSource mCameraSource;
    private GraphicOverlay<BarcodeGraphic> mGraphicOverlay;

    public ReactBarcodeReaderView(Context context, AttributeSet attr) {
        super(context, attr);
        mGraphicOverlay = new GraphicOverlay<BarcodeGraphic>(context, attr);
        mGraphicOverlay.setUpdateListener(mOnUpdateListener);

        // read parameters from the intent used to launch the activity.
        boolean autoFocus = true;
        boolean useFlash = false;
        int barcodeFormats = Barcode.ALL_FORMATS;
        createCameraSource(autoFocus, useFlash, barcodeFormats);
        startCameraSource();
    }
    /**
     * Override in activity to handle barcode detector updates
     *
     * @param barcodes - a list of detected barcodes
     */
    protected void onBarcodesUpdated(List<Barcode> barcodes){
        if (barcodes.size() > 0) {
            Barcode result = barcodes.get(0);
            WritableMap event = Arguments.createMap();

            Log.d(Logger.TAG, "barcode scanned is " + result.toString());
            event.putString("data", result.rawValue);
            event.putString("type", Integer.toString(result.format));

            ReactContext reactContext = (ReactContext) getContext();

            reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                    getId(),
                    "topChange",
                    event
            );
            stopUpdates();
        }
    }

    /**
     * Removes update listener from graphic overlay
     */

    protected void stopUpdates(){
        mGraphicOverlay.setUpdateListener(null);
    }
    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes
     * at long distances.
     *
     * Suppressing InlinedApi since there is a check that the minimum version is met before using
     * the constant.
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("InlinedApi")
    private void createCameraSource(boolean autoFocus, boolean useFlash, int barcodeFormats) {
        Context context = getContext().getApplicationContext();

        // A barcode detector is created to track barcodes.  An associated multi-processor instance
        // is set to receive the barcode detection results, track the barcodes, and maintain
        // graphics for each barcode on screen.  The factory is used by the multi-processor to
        // create a separate tracker instance for each barcode.
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context).
                setBarcodeFormats(barcodeFormats).
                build();

        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(mGraphicOverlay);

        barcodeDetector.setProcessor(
                new MultiProcessor.Builder<Barcode>(barcodeFactory).build());

        if (!barcodeDetector.isOperational()) {
            // Note: The first time that an app using the barcode or face API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any barcodes
            // and/or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.
            Log.w(Logger.TAG, "Barcode detector dependencies are not yet available");

        }

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the barcode detector to detect small barcodes
        // at long distances.
        CameraSource.Builder builder = new CameraSource.Builder(getContext().getApplicationContext(), barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1600, 1200)
                .setRequestedFps(15.0f);

        // make sure that auto focus is an available option
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            builder = builder.setFocusMode(
                    autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null);
        }

        mCameraSource = builder
                .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                .build();
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    public void startCameraSource() throws SecurityException {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext().getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog((Activity) super.getContext(), code, RC_HANDLE_GMS);
            dlg.show();
        }
        if (mCameraSource != null) {
            try {
                start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(Logger.TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }


    private GraphicOverlay.OnUpdateListener mOnUpdateListener = new GraphicOverlay.OnUpdateListener(){

        @Override
        public void onUpdated() {
            List<Barcode> barcodes = new ArrayList<Barcode>();
            List<BarcodeGraphic> detected = mGraphicOverlay.getGraphics();
            if(null != detected){
                for(BarcodeGraphic g : detected){
                    if(null != g && null != g.getBarcode()){
                        barcodes.add(g.getBarcode());
                    }
                }
            }
            onBarcodesUpdated(barcodes);
        }
    };
}
