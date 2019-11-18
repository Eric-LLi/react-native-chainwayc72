package com.chainwayc72;

import android.util.Log;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Promise;
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
			this.scannerthread.start();
		} catch (Exception err) {
			Log.e("InitialThread", err.getMessage());
		}
	}

	@ReactMethod
	public void init(Promise promise) {
		try {
			if (this.scannerthread != null) {
				boolean result = this.scannerthread.init();
				promise.resolve(result);
			}
		} catch (Exception err) {
			promise.reject(err);
		}
	}

	@ReactMethod
	public void shutdown(Promise promise) {
		try {
			if (this.scannerthread != null) {
				this.scannerthread.shutdown();
				promise.resolve(true);
			}
		} catch (Exception err) {
			promise.reject(err);
		}
	}

	@ReactMethod
	public void enableReader(boolean isEnable, Promise promise) {
		try {
			if (this.scannerthread != null) {
				boolean result = this.scannerthread.enableReader(isEnable);
				promise.resolve(result);
			}
		} catch (Exception err) {
			promise.reject(err);
		}
	}

	@ReactMethod
	public void read(boolean isSingleRead, Promise promise) {
		try {
			if (this.scannerthread != null) {
				this.scannerthread.read(isSingleRead);
				promise.resolve(true);
			}
		} catch (Exception err) {
			promise.reject(err);
		}
	}

	@ReactMethod
	public void cancel(Promise promise) {
		try {
			if (this.scannerthread != null) {
				boolean result = this.scannerthread.cancel();
				promise.resolve(result);
			}
		} catch (Exception err) {
			promise.reject(err);
		}
	}

	@ReactMethod
	public void getAntennaLevel(Promise promise) {
		try {
			if (this.scannerthread != null) {
				int power = this.scannerthread.getAntennaLevel();
				promise.resolve(power);
			}
		} catch (Exception err) {
			promise.reject(err);
		}
	}

	@ReactMethod
	public void setAntennaLevel(int power, Promise promise) {
		try {
			if (this.scannerthread != null) {
				boolean result = this.scannerthread.setAntennaLevel(power);
				promise.resolve(result);
			}
		} catch (Exception err) {
			promise.reject(err);
		}
	}

	@ReactMethod
	public void saveCurrentRoute(String route, Promise promise) {
		try {
			if (this.scannerthread != null) {
				this.scannerthread.saveCurrentRoute(route);
			}
			promise.resolve(true);
		} catch (Exception err) {
			promise.reject(err);
		}
	}

	@ReactMethod
	public void writeTag(String targetTag, String newTag, Promise promise) {
		try {
			if (this.scannerthread != null) {
				promise.resolve(this.scannerthread.writeTag(targetTag, newTag));
			}
		} catch (Exception err) {
			promise.reject(err);
		}
	}

	@ReactMethod
	public void cleanTags() {
		if (this.scannerthread != null) {
			this.scannerthread.cleanTags();
		}
	}
}
