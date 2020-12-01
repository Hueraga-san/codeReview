package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatUI extends JFrame {
    private Socket clientSocket;
    private Scanner inMessage;
    private PrintWriter outMessage;
    private JTextField tfMessage;
    private JTextField tfName;
    private JTextArea taTextAreaMessage;
    private String clientName = "";
    private User user;
    public String getClientName() {
        return this.clientName;
    }
    // Этот UI честно украден из интернета
    public ChatUI(User user, Socket server) {
        clientSocket = server;
        try {
            inMessage = new Scanner(clientSocket.getInputStream());
            outMessage = new PrintWriter(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.user = user;
        setBounds(600, 300, 600, 500);
        setTitle("Чатик");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        taTextAreaMessage = new JTextArea();
        taTextAreaMessage.setEditable(false);
        taTextAreaMessage.setLineWrap(true);
        JScrollPane jsp = new JScrollPane(taTextAreaMessage);
        add(jsp, BorderLayout.CENTER);
        JLabel jlNumberOfClients = new JLabel("Количество клиентов в чате: ");
        add(jlNumberOfClients, BorderLayout.NORTH);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        add(bottomPanel, BorderLayout.SOUTH);
        JButton jbSendMessage = new JButton("Отправить");
        bottomPanel.add(jbSendMessage, BorderLayout.EAST);
        tfMessage = new JTextField("Введите ваше сообщение: ");
        bottomPanel.add(tfMessage, BorderLayout.CENTER);
        tfName = new JTextField(user.getUserName()+":");
        tfName.setEnabled(false);
        bottomPanel.add(tfName, BorderLayout.WEST);
        // обработчик события нажатия кнопки отправки сообщения
        jbSendMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // если имя клиента, и сообщение непустые, то отправляем сообщение
                if (!tfMessage.getText().trim().isEmpty() && !tfName.getText().trim().isEmpty()) {
                    clientName = tfName.getText();
                    sendMsg();
                    // фокус на текстовое поле с сообщением
                    tfMessage.grabFocus();
                }
            }
        });
        // при фокусе поле сообщения очищается
        tfMessage.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                tfMessage.setText("");
            }
        });
        // при фокусе поле имя очищается
        tfName.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                tfName.setText("");
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // бесконечный цикл
                    while (true) {
                        // если есть входящее сообщение
                        if (inMessage.hasNext()) {
                            // считываем его
                            String inMes = inMessage.nextLine();
                            String clientsInChat = "Клиентов в чате = ";
                            if (inMes.indexOf(clientsInChat) == 0) {
                                jlNumberOfClients.setText(inMes);
                            } else {
                                // выводим сообщение
                                taTextAreaMessage.append(inMes);
                                // добавляем строку перехода
                                taTextAreaMessage.append("\n");
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
        }).start();
        // добавляем обработчик события закрытия окна клиентского приложения
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try {
                    // здесь проверяем, что имя клиента непустое и не равно значению по умолчанию
                    if (!clientName.isEmpty() && clientName != "Введите ваше имя: ") {
                        outMessage.println(clientName + " вышел из чата!");
                    }
                    // отправляем служебное сообщение, которое является признаком того, что клиент вышел из чата
                    outMessage.println("##session##end##");
                    outMessage.flush();
                    outMessage.close();
                    inMessage.close();
                    clientSocket.close();
                } catch (IOException exc) {

                }
            }
        });
    }

    // отправка сообщения
    public void sendMsg() {
        // формируем сообщение для отправки на сервер
        String messageStr = tfName.getText() + " " + tfMessage.getText();
        // отправляем сообщение
        outMessage.println(messageStr);
        outMessage.flush();
        tfMessage.setText("");
    }
}

