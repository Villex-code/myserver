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
        List<Double> midresults;
        int workerReturn = 0;
        while (true) {
            try {

                midresults = (List<Double>) inW.readObject();

                System.out
                        .println("Eimai ston Workerhandler kai exw ta mid results: " + midresults);
                System.out.println("Eimai ston Workerhandler kai exw workers: " + workerHandlers.size());

                // gia kathe user metra X epistrofes
                // an einai X kane Reduce

                String finalResults = midresults.toString();
                String tempMessage = "Hello there client , this is a message from Worker Handler ";

                ClientHandler.broadcastToClient(finalResults);

                System.out.println("-------------");
                System.out.println("Sending to client done !");
                System.out.println("-------------");

            } catch (ClassNotFoundException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    public void broadcastToClient(ArrayList<HashMap<String, ArrayList<Waypoint>>> waypoints) {

        // steilto se olous tous clients
        String messageToSend = "client took the message";
        for (ClientHandler clientHandler : ClientHandler.clientHandlers) {
            try {

                // if (!clientHandler.clientUsername.equals(clientUsername)) {
                clientHandler.bufferedWriterC.write(messageToSend);
                clientHandler.bufferedWriterC.newLine();
                clientHandler.bufferedWriterC.flush();
                // }
            } catch (IOException e) {
                closeEverything(socket, inW, outW);
                break;
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
