import { NativeModules, DeviceEventEmitter } from 'react-native';
import { RFIDScannerEvent } from './RFIDScannerEvent';

const rfidScannerManager = NativeModules.Chainwayc72;

let instance = null;

export class RFIDScanner {
	constructor() {
		if (!instance) {
			instance = this;
			this.onCallbacks = {};
		}
	}

	on(event, callback) {
		this.onCallbacks[event] = callback;
	}

	removeon(event, callback) {
		if (this.onCallbacks.hasOwnProperty(event)) {
			this.onCallbacks[event] = null;
			delete this.onCallbacks[event];
		}
	}

	InitThread = () => {
		rfidScannerManager.InitialThread();
	};

	init = () => {
		return rfidScannerManager.init();
	};

	shutdown() {
		rfidScannerManager.shutdown();
	}

	isConnected = () => {
		return rfidScannerManager.isConnected();
	};

	read = () => {
		rfidScannerManager.read();
	}

	cancel = () => {
		rfidScannerManager.cancel();
	}
}

export default new RFIDScanner();
