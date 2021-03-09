package com.wahyoo.otp_reader

import androidx.annotation.NonNull

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import android.content.Context
import android.util.Log
import android.content.IntentFilter

class OtpReaderPlugin : FlutterPlugin, MethodCallHandler, MySmsListener {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private var mResult: MethodChannel.Result? = null
    private lateinit var channel: MethodChannel
    private lateinit var context: Context

    private var receiver: SmsBroadcastReceiver? = null
    private var alreadyCalledSmsRetrieve = false

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "otp_reader")
        context = flutterPluginBinding.applicationContext
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            "startListening" -> {
                this.mResult = result
                receiver = SmsBroadcastReceiver()
                startListening()
            }
            "stopListening" -> {
                alreadyCalledSmsRetrieve = false
                unregister()
            }
            else -> result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    private fun startListening() {
        val client = SmsRetriever.getClient(context)
        val task = client.startSmsRetriever()
        task.addOnSuccessListener {
            // Successfully started retriever, expect broadcast intent
            Log.d(javaClass::getSimpleName.name, "Listening OTP...")
            receiver?.setSmsListener(this)
            context.registerReceiver(receiver, IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION))
        }
    }

    private fun unregister() {
        context.unregisterReceiver(receiver)
    }

    override fun onOtpReceived(message: String?) {
        message?.let {
            if (!alreadyCalledSmsRetrieve) {
                mResult?.success(it)
                alreadyCalledSmsRetrieve = true
            }
        }
    }

    override fun onOtpTimeout() {}
}
