package com.chainwayc72;

import android.util.Log;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public class Chainwayc72Module extends ReactContextBaseJavaModule implements LifecycleEventListener {

	private final ReactApplicationContext reactContext;
	private Chinawayc72Thread scannerthread = null;

	public Chainwayc72Module(ReactApplicationContext reactContext) {
		super(reactContext);
		this.reactContext = reactContext;
		this.reactContext.addLifecycleEventListener(this);

		if (this.scannerthread == null) {
			InitialThread();
		}
		Log.v("RFID", "RFIDScannerManager created");
	}

	@Override
	public String getName() {
		return "Chainwayc72";
	}

	@Override
	public void onHostResume() {
		if (this.scannerthread != null) {
			this.scannerthread.onHostResume();
		}
	}

	@Override
	public void onHostPause() {
		if (this.scannerthread != null) {
			this.scannerthread.onHostPause();
		}
	}

	@Override
	public void onHostDestroy() {
		if (this.scannerthread != null) {
			this.scannerthread.onHostDestroy();
		}
	}

	@ReactMethod
	public void InitialThread() {
		try {
			if (this.scannerthread != null) {
				this.scannerthread.interrupt();
			}
			this.scannerthread = new Chinawayc72Thread(this.reactContext) {

				@Override
				public void dispatchEvent(String name, WritableMap data) {
					Chainwayc72Module.this.reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(name, data);
				}

				@Override
				public void dispatchEvent(String name, String data) {
					Chainwayc72Module.this.reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(name, data);
				}

				@Override
				public void dispatchEvent(String name, WritableArray data) {
					Chainwayc72Module.this.reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(name, data);
				}

				@Override
				public void dispatchEvent(String name, boolean data) {
					Chainwayc72Module.this.reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(name, data);
				}
			};
			scannerthread.start();
		} catch (Exception err) {
			Log.e("Initial_Thread", err.getMessage());
		}
	}
}
