import java.util.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.*;
import java.io.*;

public class WorkerHandler implements Runnable {

    public static ArrayList<WorkerHandler> workerHandlers = new ArrayList<>();
    private Socket socket;
    public static int f = 0;
    public ArrayList<ArrayList<Double>> for_reduce = new ArrayList<>();
    // public BufferedReader bufferedReader;
    // public BufferedWriter bufferedWriter;
    public static int counting = 0;

    public ObjectOutputStream outW;
    public ObjectInputStream inW;

    private String workerUsername;

    public WorkerHandler(Socket socket) {
        try {
            this.socket = socket;
            // this.bufferedWriter = new BufferedWriter(new
            // OutputStreamWriter(socket.getOutputStream()));
            // this.bufferedReader = new BufferedReader(new
            // InputStreamReader(socket.getInputStream()));

            this.outW = new ObjectOutputStream(socket.getOutputStream());
            this.inW = new ObjectInputStream(socket.getInputStream());

            f++;
            workerUsername = "worker" + f;
            workerHandlers.add(this);

            // broadcastMessage("Server : " + workerUsername + " has connected !");

        } catch (IOException e) {
            closeEverything(socket, inW, outW);
        }
    }

    @Override
    public void run() {
        // String messageFromClient;
        HashMap<String, ArrayList<Double>> midresults;

        while (true) {
            try {

                midresults = (HashMap<String, ArrayList<Double>>) inW.readObject();

                String clientUsername = (String) midresults.keySet().toArray()[0];
                ArrayList<Double> worker_results = midresults.get(clientUsername);

                System.out.println("From workerhandler The user :" + clientUsername + "will send to broadToClient "
                        + worker_results);

                ClientHandler.broadcastToClient(worker_results, clientUsername);

                System.out.println("-------------");
                System.out.println("Sending to client done !");
                System.out.println("-------------");

            } catch (Exception e) {

                System.out.println("Had an exception in the workerhandler");
                e.printStackTrace();

            }
        }

    }

    public void closeEverything(Socket socket, ObjectInputStream inW, ObjectOutputStream outW) {
        // removeworkerHandler();
        try {
            if (inW != null) {
                inW.close();
            }
            if (outW != null) {
                outW.close();

            }
            if (socket != null) {
                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
