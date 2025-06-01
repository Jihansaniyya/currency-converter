package com.currencyapp;

import com.currencyapp.gui.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Entry point aplikasi konversi mata uang
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("Starting application...");
        // Set look and feel
        try {
            // Coba gunakan look and feel sistem operasi
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            System.out.println("Look and feel set");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println("Showing splash screen...");
        showSplashScreen();
        
        System.out.println("Creating main frame...");
        // Jalankan aplikasi di Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error creating main frame: " + e.getMessage());
            }
        });
    }
    
    private static void showSplashScreen() {
        // Implementasi sederhana untuk splash screen
        JWindow splashScreen = new JWindow();
        JLabel splashLabel = new JLabel("Currency Converter Loading...");
        splashLabel.setHorizontalAlignment(JLabel.CENTER);
        splashLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setIndeterminate(true);
        
        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        content.add(splashLabel, BorderLayout.CENTER);
        content.add(progressBar, BorderLayout.SOUTH);
        
        splashScreen.setContentPane(content);
        splashScreen.setSize(400, 200);
        splashScreen.setLocationRelativeTo(null);
        splashScreen.setVisible(true);
        
        // Tutup splash screen setelah 2 detik
        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                splashScreen.setVisible(false);
                splashScreen.dispose();
            }
        }).start();
    }
}