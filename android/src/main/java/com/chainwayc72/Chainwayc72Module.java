package com.chainwayc72;

import android.util.Log;
import android.view.KeyEvent;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Promise;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public class Chainwayc72Module extends ReactContextBaseJavaModule implements LifecycleEventListener {

	private final ReactApplicationContext context;
	private Chinawayc72Thread scannerthread = null;
	private static boolean isPulledTrigger = false;
	private static Chainwayc72Module instance = null;

	public Chainwayc72Module(ReactApplicationContext reactContext) {
		super(reactContext);
		this.context = reactContext;
		this.context.addLifecycleEventListener(this);

		instance = this;
		if (scannerthread == null) {
			InitialThread();
		}
		Log.v("RFID", "RFIDScannerManager created");
	}

	public static Chainwayc72Module getInstance() {
		return instance;
	}

	public boolean getIsPulledTrigger() {
		return isPulledTrigger;
	}

	public void onKeyDownEvent(int keyCode, KeyEvent keyEvent) {
		if (scannerthread != null && !isPulledTrigger) {
			isPulledTrigger = true;
			scannerthread.onKeyDownEvent(keyCode, keyEvent);
		}
	}

	public void onKeyUpEvent(int keyCode, KeyEvent keyEvent) {
		if (scannerthread != null) {
			isPulledTrigger = false;
			scannerthread.onKeyUpEvent(keyCode, keyEvent);
		}
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
			this.scannerthread = new Chinawayc72Thread(context) {

				@Override
				public void dispatchEvent(String name, WritableMap data) {
					Chainwayc72Module.this.context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(name, data);
				}

				@Override
				public void dispatchEvent(String name, String data) {
					Chainwayc72Module.this.context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(name, data);
				}

				@Override
				public void dispatchEvent(String name, WritableArray data) {
					Chainwayc72Module.this.context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(name, data);
				}

				@Override
				public void dispatchEvent(String name, boolean data) {
					Chainwayc72Module.this.context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(name, data);
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
	public void isConnected(Promise promise) {
		if (this.scannerthread != null) {
			promise.resolve(this.scannerthread.isConnected());
		}
	}

	@ReactMethod
	public void getModuleName(Promise promise) {
		if (this.scannerthread != null) {
			promise.resolve(this.scannerthread.getModuleName());
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
	public void cleanTags(Promise promise) {
		if (this.scannerthread != null) {
			promise.resolve(this.scannerthread.cleanTags());
		}
	}

	@ReactMethod
	public void SaveCurrentRoute(String routeName, Promise promise) {
		if (this.scannerthread != null) {
			this.scannerthread.SaveCurrentRoute(routeName);
			promise.resolve(true);
		}
	}

	@ReactMethod
	public void IsReadBarcode(boolean value, Promise promise) {
		if (this.scannerthread != null) {
			boolean result = this.scannerthread.IsReadBarcode(value);
			promise.resolve(result);
		}
	}

	@ReactMethod
	public void GetConnectedReader(Promise promise) {
		if (this.scannerthread != null) {
			String result = this.scannerthread.GetConnectedReader();
			promise.resolve(result);
		}
	}

	@ReactMethod
	public void SaveSelectedScanner(String value, Promise promise) {
		if (this.scannerthread != null) {
			this.scannerthread.SaveSelectedScanner(value);
			promise.resolve(true);
		}
	}
}
