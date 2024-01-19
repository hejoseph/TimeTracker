package com.hejoseph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class DisplayImage extends JFrame {

    public static void main(String[] args) {
        run();
    }

    public DisplayImage() throws Exception {
        setTitle("Image Display");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Replace this path with the actual path to your background image file
        String backgroundImagePath = "break_reminder.jpg";
        String topImagePath = "smallclose.png";

        File file = new File(backgroundImagePath);
        File file2 = new File(topImagePath);

        if(!file.exists()){
            System.out.println(String.format("ERROR : file %s does not exist", file.getAbsolutePath()));
            return;
        }

        if(!file2.exists()){
            System.out.println(String.format("ERROR : file %s does not exist", file2.getAbsolutePath()));
            return;
        }


        ImageIcon backgroundIcon = new ImageIcon(backgroundImagePath);
        Image backgroundImage = backgroundIcon.getImage();

        // Set the desired width and height for the resized background image
        int desiredWidth = 300;
        int desiredHeight = 200;

        // Resize the background image
        Image resizedBackgroundImage = backgroundImage.getScaledInstance(desiredWidth, desiredHeight, Image.SCALE_SMOOTH);
        ImageIcon resizedBackgroundIcon = new ImageIcon(resizedBackgroundImage);

        JLabel backgroundLabel = new JLabel(resizedBackgroundIcon);
        backgroundLabel.setBounds(0, 0, 300, 200);
        backgroundLabel.setOpaque(true);

        // Create a transparent panel to overlay on top of the background image
//        JPanel overlayPanel = new JPanel(null);
//        overlayPanel.setBounds(50, 50, 50, 50);
//        overlayPanel.setBackground(Color.BLUE);
//        overlayPanel.setOpaque(true);

        // Replace this path with the actual path to your top image file
        ImageIcon topIcon = new ImageIcon(topImagePath);
        Image topImage = topIcon.getImage();

        // Resize the top image
        Image resizedTopImage = topImage.getScaledInstance(15, 15, Image.SCALE_SMOOTH);
        ImageIcon resizedTopIcon = new ImageIcon(resizedTopImage);

        JLabel topLabel = new JLabel(resizedTopIcon);
        float alpha = 1f; // Adjust the alpha value (0.0f - fully transparent, 1.0f - fully opaque)
        topLabel.setBounds(5, 5, 15, 15); // Adjust the position as needed
        topLabel.setForeground(new Color(0, 0, 0, alpha));

//        topLabel.setBorder(new LineBorder(Color.RED,2));

        // Add a MouseListener to the top label for handling click event
        topLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                closeWindow();
            }
        });

        // Use a JLayeredPane to layer components
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(300, 200));
        layeredPane.add(backgroundLabel, JLayeredPane.DEFAULT_LAYER);
//        layeredPane.add(overlayPanel, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(topLabel, JLayeredPane.MODAL_LAYER);

        // Add the layeredPane to the frame
        getContentPane().add(layeredPane);

        javax.swing.Timer timer = new javax.swing.Timer(10000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeWindow();
            }
        });

        timer.setRepeats(false); // Set to false to run the timer only once
        timer.start();

        setUndecorated(true); // Removes window decorations (title bar, etc.)
        pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int) screenSize.getWidth();
        int screenHeight = (int) screenSize.getHeight();
        setLocation(screenWidth - getWidth() - 10, screenHeight - getHeight() - 10);

        setVisible(true);
        setAlwaysOnTop(true);
    }

    private void closeWindow() {
        dispose(); // Close the window
    }

    public static void run() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new DisplayImage();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
