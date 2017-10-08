import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {
	
	public static final int N = 10;
	public static final int R = 300;
	public static ExecutorService TCP_WORKER_SERVICE = Executors.newFixedThreadPool(N);
	public static boolean[] ended = {false, false, false, false, false, false, false, false, false, false};
	public static double sum = 0;
	public static double sum2 = 0;

	private static class TCPWorker implements Runnable {

		private Socket socket;
		private String IPaddress;
		private int port;
		private int id;

		public TCPWorker(Socket socket, int id) {
			this.socket = socket;
			this.id = id;
		}

		@Override
		public void run() {

			try {
				DataOutputStream output = new DataOutputStream(socket.getOutputStream());
				BufferedReader server = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				for (int i = 0; i < R; i++) {
					String response;
					double before = System.currentTimeMillis();
					output.writeBytes("HELLO " + socket.getLocalAddress() + " " + socket.getLocalPort() + " " + id + System.lineSeparator());
					output.flush();
					response = server.readLine();
					double after = System.currentTimeMillis();
					//System.out.println("Request: " + i + "[" + new Date() + "] Received: " + response);
					double total = (after - before)/1000;
					sum = sum + total;
				}
				System.out.println("Client " + id + ": " + sum/R + " seconds.");
				sum2 = sum2 + sum/R;
				output.close();
				server.close();
				socket.close();
				ended[id-1] = true;
				boolean terminate = true;
				for (int i=0; i<ended.length; i++)
					if (ended[i]==false)
						terminate = false;
				if (terminate) {
					System.out.println("Average Communication Latency for all Clients: " + sum2/N + " seconds.");
					System.exit(0);
				}
				} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public static void main(String args[]) {
		try {
			//System.out.println("Average Communication Latency for 300 requests:");
			int id = 1;
			for (id = 1; id <= N; id++) {
				Socket socket = new Socket(args[0], Integer.parseInt(args[1]));
				TCP_WORKER_SERVICE.submit(new TCPWorker(socket, id));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
