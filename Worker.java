import java.io.*;
import java.net.*;
import java.util.*;

public class Worker {

    private Socket socket;
    // private BufferedWriter bufferedWriter;
    // private BufferedReader bufferedReader;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String username;

    HashMap<String, ArrayList<String>> mymap = new HashMap<>();

    public Worker(Socket socket) {
        try {
            this.socket = socket;
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            closeEverything(socket, in, out);
        }
    }

    public synchronized void sendMessage(List<Double> toSend) {

        try {

            out.writeObject(toSend);
            out.flush();

        } catch (IOException e) {
            System.out.println("Had an issue sending message in the worker");
            closeEverything(socket, in, out);
        }

    }

    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Waypoint> listpart = new ArrayList<>();
                System.out.println("Worker has started ");
                while (true) {
                    try {

                        System.out.println("Worker is listening for an object ...");

                        listpart = (ArrayList<Waypoint>) in.readObject();

                        System.out.println("Eimai ston Worker kai phra list : " + listpart);

                        ArrayList<Double> worker_results = MapReduce.Process(listpart);

                        sendMessage(worker_results);

                    } catch (IOException e) {

                        closeEverything(socket, in, out);
                        break;

                    } catch (ClassNotFoundException e) {
                        System.out.println("There was an error ClassNotFound in worker");
                        e.printStackTrace();
                        break;
                    }
                }
            }
        }).start();

    }

    public void closeEverything(Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        try {
            System.out.println("Shutting down everything in worker");
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();

            }
            if (socket != null) {
                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws UnknownHostException, IOException {
        Socket socket = new Socket("localhost", 1234);
        Worker worker = new Worker(socket);
        worker.listenForMessage();
        // worker.sendMessage();
    }
}