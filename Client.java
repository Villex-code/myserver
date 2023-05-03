import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private ObjectInputStream in;
    private String gpxfile;

    public Client(Socket socket, String gpxfile) {
        try {
            this.socket = socket;
            this.gpxfile = gpxfile;
            in = new ObjectInputStream(socket.getInputStream());
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        } catch (IOException e) {
            closeEverything(socket, in, bufferedWriter);
        }
    }

    public void sendMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // String msgFromGroupChat;
                try {

                    bufferedWriter.write(gpxfile);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();

                    System.out.println("Client has sent the message : " + gpxfile);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    System.out.println("There was an error in the Client sendMessage");

                }

            }
        }).start();
    }

    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    try {

                        System.out.println("Client is waiting for results ");

                        ArrayList<Double> results = (ArrayList<Double>) in.readObject();

                        System.out.println("My results are " + results);

                        if (results.size() == 4) {
                            System.out.println("Your total climb was : " + results.get(0));
                            System.out.println("Your total distance was : " + results.get(1));
                            System.out.println("Your total time was : " + results.get(2));
                            System.out.println("Your average speed was : " + results.get(3));
                        }

                    } catch (IOException e) {
                        System.out.println("Had an issue receiving the message ");
                        e.printStackTrace();

                    } catch (ClassNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        System.out.println("Class not found here");

                    }
                }

            }
        }).start();
    }

    public void closeEverything(Socket socket, ObjectInputStream in, BufferedWriter bufferedWriter) {
        try {
            System.out.println("Shutting down everything in client");
            if (in != null) {
                in.close();
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

    public static void main(String[] args) throws UnknownHostException, IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("What is the name of your gpx file : ");
        String gpxfile = scanner.nextLine();
        Socket socket = new Socket("localhost", 1234);
        Client client = new Client(socket, gpxfile);
        client.listenForMessage();
        client.sendMessage();
    }
}