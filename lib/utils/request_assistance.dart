import 'dart:convert' as convert;
import 'package:http/http.dart' as http;

class RequestAssistance {
  static Future<dynamic> getRequest(String url) async {
    Uri urll = Uri.parse(url);
    
    try {
      http.Response response = await http.get(urll);
      if (response.statusCode == 200) {
        String jsonData = response.body;
        var decodeData = convert.jsonDecode(jsonData);
        if (decodeData == null) {
          return "failed";
        }
        if (decodeData["token"] == "1") {
          return "success";
        } else{
          return "failed";
        }
      } else {
        return "failed";
      }
    } catch (exp) {
      print(exp);
      return "failed";
    }
  }
}
