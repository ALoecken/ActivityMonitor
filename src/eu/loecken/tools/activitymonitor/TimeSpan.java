package eu.loecken.tools.activitymonitor;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author Andreas@Loecken.eu
 */
public class TimeSpan implements Comparable<TimeSpan> {
  
  private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
  private volatile long startMillis;
  private volatile long stopMillis;
  
  public TimeSpan() {
    startMillis = System.currentTimeMillis();
    stopMillis = startMillis;
  }
  
  void updateStopTime(long newStop) {
    stopMillis = newStop;
  }
  
  public long getStopMillis() {
    return stopMillis;
  }
  
  public void merge(TimeSpan otherSpan) {
    // given: a->b, c->d; want: a->d, a->d
    long start, stop;
    start = Math.min(startMillis, otherSpan.startMillis);
    stop = Math.max(stopMillis, otherSpan.stopMillis);
    
    this.startMillis = start;
    otherSpan.startMillis = start;
    this.stopMillis = stop;
    otherSpan.stopMillis = stop;
  }
  
  public long getMillis() {
    return stopMillis - startMillis;
  }
  
  @Override
  public String toString() {
    return timeFormat.format(new Date(startMillis)) + " " + "bis "
        + timeFormat.format(new Date(stopMillis)) + ". " + "Dauer: "
        + String.format("%.3f", getMillis() / (1000d * 60 * 60));
  }
  
  @Override
  public int compareTo(TimeSpan o) {
    return new Long(startMillis).compareTo(o.startMillis);
  }
}
