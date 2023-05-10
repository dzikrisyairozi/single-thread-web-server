package com.serversocket;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class FileService {

    private String fetchedFilePath;
    private String contentType;
    private String contentDisposition;
    private int fileLength;
    private byte[] fileData;

    public FileService(String root, String path, String defaultPath) throws IOException {
        String fullPath = root + path;

        if (!isDirectory(fullPath)) {
            this.initializeByFetchedFilePath(fullPath);
            return;
        }

        if (fileExists(fullPath + "/" + defaultPath)) {
            this.initializeByFetchedFilePath(fullPath + "/" + defaultPath);
            return;
        }

        ArrayList<HashMap<String, String>> files = getAllDirectoryContents(root, path);
        ListBuilder listBuilder = new ListBuilder(files, (path.equals(defaultPath)) ? "" : path);

        this.contentType = "text/html";
        this.contentDisposition = "inline";
        this.fileData = listBuilder.getHtml().getBytes("UTF-8");
        this.fileLength = this.fileData.length;
    }

    private ArrayList<HashMap<String, String>> getAllDirectoryContents(String root, String path) {
        ArrayList<HashMap<String, String>> files = new ArrayList<>();
        File folder = new File(root + path);
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles == null) {
            return files;
        }
        String rootPath = "/" + path + (path.equals("") ? "" : "/");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (File file : listOfFiles) {
            HashMap<String, String> data = new HashMap<>();
            long sizeInByte = (file.isFile()) ? file.length() : getDirectorySize(file);
            data.put("name", file.getName());
            data.put("path", rootPath + file.getName());
            data.put("lastModified", sdf.format(file.lastModified()));
            data.put("type", (file.isFile()) ? "file" : "folder");
            data.put("size", Integer.toString((int) sizeInByte));
            files.add(data);
        }
        return files;
    }

    public static boolean fileExists(String path) {
        return (new File(path)).exists();
    }

    public static boolean isDirectory(String path) {
        File file = new File(path);
        return file.isDirectory();
    }

    public static long getDirectorySize(File dir) {
        long length = 0;
        File[] files = dir.listFiles();
        if (files == null) {
            return length;
        }
        for (File file : files) {
            long adder = (file.isFile()) ? file.length() : getDirectorySize(file);
            length += adder;
        }
        return length;
    }

    private void initializeByFetchedFilePath(String path) throws IOException {
        this.fetchedFilePath = path;
        this.setFileLength();
        this.setContentType();
        this.setFileData();
        this.setContentDisposition();
    }

    private void setFileLength() throws IOException {
        this.fileLength = (int) Files.size(Path.of(this.fetchedFilePath));
    }

    private void setContentType() throws IOException {
        String type = Files.probeContentType(Path.of(this.fetchedFilePath));
        if (type == null || type.equals("")) {
            File file = new File(this.fetchedFilePath);
            String filename = file.getName();
            int idx = filename.lastIndexOf(".");
            type = (filename.substring(idx + 1).equals("js")) ? "application/javascript" : "text/plain";
        }
        this.contentType = type;
    }

    private void setFileData() throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(this.fetchedFilePath)) {
            this.fileData = fileInputStream.readAllBytes();
        }
    }

    public void setContentDisposition() {
        this.contentDisposition = (this.contentType.split("/")[0].equals("text")) ? "inline" : "attachment";
    }

    public String getContentDisposition() {
        return this.contentDisposition;
    }

    public String getContentType() {
        return this.contentType;
    }

    public int getFileLength() {
        return this.fileLength;
    }

    public byte[] getFileData() {
        return this.fileData;
    }
}
