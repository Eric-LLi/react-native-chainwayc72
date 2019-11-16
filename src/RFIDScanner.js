import { NativeModules, DeviceEventEmitter } from 'react-native';
import _ from 'lodash';
import { RFIDScannerEvent } from './RFIDScannerEvent';

const rfidScannerManager = NativeModules.RNRfidBarcodeZebra;

let instance = null;

export class RFIDScanner {
	constructor() {
		if (!instance) {
			instance = this;
			this.opened = false;
			this.deferReading = false;
			this.oncallbacks = [];
			this.config = {};
			// this.ActiveAllListener();
		}
	}

	handlerLocateTagEvent(event) {
		if (!_.isEmpty(event) && !_.isEmpty(event.error)) {
			if (this.oncallbacks.hasOwnProperty(RFIDScannerEvent.LOCATE_TAG)) {
				this.oncallbacks[RFIDScannerEvent.LOCATE_TAG].forEach(callback => {
					callback({ error: event.error });
				});
			}
		} else if (!_.isEmpty(event)) {
			if (this.oncallbacks.hasOwnProperty(RFIDScannerEvent.LOCATE_TAG)) {
				this.oncallbacks[RFIDScannerEvent.LOCATE_TAG].forEach(callback => {
					callback(event);
				});
			}
		}
	}

	handlerBarcodeEvent(event) {
		console.log(`Barcode event: ${event}`);
		if (!_.isEmpty(event) && !_.isEmpty(event.error)) {
			if (this.oncallbacks.hasOwnProperty(RFIDScannerEvent.BARCODE)) {
				this.oncallbacks[RFIDScannerEvent.BARCODE].forEach(callback => {
					callback({ error: event.error });
				});
			}
		} else if (!_.isEmpty(event)) {
			if (this.oncallbacks.hasOwnProperty(RFIDScannerEvent.BARCODE)) {
				this.oncallbacks[RFIDScannerEvent.BARCODE].forEach(callback => {
					callback(event);
				});
			}
		}
	}

	handleWriteTagEvent(event) {
		console.log(`RFID write event: ${event}`);
		if (!_.isEmpty(event) && !_.isEmpty(event.error)) {
			if (this.oncallbacks.hasOwnProperty(RFIDScannerEvent.WRITETAG)) {
				this.oncallbacks[RFIDScannerEvent.WRITETAG].forEach(callback => {
					callback(event.error);
				});
			}
		} else if (!_.isEmpty(event)) {
			if (this.oncallbacks.hasOwnProperty(RFIDScannerEvent.WRITETAG)) {
				this.oncallbacks[RFIDScannerEvent.WRITETAG].forEach(callback => {
					callback(event);
				});
			}
		}
	}

	handleStatusEvent(event) {
		console.log(`RFID status event ${event.RFIDStatusEvent}`);
		if (event.hasOwnProperty('ConnectionState')) {
			if (this.oncallbacks.hasOwnProperty('RFIDStatusEvent')) {
				this.oncallbacks.RFIDStatusEvent.forEach(callback => {
					callback(event);
				});
			}
		} else if (event.RFIDStatusEvent === 'opened') {
			this.opened = true;
			if (this.deferReading) {
				rfidScannerManager.read(this.config);
				this.deferReading = false;
			}
		} else if (event.RFIDStatusEvent === 'closed') {
			this.opened = false;
		} else if (event.RFIDStatusEvent.split(' ')[0] == 'battery') {
			if (this.oncallbacks.hasOwnProperty('RFIDStatusEvent')) {
				this.oncallbacks.RFIDStatusEvent.forEach(callback => {
					callback(`${event.RFIDStatusEvent}%`);
				});
			}
		} else if (
			event.RFIDStatusEvent === 'inventoryStart' ||
			event.RFIDStatusEvent === 'inventoryStop'
		) {
			if (this.oncallbacks.hasOwnProperty(RFIDScannerEvent.triggerAction)) {
				this.oncallbacks[RFIDScannerEvent.triggerAction].forEach(callback => {
					callback(event);
				});
			}
		}
	}

	handleTagEvent(tag) {
		if (this.oncallbacks.hasOwnProperty(RFIDScannerEvent.TAG)) {
			this.oncallbacks[RFIDScannerEvent.TAG].forEach(callback => {
				callback(tag);
			});
		}
	}

	handleTagsEvent(tags) {
		if (this.oncallbacks.hasOwnProperty(RFIDScannerEvent.TAGS)) {
			this.oncallbacks[RFIDScannerEvent.TAGS].forEach(callback => {
				callback(tags);
			});
		}
	}

	RemoveAllListener = () => {
		if (!_.isEmpty(this.tagEvent)) {
			this.tagEvent.remove();
			this.tagEvent = null;
		}
		if (!_.isEmpty(this.rfidStatusEvent)) {
			this.rfidStatusEvent.remove();
			this.rfidStatusEvent = null;
		}
		if (!_.isEmpty(this.writeTagEvent)) {
			this.writeTagEvent.remove();
			this.writeTagEvent = null;
		}
		if (!_.isEmpty(this.barcodeEvent)) {
			this.barcodeEvent.remove();
			this.barcodeEvent = null;
		}
		if (!_.isEmpty(this.locateTagEvent)) {
			this.locateTagEvent.remove();
			this.locateTagEvent = null;
		}
	};

	ActiveAllListener = () => {
		if (_.isEmpty(this.tagEvent))
			this.tagEvent = DeviceEventEmitter.addListener(
				RFIDScannerEvent.TAG,
				this.handleTagEvent.bind(this)
			);
		if (_.isEmpty(this.rfidStatusEvent))
			this.rfidStatusEvent = DeviceEventEmitter.addListener(
				RFIDScannerEvent.RFID_Status,
				this.handleStatusEvent.bind(this)
			);
		if (_.isEmpty(this.writeTagEvent))
			this.writeTagEvent = DeviceEventEmitter.addListener(
				RFIDScannerEvent.WRITETAG,
				this.handleWriteTagEvent.bind(this)
			);
		if (_.isEmpty(this.barcodeEvent))
			this.barcodeEvent = DeviceEventEmitter.addListener(
				RFIDScannerEvent.BARCODE,
				this.handlerBarcodeEvent.bind(this)
			);
		if (_.isEmpty(this.locateTagEvent))
			this.locateTagEvent = DeviceEventEmitter.addListener(
				RFIDScannerEvent.LOCATE_TAG,
				this.handlerLocateTagEvent.bind(this)
			);
	};

	InitThread = () => {
		rfidScannerManager.InitialThread();
	};

	barcodePullTrigger() {
		rfidScannerManager.barcodePullTrigger();
	}

	barcodeReleaseTrigger() {
		rfidScannerManager.barcodeReleaseTrigger();
	}

	getAntennaConfig(callback) {
		rfidScannerManager.getAntennaConfig(callback);
	}

	getConfig(callback) {
		rfidScannerManager.getConfig(callback);
	}

	SaveAntennaConfig(config) {
		return rfidScannerManager.saveAntennaConfig(config);
	}

	saveTagID(value) {
		rfidScannerManager.saveTagID(value);
	}

	locateMode(value) {
		rfidScannerManager.locateMode(value);
	}

	locateTag(tag) {
		rfidScannerManager.locateTag(tag);
	}

	auditMode(value) {
		rfidScannerManager.AuditMode(value);
	}

	TagITMode(value) {
		rfidScannerManager.TagITMode(value);
	}

	SaveCurrentRoute = value => {
		return rfidScannerManager.SaveCurrentRoute(value);
	};

	TagITReadBarcode(value) {
		return rfidScannerManager.TagITReadBarcode(value);
	}

	barcodeConnect = () => {
		return rfidScannerManager.barcodeConnect();
	};

	switchDPO(value, callback) {
		rfidScannerManager.switchDPO(value, callback);
	}

	cleanTags() {
		rfidScannerManager.cleanTags();
	}

	GetAvailableBluetoothDevices = () => {
		return rfidScannerManager.GetAvailableBluetoothDevices();
	};

	InitialThread = () => {
		rfidScannerManager.InitialThread();
	};

	init = () => {
		// this.oncallbacks = [];
		return rfidScannerManager.init();
	};

	SaveSelectedScanner = item => {
		rfidScannerManager.SaveSelectedScanner(item);
	};

	GetConnectedReader = () => {
		return rfidScannerManager.GetConnectedReader();
	};

	read(config = {}) {
		this.config = config;

		if (this.opened) {
			rfidScannerManager.read(this.config);
		} else {
			this.deferReading = true;
		}
	}

	reconnect() {
		rfidScannerManager.reconnect();
	}

	cancel() {
		rfidScannerManager.cancel();
	}

	AttemptToReconnect = () => {
		return rfidScannerManager.AttemptToReconnect();
	};

	writeTag(targetTag, newTag) {
		return rfidScannerManager.writeTag(targetTag, newTag);
	}

	isConnected = () => {
		return rfidScannerManager.isConnected();
	};

	barcodeDisconnect() {
		rfidScannerManager.barcodeDisconnect();
	}

	shutdown() {
		rfidScannerManager.shutdown();
	}

	ChangeBeeperVolume(value) {
		rfidScannerManager.ChangeBeeperVolume(value);
	}

	OpenAndroidSetting = () => {
		rfidScannerManager.OpenAndroidSetting();
	};

	on(event, callback) {
		// if (!this.oncallbacks[event]) {
		// 	this.oncallbacks[event] = [];
		// }
		this.oncallbacks[event] = [];
		this.oncallbacks[event].push(callback);
	}

	removeon(event, callback) {
		if (this.oncallbacks.hasOwnProperty(event)) {
			this.oncallbacks[event].forEach((funct, index) => {
				// if (callback === undefined || callback === null) {
				// this.oncallbacks[event] = [];
				// } else
				if (funct.toString() === callback.toString()) {
					this.oncallbacks[event].splice(index, 1);
				}
			});
		}
	}

	hason(event, callback) {
		let result = false;
		if (this.oncallbacks.hasOwnProperty(event)) {
			this.oncallbacks[event].forEach((funct, index) => {
				if (funct.toString() === callback.toString()) {
					result = true;
				}
			});
		}
		return result;
	}
}

export default new RFIDScanner();
