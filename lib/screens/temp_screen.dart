import 'package:ayt/components/default_snackbar.dart';
import 'package:ayt/utils/counter.dart';
import 'package:flutter/material.dart';
import '../constants.dart';
import 'dart:async';
import 'package:provider/provider.dart';

class TempScreen extends StatefulWidget {
  @override
  _TempScreenState createState() => _TempScreenState();
}

class _TempScreenState extends State<TempScreen> {
  Timer? t;
  double _value = 0;
  String _minimum = "-", _maximum = "-";
  bool _showMeasuring = false;
  double _timer = 0, _duration = 0;
  late void Function(void Function()) _state;

  @override
  void initState() {
    super.initState();
    _duration = 15;
    _timer = _duration;
    _getData();
  }

  void _getData() {
    Counter _ctx = context.read<Counter>();
    if (_ctx.isConnected) {
      t = Timer.periodic(Duration(milliseconds: 1000), (Timer timer) {
        _getValue();
        if (_showMeasuring && _timer != 0) {
          if (_ctx.finger) {
            _state(() {
              _timer -= 1;
            });
          } else {
            _state(() {
              _timer = _duration;
            });
          }
        }
        if (_timer == 0) {
          _state(() {
            _timer = _duration;
          });
        }
        if (_value <= 0 && !_showMeasuring) {
          _showMeasuring = true;
          _showMeasuringDialog();
        } else if (_value > 0) {
          _timer = _duration;
          if (_showMeasuring) {
            _showMeasuring = false;
            Navigator.pop(context);
            t?.cancel();
          }
        }
      });
    }
  }

  void _getValue() {
    Counter _ctx = context.read<Counter>();
    setState(() {
      _value = _ctx.temp;
    });
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
          "Temperature",
          style: TextStyle(
            color: hTextColor,
            fontWeight: FontWeight.bold,
            fontSize: 18,
          ),
        ),
        elevation: 0,
      ),
      body: SingleChildScrollView(
        child: Column(
          children: [
            Container(
              width: double.infinity,
              padding: EdgeInsets.all(30),
              decoration: BoxDecoration(
                  color: Colors.white, borderRadius: BorderRadius.circular(50)),
              child: Column(
                children: [
                  Image.asset(
                    "assets/icons/temp.png",
                    height: 100,
                  ),
                  SizedBox(height: 20),
                  Text(
                    "${_value.toStringAsFixed(1)} F",
                    style: TextStyle(
                      color: hTextColor,
                      fontSize: 28,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  Container(
                    margin: EdgeInsets.all(20),
                    padding: EdgeInsets.all(8),
                    decoration: BoxDecoration(
                      color: context.watch<Counter>().isConnected
                          ? Colors.green[100]
                          : Colors.red[100],
                      borderRadius: BorderRadius.circular(10),
                    ),
                    child: Text(
                      context.watch<Counter>().isConnected
                          ? "Device Online"
                          : "Decice Offline",
                      style: TextStyle(
                        color: context.watch<Counter>().isConnected
                            ? Colors.green
                            : Colors.red,
                      ),
                    ),
                  ),
                  Padding(
                    padding: EdgeInsets.all(8.0),
                    child: Text("Preferred Levels"),
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                    children: [
                      Column(
                        children: [
                          Text("MINIMUM"),
                          SizedBox(height: 5),
                          Text(
                            _minimum,
                            style: TextStyle(
                              fontWeight: FontWeight.w600,
                              fontSize: 18,
                            ),
                          )
                        ],
                      ),
                      Container(
                        height: 30,
                        child: VerticalDivider(color: Colors.black87),
                      ),
                      Column(
                        children: [
                          Text("MAXIMUM"),
                          SizedBox(height: 5),
                          Text(
                            _maximum,
                            style: TextStyle(
                              fontWeight: FontWeight.w600,
                              fontSize: 18,
                            ),
                          )
                        ],
                      )
                    ],
                  ),
                  ElevatedButton(
                    style: ElevatedButton.styleFrom(
                      primary: hButtonColor,
                    ),
                    onPressed: () {
                      context.read<Counter>().saveeTemp(_value);
                      ShowSnackBarwithOk(context, "Record Saved");
                    },
                    child: Text("Save",style: TextStyle(color: hPrimary),),
                  )
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
                        context.watch<Counter>().finger
                            ? "Measuring, Please wait..."
                            : "Place your finger on the device",
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
                          "${_timer.round()}",
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
