import java.util.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.*;
import java.io.*;

public class ClientHandler implements Runnable {

    public static ArrayList<String> clientHandlers = new ArrayList<>();
    private Socket socket;
    public static int w=0;
    private BufferedReader bufferedReaderC;
    // private BufferedWriter bufferedWriter;
    private ObjectOutputStream out;
    private String clientUsername;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            out = new ObjectOutputStream(socket.getOutputStream());
            // this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReaderC = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // this.clientUsername = bufferedReader.readLine();
            //clientHandlers.add(this);
            //broadcastMessage("Server : " + clientUsername + " has connected !");

        } catch (IOException e) {
            closeEverything(socket, bufferedReaderC, out);
        }
    }

    @Override
    public void run() {
        String messageFromClient;
            try {
                
                messageFromClient = bufferedReaderC.readLine();
                w++;
                clientUsername = "user"+w;
                clientHandlers.add(clientUsername);

                System.out.println("Eimai ston client handler kai phra apo ton client: "+clientUsername+ " to gpx arxeio: "+messageFromClient);

                //parse to gpx file
                GPXparser gp = new GPXparser();
                ArrayList<Waypoint> wpts =  new ArrayList<>();
                wpts = gp.parseGpx(new File(messageFromClient)); 

                // //print the ArrayList with the waypoints fron the gpx file
                // for (int i=0;i<wpts.size();i++){
                //     System.out.println(wpts.get(i).toString());
                // }

                //create a partition
                //HashMap<String,ArrayList<String>> mymap = new HashMap<>();
                ArrayList<HashMap<String,ArrayList<Waypoint>>> waypoints =  new ArrayList<>(); //Arraylist<ArrayList<String>>
                ArrayList<Waypoint> sublist1 = new ArrayList<>();
                HashMap<String,ArrayList<Waypoint>> hashmap1 = new HashMap<>();
                sublist1.add(wpts.get(0));
                hashmap1.put(clientUsername,sublist1);

                ArrayList<Waypoint> sublist2 = new ArrayList<>();
                HashMap<String,ArrayList<Waypoint>> hashmap2 = new HashMap<>();
                sublist2.add(wpts.get(1));
                sublist2.add(wpts.get(2));
                hashmap2.put(clientUsername,sublist2);
                
                waypoints.add(hashmap1);
                waypoints.add(hashmap2);

                // for (int i=0;i<sublist1.size();i++){
                //     System.out.println("Sublist1: "+wpts.get(i).toString());
                // }
                // for (int i=0;i<sublist2.size();i++){
                //     System.out.println("Sublist2: "+wpts.get(i).toString());
                // }

                // waypoints.add(wpts.get(0));
                // waypoints.add("Sublist2");
                // waypoints.add("Sublist3");

                

                broadcastMessage(clientUsername, waypoints);
                
            } catch (IOException e) {
                closeEverything(socket, bufferedReaderC, out);
                
            }
        
    }

    public void broadcastMessage(String clientUsername,ArrayList<HashMap<String,ArrayList<Waypoint>>>  waypoints) {

        // // steilto se olous tous clients
        // for (ClientHandler clientHandler : clientHandlers) {
        //     try {
        //         if (!clientHandler.clientUsername.equals(clientUsername)) {
        //             clientHandler.bufferedWriter.write(messageToSend);
        //             clientHandler.bufferedWriter.newLine();
        //             clientHandler.bufferedWriter.flush();
        //         }
        //     } catch (IOException e) {
        //         closeEverything(socket, in, out);
        //         break;
        //     }
        // }

        // steilto se olous tous workers
        int i=0;
        for (WorkerHandler workerHandler : WorkerHandler.workerHandlers) {
            try {
                    workerHandler.outW.writeObject(waypoints.get(i)); //kathe Worker apo thn lista workerhandlers pairnei ena <clientname,[chunk]>
                    workerHandler.outW.flush();
                    i++;
            } catch (IOException e) {
                closeEverything(socket, bufferedReaderC, out);
                break;
            }
        }
    }

    // public void removeClientHandler() {
    //     clientHandlers.remove(this);
    //     broadcastMessage(clientUsername + " Has left the chat");
    // }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, ObjectOutputStream out) {
        //removeClientHandler();
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