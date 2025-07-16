import javax.swing.*;
import java.awt.*;

public class WelcomeWindow {
    public static String[] show() {
            //Create a new JFrame
            final JFrame welcomeFrame = new JFrame("Welcome");
            welcomeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            welcomeFrame.setSize(800,650);
            welcomeFrame.setLocationRelativeTo(null);//Center the window on screen
            welcomeFrame.setLayout(new BorderLayout());

            //Top Beige Box with Welcome Text
            JPanel titlePanel = new JPanel();
            titlePanel.setBackground(new Color(181, 136, 99));//Beige Color

            //Create a Label for the welcome message
            JLabel welcomeLabel = new JLabel("Welcome to Checker Game!!!",SwingConstants.CENTER);
            welcomeLabel.setFont(new Font("Serif",Font.BOLD, 32));
            welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20,10,20,10));
            titlePanel.add(welcomeLabel);//adding welcomeLabel to the title panel

            //Center PNG image
            ImageIcon originalIcon = new ImageIcon("D:\\Java\\JavaGreatProjects\\DiskChess\\untitled\\src\\Checker_Symbol.PNG");//make sure this file is in your project folder

            //Scale image to fit within 200x200
            Image scaledImage = originalIcon.getImage().getScaledInstance(200,200,Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);

            //Centered image label
            JLabel imageLabel = new JLabel(scaledIcon);
            imageLabel.setVerticalAlignment(SwingConstants.CENTER);
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);


            JPanel imagePanel = new JPanel(new GridLayout()); //Use GridBagLayout for centering
            imagePanel.add(imageLabel);//This will center the image in the panel



            //Bottom "Click to Play" Button
            JButton playButton = new JButton("Click to Play");
            playButton.setFont(new Font("SansSerif",Font.BOLD,20));
            playButton.setFocusPainted(false);

            JPanel bottomPanel = new JPanel();
            bottomPanel.setBackground(Color.LIGHT_GRAY);
            bottomPanel.setBorder(BorderFactory.createEmptyBorder(20,10,20,10));
            bottomPanel.add(playButton);

            //Add the label to the frame
            welcomeFrame.add(titlePanel, BorderLayout.NORTH);
            welcomeFrame.add(imagePanel, BorderLayout.CENTER);
            welcomeFrame.add(bottomPanel, BorderLayout.SOUTH);
            //Show the window
            welcomeFrame.setVisible(true);

            //Wait for button press
            final String[] playerNames = new String[2];
            final Object lock = new Object();

            playButton.addActionListener(e -> {
                String p1 = JOptionPane.showInputDialog(welcomeFrame, "Enter name for Player1:","Player Setup",JOptionPane.PLAIN_MESSAGE);
                if(p1 == null || p1.trim().isEmpty()) p1 = "Player 1";

                String p2 = JOptionPane.showInputDialog(welcomeFrame, "Enter name for Player2:","Player Setup",JOptionPane.PLAIN_MESSAGE);
                if(p2 == null || p2.trim().isEmpty()) p2 = "Player 2";

                playerNames[0] = p1;
                playerNames[1] = p2;

                synchronized (lock) {
                    lock.notify();
                }
                welcomeFrame.dispose(); //Close the welcome window

            });

            synchronized (lock) {
                try{
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }




        return playerNames;
    }
}
