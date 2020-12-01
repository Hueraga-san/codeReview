package org.example;

import javax.swing.*;

public class Client extends JFrame {
    {
        Login login = new Login();
        login.setVisible(true);
        login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        login.setSize(750, 100);
        login.setResizable(false);
        login.setLocationRelativeTo(null);
    }

}
