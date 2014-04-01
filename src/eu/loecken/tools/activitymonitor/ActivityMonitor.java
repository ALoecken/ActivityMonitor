package eu.loecken.tools.activitymonitor;

import eu.loecken.tools.activitymonitor.gui.MainFrame;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UnsupportedLookAndFeelException;
import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;
import org.jnativehook.mouse.NativeMouseWheelEvent;
import org.jnativehook.mouse.NativeMouseWheelListener;

/**
 *
 * @author andreas@loecken.eu
 */
public class ActivityMonitor implements NativeKeyListener,
        NativeMouseInputListener, NativeMouseWheelListener {

  private final static long MINIMUM_PAUSE = 5 * 60 * 1000; // 5 minutes
  private final static int SLEEP_TIME = 1000 / 50; //50 frames per second
  private final MainFrame mainFrame;
  private ColorChange colorchanger;
  private TimeSpan lastWrittenTimeSpan;
  private final String logName;
  private TimeSpan currentTimeSpan;
  private volatile long lastInteraction;
  private long lastUpdate;

  public ActivityMonitor() {
    this.logName = System.currentTimeMillis() + ".log";
    this.lastInteraction = System.currentTimeMillis();
    this.lastUpdate = this.lastInteraction;

    System.out.println("Starting color change");
    this.colorchanger = new ColorChange();

    System.out.println("Register Hook");
    try {
      // Initialze native hook.
      GlobalScreen.registerNativeHook();
    } catch (Exception ex) {
      Logger.getLogger(ActivityMonitor.class.getName()).log(Level.SEVERE,
              "There was a problem registering the native hook.", ex);
      System.exit(-1);
    }

    System.out.println("Starting Main-Thread");
    final CheckActivityThread thread = new CheckActivityThread();

    System.out.println("Opening Main-Frame");
    this.mainFrame = new MainFrame();
    this.mainFrame.addWindowListener(new java.awt.event.WindowAdapter() {

      public void windowClosing(java.awt.event.WindowEvent evt) {
        thread.running = false;
        try {
          thread.join();
        } catch (InterruptedException ex) {
          Logger.getLogger(ActivityMonitor.class.getName()).log(Level.SEVERE, null, ex);
        }
        colorchanger.stop();
      }
    });
    thread.start();

    System.out.println("Done.");
  }

  /**
   * Called by every listener.
   */
  public synchronized void onInteraction() {
    this.lastInteraction = System.currentTimeMillis();
  }

  synchronized void update(long now) {
    if (!this.mainFrame.isRunning() || now - lastInteraction >= MINIMUM_PAUSE) {
      // pause
      this.currentTimeSpan = null;
      colorchanger.addPauseTime((now - this.lastUpdate)/1000.);
    } else {
      // work
      if (this.currentTimeSpan == null) {
        this.currentTimeSpan = new TimeSpan();
        this.mainFrame.getTimeList().add(this.currentTimeSpan);
      }
      this.currentTimeSpan.updateStopTime(this.lastInteraction);
      colorchanger.addWorkTime((now - this.lastUpdate)/1000.);
    }
    
    // update gui and file
    this.mainFrame.repaint();
    writeTimeSpans();
    this.lastUpdate = now;
  }

  private void writeTimeSpans() {
    if (this.currentTimeSpan == null || !this.currentTimeSpan.equals(this.lastWrittenTimeSpan)) {
      try {
        Path path = Paths.get(this.logName);
        ArrayList<String> list = new ArrayList<>();
        for (TimeSpan ts : this.mainFrame.getTimeList()) {
          list.add(ts.toString());
        }
        Files.write(path, list, StandardCharsets.UTF_8);
        this.lastWrittenTimeSpan = this.currentTimeSpan;
      } catch (Exception ex) {
        Logger.getLogger(ActivityMonitor.class.getName()).log(
                Level.SEVERE, null, ex);
      }
    }
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String args[]) {
    /*
     * Set the Nimbus look and feel
     */
    // <editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
		/*
     * If Nimbus (introduced in Java SE 6) is not available, stay with the
     * default look and feel. For details see
     * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
     */
    try {
      for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
          javax.swing.UIManager.setLookAndFeel(info.getClassName());
          break;
        }
      }
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
      java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
    // </editor-fold>

    /*
     * Create and display the form
     */
    java.awt.EventQueue.invokeLater(new Runnable() {

      @Override
      public void run() {
        ActivityMonitor activityMonitor = new ActivityMonitor();
        GlobalScreen.getInstance().addNativeKeyListener(activityMonitor);
        GlobalScreen.getInstance().addNativeMouseListener(
                activityMonitor);
        GlobalScreen.getInstance().addNativeMouseMotionListener(
                activityMonitor);
        GlobalScreen.getInstance().addNativeMouseWheelListener(
                activityMonitor);
        activityMonitor.mainFrame.setVisible(true);
      }
    });
  }

  // <editor-fold defaultstate="collapsed" desc=" ListenerFunctions ">
  @Override
  public void nativeKeyPressed(NativeKeyEvent nke) {
    this.onInteraction();
  }

  @Override
  public void nativeKeyReleased(NativeKeyEvent nke) {
    this.onInteraction();
  }

  @Override
  public void nativeKeyTyped(NativeKeyEvent nke) {
    this.onInteraction();
  }

  @Override
  public void nativeMouseClicked(NativeMouseEvent nme) {
    this.onInteraction();
  }

  @Override
  public void nativeMousePressed(NativeMouseEvent nme) {
    this.onInteraction();
  }

  @Override
  public void nativeMouseReleased(NativeMouseEvent nme) {
    this.onInteraction();
  }

  @Override
  public void nativeMouseMoved(NativeMouseEvent nme) {
    this.onInteraction();
  }

  @Override
  public void nativeMouseDragged(NativeMouseEvent nme) {
    this.onInteraction();
  }

  @Override
  public void nativeMouseWheelMoved(NativeMouseWheelEvent nmwe) {
    this.onInteraction();
  }

  // </editor-fold>
  @Override
  protected void finalize() throws Throwable {
    // Clean up the native hook.
    GlobalScreen.unregisterNativeHook();
    super.finalize();
  }

  private class CheckActivityThread extends Thread {

    private long lastMillis = -1;
    private boolean running;
    private int count;
    int MAX_COUNT = 10; // every 10th time

    public CheckActivityThread() {
      this.running = true;
      this.count = 0;
    }

    @Override
    public void run() {
      while (running) {
        if (lastMillis < 0) {
          lastMillis = System.currentTimeMillis();
        } else {
          // update light
          long currentMillis = System.currentTimeMillis();
          float delta = (currentMillis - lastMillis) / 1000f;
          colorchanger.update(delta);
          lastMillis = currentMillis;

          // upate times
          this.count++;
          this.count = this.count % MAX_COUNT;
          if (mainFrame != null && this.count == MAX_COUNT - 1) {
            // update work/pause
            update(currentMillis);
          }
        }
        try {
          CheckActivityThread.sleep(SLEEP_TIME);
        } catch (InterruptedException ex) {
        }
      }
      colorchanger.stop();
    }
  }
}
