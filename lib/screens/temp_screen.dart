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
      });
    }
  }

  void _getValue() {
    Counter _ctx = context.read<Counter>();
    setState(() {
      _value = _ctx.temp;
    });
    if (!_ctx.tempFlag) {
      _ctx.saveeTemp(_value);
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
                  if (context.watch<Counter>().tempFlag)
                    Column(
                      children: [
                        Padding(
                          padding: EdgeInsets.symmetric(horizontal: 10),
                          child: Text(
                            "Measuring, Please wait...",
                            textAlign: TextAlign.center,
                          ),
                        ),
                        SizedBox(height: 15),
                      ],
                    ),
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
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
