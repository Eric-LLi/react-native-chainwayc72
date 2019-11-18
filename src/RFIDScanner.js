import { NativeModules, DeviceEventEmitter,NativeEventEmitter } from 'react-native';
import { RFIDScannerEvent } from './RFIDScannerEvent';

const rfidScannerManager = NativeModules.Chainwayc72;

let instance = false;

export class RFIDScanner {
	constructor() {
		if (!instance) {
			instance = true;
			this.onCallbacks = {};
			this.eventEmitter = new NativeEventEmitter(rfidScannerManager);
		}
	}

	ActiveAllListener = () => {
		if(this.eventEmitter){
			this.eventEmitter.addListener(RFIDScannerEvent.TAG, this.handleTagEvent);
		}
		
	};

	RemoveAllListener = () => {
		if(this.eventEmitter){
			this.eventEmitter.removeListener(RFIDScannerEvent.TAG, this.handleTagEvent);
		}
	};
	
	/**
	 * tags come from native module
	 * @param  {String} tag
	 */
	handleTagEvent = (tag: String) => {
		if (this.onCallbacks.hasOwnProperty(RFIDScannerEvent.TAG)) {
			this.onCallbacks[RFIDScannerEvent.TAG](tag);
		}
	}

	
	/**
	 * @param  {String} event name of the event, using RFIDScannerEvent
	 * @param  {Function} callback callback function to UI thread
	 */
	on(event: String, callback: Function) {
		this.onCallbacks[event] = callback;
	}

	
	/**
	 * @param  {String} event name of the event want to remove
	 * @param  {Function} callback
	 */
	removeon(event: String, callback : Function) {
		if (this.onCallbacks.hasOwnProperty(event)) {
			this.onCallbacks[event] = null;
			delete this.onCallbacks[event];
		}
	}

	InitThread = () => {
		rfidScannerManager.InitialThread();
	};

	init = () => {
		this.ActiveAllListener();
		return rfidScannerManager.init();
	};

	shutdown() {
		return rfidScannerManager.shutdown();
	}

	isConnected = () => {
		return rfidScannerManager.isConnected();
	};

	enableReader = (isEnable: Boolean) => {
		return rfidScannerManager.enableReader(isEnable);
	}

	read = (isSingleRead: Boolean) => {
		rfidScannerManager.read(isSingleRead);
	}

	cancel = () => {
		return rfidScannerManager.cancel();
	}

	getAntennaLevel = () => {
		return rfidScannerManager.getAntennaLevel();
	}

	setAntennaLevel = (antennaLevel: Number) => {
		return rfidScannerManager.setAntennaLevel(antennaLevel);
	}

	saveCurrentRoute = (route: String) => {
		rfidScannerManager.saveCurrentRoute(route);
	}
	writeTag = (targetTag: String, newTag: String) => {
		return rfidScannerManager.writeTag(targetTag,newTag);
	}
}

export default new RFIDScanner();
