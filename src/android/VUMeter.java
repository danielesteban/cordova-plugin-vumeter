package com.gatunes.vumeter;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.audiofx.Visualizer;

public class VUMeter extends CordovaPlugin implements Visualizer.OnDataCaptureListener {
  public static final String RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;

  private Visualizer visualizer = null;
  private double amplitude = 0;

  public VUMeter() {

  }

  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);
    if (cordova.hasPermission(RECORD_AUDIO)) {
      initVisualizer();
    } else {
      cordova.requestPermission(this, 0, RECORD_AUDIO);
    }
  }

  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    if ("getMeasurement".equals(action)) {
      JSONObject r = new JSONObject();
      r.put("amplitude", amplitude);
      callbackContext.success(r);
      return true;
    }
    return false;
  }

  private void initVisualizer() {
    visualizer = new Visualizer(0);
    visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
    visualizer.setDataCaptureListener(this, Visualizer.getMaxCaptureRate(), false, true);
    visualizer.setEnabled(true);
  }

  public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {

  }

  public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
    float sum = 0;
    for (int i = 0; i < fft.length; i += 2) {
      float magnitude = (float) Math.hypot(fft[i], fft[i + 1]);
      sum += magnitude * magnitude;
    }
    amplitude = (float) Math.sqrt(sum / (fft.length / 2));
  }

  public void onPause(boolean multitasking) {
    if (visualizer != null) {
      visualizer.setEnabled(false);
      visualizer = null;
    }
  }

  public void onResume(boolean multitasking) {
    if (cordova.hasPermission(RECORD_AUDIO)) {
      initVisualizer();
    } else {
      cordova.requestPermission(this, 0, RECORD_AUDIO);
    }
  }

  public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
    for (int r:grantResults) {
      if (r == PackageManager.PERMISSION_DENIED) {
        return;
      }
    }
    if (visualizer == null) {
      initVisualizer();
    }
  }
}
