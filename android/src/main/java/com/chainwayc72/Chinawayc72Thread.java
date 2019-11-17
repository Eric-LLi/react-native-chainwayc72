package com.chainwayc72;


import android.text.TextUtils;
import android.util.Log;

import com.facebook.common.util.ExceptionWithNoStacktrace;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import com.rscja.deviceapi.Barcode1D;
import com.rscja.deviceapi.RFIDWithUHF;
import com.rscja.deviceapi.exception.ConfigurationException;
import com.rscja.utility.StringUtility;

import java.util.ArrayList;

public abstract class Chinawayc72Thread extends Thread {

	private ReactApplicationContext context;
	private String currentRoute = null;
	private Boolean isReading = false;
	private int batteryLevel = -1;

	//RFID Instance
	private ArrayList<String> scannedTags = new ArrayList<>();
	private RFIDWithUHF mReader;

	// Save scanner name
	private String selectedScanner = null;

	// Locate Tag
	private boolean isLocatingTag = false;
	private boolean isLocateMode = false;
	private String tagID = "";

	// Tag IT
	private boolean isTagITMode = false;
	private boolean isReadBarcode = false;
	private boolean isProgrammingTag = false;

	// Audit
	private boolean isAuditMode = false;

	public Chinawayc72Thread(ReactApplicationContext context) {
		this.context = context;
	}

	public void onHostResume() {
		//
	}

	public void onHostPause() {
		//
	}

	public void onHostDestroy() {
		if (mReader != null) {
			mReader.free();
		}
	}

	public abstract void dispatchEvent(String name, WritableMap data);

	public abstract void dispatchEvent(String name, String data);

	public abstract void dispatchEvent(String name, WritableArray data);

	public abstract void dispatchEvent(String name, boolean data);


	public void init() throws Exception {
		if (mReader != null) {
			mReader = RFIDWithUHF.getInstance();
		}
	}

	public void shutdown() throws Exception {
		if (mReader != null) {
			mReader.free();

			currentRoute = null;
			isReading = false;
			batteryLevel = -1;

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

	public void read(boolean isSingleRead) throws Exception {
		if (mReader != null) {
			if (isReading) {
				cancel();
			}

			if (isSingleRead) {
				String strUII = mReader.inventorySingleTag();
				if (!TextUtils.isEmpty(strUII)) {
					String strEPC = mReader.convertUiiToEPC(strUII);

					//Dispatch........
				}
			} else {
				if (mReader.startInventoryTag(0, 0)) {
					isReading = true;
				} else {
					mReader.stopInventory();
				}
			}
		}
	}

	public void cancel() throws Exception {
		if (mReader != null && isReading) {
			isReading = false;
			if (!mReader.stopInventory()) {
				throw new ExceptionWithNoStacktrace("Stop inventory fail");
			}
		}
	}

	public void getAntennaLevel() {
		if (mReader != null) {
			//
		}
	}

	public void setAntennaLevel() {
		if (mReader != null) {
			//
		}
	}

}
