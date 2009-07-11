package com.jbooktrader.platform.startup;

import com.jbooktrader.platform.model.*;
import com.jbooktrader.platform.preferences.*;
import com.jbooktrader.platform.util.*;
import org.jvnet.substance.*;
import org.jvnet.substance.skin.*;

import javax.swing.*;
import java.io.*;
import java.nio.channels.*;


/**
 * Application starter.
 */
public class JBookTrader {
    public static final String APP_NAME = "JBookTrader";
    public static final String VERSION = "6.11";
    public static final String RELEASE_DATE = "July 7, 2009";
    public static final double MAC_MENUBAR_HEIGHT = 22;
    private static String appPath;
    private static boolean onMac = false;

    /**
     * Instantiates the necessary parts of the application: the application model,
     * views, and controller.
     */
    private JBookTrader() throws JBookTraderException {
        // Are we on an Apple Mac?
        String name = System.getProperty("os.name").toLowerCase();
        onMac = name.startsWith("mac os x");

        try {
            if (onMac) {
                // Menu bar at top of screen
                System.setProperty("apple.laf.useScreenMenuBar", "true");
                // Set application name
                System.setProperty("com.apple.mrj.application.apple.menu.about.name", APP_NAME);
                // Set default look and feel.
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } else {
                String currentSkinName = PreferencesHolder.getInstance().get(JBTPreferences.Skin);
                for (final SkinInfo skinInfo : SubstanceLookAndFeel.getAllSkins().values()) {
                    if (skinInfo.getDisplayName().equals(currentSkinName)) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                try {
                                    SubstanceLookAndFeel.setSkin(skinInfo.getClassName());
                                } catch (Exception e) {
                                    MessageDialog.showError(null, e);
                                }
                            }
                        });
                        break;
                    }
                }
            }
        } catch (Throwable t) {
            String msg = t.getMessage() + ": Unable to set custom look & feel. The default L&F will be used.";
            MessageDialog.showMessage(null, msg);
        }


        Dispatcher.setReporter();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new MainFrameController();
                } catch (Exception e) {
                    MessageDialog.showError(null, e);
                }
            }
        });
    }

    /**
     * Starts JBookTrader application.
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            File file = new File(System.getProperty("user.home"), APP_NAME + ".tmp");
            FileChannel channel = new RandomAccessFile(file, "rw").getChannel();

            if (channel.tryLock() == null) {
                MessageDialog.showMessage(null, APP_NAME + " is already running.");
                return;
            }

            if (args.length != 1) {
                String msg = "Exactly one argument must be passed. Usage: JBookTrader <JBookTraderDirectory>";
                throw new JBookTraderException(msg);
            }
            JBookTrader.appPath = args[0];
            new JBookTrader();
        } catch (Throwable t) {
            MessageDialog.showError(null, t);
        }
    }

    public static String getAppPath() {
        return JBookTrader.appPath;
    }

    /**
     * Are we running on an Apple Mac?
     *
     * @return true/false
     */
    public static boolean onMac() {
        return JBookTrader.onMac;
    }

}
