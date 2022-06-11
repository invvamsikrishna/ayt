import 'package:ayt/utils/counter.dart';
import 'package:flutter/material.dart';
import '../constants.dart';
import 'dart:async';
import 'package:provider/provider.dart';

class ProductScreen extends StatefulWidget {
  @override
  _ProductScreenState createState() => _ProductScreenState();
}

class _ProductScreenState extends State<ProductScreen> {
  Timer? t;
  double _value = 0, _value1 = 0;
  String _bpValue = "0 / 0";
  bool _finger = false, _showMeasuring = false;
  double _timer = 0;
  late void Function(void Function()) _state;

  @override
  void initState() {
    super.initState();
    _getData();
  }

  void _getData() {
    Counter _ctx = context.read<Counter>();
    if (_ctx.isConnected) {
      t = Timer.periodic(Duration(milliseconds: 1000), (Timer timer) {
        _getValue();
        // if ((_value <= 0 || _value1 <= 0) && !_showMeasuring) {
        //   _showMeasuring = true;
        //   _showMeasuringDialog();
        // } else if (_value > 0 && _value1 > 0) {
        //   if (_showMeasuring) {
        //     _showMeasuring = false;
        //     Navigator.pop(context);
        //   }
        // }
      });
    }
  }

  void _getValue() {
    Counter _ctx = context.read<Counter>();
    if (_ctx.sys <= 0 &&
        _ctx.dia <= 0 &&
        _ctx.hr <= 0 &&
        _ctx.spo2 <= 0 &&
        _ctx.calibrateValue <= 0) {
      _finger = false;
    } else {
      _finger = true;
    }
    setState(() {
      _timer = _ctx.calibrateValue * 0.6;
      if (_ctx.sys > 0) _timer += 20;
      if (_ctx.hr > 0) _timer += 10;
      if (_ctx.spo2 > 0) _timer += 10;
      _value = _ctx.hr;
      _bpValue =
          _ctx.sys.round().toString() + "/" + _ctx.dia.round().toString();
      _value1 = _ctx.spo2;
    });
    if (!_ctx.isConnected) {
      t?.cancel();
    }
  }

  @override
  void dispose() {
    super.dispose();
    t?.cancel();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: hBackground,
      appBar: AppBar(
        centerTitle: true,
        backgroundColor: hBackground,
        title: Text(
          "Multi Vital",
          style: TextStyle(
            color: hTextColor,
            fontWeight: FontWeight.bold,
            fontSize: 18,
          ),
        ),
        elevation: 0,
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
        child: Column(
          children: [
            if (_timer < 100)
              Column(
                children: [
                  Padding(
                    padding: EdgeInsets.symmetric(horizontal: 10),
                    child: Text(
                       context.watch<Counter>().calibrationfailed
                    ? "Calibration failed, Please restart device":
                      _finger
                          ? "Calibrating, Please wait..."
                          : "Please place your finger on the device",
                      textAlign: TextAlign.center,
                    ),
                  ),
                  SizedBox(height: 15),
                  if(!context.watch<Counter>().calibrationfailed)
                  Stack(
                    alignment: Alignment.center,
                    children: [
                      Container(
                        width: 40,
                        height: 40,
                        child: CircularProgressIndicator(
                          strokeWidth: 2,
                        ),
                      ),
                      Text(
                        "${_timer.round()}%",
                        style: TextStyle(
                          fontSize: 16,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ],
                  ),
                  SizedBox(height: 15),
                ],
              ),
            Container(
              width: double.infinity,
              padding: EdgeInsets.all(30),
              decoration: BoxDecoration(
                  color: Colors.white, borderRadius: BorderRadius.circular(50)),
              child: Column(
                children: [
                  Image.asset(
                    "assets/icons/ht.png",
                    height: 100,
                  ),
                  SizedBox(height: 20),
                  Text(
                    "${_value.toInt()} BPM",
                    style: TextStyle(
                      color: hTextColor,
                      fontSize: 28,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ],
              ),
            ),
            SizedBox(height: 10),
            Container(
              width: double.infinity,
              padding: EdgeInsets.all(30),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(50),
              ),
              child: Column(
                children: [
                  Image.asset(
                    "assets/icons/spo2.png",
                    height: 100,
                  ),
                  SizedBox(height: 20),
                  Text(
                    "${_value1.toInt()}",
                    style: TextStyle(
                      color: hTextColor,
                      fontSize: 28,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ],
              ),
            ),
            SizedBox(height: 10),
            Container(
              width: double.infinity,
              padding: EdgeInsets.all(30),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(50),
              ),
              child: Column(
                children: [
                  Image.asset(
                    "assets/icons/bp.png",
                    height: 100,
                  ),
                  SizedBox(height: 20),
                  Text(
                    _bpValue,
                    style: TextStyle(
                      color: hTextColor,
                      fontSize: 28,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  _showMeasuringDialog() {
    return showDialog(
      context: context,
      barrierDismissible: false,
      builder: (context) {
        return StatefulBuilder(
          builder:
              (BuildContext context, void Function(void Function()) setStatee) {
            _state = setStatee;
            return WillPopScope(
              onWillPop: () async => false,
              child: AlertDialog(
                content: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Padding(
                      padding: EdgeInsets.symmetric(horizontal: 10),
                      child: Text(
                        "Place your finger on the device",
                        textAlign: TextAlign.center,
                      ),
                    ),
                    SizedBox(height: 15),
                    Stack(
                      alignment: Alignment.center,
                      children: [
                        Container(
                          width: 40,
                          height: 40,
                          child: CircularProgressIndicator(
                            strokeWidth: 2,
                          ),
                        ),
                        Text(
                          "0%",
                          style: TextStyle(
                            fontSize: 16,
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                      ],
                    ),
                    SizedBox(height: 15),
                    OutlinedButton(
                      onPressed: () => Navigator.of(context)..pop()..pop(),
                      child: Text("Cancel"),
                    )
                  ],
                ),
              ),
            );
          },
        );
      },
    );
  }
}
