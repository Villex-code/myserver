import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

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
            // this.bufferedReader = new BufferedReader(new
            // InputStreamReader(socket.getInputStream()));
            // this.bufferedWriter = new BufferedWriter(new
            // OutputStreamWriter(socket.getOutputStream()));
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            closeEverything(socket, in, out);
        }
    }

    public void sendMessage(ArrayList<HashMap<String, ArrayList<Waypoint>>> toSend) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // String msgFromGroupChat;
                try {

                    // h map tha parei to map kai tha to epistrepsei ta midresults
                    // ArrayList<String> arr = new ArrayList<>();
                    // arr.add("hi");
                    // arr.add("hey");

                    out.writeObject(toSend);

                } catch (IOException e) {
                    closeEverything(socket, in, out);
                }

            }
        }).start();
    }

    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<HashMap<String, ArrayList<Waypoint>>> listpart = new ArrayList<>();
                System.out.println("Worker has started ");
                while (true) {
                    try {
                        listpart = (ArrayList<HashMap<String, ArrayList<Waypoint>>>) in.readObject();

                        System.out.println("Eimai ston Worker kai phra list me: name: " + listpart.get(0).get("user1"));
                        System.out.println(listpart.get(0));

                        sendMessage(listpart);

                    } catch (IOException e) {
                        System.out.println("Shuting down worker because of exception");
                        closeEverything(socket, in, out);

                    } catch (ClassNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    public void closeEverything(Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        try {
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