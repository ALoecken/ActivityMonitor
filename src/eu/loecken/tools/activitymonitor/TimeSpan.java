package eu.loecken.tools.activitymonitor;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Andreas@Loecken.eu
 */
public class TimeSpan implements Comparable<TimeSpan> {

  private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
  private final long startMillis;
  private volatile long stopMillis;

  public TimeSpan() {
    startMillis = System.currentTimeMillis();
    stopMillis = 0;
  }

  void updateStopTime() {
    stopMillis = System.currentTimeMillis();
  }

  public long getStopMillis() {
    return stopMillis;
  }

  public boolean merge(TimeSpan otherSpan) {
    if (startMillis < otherSpan.startMillis) {
      stopMillis = otherSpan.stopMillis;
      otherSpan.stopMillis = otherSpan.startMillis;
    } else {
      return false;
    }
    return true;
  }

  public long getMillis() {
    return stopMillis - startMillis;
  }

  @Override
  public String toString() {
    return timeFormat.format(new Date(startMillis)) + " "
            + "bis " + timeFormat.format(new Date(stopMillis)) + ". "
            + "Dauer: " + String.format("%.3f", getMillis() / (1000d * 60 * 60));
  }

  @Override
  public int compareTo(TimeSpan o) {
    return new Long(startMillis).compareTo(o.startMillis);
  }
}
