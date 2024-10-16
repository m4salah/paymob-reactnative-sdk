package com.paymobreactnative

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule

abstract class EventEmitterModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
  private var listenerCount = 0

  fun sendEvent(event: String, params: WritableMap?) {
    if (listenerCount > 0) {
      reactApplicationContext
        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
        .emit(event, params)
    }
  }

  protected open fun addListener(event: String?) {
    listenerCount++
  }

  protected open fun removeListeners(count: Int?) {
    listenerCount -= count!!
  }
}
