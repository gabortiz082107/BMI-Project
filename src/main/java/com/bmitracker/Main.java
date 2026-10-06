package com.bmitracker;

import com.bmitracker.gui.BMITrackerGUI;
import com.bmitracker.util.DatabaseConnection;

import javax.swing.*;

/**
 * Application entry point. Initializes the database schema once, then
 * launches the Swing GUI on the Event Dispatch Thread (best practice
 * for event-driven Swing applications).
 */
public class Main {
    public static void main(String[] args) {
        DatabaseConnection.getInstance().initializeDatabase();

        SwingUtilities.invokeLater(() -> {
            BMITrackerGUI gui = new BMITrackerGUI();
            gui.setVisible(true);
        });
    }
}
