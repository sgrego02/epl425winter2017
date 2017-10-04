package com.tcp.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Date;

public class TCPClient {

    public static void main(String args[]) {
        try {

            String message, response;
            Socket socket = new Socket("35.197.39.93", 80);

            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            BufferedReader server = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(System.in)
            );
            message = reader.readLine() + System.lineSeparator();


            output.writeBytes(message);
            response = server.readLine();

            System.out.println("[" + new Date() + "] Received: " + response);
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
