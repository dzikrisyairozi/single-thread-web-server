package com.serversocket;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.Objects;

public class ClientServer {
    private Socket client;
    private static final String ROOT_DIR = "./src/com/serversocket/root/";
    private static final String DEFAULT_FILE = "index.html";
    private static final String FILE_NOT_FOUND = "404.html";

    public ClientServer(Socket client) {
        this.client = client;
    }

    public void serve() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            BufferedOutputStream output = new BufferedOutputStream(client.getOutputStream());

            String requestStatus = "";
            do {
                requestStatus = reader.readLine();
            } while (requestStatus == null || Objects.equals(requestStatus, ""));
            String[] parsed = requestStatus.split(" ");
            String requestedFile = (parsed[1].equals("/")) ? "" : parsed[1].substring(1);
            System.out.format("[%s] %s - Accepted\n", new Date(), requestStatus);

            boolean fileExists = FileService.fileExist(ROOT_DIR + requestedFile);

            String fetchedFile = (fileExists) ? requestedFile : FILE_NOT_FOUND;
            String responseStatus = (fileExists) ? "200 OK" : "404 File Not Found";

            FileService fileService = new FileService(ROOT_DIR, fetchedFile, DEFAULT_FILE);

            writer.write("HTTP/1.1 " + responseStatus + "\r\n");
            writer.write("Content-Type: " + fileService.getContentType() + "\r\n");
            writer.write("Content-Length: " + fileService.getFileLength() + "\r\n");
            writer.write("Content-Disposition: " + fileService.getContentDisposition() + "\r\n");
            writer.write("\r\n");
            writer.flush();

            output.write(fileService.getFileData(), 0, fileService.getFileLength());
            output.flush();

            System.out.format("[%s] %s - Closing\n", new Date(), requestStatus);
            client.close();

        } catch (IOException ex) {
            System.err.println("Error: " + ex);
        }
    }
}