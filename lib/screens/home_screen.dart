import 'dart:io';

import 'package:ayt/components/default_snackbar.dart';
import 'package:ayt/screens/product_screen.dart';
import 'package:ayt/screens/temp_screen.dart';
import 'package:ayt/utils/bluetooth_helper.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:path_provider/path_provider.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:provider/provider.dart';
import '../constants.dart';
import '../utils/counter.dart';
import 'bluetooth_devices.dart';

class Item {
  String title;
  String value;
  String showValue;
  String icon;
  Item({
    required this.title,
    required this.icon,
    required this.value,
    this.showValue = "0",
  });
}

class HomeScreen extends StatefulWidget {
  const HomeScreen({Key? key}) : super(key: key);

  @override
  _HomeScreenState createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  BluetoothHelper _helper = BluetoothHelper();
  List<Item> _myItems = [];

  void _showData() {
    Counter ctx = context.watch<Counter>();
    Item item1 = new Item(
      title: "Heart Rate",
      icon: "assets/icons/ht.png",
      value: "1",
      showValue: "${ctx.gettHr.toInt()}",
    );
    Item item2 = new Item(
      title: "Spo2",
      icon: "assets/icons/spo2.png",
      value: "1",
      showValue: "${ctx.gettSpo2.toInt()}",
    );
    Item item3 = new Item(
      title: "Blood Pressure",
      icon: "assets/icons/bp.png",
      value: "1",
      showValue: "${ctx.gettSys.toInt()} / ${ctx.gettDia.toInt()}",
    );
    Item item4 = new Item(
      title: "Temperature",
      icon: "assets/icons/temp.png",
      value: "4",
      showValue: ctx.gettTemp.toStringAsFixed(1),
    );
    Item item5 = new Item(
      title: "Ecg",
      icon: "assets/icons/ecg.png",
      value: "5",
    );
    _myItems = [item1, item2, item3, item4, item5];
  }

  void _getBondedDevices() async {
    bool _request = await _helper.requestBluetooth();
    if (!_request) return;
    Navigator.push(context, MaterialPageRoute(builder: (context) {
      return BluetoothDevices();
    }));
  }

  void onButtonPress(Item data) async {
    Counter ctx = context.read<Counter>();
    if (ctx.isConnected) {
      if (data.value == "5") {
        await _saveFile();
        ctx.sendData("0");
        ctx.disconnectDevice();
        _startActivity();
      } else if (data.value == "4") {
        ctx.sendData(data.value);
        await Navigator.push(
          context,
          MaterialPageRoute(builder: (context) => TempScreen()),
        );
      } else {
        ctx.sendData(data.value);
        await Navigator.push(
          context,
          MaterialPageRoute(builder: (context) => ProductScreen()),
        );
      }
      ctx.sendData("0");
      ctx.clearValues();
    } else {
      ShowSnackBarwithOk(
        context,
        "Device offline, Please connected to device...",
      );
    }
  }

  Future<void> _startActivity() async {
    try {
      final String result = await platform.invokeMethod('StartEcgActivity');
      print('Result: $result ');
    } on PlatformException catch (e) {
      print("Error: '${e.message}'.");
    }
  }

  Future<void> _saveFile() async {
    Counter _ctx = context.read<Counter>();
    Directory? directory = await getExternalStorageDirectory();
    if (directory != null) {
      Directory fileDir =
          await Directory(directory.path + "/cache").create(recursive: true);
      if (await _checkPermission()) {
        File file = File('${fileDir.path}/vitals.txt');
        await file.writeAsString(
            "${_ctx.gettHr.toInt()}\n${_ctx.gettSys.toInt()}\n${_ctx.gettDia.toInt()}\n${_ctx.gettSpo2.toInt()}\n${_ctx.gettTemp.toStringAsFixed(1)}\n");
        // _ctx.clearSavedValues();
      }
    }
  }

  Future<bool> _checkPermission() async {
    Permission _permission = Permission.storage;
    var result = await _permission.status;
    if (result.isDenied || result.isPermanentlyDenied) {
      var result = await _permission.request();
      if (result.isDenied || result.isPermanentlyDenied)
        return false;
      else
        return true;
    } else {
      return true;
    }
  }

  @override
  Widget build(BuildContext context) {
    _showData();
    return WillPopScope(
      onWillPop: _onBackPressed,
      child: Scaffold(
        backgroundColor: hBackground,
        appBar: AppBar(
          centerTitle: true,
          elevation: 1,
          leading: Padding(
            padding: const EdgeInsets.all(8.0),
            child: Image.asset("assets/images/logo.png"),
          ),
          title: Text(
            "AYT Multi Vital",
            style: TextStyle(
              color: hTextColor,
              fontWeight: FontWeight.bold,
              fontSize: 18,
            ),
          ),
          actions: [
            Row(
              children: [
                Icon(
                  Icons.adjust_outlined,
                  size: 14,
                  color: context.watch<Counter>().isConnected
                      ? Colors.green
                      : Colors.red,
                ),
                Container(
                  padding: EdgeInsets.all(8.0),
                  child: Text(
                    context.watch<Counter>().isConnected ? "Online" : "Offline",
                    style: TextStyle(
                      color: context.watch<Counter>().isConnected
                          ? Colors.green
                          : Colors.red,
                      fontWeight: FontWeight.bold,
                      fontSize: 14,
                    ),
                  ),
                ),
              ],
            )
          ],
        ),
        body: SingleChildScrollView(
          physics: BouncingScrollPhysics(),
          child: Padding(
            padding: const EdgeInsets.symmetric(vertical: 16, horizontal: 18),
            child: Column(
              children: [
                Row(
                  children: [
                    Text(
                      "Dashboard",
                      style: TextStyle(
                        fontSize: 20,
                        color: hTextColor,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    Spacer(),
                    Text(
                      context.read<Counter>().isConnected
                          ? "${context.watch<Counter>().batteryValue.toInt()}%"
                          : "-",
                      style: TextStyle(
                        color: hTextColor,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    Icon(
                      Icons.battery_full,
                      color: context.watch<Counter>().batteryValue > 60
                          ? Colors.green
                          : Colors.red,
                    ),
                  ],
                ),
                SizedBox(height: 15),
                Container(
                  padding: EdgeInsets.symmetric(horizontal: 28, vertical: 20),
                  decoration: BoxDecoration(
                    color: hPrimary,
                    borderRadius: BorderRadius.circular(10),
                  ),
                  child: Column(
                    children: [
                      Text(
                        "Welcome",
                        style: TextStyle(
                          color: Colors.white,
                          fontWeight: FontWeight.w600,
                          fontSize: 24,
                        ),
                      ),
                      SizedBox(height: 12),
                      Text(
                        "Let's check your health with us, care with your from now to get more live better",
                        textAlign: TextAlign.center,
                        style: TextStyle(
                          color: Colors.white,
                          fontSize: 14,
                        ),
                      ),
                      SizedBox(height: 16),
                      Align(
                        alignment: Alignment.centerLeft,
                        child: Text(
                          "Device status: ${context.watch<Counter>().status}",
                          style: TextStyle(
                            fontWeight: FontWeight.w600,
                            color: Colors.white,
                            fontSize: 16,
                          ),
                        ),
                      ),
                      SizedBox(height: 16),
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceAround,
                        children: [
                          Expanded(
                            child: ElevatedButton(
                              style: ElevatedButton.styleFrom(
                                primary: hButtonColor,
                              ),
                              onPressed: () => context
                                      .read<Counter>()
                                      .isConnected
                                  ? context.read<Counter>().disconnectDevice()
                                  : _getBondedDevices(),
                              child: Text(
                                context.watch<Counter>().isConnected
                                    ? "Disconnect"
                                    : "Connect",
                                style: TextStyle(color: hPrimary),
                              ),
                            ),
                          ),
                          // SizedBox(width: 8),
                          // Expanded(
                          //   child: ElevatedButton(
                          //     style: ElevatedButton.styleFrom(
                          //       primary: hButtonColor,
                          //     ),
                          //     onPressed: () {
                          //       if (context.read<Counter>().isConnected) {
                          //         Navigator.push(
                          //           context,
                          //           MaterialPageRoute(
                          //             builder: (context) {
                          //               return CalibrationScreen();
                          //             },
                          //           ),
                          //         );
                          //       } else {
                          //         ShowSnackBarwithOk(
                          //           context,
                          //           "Device offline, Please connected to device...",
                          //         );
                          //       }
                          //     },
                          //     child: Text(
                          //       "Calibrate Now",
                          //       style: TextStyle(color: hPrimary),
                          //     ),
                          //   ),
                          // ),
                        ],
                      )
                    ],
                  ),
                ),
                SizedBox(height: 10),
                (context.watch<Counter>().isConnected &&
                        !context.watch<Counter>().calibration)
                    ? Container(
                        height: 300,
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            Padding(
                              padding: EdgeInsets.symmetric(horizontal: 10),
                              child: Text(
                                "Please place your finger on the device and Press the start button",
                                textAlign: TextAlign.center,
                              ),
                            ),
                            SizedBox(height: 15),
                            OutlinedButton(
                              onPressed: () async {
                                context.read<Counter>().sendData("1");
                                await Navigator.push(
                                  context,
                                  MaterialPageRoute(
                                      builder: (context) => ProductScreen()),
                                );
                                context.read<Counter>().sendData("0");
                                context.read<Counter>().clearValues();
                              },
                              child: Text(
                                "Start",
                                style: TextStyle(fontSize: 18),
                              ),
                            )
                          ],
                        ),
                      )
                    : GridView.count(
                        shrinkWrap: true,
                        physics: NeverScrollableScrollPhysics(),
                        childAspectRatio: 1.1,
                        crossAxisCount: 2,
                        padding: EdgeInsets.symmetric(vertical: 16),
                        crossAxisSpacing: 16,
                        mainAxisSpacing: 16,
                        children: _myItems
                            .map(
                              (data) => GestureDetector(
                                onTap: () => onButtonPress(data),
                                child: Container(
                                  padding: EdgeInsets.symmetric(
                                      horizontal: 16, vertical: 8),
                                  decoration: BoxDecoration(
                                    color: Colors.white,
                                    borderRadius: BorderRadius.circular(10),
                                  ),
                                  child: Column(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    mainAxisAlignment:
                                        MainAxisAlignment.spaceAround,
                                    children: <Widget>[
                                      Image.asset(
                                        data.icon,
                                        height: 40,
                                      ),
                                      Text(
                                        data.title,
                                        style: TextStyle(
                                          fontSize: 18,
                                          fontWeight: FontWeight.bold,
                                          color: hTextColor,
                                        ),
                                      ),
                                      if (data.value != "5")
                                        Text(
                                          data.showValue,
                                          style: TextStyle(
                                            fontSize: 20,
                                            fontWeight: FontWeight.bold,
                                            color: hTextColor,
                                          ),
                                        ),
                                    ],
                                  ),
                                ),
                              ),
                            )
                            .toList(),
                      ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Future<bool> _onBackPressed() async {
    return (await showDialog(
      context: context,
      builder: (context) => new AlertDialog(
        content: Text('Are you sure, Do you want to exit ?'),
        actions: <Widget>[
          TextButton(
            onPressed: () => Navigator.of(context).pop(false),
            child: Text('No'),
          ),
          TextButton(
            onPressed: () {
              context.read<Counter>().sendData("0");
              context.read<Counter>().disconnectDevice();
              Navigator.of(context).pop(true);
            },
            child: Text('Yes'),
          ),
        ],
      ),
    ));
  }
}
