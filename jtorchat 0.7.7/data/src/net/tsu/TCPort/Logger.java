package net.tsu.TCPort;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;

public class Logger {
	public static final PrintStream oldOut;
	public static final int FATAL = 0;
	public static final int SEVERE = 1;
	public static final int WARNING = 2;
	public static final int NOTICE = 3;
	public static final int INFO = 4;
	public static final int DEBUG = 5;

	private static int logLevel = DEBUG;
	private static boolean usingLog;
	private static Object LOCK = new Object();
	private static boolean override = true; 
	
	static {
		oldOut = System.out;
		boolean hasGUI = hasGUI();
		if (hasGUI) {
			System.setOut(new PrintStream(new OutputStream() {
	
				@Override
				public void write(int b) throws IOException {
					// cheap seperation for now
					try {
						Class<?> c = Class.forName("net.tsu.TCPort.Gui.Log"); // will ClassNotFoundException here if no Log class
						c.getDeclaredMethod("updateOut", String.class).invoke(null, String.valueOf((char) b));
					} catch (ClassNotFoundException cnfe) {
						cnfe.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
//					Log.updateOut(String.valueOf((char) b));
				}
				
			}));
			System.setErr(new PrintStream(new OutputStream() {
	
				@Override
				public void write(int b) throws IOException {
					// cheap seperation for now
					try {
						Class<?> c = Class.forName("net.tsu.TCPort.Gui.Log"); // will ClassNotFoundException here if no Log class
						c.getDeclaredMethod("updateErr", String.class).invoke(null, String.valueOf((char) b));
					} catch (ClassNotFoundException cnfe) {
						cnfe.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
//					Log.updateErr(String.valueOf((char) b));
//					oldOut.print(String.valueOf((char) b));
				}
				
			}));
			usingLog = true; // reffering to Log class
		}
	}

	public static void log(int i, String s, String string) {
		synchronized(LOCK) {
			if (i == FATAL) {
				System.setErr(oldOut);
				System.setOut(oldOut);
				usingLog = false;
			}
			if (logLevel >= i) {
				if (usingLog) {
					try {
						// cheap seperation for now
						try {
							Class<?> c = Class.forName("net.tsu.TCPort.Gui.Log"); // will ClassNotFoundException here if no Log class
							Method meth = c.getDeclaredMethod("append", String.class, String.class);
							meth.invoke(null, "[" + Class.forName("net.tsu.TCPort.Gui.ChatWindow").getDeclaredMethod("getTime").invoke(null) + " - ", "Time Stamp");
							meth.invoke(null, s + "] ", "Class-t");
							meth.invoke(null, string + "\n", null);
						} catch (ClassNotFoundException cnfe) {
							cnfe.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
//						Log.append("[" + ChatWindow.getTime() + " - ", "Time Stamp");
//						Log.append(s + "] ", "Class-t");
//						Log.append(string + "\n", null);
					} catch (Exception e) {
						System.setErr(oldOut);
						System.setOut(oldOut);
						usingLog = false;
					}
				} 
				if (!usingLog || override) {
					if (logLevel <= WARNING)
						oldOut.println("Log: !{" + s + "}! " + string);
					else
						oldOut.println("Log: {" + s + "} " + string);
					if (i <= SEVERE) // dump stack if SEVERE or greater (actually lower, but you get the point)
						Thread.dumpStack();
				}
			}
		}
	}
	
	private static boolean hasGUI() {
		try {
			Class.forName("net.tsu.TCPort.Gui.Gui");
			return true;
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
		return false;
	}
	
	public static void log(int i, Object o, String string) {
		synchronized(LOCK) {
			if (logLevel >= i) {
				if (i == FATAL) {
					System.setErr(oldOut);
					System.setOut(oldOut);
					usingLog = false;
				}
				if (usingLog) {
					try {
						// cheap seperation for now
						try {
							Class<?> c = Class.forName("net.tsu.TCPort.Gui.Log"); // will ClassNotFoundException here if no Log class
							Method meth = c.getDeclaredMethod("append", String.class, String.class);
							meth.invoke(null, "[" + Class.forName("net.tsu.TCPort.Gui.ChatWindow").getDeclaredMethod("getTime").invoke(null) + " - ", "Time Stamp");
							meth.invoke(null, o.getClass().getName() + "] ", "Class-c");
							meth.invoke(null, string + "\n", null);
						} catch (ClassNotFoundException cnfe) {
							cnfe.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
//						Log.append("[" + ChatWindow.getTime() + " - ", "Time Stamp");
//						Log.append(o.getClass().getName() + "] ", "Class-c");
//						Log.append(string + "\n", null);
					} catch (Exception e) {
						System.setErr(oldOut);
						System.setOut(oldOut);
						usingLog = false;
					}
				} 
				if (!usingLog || override) {
					if (logLevel <= WARNING)
						oldOut.println("Log: ![" + o.getClass().getCanonicalName() + "]! " + string);
					else
						oldOut.println("Log: [" + o.getClass().getCanonicalName() + "] " + string);
					if (i <= SEVERE) // dump stack if SEVERE or greater (actually lower, but you get the point)
						Thread.dumpStack();
				}
			}
		}
	}

	public static void setOverride(boolean o) {
		override = o;
	}

	public static void stopGLog() {
		usingLog = false;
	}

}
