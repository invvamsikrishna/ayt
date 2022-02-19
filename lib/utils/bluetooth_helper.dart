import 'dart:async';
import 'package:flutter_bluetooth_serial/flutter_bluetooth_serial.dart';

class BluetoothHelper {
  Future<bool> requestBluetooth() async {
    bool? _task = await FlutterBluetoothSerial.instance.requestEnable();
    return _task ?? false;
  }

  Future<List<BluetoothDevice>> checkBoundedDevices() async {
    List<BluetoothDevice> bondedDevices =
        await FlutterBluetoothSerial.instance.getBondedDevices();
    return bondedDevices;
  }
}
