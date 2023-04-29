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

        String waypoints = "";
        int count=0;
        // connect all the workers
        while (!serverSocket.isClosed()) {
        try {
            
                socket1 = serverSocket.accept();
                System.out.println("A new worker has connected");
                WorkerHandler workerHandler = new WorkerHandler(socket1);
                Thread thread = new Thread(workerHandler);
                thread.start();
                count++;
                if (count > 1) {
                    break;
                }
                
                
            }
        catch (IOException e) {
            closeServerSocket();
        }

        try {   
                socket2 = serverSocket.accept();
                System.out.println("A new client has connected");
                ClientHandler clientHandler = new ClientHandler(socket2);

                Thread thread2 = new Thread(clientHandler);
                thread2.start();

                }
            
        catch (IOException e) {
            closeServerSocket();
        }
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