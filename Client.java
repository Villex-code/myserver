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
            // in = new ObjectInputStream(socket.getInputStream());
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
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (socket.isConnected()) {

                    try {
                        String results = bufferedReader.readLine();

                        System.out.println(results);

                    } catch (IOException e) {
                        System.out.println("Had an issue receiving the message ");
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

    public void closeEverything(Socket socket, ObjectInputStream in, BufferedWriter bufferedWriter) {
        try {
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
        Client client = new Client(socket, "route1.gpx");
        client.listenForMessage();
        client.sendMessage();
    }
}