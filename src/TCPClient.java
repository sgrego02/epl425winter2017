package com.tcp.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPClient {

	private static class TCPWorker implements Runnable {

		private Socket socket;
		private String IPaddress;
		private int port;
		private int id;

		public TCPWorker(Socket socket, String address, int port, int id) {
			this.socket = socket;
			this.IPaddress = address;
			this.port = port;
			this.id = id;
		}

		@Override
		public void run() {

			try {
				for (int i = 0; i < 300; i++) {
					String message, response;
					DataOutputStream output = new DataOutputStream(socket.getOutputStream());
					BufferedReader server = new BufferedReader(new InputStreamReader(socket.getInputStream()));

					message = "HELLO" + System.lineSeparator() + IPaddress + " " + port + System.lineSeparator() + id + System.lineSeparator();

					output.writeBytes(message);
					response = server.readLine();

					System.out.println("[" + new Date() + "] Received: " + response);
				}
				socket.close();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public static final int N = 10;
	public static ExecutorService TCP_WORKER_SERVICE = Executors.newFixedThreadPool(N);

	public static void main(String args[]) {
		try {
			int id;
			Socket socket = new Socket(args[0], Integer.parseInt(args[1]));
			for (id = 1; id <= 10; id++)
				TCP_WORKER_SERVICE.submit(new TCPWorker(socket, args[0], Integer.parseInt(args[1]), id));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
