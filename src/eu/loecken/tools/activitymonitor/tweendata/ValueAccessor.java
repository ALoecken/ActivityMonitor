package eu.loecken.tools.activitymonitor.tweendata;

import aurelienribon.tweenengine.TweenAccessor;

/**
 * 
 * @author Andreas
 */
public class ValueAccessor implements TweenAccessor<float[]> {

	public static final int HUE = 2;

	@Override
	public int getValues(float[] target, int tweenType, float[] returnValues) {
		if (tweenType == HUE) {
			returnValues[0] = target[2];
			return 1;
		}

		assert false;
		return -1;
	}

	@Override
	public void setValues(float[] target, int tweenType, float[] newValues) {
		if (tweenType == HUE) {
			target[2] = newValues[0];
		} else
			assert false;
	}
}
