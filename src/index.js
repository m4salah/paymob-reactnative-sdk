import {
  NativeEventEmitter,
  NativeModules,
  Platform,
  processColor,
} from 'react-native';

const LINKING_ERROR =
  `The package 'paymob-reactnative' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const PaymobReactnative = NativeModules.PaymobReactnative
  ? NativeModules.PaymobReactnative
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

const paymobEvents = new NativeEventEmitter(PaymobReactnative);

const Paymob = {
  /**
   * Sets the icon of the merchant to be displayed.
   * To use, pass an encoded base64 image.
   * @param {string} base64Image - Base64 encoded image.
   */
  setAppIcon(base64Image) {
    PaymobReactnative.setAppIcon(base64Image);
  },
  /**
   * Sets the name of the merchant to be displayed.
   * @param {string} name - Display name.
   */
  setAppName(name) {
    PaymobReactnative.setAppName(name);
  },
  /**
   * Sets the background color of SDK buttons.
   * @param {string} color - A Hex color string.
   */
  setButtonBackgroundColor(color) {
    PaymobReactnative.setButtonBackgroundColor(processColor(color));
  },
  /**
   * Sets the text color of SDK buttons.
   * @param {string} color - A Hex color string.
   */
  setButtonTextColor(color) {
    PaymobReactnative.setButtonTextColor(processColor(color));
  },
  /**
   * Sets whether or not the save card option is checked by default.
   * @param {boolean} isEnabled - A boolean to check/uncheck.
   */
  setSaveCardDefault(isEnabled) {
    PaymobReactnative.setSaveCardDefault(isEnabled);
  },
  /**
   * Sets whether or not the save card option is shown.
   * @param {boolean} isVisible - A boolean to show/hide.
   */
  setShowSaveCard(isVisible) {
    PaymobReactnative.setShowSaveCard(isVisible);
  },
  /**
   * Presents the payment view controller.
   * @param {string} clientSecret - The client secret.
   * @param {string} publicKey - The public key.
   * @param {Array} savedBankCards - An array of saved bank cards.
   */
  presentPayVC(clientSecret, publicKey, savedBankCards) {
    PaymobReactnative.presentPayVC(clientSecret, publicKey, savedBankCards);
  },
  /**
   * Sets a listener for SDK transaction status updates.
   * @param {function} listener - A callback function that receives the transaction status.
   */
  setSdkListener(listener) {
    paymobEvents.addListener('onTransactionStatus', (event) => {
      Platform.OS === 'ios' ? listener(event.status) : listener(event);
    });
  },
  /**
   * Removes all listeners for the SDK transaction status updates.
   * This can be used to clean up listeners when they are no longer needed.
   */
  removeSdkListener() {
    paymobEvents.removeAllListeners('onTransactionStatus');
  },
};

/**
 * Enum for credit card types.
 * @readonly
 * @enum {string}
 */
export const CreditCardType = {
  VISA: 'VISA',
  MASTERCARD: 'MASTERCARD',
  AMERICAN_EXPRESS: 'AMERICAN_EXPRESS',
  MEEZA: 'MEEZA',
  JCB: 'JCB',
  MAESTRO: 'MAESTRO',
  OMAN_NET: 'OMAN_NET',
};

/**
 * Enum for payment result.
 * @readonly
 * @enum {string}
 */
export const PaymentResult = {
  SUCCESS: 'Success',
  FAIL: 'Fail',
  PENDING: 'Pending',
};

export default Paymob;
