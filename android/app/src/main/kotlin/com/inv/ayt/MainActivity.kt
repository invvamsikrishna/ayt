package com.inv.ayt

import android.content.Intent
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity: FlutterActivity() {
    private val CHANNEL = "test_activity"

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler {
            call, result ->
            if(call.method.equals("StartEcgActivity")){
                startNewActivity();
                result.success("ActivityStarted")
            }
            else{
                result.notImplemented()
            }
        }
    }

    private fun startNewActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }
}
