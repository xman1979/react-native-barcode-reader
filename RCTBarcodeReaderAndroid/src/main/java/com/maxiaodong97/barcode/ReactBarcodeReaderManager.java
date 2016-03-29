package com.maxiaodong97.barcode;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;

public class ReactBarcodeReaderManager extends ViewGroupManager<ReactBarcodeReaderView> implements LifecycleEventListener {
    private static final String REACT_CLASS = "RCTBarcodeReaderView";

    private ReactBarcodeReaderView mReaderView;
    private boolean mReaderViewVisible;

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public ReactBarcodeReaderView createViewInstance(ThemedReactContext context) {
        context.addLifecycleEventListener(this);
        mReaderView = new ReactBarcodeReaderView(context, null);
        mReaderViewVisible = true;
        return mReaderView;
    }

    @Override
    public void onDropViewInstance(ReactBarcodeReaderView view) {
        mReaderViewVisible = false;
        mReaderView.stop();
    }

    @Override
    public void onHostResume() {
        if (mReaderViewVisible) {
            mReaderView.startCameraSource();
        }
    }

    @Override
    public void onHostPause() {
        mReaderView.stop();
    }

    @Override
    public void onHostDestroy() {
        mReaderView.stop();
        mReaderView.release();
    }
}
