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

  double _calibrateValue = 0, _batteryLevel = 0;
  double _sys = 0, _dia = 0, _hr = 0, _spo2 = 0, _temp = 0;
  double _saveHr = 0, _saveSys = 0, _saveDia = 0, _saveSpo2 = 0, _saveTemp = 0;

  // List<double> _hrList = [];
  // List<double> _spo2List = [];
  int _bpList = 0;
  int _hrList = 0;
  int _spo2List = 0;
  int _tempList = 0;
  bool _bpFlag = true;
  bool _hrFlag = true;
  bool _spo2Flag = true;
  bool _tempFlag = true;

  bool get isConnected => _isConnected;
  String get status => _status;
  bool get calibration => _calibration;
  bool get calibrationfailed => _calibrationfailed;

  double get calibrateValue => _calibrateValue;
  double get batteryValue => _batteryLevel;
  double get sys => _sys;
  double get dia => _dia;
  double get hr => _hr;
  double get spo2 => _spo2;
  double get temp => _temp;
  double get gettHr => _saveHr;
  double get gettSys => _saveSys;
  double get gettDia => _saveDia;
  double get gettSpo2 => _saveSpo2;
  double get gettTemp => _saveTemp;

  bool get bpFlag => _bpFlag;
  bool get hrFlag => _hrFlag;
  bool get spo2Flag => _spo2Flag;
  bool get tempFlag => _tempFlag;

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
    _temp = 0;
    // _hrList.clear();
    // _spo2List.clear();
    // _spo2List.clear();
    _bpFlag = true;
    _hrFlag = true;
    _spo2Flag = true;
    _tempFlag = true;
    notifyListeners();
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
        _status = "Error in device connections, Please restart the device";
        _batteryLevel = 0;
      }

      if (dataa.contains("p=")) {
        _calibration = false;
        _calibrationfailed = false;
        _calibrateValue = double.tryParse(dataa.split("=").last) ?? 0;
        _status = "Calibration in progress";
        _batteryLevel = 0;
      }

      if (dataa.contains("on")) {
        _isConnected = true;
        _calibrationfailed = false;
        _calibrateValue = 100;
        _status = "Active";
        List<String> _split = dataa.split("on");
        double? value = double.tryParse(_split[1]);
        if (value != null) {
          _batteryLevel = (value >= 100) ? 100 : value;
        }
        clearValues();
      }

      // if (dataa.trim() == "nofinger") {
      //   _finger = false;
      // }

      // if (dataa.contains(":")) {
      //   _calibration = true;
      //   _finger = true;
      //   List<String> _split = dataa.split(":");
      //   _sys = double.tryParse(_split[0]) ?? 0;
      //   _dia = double.tryParse(_split[1]) ?? 0;
      // }
      if (dataa.contains("!")) {
        _calibration = true;
        List<String> _split = dataa.split("!");
        if (_split.length >= 2) {
          double? value = double.tryParse(_split[0]);
          double? value1 = double.tryParse(_split[1]);
          if (value != null && value1 != null) getBp(value, value1);
        }
        if (_split.length >= 3) {
          double? value = double.tryParse(_split[2]);
          if (value != null) getHR(value);
        }
        if (_split.length >= 4) {
          double? value = double.tryParse(_split[3]);
          if (value != null) getSpo2(value);
        }
      }
      if (dataa.contains(";")) {
        _calibration = true;
        List<String> _split = dataa.split(";");
        double? value = double.tryParse(_split[0]) ?? 0;
        if (value != 0) getTemp(value);
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

  void getBp(double value, double value1) {
    _sys = value;
    _dia = value1;
    if (_sys > 0 && _dia > 0) {
      _saveSys = _sys;
      _saveDia = _dia;
    }
    // if (value > _sys) {
    // _bpList = 0;
    // } else {
    //   _bpList++;
    // }
    // if (_bpList > 1) {
    //   _saveSys = _sys;
    //   _saveDia = _dia;
    //   _bpList = 0;
    //   _bpFlag = false;
    // }
  }

  void getHR(double value) {
    _hr = value;
    if (_hr > 0) {
      _saveHr = _hr;
    }
    // if (value > _hr) {
    //   _hrList = 0;
    // } else {
    //   _hrList++;
    // }
    // if (_hrList > 1) {
    // _saveHr = _hr;
    // _hrList = 0;
    // _hrFlag = false;
    // }
    // if (value < 60 || !_bpFlag) {
    //   return;
    // }
    // _hrList.add(value / 2);
    // _calibrateValue = (_hrList.length + _spo2List.length) * 5;
    // _hrList.sort();
    // _hr =
    //     (_hrList.reduce((value, element) => element + value) / _hrList.length);
    // _sys = (_hr > 80)
    //     ? (_hr + 20 + Random().nextInt(20))
    //     : (_hr + 40 + Random().nextInt(20));
    // _dia = (_hr > 80)
    //     ? (_hr + 5 - Random().nextInt(10))
    //     : (_hr - 5 + Random().nextInt(10));
    // if (_hrList.length >= 10) {
    //   print(_hrList);
    //   _saveHr = _hr;
    //   _saveSys = _sys;
    //   _saveDia = _dia;
    //   _bpFlag = false;
    // }
  }

  void getSpo2(double value) {
    if (value >= 99) {
      _spo2 = 99;
    } else if (value >= 94 && value <= 98) {
      _spo2 = 98;
    } else {
      _spo2 = value;
    }
    if (_spo2 > 0) {
      _saveSpo2 = _spo2;
    }
    // if (value > _spo2) {
    // _spo2List = 0;
    // } else {
    //   _spo2List++;
    // }
    // if (_spo2List > 1) {
    // _saveSpo2 = _spo2;
    // _spo2List = 0;
    // _spo2Flag = false;
    // }
    // if (value < 90 || !_spo2Flag) {
    //   return;
    // }
    // _spo2List.add(value);
    // _calibrateValue = (_hrList.length + _spo2List.length) * 5;
    // _spo2List.sort();
    // _spo2 = _spo2List[_spo2List.length - 1];
    // if (_spo2List.length >= 10) {
    //   _saveSpo2 = _spo2;
    //   print(_spo2List);
    //   _spo2Flag = false;
    // }
  }

  void getTemp(value) {
    if (!_tempFlag) {
      return;
    }
    double value1 = value + 5;
    if (value1 > _temp) {
      _temp = value1;
      _tempList = 0;
    } else {
      _tempList++;
    }
    if (_tempList > 15) {
      _tempList = 0;
      _tempFlag = false;
    }
  }
}
