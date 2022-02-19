import 'dart:async';
import 'package:ayt/components/default_snackbar.dart';
import 'package:ayt/utils/counter.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bluetooth_serial/flutter_bluetooth_serial.dart';
import '../constants.dart';
import 'package:provider/provider.dart';

class BluetoothDevices extends StatefulWidget {
  const BluetoothDevices({Key? key}) : super(key: key);

  @override
  _BluetoothDevicesState createState() => _BluetoothDevicesState();
}

class _BluetoothDevicesState extends State<BluetoothDevices> {
  List<BluetoothDevice> _myDevices = [];
  StreamSubscription<BluetoothDiscoveryResult>? _discoverySub;
  bool _isLoading = true;
  bool _isPairing = false;

  @override
  void initState() {
    super.initState();
    _getDiscoveredDevices();
  }

  void _getDiscoveredDevices() {
    setState(() {
      _isLoading = true;
      _myDevices.clear();
    });
    _discoverySub =
        FlutterBluetoothSerial.instance.startDiscovery().listen((event) {
      if (!_myDevices.contains(event.device)) {
        setState(() {
          _myDevices.add(event.device);
        });
      }
    });
    _discoverySub?.onDone(() {
      setState(() {
        _isLoading = false;
      });
    });
  }

  void _pairDevice(BluetoothDevice device) async {
    try {
      setState(() {
        _isPairing = true;
      });
      ShowSnackBarwithOk(context, "Pairing...");
      FlutterBluetoothSerial.instance
          .bondDeviceAtAddress(device.address)
          .then((value) {
        if (value ?? false) {
          _connectDevice(device);
        } else {
          ShowSnackBarwithOk(context, "Pairing Failed");
          setState(() {
            _isPairing = false;
          });
        }
      });
    } catch (error) {
      print(error);
    }
  }

  void _connectDevice(BluetoothDevice device) async {
    setState(() {
      _isPairing = true;
    });
    ShowSnackBarwithOk(context, "Connecting...");
    if (device.address.isNotEmpty) {
      String result = await context.read<Counter>().connectToDevice(device);
      if (result == "connected") {
        ShowSnackBarwithOk(context, "Connected to device");
        context.read<Counter>().getData();
        Navigator.pop(context);
      } else {
        ShowSnackBarwithOk(context, "Connecting failed, Please try again");
      }
    }
    setState(() {
      _isPairing = false;
    });
  }

  @override
  void dispose() {
    _discoverySub?.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: hBackground,
      appBar: AppBar(
        title: Text(
          "Bluetooth Devices",
          style: TextStyle(
            color: hTextColor,
            fontWeight: FontWeight.bold,
            fontSize: 18,
          ),
        ),
        actions: [
          Visibility(
            visible: !_isLoading,
            child: IconButton(
              onPressed: _getDiscoveredDevices,
              icon: Icon(Icons.refresh_rounded),
            ),
          )
        ],
      ),
      body: SingleChildScrollView(
        child: Column(
          children: [
            Visibility(
              visible: _isLoading,
              child: LinearProgressIndicator(
                color: hPrimary,
                backgroundColor: Colors.transparent,
              ),
            ),
            ListView.builder(
              physics: NeverScrollableScrollPhysics(),
              shrinkWrap: true,
              itemCount: _myDevices.length,
              itemBuilder: (context, index) {
                return ListTile(
                  minLeadingWidth: 30,
                  leading: Icon(Icons.devices, color: hPrimary),
                  visualDensity: VisualDensity(horizontal: -1, vertical: -1),
                  title: Text(_myDevices[index].name ?? "Unknown device"),
                  subtitle: Text(_myDevices[index].address),
                  trailing: Visibility(
                    visible: !_isPairing,
                    child: ElevatedButton(
                      style: ElevatedButton.styleFrom(primary: hPrimary),
                      onPressed: () => _myDevices[index].isBonded
                          ? _connectDevice(_myDevices[index])
                          : _pairDevice(_myDevices[index]),
                      child:
                          Text(_myDevices[index].isBonded ? "Connect" : "Pair"),
                    ),
                  ),
                );
              },
            ),
          ],
        ),
      ),
    );
  }
}
