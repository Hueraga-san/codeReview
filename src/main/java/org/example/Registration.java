package org.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Registration extends Thread {
    private String ip;
    private Integer port;
    private User user;
    private Socket clientSocket;

    public Registration(String ip, Integer port, User user){
        this.ip = ip;
        this.port = port;
        this.user = user;
    }

    public Socket createUser() throws IOException{
        System.out.println("Подключение...");
        try {
            clientSocket = new Socket(ip, port);
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
            Scanner scanner = new Scanner(clientSocket.getInputStream());
            writer.println("registration");
            writer.println(user.getUserName());
            writer.println(user.getPassword());
            if (scanner.hasNextLine()) {
                String str = scanner.nextLine();
                if (!str.equals("User was created")) {
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
