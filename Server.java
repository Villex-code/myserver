import java.io.*;
import java.net.*;

public class Server {
    private ServerSocket serverSocket;
    private Socket socket1;
    private Socket socket2;

    public Server(ServerSocket serverSocket) throws IOException {
        this.serverSocket = serverSocket;
    }

    public void startServer() throws IOException {
        System.out.println("Chat app has started");

        int count = 0;
        int workerAmount = 2;

        try {
            while (!serverSocket.isClosed()) {
                Socket socket1 = serverSocket.accept();
                System.out.println("A new worker has connected");
                WorkerHandler workerHandler = new WorkerHandler(socket1);
                Thread thread = new Thread(workerHandler);
                thread.start();
                count++;
                if (count > workerAmount - 1) {
                    break;
                }
            }
        } catch (IOException e) {
            closeServerSocket();
        }

        try {
            while (!serverSocket.isClosed()) {
                Socket socket1 = serverSocket.accept();
                System.out.println("A new client has connected");
                ClientHandler clientHandler = new ClientHandler(socket1);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            closeServerSocket();
        }
    }

    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}