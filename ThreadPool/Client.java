package ThreadPool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        String requestedFile;
        if (args.length > 0) {
            requestedFile = args[0];
        } else {
            System.out.print("Enter file path to request from server: ");
            Scanner scanner = new Scanner(System.in);
            requestedFile = scanner.nextLine().trim();
            scanner.close();
        }

        if (requestedFile.isBlank()) {
            System.out.println("No file path provided.");
            return;
        }

        try (Socket socket = new Socket("localhost", 8010);
             PrintWriter toServer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            toServer.println(requestedFile);

            String status = fromServer.readLine();
            if (status == null) {
                System.out.println("No response from server.");
                return;
            }
            if (!"OK".equals(status)) {
                System.out.println("Server response: " + status);
                return;
            }

            String returnedName = fromServer.readLine();
            if (returnedName == null) {
                System.out.println("Server closed the connection unexpectedly.");
                return;
            }

            File outputFile = new File("received-" + returnedName);
            try (PrintWriter fileWriter = new PrintWriter(new FileWriter(outputFile))) {
                String line;
                while ((line = fromServer.readLine()) != null) {
                    if ("__END_OF_FILE__".equals(line)) {
                        break;
                    }
                    fileWriter.println(line);
                }
            }

            System.out.println("Received file: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
