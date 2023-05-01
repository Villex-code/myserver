import java.util.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.*;
import java.io.*;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    public static int w = 0;
    public BufferedReader bufferedReaderC;
    public BufferedWriter bufferedWriterC;
    public ObjectOutputStream out;
    private String clientUsername;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            out = new ObjectOutputStream(socket.getOutputStream());
            this.bufferedWriterC = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReaderC = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // this.clientUsername = bufferedReader.readLine();
            // clientHandlers.add(this);
            // broadcastMessage("Server : " + clientUsername + " has connected !");
            w++;
            clientUsername = "user" + w;
            clientHandlers.add(this);

        } catch (IOException e) {
            closeEverything(socket, bufferedReaderC, out);
        }
    }

    @Override
    public void run() {
        String messageFromClient;
        while (this.socket.isConnected()) {
            try {
                System.out.println("Waiting in the clientHandler for a message ...");

                messageFromClient = bufferedReaderC.readLine();

                System.out.println("Eimai ston client handler kai PHRA apo ton client: " + clientUsername
                        + " to gpx arxeio: " + messageFromClient);

                // parse to gpx file
                GPXparser gp = new GPXparser();
                ArrayList<Waypoint> wpts = new ArrayList<>();
                wpts = gp.parseGpx(new File(messageFromClient));

                // //print the ArrayList with the waypoints fron the gpx file
                // for (int i=0;i<wpts.size();i++){
                // System.out.println(wpts.get(i).toString());
                // }

                // create a partition
                // HashMap<String,ArrayList<String>> mymap = new HashMap<>();
                ArrayList<HashMap<String, ArrayList<Waypoint>>> waypoints = new ArrayList<>(); // Arraylist<ArrayList<String>>
                HashMap<String, ArrayList<Waypoint>> hashmap1 = new HashMap<>();

                hashmap1.put(clientUsername, wpts);

                waypoints.add(hashmap1);

                broadcastToWorker(messageFromClient, waypoints);

            } catch (IOException e) {
                closeEverything(socket, bufferedReaderC, out);

            }
        }

    }

    public static void broadcastToClient(String results) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                clientHandler.bufferedWriterC.newLine();
                clientHandler.bufferedWriterC.write(results);
                clientHandler.bufferedWriterC.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void broadcastToWorker(String clientUsername, ArrayList<HashMap<String, ArrayList<Waypoint>>> waypoints) {
        // steilto se olous tous workers

        for (WorkerHandler workerHandler : WorkerHandler.workerHandlers) {
            try {
                workerHandler.outW.writeObject(waypoints); // kathe Worker apo thn lista workerhandlers pairnei ena
                                                           // <clientname,[chunk]>
                workerHandler.outW.flush();
                System.out.println("Waypoints have been sent from Client Handler !");

            } catch (IOException e) {
                closeEverything(socket, bufferedReaderC, out);
                break;
            }
        }
    }

    // public void removeClientHandler() {
    // clientHandlers.remove(this);
    // broadcastMessage(clientUsername + " Has left the chat");
    // }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, ObjectOutputStream out) {
        // removeClientHandler();
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
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
}