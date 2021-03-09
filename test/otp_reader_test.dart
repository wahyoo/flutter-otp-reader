import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:otp_reader/otp_reader.dart';

void main() {
  const MethodChannel channel = MethodChannel('otp_reader');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });
}
