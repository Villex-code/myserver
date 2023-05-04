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
    public static int partition_size;

    public static HashMap<String, ArrayList<ArrayList<Waypoint>>> Userlog = new HashMap();

    public static HashMap<String, ArrayList<ArrayList<Double>>> for_reduce;

    private String clientUsername;

    public ClientHandler(Socket socket) {
        try {

            this.for_reduce = new HashMap<>();
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
        while (socket.isConnected()) {
            try {

                System.out.println("Waiting in the clientHandler for a message ...");

                messageFromClient = bufferedReaderC.readLine();

                System.out.println("Eimai ston client handler kai PHRA apo ton client: " + clientUsername
                        + " to gpx arxeio: " + messageFromClient);

                // parse to gpx file
                GPXparser gp = new GPXparser();
                ArrayList<Waypoint> wpts = new ArrayList<>();
                wpts = gp.parseGpx(new File(messageFromClient));

                ArrayList<ArrayList<Waypoint>> partitions = MapReduce.Mapping(messageFromClient, wpts);

                partition_size = partitions.size();

                Userlog.put(clientUsername, partitions);

                // System.out.println("Partitions in the clientHandler are : " + partitions);

                // HashMap<String, ArrayList<ArrayList<Waypoint>>> hashmap1 = new HashMap<>();

                broadcastToWorker(clientUsername, partitions);

            } catch (Exception e) {
                System.out.println("There was an exception in the ClientHandler");
                e.printStackTrace();
                closeEverything(socket, bufferedReaderC, out);
                break;
            }
        }

    }

    public synchronized void broadcastToWorker(String clientUsername, ArrayList<ArrayList<Waypoint>> waypoints) {
        // steilto se olous tous workers

        int size = WorkerHandler.workerHandlers.size();

        for (int i = 0; i < waypoints.size(); i++) {

            try {
                int to_worker = i % size;

                ArrayList<Waypoint> to_proccess = waypoints.get(i);
                HashMap<String, ArrayList<Waypoint>> sendingHash = new HashMap<>();

                sendingHash.put(clientUsername, to_proccess);

                WorkerHandler.workerHandlers.get(to_worker).outW.writeObject(sendingHash);
                WorkerHandler.workerHandlers.get(to_worker).outW.flush();

                System.out.println("Waypoints have been sent from Client Handler ! " + i);

            } catch (IOException e) {
                closeEverything(socket, bufferedReaderC, out);
                System.out.println("Had an issue while distributing in the Client Hanlder");
                e.printStackTrace();
                break;
            } catch (Exception e) {
                System.out.println("Had an issue while distributing in the Client Hanlder");
                e.printStackTrace();
                closeEverything(socket, bufferedReaderC, out);
            }

        }
    }

    public synchronized static void broadcastToClient(ArrayList<Double> results, String clientName) {
        System.out.println("I am in the broadcastToClient in client handler and I will deliver " + results);

        for_reduce.get(clientName).add(results);

        if (for_reduce.get(clientName).size() == Userlog.get(clientName).size()) {
            try {
                ArrayList<Double> tosend = MapReduce.Reduce("client1", for_reduce.get(clientName));
                ClientHandler.clientHandlers.get(0).out.writeObject(tosend);
                ClientHandler.clientHandlers.get(0).out.flush();

                ClientHandler.clientHandlers.remove(0);

            } catch (Exception e) {

                System.out.println("There was an error in the BroadcasttoClinet in the ClientHandler");
                e.printStackTrace();

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