# Java WebServer Projects

This workspace demonstrates three progressively more advanced Java socket server examples:

1. `SingleThreaded/` — a simple blocking server that handles one client connection at a time.
2. `MultiThreaded/` — a server that accepts clients concurrently by creating a new thread for each connection.
3. `ThreadPool/` — a server that uses a fixed-size thread pool and implements a simple file-transfer protocol.

## What each folder does

### `SingleThreaded`
- `Server.java` opens a server socket on port `8010`.
- It accepts a client, sends a greeting, and then closes the connection.
- The server processes one client at a time, so any additional client must wait until the current connection ends.
- `Client.java` connects to the server and reads the server response.

### `MultiThreaded`
- `Server.java` also listens on port `8010`, but it accepts each client in a separate thread.
- This allows multiple clients to connect at the same time without waiting for the previous client to finish.
- It is useful for learning how thread-per-connection servers work.
- `Client.java` opens many client connections concurrently and receives greetings from the server.

### `ThreadPool`
- `Server.java` uses `Executors.newFixedThreadPool(...)` to handle clients.
- Instead of creating an unbounded number of threads, it reuses a fixed pool of worker threads.
- The server reads a requested file path from the client, validates it against the `ThreadPool` folder, and sends the file contents.
- `Client.java` requests a file name, receives the file from the server, and writes it locally as `received-<name>`.
- This is closer to a real-world server design because it limits thread creation and adds a simple protocol.

## Key differences

| Folder | Concurrency model | File transfer | Use case |
|--------|-------------------|---------------|----------|
| `SingleThreaded` | One connection at a time | No | Simple proof of concept / learning sockets |
| `MultiThreaded` | One thread per connection | No | Concurrent clients, easy to understand threading |
| `ThreadPool` | Fixed pool of worker threads | Yes | More scalable, controlled resource usage, file-serving example |

## Compile instructions

Run these commands from the `WebServer` root directory:

```cmd
javac SingleThreaded\Server.java SingleThreaded\Client.java
javac MultiThreaded\Server.java MultiThreaded\Client.java
javac ThreadPool\Server.java ThreadPool\Client.java
```

## Run instructions

### Single-threaded server

```cmd
cd SingleThreaded
java Server
```

Then in another terminal:

```cmd
cd SingleThreaded
java Client
```

### Multi-threaded server

```cmd
cd MultiThreaded
java Server
```

Then in another terminal:

```cmd
cd MultiThreaded
java Client
```

### Thread-pool server and client

From the root of the workspace:

```cmd
java ThreadPool.Server
```

In another terminal:

```cmd
java ThreadPool.Client example.txt
```

This will request `example.txt` from `ThreadPool/` and save it locally as `received-example.txt` in the folder where the client runs.

## Notes

- `MultiThreaded` and `ThreadPool` use `package` declarations, so run them from the workspace root with fully qualified names.
- `SingleThreaded` does not use a package declaration and can be run directly from the `SingleThreaded` directory.
- `ThreadPool.Server` serves files from the `ThreadPool` directory by default and prevents path traversal outside that directory.
- If you want to request a different file, pass the file name as the command-line argument to `ThreadPool.Client`.
