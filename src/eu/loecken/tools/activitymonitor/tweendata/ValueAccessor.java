package eu.loecken.tools.activitymonitor.tweendata;

import aurelienribon.tweenengine.TweenAccessor;

/**
 * 
 * @author Andreas
 */
public class ValueAccessor implements TweenAccessor<float[]> {
  
  public static final int HUE = 0;
  public static final int VALUE = 2;
  
  @Override
  public int getValues(float[] target, int tweenType, float[] returnValues) {
    switch (tweenType) {
    case HUE:
      returnValues[0] = target[0];
      return 1;
    case VALUE:
      returnValues[0] = target[2];
      return 1;
    default:
      assert false;
      return -1;
    }
  }
  
  @Override
  public void setValues(float[] target, int tweenType, float[] newValues) {
    switch (tweenType) {
    case HUE:
      target[0] = newValues[0];
    case VALUE:
      target[2] = newValues[0];
      break;
    default:
      assert false;
      break;
    }
  }
}
