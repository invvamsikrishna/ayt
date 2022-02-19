import 'dart:async';

import 'package:ayt/components/default_snackbar.dart';
import 'package:ayt/screens/home_screen.dart';
import 'package:ayt/utils/counter.dart';
import 'package:ayt/utils/request_assistance.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:provider/provider.dart';

import 'constants.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  SystemChrome.setSystemUIOverlayStyle(
      SystemUiOverlayStyle(statusBarColor: Colors.transparent));
  SystemChrome.setPreferredOrientations([
    DeviceOrientation.portraitUp,
    DeviceOrientation.portraitDown,
  ]).then(
    (value) => runApp(
      MultiProvider(
        providers: [ChangeNotifierProvider(create: (_) => Counter())],
        child: MyApp(),
      ),
    ),
  );
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'AYT',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        primarySwatch: Colors.blue,
        appBarTheme: AppBarTheme(
          iconTheme: IconThemeData(color: hPrimary),
          backgroundColor: hBackground,
          elevation: 0,
        ),
      ),
      home: SplashScreen(),
    );
  }
}

class SplashScreen extends StatefulWidget {
  const SplashScreen({Key? key}) : super(key: key);
  @override
  _SplashScreenState createState() => _SplashScreenState();
}

class _SplashScreenState extends State<SplashScreen> {
  @override
  void initState() {
    super.initState();
    _checkUser();
  }

  void _checkUser() async {
    String url =
        "https://pythonwebcall-default-rtdb.firebaseio.com/health.json";
    var response = await RequestAssistance.getRequest(url);
    if (response == "success") {
      Timer(Duration(seconds: 3), () {
        Navigator.pushAndRemoveUntil(
          context,
          MaterialPageRoute(builder: (context) => HomeScreen()),
          (route) => false,
        );
      });
    } else {
      ShowSnackBar(context, "Please check internet connection...");
    }
  }

  @override
  Widget build(BuildContext context) {
    Size size = MediaQuery.of(context).size;
    return Scaffold(
      backgroundColor: Colors.white,
      body: Container(
        height: size.height,
        width: size.width,
        padding: EdgeInsets.all(20),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Spacer(flex: 2),
            Image.asset(
              "assets/images/logo.png",
              height: size.height * 0.2,
            ),
            SizedBox(height: 20),
            Text(
              "AYT Multi Vital",
              style: TextStyle(
                  color: hTextColor, fontSize: 20, fontWeight: FontWeight.bold),
            ),
            Spacer(),
            Image.asset(
              "assets/images/ecg_gif.gif",
              height: size.height * 0.1,
            ),
            Spacer(),
            Text(
              "Powered by INV TECHNOLOGIES",
              style: TextStyle(color: hTextColor, fontSize: 8),
            )
          ],
        ),
      ),
    );
  }
}
