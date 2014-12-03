// from
// http://www.lauridmeyer.com/2012/05/arduino-java-usb-serial-communication-all-you-have-to-know/http://www.lauridmeyer.com/2012/05/arduino-java-usb-serial-communication-all-you-have-to-know/
package eu.loecken.tools.activitymonitor.lights;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

public class SerialConnection implements SerialPortEventListener {
  
  SerialPort serialPort;
  
  /** Buffered input stream from the port */
  private InputStream input;
  /** The output stream to the port */
  private OutputStream output;
  /** Milliseconds to block while waiting for port open */
  private static final int TIME_OUT = 2000;
  /** Default bits per second for COM port. */
  private static final int DATA_RATE = 115200;//7200;
  
  public void initialize(String portName) {
    CommPortIdentifier portId = null;
    Enumeration<?> portEnum = CommPortIdentifier.getPortIdentifiers();
    
    // iterate through, looking for the port
    while (portEnum.hasMoreElements()) {
      CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
      if (currPortId.getName().equals(portName)) {
        portId = currPortId;
        break;
      }
    }
    
    if (portId == null) {
      System.out.println("Could not find COM port.");
      return;
    } else {
      System.out.println("Found your Port");
    }
    
    try {
      // open serial port, and use class name for the appName.
      serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);
      
      // set port parameters
      serialPort.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
          SerialPort.PARITY_NONE);
      
      // open the streams
      input = serialPort.getInputStream();
      output = serialPort.getOutputStream();
      
      // add event listeners
      serialPort.addEventListener(this);
      serialPort.notifyOnDataAvailable(true);
    } catch (Exception e) {
      System.err.println(e.toString());
    }
    
    // FIXME: for some reason we need to wait (maybe the arduino is still setting up?)
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      System.err.println(e.toString());
    }
  }
  
  /**
   * This should be called when you stop using the port.
   * This will prevent port locking
   */
  public synchronized void close() {
    if (serialPort != null) {
      serialPort.removeEventListener();
      serialPort.close();
    }
  }
  
  /**
   * This Method can be called to print a multiple bytes
   * to the serial connection
   */
  public void sendBytes(byte... bytes) {
    if (output != null) {
      try {
        for (byte b : bytes) {
          output.write(b);
        }
        output.flush();
      } catch (Exception e) {
        System.err.println(e.toString());
      }
    }
  }
  
  /**
   * This Method can be called to print a single byte
   * to the serial connection
   */
  public void sendSingleByte(byte myByte) {
    System.out.println((char) myByte);
    try {
      output.write(myByte);
      output.flush();
    } catch (Exception e) {
      System.err.println(e.toString());
    }
  }
  
  /**
   * This Method is called when Serialdata is recieved
   */
  public synchronized void serialEvent(SerialPortEvent oEvent) {
    if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
      try {
        int myByte = input.read();
        int value = myByte & 0xff;// byte to int conversion:0...127,-127...0 -> 0...255
        if (value >= 0 && value < 256) {// make shure everything is ok
          System.err.print((char) value);
          // sendSingleByte((byte) myByte);
        }
      } catch (Exception e) {
        System.err.println(e.toString());
      }
    }
  }
  
}
