package server;

import org.example.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Server extends Thread {
    private static Integer port = 5555;
    private static String ip = "localhost";
    private String userName;
    private Socket clientSocket;
    private ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();
    private User user;
    private ServerSocket serverSocket;
    {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        super.run();
        while(true) {
            try {
                clientSocket = serverSocket.accept();
                Scanner scanner = new Scanner(clientSocket.getInputStream());
                if (scanner.hasNextLine()){
                    String str = scanner.nextLine();
                    switch (str){
                        case ("connect"):
                            connect(scanner);
                            break;
                        case ("registration"):
                            registration(scanner);
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void registration(Scanner scanner){
        String userName = scanner.nextLine();
        String password = scanner.nextLine();
        try {
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(),true);
            try {
                Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/users?serverTimezone=Europe/Moscow&useSSL=false", "root", "gfhjkm");
                Statement statement = connect.createStatement();
                ResultSet resultSet = statement.executeQuery("Select * from user where username = \"" + userName + "\" and password = \"" + password +"\"");
                if (resultSet.next()){
                    writer.println("User is found");
                }else{
                    statement.executeUpdate("insert into user(username,password) values (\""+userName+"\",\"" + password+ "\")");
                    writer.println("User was created");
                }

            }catch ( SQLException e){
                e.printStackTrace();
                writer.println("Connect to DB error");
            }
        } catch (IOException e) {
            System.out.println("Ошибка при подключении клиента!!");
            e.printStackTrace();
        }
    }

    private void connect(Scanner scanner){
        String userName = scanner.nextLine();
        String password = scanner.nextLine();
        User user = new User(userName,password);
        try {
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(),true);
            try {
                Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/users?serverTimezone=Europe/Moscow&useSSL=false", "root", "gfhjkm");

                Statement statement = connect.createStatement();
                ResultSet resultSet = statement.executeQuery("Select * from user where username = \"" + userName + "\" and password = \"" + password +"\"");
                if (resultSet.next()){
                    writer.println("User is found");
                    ClientHandler client = new ClientHandler(clientSocket, this, user);
                    clients.add(client);
                    System.out.println(user.getUserName() + " в сети");
                    new Thread(client).start();
                }else{
                    writer.println("User isnt found");
                }

            }catch ( SQLException e){
                e.printStackTrace();
                writer.println("Connect to DB error");
            }
        } catch (IOException e) {
            System.out.println("Ошибка при подключении клиента!!");
            e.printStackTrace();
        }
    }

    public static Integer getPort() {
        return port;
    }

    public static String getIp() {
        return ip;
    }
    public void sendMessageToAllClients(String msg) {
        for (ClientHandler o : clients) {
            o.sendMsg(msg);
        }

    }

    // удаляем клиента из коллекции при выходе из чата
    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }
}
