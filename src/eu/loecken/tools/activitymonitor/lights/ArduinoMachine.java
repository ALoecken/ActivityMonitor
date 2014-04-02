package eu.loecken.tools.activitymonitor.lights;

import java.awt.Color;

public class ArduinoMachine {
  
  private SerialConnection arduinoLights;
  
  public ArduinoMachine(String comPort) {
    this.arduinoLights = new SerialConnection();
    this.arduinoLights.initialize(comPort);
  }
  
  public synchronized void updateColor(Color color) {
    if (this.arduinoLights != null) {
      if (color == null) {// black
        // Dj -> all off
        this.arduinoLights.sendBytes((byte) ((char) 'D'), (byte) ((char) 'j'));
      } else {
        // change color,
        // Dc -> all change
        this.arduinoLights.sendBytes((byte) ((char) 'C'),
            (byte) (color.getRed()),
            (byte) (color.getGreen()),
            (byte) (color.getBlue()), 
            (byte) ((char) 'D'),
            (byte) ((char) 'c'));
      }
    }
  }
  
  @Override
  protected void finalize() throws Throwable {
    updateColor(null);
    this.arduinoLights.close();
    super.finalize();
  }
  
}
