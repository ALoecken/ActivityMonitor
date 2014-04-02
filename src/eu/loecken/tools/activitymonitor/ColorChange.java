package eu.loecken.tools.activitymonitor;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Expo;
import eu.loecken.tools.activitymonitor.lights.ArduinoMachine;
import eu.loecken.tools.activitymonitor.lights.OpenDMXLight;
import eu.loecken.tools.activitymonitor.tweendata.ValueAccessor;
import java.awt.Color;

/**
 * 
 * @author Andreas
 */
public class ColorChange {
  
  private final TweenManager tweenManager = new TweenManager();
  public final static int greenHue = 110; // green (normally 120)
  public final static int maxAkku = 3600; // one hour
  private OpenDMXLight openDMXlight;
  private ArduinoMachine arduinoLight;
  private float[] hsvColor;
  private double akku;
  
  public ColorChange() {
    this.hsvColor = new float[] {
        greenHue, 1f, 1f
    };
    this.akku = maxAkku;
    this.openDMXlight = new OpenDMXLight();
    try {
      this.arduinoLight = new ArduinoMachine("COM7");
    } catch (Exception ex) {
      System.err.println(ex);
      this.arduinoLight = null;
    }
    
    Tween.registerAccessor(float[].class, new ValueAccessor());
    
    Tween.to(this.hsvColor, ValueAccessor.HUE, 10).target(.1f).ease(Expo.OUT)
        .repeatYoyo(Tween.INFINITY, 0).start(tweenManager);
    
  }
  
  public void addPauseTime(double delta) {
    this.akku += (delta * 3);
    if (this.akku > maxAkku) {
      this.akku = maxAkku;
    }
    update();
  }
  
  public void addWorkTime(double delta) {
    this.akku -= delta;
    if (this.akku < 0) {
      this.akku = 0;
    }
    update();
  }
  
  private void update() {
    if (this.openDMXlight == null || !this.openDMXlight.isConnected()) {
      return;
    }
    float h = (float) (greenHue * (Math.pow((this.akku) / maxAkku, 2)));
    if (h > greenHue) {
      h = greenHue;
    }
    if (h < 0) {
      h = 0;
    }
    this.hsvColor[0] = h;
  }
  
  void update(float delta) {
    tweenManager.update(delta);
    openDMXlight.updateColor(Color.getHSBColor((float) (hsvColor[0] / 360.), hsvColor[1],
        hsvColor[2]));
    if (arduinoLight != null)
      arduinoLight.updateColor(Color.getHSBColor((float) (hsvColor[0] / 360.), hsvColor[1],
          hsvColor[2]));
  }
  
  void stop() {
    tweenManager.killAll();
    openDMXlight.updateColor(null);
    if (arduinoLight != null) arduinoLight.updateColor(null);
  }
}
