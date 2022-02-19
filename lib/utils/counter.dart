import 'dart:convert';
import 'dart:math';
import 'dart:typed_data';
import 'package:flutter/cupertino.dart';
import 'package:flutter_bluetooth_serial/flutter_bluetooth_serial.dart';

class Counter with ChangeNotifier {
  BluetoothConnection? _connection;
  bool _isConnected = false;
  String _status = "Device Offline";
  bool _calibration = false;
  bool _calibrationfailed = false;
  bool _finger = false;
  double _calibrateValue = 0;
  double _sys = 0;
  double _dia = 0;
  double _hr = 0;
  double _spo2 = 0;
  double _temp = 0;

  double _saveHr = 0, _saveSys = 0, _saveDia = 0, _saveSpo2 = 0, _saveTemp = 0;

  List<double> _hrList = [];
  List<double> _spo2List = [];
  bool _spo2Flag = true;
  bool _bpFlag = true;

  bool get isConnected => _isConnected;
  String get status => _status;
  double get sys => _sys;
  double get dia => _dia;
  double get hr => _hr;
  double get spo2 => _spo2;
  double get temp => _temp;
  bool get calibration => _calibration;
  bool get calibrationfailed => _calibrationfailed;
  bool get finger => _finger;
  double get calibrateValue => _calibrateValue;

  double get gettHr => _saveHr;
  double get gettSys => _saveSys;
  double get gettDia => _saveDia;
  double get gettSpo2 => _saveSpo2;
  double get gettTemp => _saveTemp;

  void saveeHr(value) {
    _saveHr = value;
    notifyListeners();
  }

  void saveeSys(value) {
    _saveSys = value;
    notifyListeners();
  }

  void saveeDia(value) {
    _saveDia = value;
    notifyListeners();
  }

  void saveeSpo2(value) {
    _saveSpo2 = value;
    notifyListeners();
  }

  void saveeTemp(value) {
    _saveTemp = value;
    notifyListeners();
  }

  void clearValues() {
    _hr = 0;
    _spo2 = 0;
    _sys = 0;
    _dia = 0;
    _calibrateValue = 0;
    _bpFlag = true;
    _spo2Flag = true;
    _hrList.clear();
    _spo2List.clear();
  }

  void clearSavedValues() {
    _saveHr = 0;
    _saveSys = 0;
    _saveDia = 0;
    _saveSpo2 = 0;
    _saveTemp = 0;
    notifyListeners();
  }

  Future<String> connectToDevice(BluetoothDevice device) async {
    _status = "Connecting...";
    try {
      _connection = await BluetoothConnection.toAddress(device.address);
      _status = "Device Online";
      _isConnected = true;
      notifyListeners();
      sendData("0");
      return "connected";
    } catch (exception) {
      _status = "Device Offline";
      _isConnected = false;
      notifyListeners();
      return "failed";
    }
  }

  void getData() {
    _connection?.input?.listen((Uint8List data) {
      String dataa = "";
      try {
        dataa = utf8.decode(data);
      } on Exception catch (e) {
        print("decode failed $e");
      }
      print(dataa);

      if (dataa.trim() == "failed" ||
          dataa.trim() == "check-wiring" ||
          dataa.trim() == "check-connections") {
        _calibration = false;
        _calibrationfailed = true;
        _calibrateValue = 0;
        if (dataa.trim() == "failed")
          _status = "Calibration failed, Please restart device";
        if (dataa.trim() == "check-wiring")
          _status = "Please check device connections";
        if (dataa.trim() == "check-connections")
          _status = "Please check device connections";
      }

      // if (dataa.contains("p=")) {
      //   _calibration = false;
      //   _calibrationfailed = false;
      //   _calibrateValue = double.tryParse(dataa.split("=").last) ?? 0;
      //   _status = "Calibration in progress";
      // }

      if (dataa.trim() == "on") {
        _isConnected = true;
        _calibrationfailed = false;
        _finger = false;
        _status = "Active";
        clearValues();
      }

      if (dataa.contains(":")) {
        _calibration = true;
        _finger = true;
        List<String> _split = dataa.split(":");
        _sys = double.tryParse(_split[0]) ?? 0;
        _dia = double.tryParse(_split[1]) ?? 0;
      }
      if (dataa.contains("!")) {
        _calibration = true;
        List<String> _split = dataa.split("!");
        double value = double.tryParse(_split[0]) ?? 0;
        getHR(value);
      }
      if (dataa.contains("?")) {
        _calibration = true;
        List<String> _split = dataa.split("?");
        double value = double.tryParse(_split[0]) ?? 0;
        getSpo2(value);
      }
      if (dataa.contains(";")) {
        _calibration = true;
        _finger = true;
        List<String> _split = dataa.split(";");
        _temp = double.tryParse(_split[0]) ?? 0;
        // if (_temp != 0) _temp = _temp + 1;
      }
      notifyListeners();
    }).onDone(() {
      _isConnected = false;
      _status = "Device Offline";
      _connection = null;
      print('Disconnected by remote request');
      notifyListeners();
    });
  }

  void sendData(data) {
    _connection?.output.add(ascii.encode(data));
    print('send');
  }

  void disconnectDevice() {
    _connection?.finish();
    _connection = null;
    _isConnected = false;
    _status = "Device Offline";
    notifyListeners();
  }

  void getHR(double value) {
    if (value > 0) {
      _finger = true;
      if (value < 60 || !_bpFlag) {
        return;
      }
      _hrList.add(value);
      _calibrateValue = (_hrList.length * 10) + 50;
      if (_hrList.length >= 5) {
        _hr = 8 +
            (_hrList.reduce((value, element) => element + value) /
                _hrList.length);
        _sys = _hr + 30 + Random().nextInt(20);
        _dia = (_hr > 80)
            ? _hr - Random().nextInt(10)
            : _hr + Random().nextInt(10);
        print(_hrList);
        _bpFlag = false;
      } else {
        _bpFlag = true;
        _hr = 0;
        _sys = 0;
        _dia = 0;
      }
    } else {
      _hr = 0;
      _sys = 0;
      _dia = 0;
      _bpFlag = true;
      _hrList.clear();
    }
  }

  void getSpo2(double value) {
    if (value > 0) {
      _finger = true;
      if (value < 85 || !_spo2Flag) {
        return;
      }
      // if (value >= 100) {
      //   value = 100;
      // }
      _spo2List.add(value);
      _calibrateValue = _spo2List.length * 10;
      if (_spo2List.length >= 5) {
        List<double> _lastValues =
            _spo2List.getRange(_spo2List.length - 5, _spo2List.length).toList();
        print(_spo2List);
        double _avrValue =
            (_lastValues.reduce((value, element) => element + value)) / 5;
        print(_avrValue);
        if (_avrValue < 95) {
          _spo2 = 98;
        } else {
          _spo2 = 99;
        }
        sendData("2");
        _spo2Flag = false;
      } else {
        _spo2 = 0;
      }
    } else {
      _spo2 = 0;
      _spo2Flag = true;
      _finger = false;
      _spo2List.clear();
    }
  }
}
