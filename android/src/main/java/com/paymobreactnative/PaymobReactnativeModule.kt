package com.paymobreactnative

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.WritableMap
import com.paymob.paymob_sdk.PaymobSdk
import com.paymob.paymob_sdk.domain.model.CreditCard
import com.paymob.paymob_sdk.domain.model.SavedCard
import com.paymob.paymob_sdk.ui.PaymobSdkListener


class PaymobReactnativeModule(reactContext: ReactApplicationContext) :
  EventEmitterModule(reactContext), PaymobSdkListener {

  private var appIcon: Bitmap? = null
  private var appName: String? = null
  private var buttonBackgroundColor: Int? = null
  private var buttonTextColor: Int? = null
  private var saveCardDefault: Boolean? = null
  private var showSaveCard: Boolean? = null

  override fun getName(): String {
    return "PaymobReactnative"
  }

  /**
   * Sets the logo of the merchant to be displayed in the SDK.
   *
   * @param base64Image Base64 encoded image.
   */
  @ReactMethod
  fun setAppIcon(base64Image: String) {
    try {
      val decodedString: ByteArray = Base64.decode(base64Image, Base64.DEFAULT)
      appIcon = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  /**
   * Sets the name of the merchant to be displayed in the SDK.
   *
   * @param name Display name of the merchant.
   */
  @ReactMethod
  fun setAppName(name: String) {
    appName = name
  }

  /**
   * Sets the background color of SDK buttons.
   *
   * @param color The color represented as an integer.
   */
  @ReactMethod
  fun setButtonBackgroundColor(color: Int) {
    buttonBackgroundColor = color
  }

  /**
   * Sets the text color of SDK buttons.
   *
   * @param color The color represented as an integer.
   */
  @ReactMethod
  fun setButtonTextColor(color: Int) {
    buttonTextColor = color
  }

  /**
   * Sets whether the save card option is checked by default.
   *
   * @param isEnabled Boolean to enable/disable save card by default.
   */
  @ReactMethod
  fun setSaveCardDefault(isEnabled: Boolean) {
    saveCardDefault = isEnabled
  }

  /**
   * Sets whether the save card option is shown in the SDK.
   *
   * @param isVisible Boolean to show/hide the save card option.
   */
  @ReactMethod
  fun setShowSaveCard(isVisible: Boolean) {
    showSaveCard = isVisible
  }

  /**
   * Presents the payment view controller with the specified parameters.
   *
   * @param clientSecret The client secret provided by Paymob.
   * @param publicKey The public key provided by Paymob.
   * @param savedBankCards An optional array of saved bank cards.
   */
  @ReactMethod
  fun presentPayVC(clientSecret: String, publicKey: String, savedBankCards: ReadableArray?) {
    val context: Context = currentActivity ?: reactApplicationContext
    val savedCards = mutableListOf<SavedCard>()

    // Check if savedBankCards is not null
    if (savedBankCards != null) {
      for (i in 0 until savedBankCards.size()) {
        try {
          val savedBankCard = savedBankCards.getMap(i)
          val maskedPan = savedBankCard.getString("maskedPan")
          val savedCardToken = savedBankCard.getString("savedCardToken")
          val creditCard = savedBankCard.getString("creditCard")

          // Ensure necessary fields are not null or empty
          if (!maskedPan.isNullOrEmpty() && !savedCardToken.isNullOrEmpty() && !creditCard.isNullOrEmpty()) {
            val mappedCreditCard = mapCreditCard(creditCard)
            if (mappedCreditCard != null) {
              // Add the first valid card to the savedCards list
              savedCards.add(SavedCard("**** **** **** $maskedPan", savedCardToken, mappedCreditCard))
              break // Break after the first valid card
            }
          }
        } catch (e: Exception) {
          e.printStackTrace()
        }
      }
    }

    PaymobSdk.Builder(
      context = context,
      paymobSdkListener = this,
      clientSecret = clientSecret,
      publicKey = publicKey,
      savedCard = if (savedCards.isNotEmpty()) savedCards[0] else null // Pass only the first card if available
    ).apply {
      // appIcon?.let { setAppLogo(it) }
      appName?.let { setAppName(it) }
      buttonBackgroundColor?.let { setButtonBackgroundColor(it) }
      buttonTextColor?.let { setButtonTextColor(it) }
      saveCardDefault?.let { isSavedCardCheckBoxCheckedByDefault(it) }
      showSaveCard?.let { isAbleToSaveCard(it) }
    }.build().start()
  }

  /**
   * Keep: Required for RN built-in Event Emitter Calls.
   */
  @ReactMethod
  override fun addListener(event: String?) {
    super.addListener(event!!)
  }

  /**
   * Keep: Required for RN built-in Event Emitter Calls.
   */
  @ReactMethod
  override fun removeListeners(count: Int?) {
    super.removeListeners(count!!)
  }

  /**
   * Called when the payment process fails.
   */
  override fun onFailure() {
    emitTransactionStatus("Fail")
  }

  /**
   * Called when the payment process is pending.
   */
  override fun onPending() {
    emitTransactionStatus("Pending")
  }

  /**
   * Called when the payment process is successful.
   */
  override fun onSuccess() {
    emitTransactionStatus("Success")
  }

  /**
   * Maps a string representing a credit card type to a CreditCard enum.
   *
   * @param creditCard The credit card type as a string.
   * @return The corresponding CreditCard enum or null if not found.
   */
  private fun mapCreditCard(creditCard: String): CreditCard? {
    return when (creditCard) {
      "VISA" -> CreditCard.VISA
      "MASTERCARD" -> CreditCard.MASTERCARD
      "AMERICAN_EXPRESS" -> CreditCard.AMERICAN_EXPRESS
      "MEEZA" -> CreditCard.MEEZA
      "JCB" -> CreditCard.JCB
      "MAESTRO" -> CreditCard.MAESTRO
      "OMAN_NET" -> CreditCard.OMAN_NET
      else -> null
    }
  }

  /**
   * Emits the transaction status to the JavaScript side.
   *
   * @param status The status of the transaction.
   */
  private fun emitTransactionStatus(status: String) {
    val statusMap: WritableMap = Arguments.createMap()
    statusMap.putString("status", status)
    sendEvent("onTransactionStatus", statusMap)
  }
}
