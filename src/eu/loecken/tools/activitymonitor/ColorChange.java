package eu.loecken.tools.activitymonitor;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Expo;
import aurelienribon.tweenengine.equations.Sine;
import eu.loecken.tools.activitymonitor.lights.ArduinoMachine;
import eu.loecken.tools.activitymonitor.lights.OpenDMXLight;
import eu.loecken.tools.activitymonitor.tweendata.ValueAccessor;
import java.awt.Color;

/**
 * 
 * @author Andreas
 */
public class ColorChange {
  
  // TODO: Put this into settings and/or menu
  public final static int greenHue = 240; // let's use blue (240) (green is normally 120) 
  public final static int redHue = 0; // red
  public final static int maxAkku = 1500; // let's use 25minutes --> 1500 sec.
  public final static float workPauseRatio = maxAkku / 300f; // 5 minutes break --> 300sec.
  public final static int timePerCycle = 7; // seconds ~17 "breaths" per minute
  public final static float maxBrightness = .4f; // 40% maximum brightness
  public final static float minBrightness = .07f; // 7% minimum brightness
  public static final String comPort = "COM4"; // COM-Port for Arduino-Light
  
  private final TweenManager brightnessTweenManager = new TweenManager();
  private final TweenManager hueTweenManager = new TweenManager();
  private Tween hueTween;
  private OpenDMXLight openDMXlight;
  private ArduinoMachine arduinoLight;
  private float[] hsvColor;
  private double akku, lastAkku;
  
  public ColorChange() {
    this.hsvColor = new float[] {
        greenHue, 1f, maxBrightness
    };
    this.akku = maxAkku;
    this.openDMXlight = new OpenDMXLight();
    try {
      this.arduinoLight = new ArduinoMachine(comPort);
    } catch (Exception ex) {
      System.err.println(ex);
      this.arduinoLight = null;
    }
    
    Tween.registerAccessor(float[].class, new ValueAccessor());
    Tween.to(this.hsvColor, ValueAccessor.VALUE, timePerCycle).target(minBrightness).ease(Sine.OUT)
        .repeatYoyo(Tween.INFINITY, 0).start(brightnessTweenManager);
    restartHueTween();
  }
  
  private void restartHueTween() {
    this.lastAkku = maxAkku;
    this.hsvColor[0] = greenHue;
    this.hueTween = Tween.to(this.hsvColor, ValueAccessor.HUE, maxAkku).target(redHue).ease(Sine.IN)
        .start(hueTweenManager);
  }
  
  /**
   * 
   * @param delta time since last update
   */
  public void addPauseTime(double delta) {
    this.akku += (delta * workPauseRatio);
    if (this.akku > maxAkku) {
      this.akku = maxAkku;
    }
    update();
  }
  

  /**
   * 
   * @param delta time since last update
   */
  public void addWorkTime(double delta) {
    this.akku -= delta;
    if (this.akku < 0) {
      this.akku = 0;
    }
    update();
  }
  
  private void update() {
    if ((akku <= 0 && hsvColor[0] <= redHue) || (akku >= maxAkku && hsvColor[0] >= greenHue)) {
      this.lastAkku = this.akku;
      return;
    }
    if (this.hueTween.isFinished()) {
      restartHueTween();
    }
    this.hueTweenManager.update((float) (this.lastAkku - this.akku));
    this.lastAkku = this.akku;
  }
  
  void update(float delta) {
    brightnessTweenManager.update(delta);
    openDMXlight.updateColor(Color.getHSBColor((float) (hsvColor[0] / 360.), hsvColor[1],
        hsvColor[2]));
    if (arduinoLight != null)
      arduinoLight.updateColor(Color.getHSBColor((float) (hsvColor[0] / 360.), hsvColor[1],
          hsvColor[2]));
  }
  
  void stop() {
    brightnessTweenManager.killAll();
    hueTweenManager.killAll();
    openDMXlight.updateColor(null);
    if (arduinoLight != null) arduinoLight.updateColor(null);
  }
}
