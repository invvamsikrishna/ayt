import 'dart:async';
import 'package:ayt/components/default_snackbar.dart';
import 'package:ayt/utils/counter.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class CalibrationScreen extends StatefulWidget {
  const CalibrationScreen({
    Key? key,
  }) : super(key: key);

  @override
  _CalibrationScreenState createState() => _CalibrationScreenState();
}

class _CalibrationScreenState extends State<CalibrationScreen> {
  Timer? t;
  bool _visible = true;
  int _timer = 20;
  @override
  void initState() {
    super.initState();
    _connectToDevice();
  }

  void _connectToDevice() {
    _startTimer();
  }

  void _startTimer() {
    t = Timer.periodic(Duration(seconds: 1), (timer) {
      setState(() {
        if (_timer == 0) {
          _timer = 20;
          ShowSnackBarwithOk(
            context,
            "Please place your finger on device properly",
          );
        }
        if (context.read<Counter>().calibrateValue > 0) {
          _visible = false;
          _timer = 20;
        } else {
          _timer--;
          _visible = true;
        }
      });
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
      backgroundColor: Color(0xff211d1e),
      body: SafeArea(
        child: Column(
          children: [
            Align(
              alignment: Alignment.centerRight,
              child: IconButton(
                onPressed: () => Navigator.pop(context),
                icon: Icon(
                  Icons.close_rounded,
                  color: Colors.white70,
                ),
              ),
            ),
            Padding(
              padding: EdgeInsets.symmetric(vertical: 30, horizontal: 20),
              child: Text(
                context.watch<Counter>().calibrationfailed
                    ? "Calibration failed, Please restart device"
                    : context.watch<Counter>().calibration
                        ? "Calibration Completed"
                        : context.watch<Counter>().calibrateValue <= 0
                            ? "Please keep your finger on device until reach 100%"
                            : "Calibrating, Please wait...",
                style: TextStyle(
                  color: context.watch<Counter>().calibrationfailed
                      ? Colors.red
                      : Colors.white70,
                  fontSize: 20,
                  fontWeight: FontWeight.w600,
                ),
                textAlign: TextAlign.center,
              ),
            ),
            Spacer(),
            Stack(
              alignment: Alignment.center,
              children: [
                Container(
                  width: 245,
                  height: 245,
                  child: CircularProgressIndicator(
                    valueColor: AlwaysStoppedAnimation(Color(0xff3f3c3d)),
                    strokeWidth: 5,
                  ),
                ),
                Container(
                  width: 250,
                  height: 250,
                  child: CircularProgressIndicator(
                    backgroundColor: Color(0xff3f3c3d),
                    valueColor: AlwaysStoppedAnimation(Colors.white70),
                    value: context.watch<Counter>().calibrateValue / 100,
                  ),
                ),
                Text(
                  "${context.watch<Counter>().calibrateValue}%",
                  style: TextStyle(
                    color: Colors.white70,
                    fontSize: 20,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ],
            ),
            Spacer(),
            Visibility(
              visible: context.watch<Counter>().calibration ? true : false,
              child: ElevatedButton(
                onPressed: () => Navigator.pop(context),
                child: Text("Let's Start"),
              ),
            ),
            Visibility(
              visible: _visible,
              child: Stack(
                alignment: Alignment.center,
                children: [
                  Container(
                    width: 40,
                    height: 40,
                    child: CircularProgressIndicator(
                      valueColor: AlwaysStoppedAnimation(Colors.white54),
                      strokeWidth: 2,
                    ),
                  ),
                  Text(
                    "$_timer",
                    style: TextStyle(
                      color: Colors.white70,
                      fontSize: 16,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ],
              ),
            ),
            Spacer(flex: 2),
          ],
        ),
      ),
    );
  }
}
