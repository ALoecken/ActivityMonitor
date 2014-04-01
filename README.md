# ActivityMonitor 
### Problem 
You need to write down the time you have been working on a project (e.g. for your job), but sometimes you forget to write down when you started, stopped, or how long you went to break.
### Solution 
Start ActivityMonitor.

![Screenshot](https://raw.github.com/ALoecken/ActivityMonitor/master/deploy/screenshot.png "Screenshot")
 * Writes down the starttime of your Activity
 * Updates the current endtime on any input with mouse or keyboard 
 * Automatically starts a new Activity if you were idle for more than 5 minutes (assuming you did a break)
 * Lets you merge Activities (e.g. if you were not taking a break, but working without mouse/keyboard)
 * Lets you delete Activities (e.g. if you forgot to stop the monitoring of activities)
 * Lets you set how many hours you want to work and displays the target time.
 * Displays the amount of hours you worked so far (summed up all activities)
 * Copies the amount of hours into your clipboard if you click on "kopieren"

## Needed files to run this project##
 1. Go to https://code.google.com/p/java-universal-tween-engine/
  1. Download "tween-engine-api-6.3.3.zip" (maybe a newer version is also working)
  2. Extract zip and copy "tween-engine-api.jar" into "ActivityMonitor/lib"

 2. Go to https://code.google.com/p/jnativehook/
  1. Download "JNativeHook-1.1.4.zip" (maybe a newer version is also working)
  2. Extract zip and copy "jar/JNativeHook.jar" into "ActivityMonitor/lib"

 3. Go to http://sourceforge.net/projects/opendmxjavajni/
  1. Download "opendmx_bin01.zip" (from http://sourceforge.net/projects/opendmxjavajni/files/opendmx_bin/opendmx_bin01/ )
  2. Extract zip and copy "OpenDmx.jar" into "ActivityMonitor/lib"
  3. Copy dlls to "ActivityMonitor"
 
 
##Used Projects##
 * Get key and mouse input: http://code.google.com/p/jnativehook/
 * For light: Uses OpenDMX from http://sourceforge.net/projects/opendmxjavajni/
 * For light-change: Universal Tween Engine from https://code.google.com/p/java-universal-tween-engine/
 * Icon downloaded from http://www.iconfinder.com/icondetails/23728/128/cairo_clock_stopwatch_icon


