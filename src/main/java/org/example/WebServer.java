package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class WebServer {
    public static void main(String[] args) throws Exception {
        startServer();
    }

    private static void startServer() throws Exception {
        ServerSocket serverSocket = new ServerSocket(1111);

        while (true) {
            Socket socket = serverSocket.accept();

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String request = in.readLine();
            String reqFile = request.split(" ")[1];
            if (reqFile.length() != 1) {
                reqFile = reqFile.substring(1);
            }

            if (reqFile.equals("stop")) {
                break;
            }
            System.out.println(request);

            String response = getResponse(reqFile);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(response);

            in.close();
            out.close();
        }

        serverSocket.close();
        System.out.println("Server closed");
    }

    private static String getResponse(String fileName) {
        String response;
        StringBuilder fileContent = new StringBuilder();
        try (FileReader reader = new FileReader("src/main/resources/" + fileName)) {
            Scanner scan = new Scanner(reader);
            while (scan.hasNext()) {
                fileContent.append(scan.nextLine());
            }
            response = "HTTP/1.1 200 OK\r\nContent-type: text/html; charset=UTF-8\r\nContent-Length: "
                    + fileContent.length() + "\r\n\r\n" + fileContent;
            scan.close();
        }
        catch (IOException err) {
            System.out.println("Requested file " + fileName + " is not found on the server.");
            fileContent = new StringBuilder("<h1>404</h1><h2>Not Found</h2>");
            response = "HTTP/1.1 404 Not Found\r\nContent-Type: text/html; charset=UTF-8\r\nnContent-Length: "
                    + fileContent.length() + "\r\n\r\n" + fileContent;
            return response;
        }

        return response;
    }
}