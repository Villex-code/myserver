import java.util.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.*;
import java.io.*;

public class WorkerHandler implements Runnable {

    public static ArrayList<WorkerHandler> workerHandlers = new ArrayList<>();
    public Socket socket;
    public BufferedReader bufferedReader;
    public BufferedWriter bufferedWriter;
    public String workerUsername;

    public WorkerHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.workerUsername = bufferedReader.readLine();
            workerHandlers.add(this);
            broadcastMessage("Server : " + workerUsername + " has connected !");

        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    @Override
    public void run() {
        String messageFromClient;

        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                // edw kanoume to print

                System.out.println("The message was " + messageFromClient);

                broadcastMessage(messageFromClient);
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public void processWaypoint(String waypointToProcess) {

    }

    public static void clientRequest(String clientMessage) {
        for (WorkerHandler workerHandler : workerHandlers) {
            try {
                workerHandler.bufferedWriter.write(clientMessage);
                workerHandler.bufferedWriter.newLine();
                workerHandler.bufferedWriter.flush();
            } catch (IOException e) {
                workerHandler.closeEverything(workerHandler.socket, workerHandler.bufferedReader,
                        workerHandler.bufferedWriter);
                break;
            }
        }
    }

    public void broadcastMessage(String messageToSend) {
        for (WorkerHandler workerHandler : workerHandlers) {
            try {
                if (!workerHandler.workerUsername.equals(workerUsername)) {
                    workerHandler.bufferedWriter.write(messageToSend);
                    workerHandler.bufferedWriter.newLine();
                    workerHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public void removeworkerHandler() {
        workerHandlers.remove(this);
        broadcastMessage(workerUsername + " Has left the chat");
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeworkerHandler();
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();

            }
            if (socket != null) {
                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
