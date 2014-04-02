package eu.loecken.tools.activitymonitor.lights;

import com.juanjo.openDmx.OpenDmx;

import eu.loecken.tools.activitymonitor.ActivityMonitor;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OpenDMXLight {

	private boolean connected = false;

	public OpenDMXLight() {
		// open dmx widget to send data
		if (!OpenDmx.connect(OpenDmx.OPENDMX_TX)) {
			Logger.getLogger(ActivityMonitor.class.getName()).log(
					Level.WARNING, "No OpenDMX connected!");
			return;
		}
		this.connected = true;
		updateColor(null); // send black
	}

	public boolean isConnected() {
		return this.connected;
	}

	public synchronized void updateColor(Color color) {
		if (this.connected) {
			if (color == null) {
				for (int i = 0; i < 3; i++) {
					OpenDmx.setValue(i, 0);
				}
			} else {
				OpenDmx.setValue(0, color.getRed() * color.getTransparency());
				OpenDmx.setValue(1, color.getGreen() * color.getTransparency());
				OpenDmx.setValue(2, color.getBlue() * color.getTransparency());
			}
		}
	}

	@Override
	protected void finalize() throws Throwable {
		updateColor(null);
		OpenDmx.disconnect();
		this.connected = false;
		super.finalize();
	}
}
