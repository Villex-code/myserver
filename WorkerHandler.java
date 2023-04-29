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
    // public BufferedReader bufferedReader;
    // public BufferedWriter bufferedWriter;
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
            outW = new ObjectOutputStream(socket.getOutputStream());
            inW = new ObjectInputStream(socket.getInputStream());
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
        ArrayList<HashMap<String, ArrayList<Waypoint>>> midresults;
        int workerReturn = 0;
        while (true) {
            try {
                midresults = (ArrayList<HashMap<String, ArrayList<Waypoint>>>) inW.readObject();
                System.out
                        .println("Eimai ston Workerhandler kai exw ta intermediate results: " + midresults.get(0) + " "
                                + midresults.get(1));
                System.out.println("Eimai ston Workerhandler kai exw workers: " + workerHandlers.size());

            } catch (ClassNotFoundException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    // public static void clientRequest(String clientMessage) {
    // for (WorkerHandler workerHandler : workerHandlers) {
    // try {
    // workerHandler.bufferedWriter.write(clientMessage);
    // workerHandler.bufferedWriter.newLine();
    // workerHandler.bufferedWriter.flush();
    // } catch (IOException e) {
    // workerHandler.closeEverything(workerHandler.socket,
    // workerHandler.bufferedReader,
    // workerHandler.bufferedWriter);
    // break;
    // }
    // }
    // }

    // public void broadcastMessage(String messageToSend) {
    // for (WorkerHandler workerHandler : workerHandlers) {
    // try {
    // if (!workerHandler.workerUsername.equals(workerUsername)) {
    // workerHandler.bufferedWriter.write(messageToSend);
    // workerHandler.bufferedWriter.newLine();
    // workerHandler.bufferedWriter.flush();
    // }
    // } catch (IOException e) {
    // closeEverything(socket, bufferedReader, bufferedWriter);
    // break;
    // }
    // }
    // }

    // public void removeworkerHandler() {
    // workerHandlers.remove(this);
    // broadcastMessage(workerUsername + " Has left the chat");
    // }

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
