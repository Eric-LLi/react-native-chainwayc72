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
			this.eventEmitter.addListener(RFIDScannerEvent.RFID_Status, this.handleStatusEvent);
			this.eventEmitter.addListener(RFIDScannerEvent.BarcodeTrigger, this.handleBarcodeTriggerEvent);
			this.eventEmitter.addListener(RFIDScannerEvent.WRITETAG, this.handleWriteTagEvent);
		}
		
	};

	RemoveAllListener = () => {
		if(this.eventEmitter){
			this.eventEmitter.removeListener(RFIDScannerEvent.TAG, this.handleTagEvent);
			this.eventEmitter.removeListener(RFIDScannerEvent.RFID_Status, this.handleStatusEvent);
			this.eventEmitter.removeListener(RFIDScannerEvent.BarcodeTrigger, this.handleBarcodeTriggerEvent);
			this.eventEmitter.removeListener(RFIDScannerEvent.WRITETAG, this.handleWriteTagEvent);
		}
	};
	
	handleStatusEvent = (event) => {
		if (this.onCallbacks.hasOwnProperty(RFIDScannerEvent.RFID_Status)) {
			this.onCallbacks[RFIDScannerEvent.RFID_Status](event);
		}
	}

	/**
	 * tags come from native module
	 * @param  {String} tag
	 */
	handleTagEvent = (tag: String) => {
		if (this.onCallbacks.hasOwnProperty(RFIDScannerEvent.TAG)) {
			this.onCallbacks[RFIDScannerEvent.TAG](tag);
		}
	}

	handleBarcodeTriggerEvent = (event) => {
		if (this.onCallbacks.hasOwnProperty(RFIDScannerEvent.BarcodeTrigger)) {
			this.onCallbacks[RFIDScannerEvent.BarcodeTrigger](event);
		}
	}

	handleWriteTagEvent = (event) => {
		if (this.onCallbacks.hasOwnProperty(RFIDScannerEvent.WRITETAG)) {
			this.onCallbacks[RFIDScannerEvent.WRITETAG](event);
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

	InitialThread = () => {
		rfidScannerManager.InitialThread();
	};

	init = () => {
		return rfidScannerManager.init();
	};

	shutdown() {
		return rfidScannerManager.shutdown();
	}

	getModuleName = () => {
		return rfidScannerManager.getModuleName();
	}

	isConnected = () => {
		return rfidScannerManager.isConnected();
	};

	AttemptToReconnect = () => {
		// return rfidScannerManager.AttemptToReconnect();
		return Promise.resolve(false);
	}

	enableReader = (isEnable: Boolean) => {
		return rfidScannerManager.enableReader(isEnable);
	}

	read = (isSingleRead: Boolean) => {
		rfidScannerManager.read(isSingleRead);
	}

	cancel = () => {
		return rfidScannerManager.cancel();
	}

	cleanTags = () => {
		return rfidScannerManager.cleanTags();
	}
	
	getAntennaLevel = () => {
		return rfidScannerManager.getAntennaLevel();
	}

	setAntennaLevel = (Antenna: Object) => {
		let num = null;
		if(Antenna.hasOwnProperty('antennaLevel')){
			num = parseInt(Antenna.antennaLevel);
			return rfidScannerManager.setAntennaLevel(num);	
		} else {
			return Promise.reject('Antenna level format error');
		}
	}

	saveCurrentRoute = (route: String) => {
		rfidScannerManager.saveCurrentRoute(route);
	}

	writeTag = (targetTag: String, newTag: String) => {
		return rfidScannerManager.writeTag(targetTag,newTag);
	}

	SaveCurrentRoute = (routeName: String) => {
		return rfidScannerManager.SaveCurrentRoute(routeName);
	}

	IsReadBarcode = (value: Boolean) => {
		return rfidScannerManager.IsReadBarcode(value);
	}
}

export default new RFIDScanner();
