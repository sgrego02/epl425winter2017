import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

	public static int maxRequests;
	public static double sum = 0;
	public static double sum2 = 0;
	public static int counter = 0;

	private static class TCPWorker implements Runnable {

		private Socket client;
		double requests;

		public TCPWorker(Socket client) {
			this.client = client;
			this.requests = 0;
		}

		@Override
		public void run() {

			try {
				double before = System.currentTimeMillis();
				long beforeMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				String clientbuffer;
				Random rand = new Random(System.currentTimeMillis());
				int payload;
				// System.out.println("Client connected with: " +
				// client.getInetAddress() + ":" + client.getPort());
				DataOutputStream output = new DataOutputStream(client.getOutputStream());
				BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
				int i = 0;
				while (i < maxRequests) {
					if (!client.isConnected())
						break;
					clientbuffer = reader.readLine();
					if (clientbuffer == null)
						break;
					// System.out.println("[" + new Date() + "] Received: " +
					// clientbuffer);
					if (!clientbuffer.contains("HELLO")) {
						output.writeBytes("Wrong type of request!" + System.lineSeparator());
						output.flush();
						break;
					}
					payload = rand.nextInt(2000) + 300;
					output.writeBytes("WELCOME " + clientbuffer.substring(clientbuffer.length() - 2) + " " + payload
							+ "Kb" + System.lineSeparator());
					output.flush();
					i++;
					clientbuffer = null;
					requests++;
				}
				if (i >= maxRequests) {
					output.writeBytes("Server is too busy!" + System.lineSeparator());
					output.flush();
				}
				double after = System.currentTimeMillis();
				long afterMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				double total = (after - before) / 1000;
				double throughput = requests / total;
				//System.out.println("Server's throughput: " + throughput + " requests per second");
				sum = sum + throughput;
				counter++;
				System.out.println("Average server's throughput for now: " + sum/counter);
				Runtime runtime = Runtime.getRuntime();
				runtime.gc();
				long totalMemory = afterMemory - beforeMemory;
				//System.out.println("Memory utilization: " + totalMemory);
				sum2 = sum2 + totalMemory;
				System.out.println("Average memory utilization for now: " + sum2/counter);
				output.close();
				reader.close();
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public static ExecutorService TCP_WORKER_SERVICE = Executors.newFixedThreadPool(10);

	public static void main(String args[]) {
		try {
			ServerSocket socket = new ServerSocket(Integer.parseInt(args[0]));

			System.out.println("Server listening to: " + socket.getInetAddress() + ":" + socket.getLocalPort());
			System.out.println("Waiting for requests...");
			while (true) {
				Socket client = socket.accept();
				maxRequests = Integer.parseInt(args[1]);
				TCP_WORKER_SERVICE.submit(new TCPWorker(client));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
