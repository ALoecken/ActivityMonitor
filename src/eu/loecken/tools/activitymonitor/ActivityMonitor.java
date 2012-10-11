package eu.loecken.tools.activitymonitor;

import eu.loecken.tools.activitymonitor.gui.MainFrame;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UnsupportedLookAndFeelException;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
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
public class ActivityMonitor implements NativeKeyListener, NativeMouseInputListener, NativeMouseWheelListener {

  private final static long MINIMUM_PAUSE = 5 * 60 * 1000; // 5 minutes
  private final MainFrame mainFrame;
  private TimeSpan currentTimeSpan;

  public ActivityMonitor() {
    try {
      //Initialze native hook.
      GlobalScreen.registerNativeHook();
    } catch (NativeHookException ex) {
      Logger.getLogger(ActivityMonitor.class.getName()).log(Level.SEVERE,
              "There was a problem registering the native hook.", ex);
      System.exit(-1);
    }
    currentTimeSpan = null;
    mainFrame = new MainFrame();
  }

  /**
   * Called by every listener.
   */
  public synchronized void updateTime() {
    if (!mainFrame.isRunning()) {
      if (currentTimeSpan != null) {
        currentTimeSpan.updateStopTime();
        mainFrame.getTimeList().add(currentTimeSpan);
        currentTimeSpan = null;
      }
      return;
    }

    if (currentTimeSpan == null) {
      currentTimeSpan = new TimeSpan();
    }

    long stopMillis = currentTimeSpan.getStopMillis();
    if (stopMillis == 0 || System.currentTimeMillis() - stopMillis < MINIMUM_PAUSE) {
      currentTimeSpan.updateStopTime();
    } else {
      mainFrame.getTimeList().add(currentTimeSpan);
      currentTimeSpan = new TimeSpan();
    }
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String args[]) {
    /*
     * Set the Nimbus look and feel
     */
    //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
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
    //</editor-fold>

    /*
     * Create and display the form
     */
    java.awt.EventQueue.invokeLater(new Runnable() {

      @Override
      public void run() {
        ActivityMonitor activityMonitor = new ActivityMonitor();
        GlobalScreen.getInstance().addNativeKeyListener(activityMonitor);
        GlobalScreen.getInstance().addNativeMouseListener(activityMonitor);
        GlobalScreen.getInstance().addNativeMouseMotionListener(activityMonitor);
        GlobalScreen.getInstance().addNativeMouseWheelListener(activityMonitor);
        activityMonitor.mainFrame.setVisible(true);
      }
    });
  }

  //<editor-fold defaultstate="collapsed" desc=" ListenerFunctions ">
  @Override
  public void nativeKeyPressed(NativeKeyEvent nke) {
    this.updateTime();
  }

  @Override
  public void nativeKeyReleased(NativeKeyEvent nke) {
    this.updateTime();
  }

  @Override
  public void nativeKeyTyped(NativeKeyEvent nke) {
    this.updateTime();
  }

  @Override
  public void nativeMouseClicked(NativeMouseEvent nme) {
    this.updateTime();
  }

  @Override
  public void nativeMousePressed(NativeMouseEvent nme) {
    this.updateTime();
  }

  @Override
  public void nativeMouseReleased(NativeMouseEvent nme) {
    this.updateTime();
  }

  @Override
  public void nativeMouseMoved(NativeMouseEvent nme) {
    this.updateTime();
  }

  @Override
  public void nativeMouseDragged(NativeMouseEvent nme) {
    this.updateTime();
  }

  @Override
  public void nativeMouseWheelMoved(NativeMouseWheelEvent nmwe) {
    this.updateTime();
  }
  //</editor-fold>

  @Override
  protected void finalize() throws Throwable {
    //Clean up the native hook.
    GlobalScreen.unregisterNativeHook();
    super.finalize();
  }
}
