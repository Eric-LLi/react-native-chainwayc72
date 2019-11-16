package com.chainwayc72;


import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import com.rscja.deviceapi.Barcode1D;
import com.rscja.deviceapi.exception.ConfigurationException;
import com.rscja.utility.StringUtility;

import java.util.ArrayList;

public abstract class Chinawayc72Thread extends Thread {
	private ReactApplicationContext context;
	private String currentRoute = null;
	private Boolean isReading = false;
	private int batteryLevel = -1;

	private ArrayList<String> scannedTags = new ArrayList<>();

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

	//Barcode Instance
	private Barcode1D mBarcodeInstance;
	private boolean isBarcodeReading = false;

	public Chinawayc72Thread(ReactApplicationContext context) {
		this.context = context;

		currentRoute = null;
		isReading = false;
		batteryLevel = -1;
		scannedTags = new ArrayList<>();
		selectedScanner = null;
		isLocateMode = false;
		isLocatingTag = false;
		tagID = null;
		isTagITMode = false;
		isReadBarcode = false;
		isProgrammingTag = false;
		isAuditMode = false;
		mBarcodeInstance = null;
		isBarcodeReading = false;
	}

	public void onHostResume() {
		//
	}

	public void onHostPause() {
		//
	}

	public void onHostDestroy() {
		if (this.isReading) {
//			this.cancel();
		}
//		shutdown();
//		barcodeDisconnect();
	}

	public abstract void dispatchEvent(String name, WritableMap data);

	public abstract void dispatchEvent(String name, String data);

	public abstract void dispatchEvent(String name, WritableArray data);

	public abstract void dispatchEvent(String name, boolean data);

	public boolean barcodeConnect() throws ConfigurationException {
//		if (isBarcodeReading) {
		barcodeDisconnect();
//		}
		mBarcodeInstance = Barcode1D.getInstance();

		mBarcodeInstance.open();

		return true;
	}

	public boolean barcodeDisconnect() {
		if (mBarcodeInstance != null) {
			mBarcodeInstance.close();
			mBarcodeInstance = null;
			isBarcodeReading = false;
		}
		return true;
	}
}
