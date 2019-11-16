import { NativeModules, DeviceEventEmitter } from 'react-native';
import { RFIDScannerEvent } from './RFIDScannerEvent';

const rfidScannerManager = NativeModules.Chainwayc72;

let instance = null;

export class RFIDScanner {
	constructor() {
		if (!instance) {
			instance = this;
			this.oncallbacks = {};
		}
	}
}

export default new RFIDScanner();
