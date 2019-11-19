package com.chainwayc72;


import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.rscja.deviceapi.RFIDWithUHF;
import com.rscja.utility.StringUtility;
import com.rscja.deviceapi.RFIDWithUHF.BankEnum;

import java.util.ArrayList;

// Add to MainActivity.java file under createReactActivityDelegate
/*
@Override  // <--- Add this method if you want to react to keyDown
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Chinawayc72Thread.getInstance().onKeyDownEvent(keyCode, event);
		super.onKeyUp(keyCode, event);
		return true;
	}

	@Override  // <--- Add this method if you want to react to keyUp
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Chinawayc72Thread.getInstance().onKeyUpEvent(keyCode, event);
		super.onKeyUp(keyCode, event);
		return true;
	}
*/

class Dispatch_Event {
	public static final String TagEvent = "TagEvent";
	public static final String RFIDStatusEvent = "RFIDStatusEvent";
	public static final String writeTag = "writeTag";
	public static final String BarcodeTrigger = "BarcodeTrigger";
	public static final String inventoryStart = "inventoryStart";
	public static final String inventoryStop = "inventoryStop";
}

public abstract class Chinawayc72Thread extends Thread {

	private ReactApplicationContext context;

	private static Chinawayc72Thread instance = null;
	private static String currentRoute = null;
	private static Boolean isReading = false;
	private static Boolean isConnected = false;

	//RFID Instance
	private static ArrayList<String> scannedTags = new ArrayList<>();
	private static RFIDWithUHF mReader = null;

	// Save scanner name
	private static String selectedScanner = null;

	// Locate Tag
	private static boolean isLocatingTag = false;
	private static boolean isLocateMode = false;
	private String tagID = "";

	// Tag IT
	private static boolean isTagITMode = false;
	private static boolean isReadBarcode = false;
	private static boolean isProgrammingTag = false;

	// Audit
	private static boolean isAuditMode = false;

	public Chinawayc72Thread(ReactApplicationContext context) {
		this.context = context;
		instance = this;
	}

	static public Chinawayc72Thread getInstance() {
		return instance;
	}

	public void onHostResume() {
		//
	}

	public void onHostPause() {
		//
	}

	public void onHostDestroy() {
		if (mReader != null) {
			try {
				shutdown();
			} catch (Exception err) {
				Log.e("onHostDestroy", err.getMessage());
			}
		}
	}

	public abstract void dispatchEvent(String name, WritableMap data);

	public abstract void dispatchEvent(String name, String data);

	public abstract void dispatchEvent(String name, WritableArray data);

	public abstract void dispatchEvent(String name, boolean data);

	//Trigger pull
	public void onKeyDownEvent(int keyCode, KeyEvent keyEvent) {
		if (mReader != null && keyCode == 280) {
			Log.w("onKeyDownEvent", String.valueOf(keyCode));
			if (isReadBarcode && !isReading) {
				isReading = true;
				dispatchEvent(Dispatch_Event.BarcodeTrigger, true);
			} else {
				try {
					if (currentRoute.equals("tagit")) {
						read(true);
					} else {
						read(false);
					}
					isReading = true;
				} catch (Exception err) {
					Log.e("onKeyDownEvent", err.getMessage());
				}
			}
		}
	}

	//Trigger release
	public void onKeyUpEvent(int keyCode, KeyEvent keyEvent) {
		if (mReader != null && keyCode == 280) {
			Log.w("onKeyUpEvent", String.valueOf(keyCode));
			if (isReadBarcode) {
				isReading = false;
				dispatchEvent(Dispatch_Event.BarcodeTrigger, false);
			} else {
				try {
					cancel();
					isReading = false;
				} catch (Exception err) {
					Log.e("onKeyUpEvent", err.getMessage());
				}
			}
		}
	}

	public boolean init() throws Exception {
		if (mReader == null) {
			mReader = RFIDWithUHF.getInstance();
			return true;
		}
		return false;
	}

	public void shutdown() throws Exception {
		if (mReader != null) {
			if (isReading) {
				cancel();
			}

			mReader.free();

			currentRoute = null;
			isReading = false;
			isConnected = false;

			//RFID Instance
			scannedTags = new ArrayList<>();
			mReader = null;

			// Save scanner name
			selectedScanner = null;

			// Locate Tag
			isLocatingTag = false;
			isLocateMode = false;
			tagID = null;

			// Tag IT
			isTagITMode = false;
			isReadBarcode = false;
			isProgrammingTag = false;

			// Audit
			isAuditMode = false;
		}
	}

	public boolean isConnected() {
//		if (mReader != null) {
//			int power = mReader.getPower();
//			if (power > -1) {
//				return true;
//			}
//		}
		return isConnected;
	}

	public WritableArray getModuleName() {
		WritableArray list = Arguments.createArray();
		WritableMap map = Arguments.createMap();
		map.putString("name", "ChainWay-C72");
		list.pushMap(map);
		return list;
	}

	public boolean enableReader(boolean isEnable) {
		if (mReader != null) {
			if (isEnable) {
				boolean result = mReader.init();
				if (!isConnected) {
					isConnected = true;
					WritableMap map = Arguments.createMap();
					map.putBoolean("ConnectionState", true);
					dispatchEvent(Dispatch_Event.RFIDStatusEvent, map);
				}
				return result;
			} else {
				return mReader.free();
			}
		}
		return false;
	}

	public void read(boolean isSingleRead) throws Exception {
		if (mReader != null && !isReading) {
			Log.e("Start Reading....", "");
			if (isSingleRead) {
				String strUII = mReader.inventorySingleTag();
				if (!TextUtils.isEmpty(strUII)) {
					String strEPC = mReader.convertUiiToEPC(strUII);
					Log.w(Dispatch_Event.TagEvent, strEPC);
					dispatchEvent(Dispatch_Event.TagEvent, strEPC);
				} else {
					throw new Exception("unable to read tag");
				}
			} else {
				if (mReader.startInventoryTag(0, 0)) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							String strTid;
							String strResult;
							String[] res = null;
							String EPC;

							while (isReading) {
								res = mReader.readTagFromBuffer();
								if (res != null) {
									strTid = res[0];
									EPC = res[1];
									if (strTid.length() != 0 && !strTid.equals("0000000" +
											"000000000") && !strTid.equals("000000000000000000000000")) {
										strResult = "TID:" + strTid + "\n";
									} else {
										strResult = "";
									}
									Log.e("DATA", "EPC:" + EPC + "|" + strResult);
									boolean result = addTagToList(EPC);
									if (result) {
										dispatchEvent(Dispatch_Event.TagEvent, EPC);
									}
								}
							}
						}
					}).start();
					//
				} else {
					mReader.stopInventory();
				}
			}
		}
	}

	public boolean cancel() throws Exception {
		if (mReader != null && isReading) {
			isReading = false;
			return mReader.stopInventory();
		}
		return true;
	}

	public int getAntennaLevel() throws Exception {
		if (mReader != null) {
			int power = mReader.getPower();
			if (power > -1)
				return power;
		}
		throw new Exception("Get antenna level failure");
	}

	public boolean setAntennaLevel(int power) throws Exception {
		if (mReader != null) {
			boolean result = mReader.setPower(power);
			if (result) {
				return true;
			}
		}
		throw new Exception("Set antenna level failure");
	}

	public boolean cleanTags() {
		if (mReader != null) {
			scannedTags = new ArrayList<>();
			return true;
		}
		return false;
	}

	public boolean writeTag(String targetTag, String newTag) throws Exception {
		if (mReader != null) {
			if (StringUtility.isEmpty(targetTag) || StringUtility.isEmpty(newTag)) {
				throw new Exception("Tag data format error");
			}

			String strPrt = "0";
			String strPWD = "00000000";
			String Bank = "UII";
			String filterBank = "UII";
			int filterPtr = 32;
			int filterCnt = 0;
			String filterData = targetTag;
			int strPtr = 2;
			int cntStr = 6;
			String strData = newTag;

			boolean result = mReader.writeData(
					strPWD,
					BankEnum.valueOf(filterBank),
					filterPtr,
					filterCnt,
					filterData,
					BankEnum.valueOf(Bank),
					strPtr,
					cntStr,
					strData
			);

			if (result) {
				dispatchEvent(Dispatch_Event.writeTag, "success");
			} else {
				dispatchEvent(Dispatch_Event.writeTag, "Program tag fail...");
			}
			return result;
		}
		return false;
	}

	public void SaveCurrentRoute(String routeName) {
		if (routeName != null) {
			currentRoute = routeName.toLowerCase();
		} else {
			currentRoute = null;
		}
	}

	public boolean IsReadBarcode(boolean value) {
		isReadBarcode = value;

		//If read barcode, then turn off RFID mode.
		return enableReader(!value);
	}

	private boolean addTagToList(String strEPC) {
		if (mReader != null && strEPC != null) {
			if (!checkIsExisted(strEPC)) {
				scannedTags.add(strEPC);
				return true;
			}
		}
		return false;
	}

	private boolean checkIsExisted(String strEPC) {
		for (int i = 0; i < scannedTags.size(); i++) {
			String tag = scannedTags.get(i);
			if (strEPC.equals(tag)) {
				return true;
			}
		}
		return false;
	}

}
