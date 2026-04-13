package ThreadPool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final ExecutorService threadPool;
    private final File baseDir;

    public Server(int poolSize, File baseDir) {
        this.threadPool = Executors.newFixedThreadPool(poolSize);
        this.baseDir = baseDir;
    }

    public void handleClient(Socket clientSocket) {
        try (
                BufferedReader fromSocket = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter toSocket = new PrintWriter(clientSocket.getOutputStream(), true)) {
            String requestedFile = fromSocket.readLine();
            if (requestedFile == null || requestedFile.isBlank()) {
                toSocket.println("ERROR: missing file name");
                return;
            }

            File file = new File(baseDir, requestedFile).getCanonicalFile();
            File root = baseDir.getCanonicalFile();
            if (!file.getPath().startsWith(root.getPath() + File.separator) && !file.equals(root)) {
                toSocket.println("ERROR: invalid file path");
                return;
            }

            if (!file.exists() || !file.isFile()) {
                toSocket.println("ERROR: file not found");
                return;
            }

            toSocket.println("OK");
            toSocket.println(file.getName());

            try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = fileReader.readLine()) != null) {
                    toSocket.println(line);
                }
            }

            toSocket.println("__END_OF_FILE__");
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException ignored) {
            }
        }
    }

    public static void main(String[] args) {
        int port = 8010;
        int poolSize = 100;
        File baseDir = new File("ThreadPool");
        if (!baseDir.exists() || !baseDir.isDirectory()) {
            baseDir = new File(".");
        }
        System.out.println("Server is serving files from: " + baseDir.getAbsolutePath());

        Server server = new Server(poolSize, baseDir);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setSoTimeout(70000);
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                server.threadPool.execute(() -> server.handleClient(clientSocket));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            server.threadPool.shutdown();
        }
    }
}
