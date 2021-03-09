import 'dart:async';
import 'dart:io' show Platform;

import 'package:flutter/services.dart';

class OtpReader {
  OtpReader._();

  static const MethodChannel _channel = const MethodChannel('otp_reader');

  static Future<String> getUpcomingSms() async {
    if (Platform.isAndroid) {
      try {
        final String result = await _channel.invokeMethod('startListening');
        return result;
      } catch (e) {
        return e.toString();
      }
    } else {
      return null;
    }
  }

  static Future<String> stopListening() async {
    if (Platform.isAndroid) {
      final String smsCode = await _channel.invokeMethod('stopListening');
      return smsCode;
    } else {
      return null;
    }
  }
}
