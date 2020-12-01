package org.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Connection extends Thread {
    private Socket clientSocket;
    private String ip;
    private Integer port;
    private User user;

    public Connection(String ip, Integer port, User user){
        this.ip = ip;
        this.port = port;
        this.user = user;
    }
    @Override
    public void run() {
        super.run();
        try {
            open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket open() throws IOException {
        System.out.println("Подключение...");

        try {
            clientSocket = new Socket(ip, port);
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
            Scanner scanner = new Scanner(clientSocket.getInputStream());
            writer.println("connect");
            writer.println(user.getUserName());
            writer.println(user.getPassword());
            if (scanner.hasNextLine()) {
                String str = scanner.nextLine();
                if (!str.equals("User is found")) {
                    System.out.println("не верный логин или пароль!");
                    clientSocket.close();
                    return null;
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка при подключении!");
            clientSocket.close();
            return null;
        }
        return clientSocket;
    }
}
