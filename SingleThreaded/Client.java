import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    public void run() throws Exception {
        int port = 8010;
        InetAddress address = InetAddress.getByName("localhost");
        Socket socket = new Socket(address, port);
        PrintWriter toServer = new PrintWriter(socket.getOutputStream());
        BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("Hello from Client!");
        String line = fromServer.readLine();
        System.out.println("Received from Server: " + line);
    }

    public static void main(String[] args) {
        Client client = new Client();
        try {
            client.run();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
