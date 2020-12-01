package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;


public class Login extends JFrame {
    private JButton btnLogin,btnRegister;
    private JLabel lName,lPassword;
    private JTextField tfName,tfPassword;
    private User user;
    private Socket clientSocket;

    public Login(){
        super();
        setLayout(new FlowLayout());
        btnLogin = new JButton("Войти");
        btnRegister = new JButton("Регистрация");
        lName = new JLabel("Имя пользователя");
        lPassword = new JLabel("Пароль");
        tfName = new JTextField(10);
        tfPassword = new JTextField(10);
        add(lName);
        add(tfName);
        add(lPassword);
        add(tfPassword);
        add(btnLogin);
        add(btnRegister);
        ActionListener handler = new eHandler();
        btnLogin.addActionListener(handler);
        btnRegister.addActionListener(handler);
    }

    public class eHandler implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(btnLogin)){
                if (login()){
                    setVisible(false);
                    ChatUI chat = new ChatUI(user, clientSocket);
                    chat.setVisible(true);
                }
            }else if (e.getSource().equals(btnRegister)){
                if (register()){
                    System.out.println("успешная регистрация!");
                }
            }
        }
    }

    private boolean login(){
        if (tfName.getText().isEmpty() || tfPassword.getText().isEmpty())
        {
            System.out.println("не введен логин или пароль");
            return false;
        }else{
            user = new User(tfName.getText(),tfPassword.getText());
            Connection connection = new Connection("localhost",5555, user);
            try {
                clientSocket = connection.open();
                if (clientSocket == null) {
                    return false;
                }
            } catch (IOException e) {
                System.out.println("Ошибка при подключении");
                return false;
            }
            return true;
        }
    }
    private boolean register(){
        if (tfName.getText().isEmpty() || tfPassword.getText().isEmpty())
        {
            System.out.println("не введен логин или пароль");
            return false;
        }else{
            user = new User(tfName.getText(),tfPassword.getText());
            Registration connection = new Registration("localhost",5555, user);
            try {
                clientSocket = connection.createUser();
                if (clientSocket == null) {
                    clientSocket.close();
                    return false;
                }
                clientSocket.close();
            } catch (IOException e) {
                //e.printStackTrace();
                System.out.println("Ошибка при подключении");
                return false;
            }
        }

        return true;
    }
}
