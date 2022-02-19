import 'package:flutter/material.dart';
import '../constants.dart';

class ShowSnackBar {
  final BuildContext context;
  final String text;
  ShowSnackBar(this.context, this.text) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        duration: Duration(minutes: 2),
        content: Text(text),
        behavior: SnackBarBehavior.floating,
        backgroundColor: hPrimary,
      ),
    );
  }
}

class ShowSnackBarwithOk {
  final BuildContext context;
  final String text;
  ShowSnackBarwithOk(this.context, this.text) {
    ScaffoldMessenger.of(context)
      ..clearSnackBars()
      ..showSnackBar(
        SnackBar(
          content: Text(text),
          action: SnackBarAction(
            label: "OK",
            textColor: Colors.white,
            onPressed: () {
              ScaffoldMessenger.of(context).hideCurrentSnackBar();
            },
          ),
          behavior: SnackBarBehavior.floating,
          backgroundColor: hPrimary,
        ),
      );
  }
}
