package com.example.ftp_listener.service;


import com.example.ftp_listener.config.FtpConfig;
import com.jcraft.jsch.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Vector;

@Service
public class FtpPollingService {

    private final String server = "sftp.tradinggrid.gxs.com";
    private final int port = 22; // Default SFTP port
    private final String user = "ANO22766";
    private final String pass = "M24TDB08";
    private final String remoteDir = "/ANO22766";
    // Use forward slashes for SFTP paths
    private final FtpConfig ftpConfig;

    @Autowired
    public FtpPollingService(FtpConfig ftpConfig) {
        this.ftpConfig = ftpConfig;
    }



    private String extractSenderName(String fileName) {
        try {
            int startIndex = fileName.indexOf("%");
            if (startIndex == -1) return null;

            // Start after "done%%"
            int senderStart = startIndex + "%".length();
            int senderEnd = fileName.indexOf("%", senderStart);

            if (senderEnd == -1) return null;

            String sender = fileName.substring(senderStart, senderEnd).trim();
             // sanitize

            return sender.isEmpty() ? null : sender;
        } catch (Exception e) {
            return null;
        }
    }

    @Scheduled(fixedDelay = 1000) // every 1 second
    public void pollSftpServer() {
        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp channelSftp = null;
        String localDir = ftpConfig.getDirectoryPath();
        System.out.println("Local directory path is: " + localDir);
        try {
            session = jsch.getSession(user, server, port);
            session.setPassword(pass);

            // Disable strict host checking (for demo purposes)
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();

            channelSftp.cd(remoteDir);

            // List files in the remote directory
            Vector<ChannelSftp.LsEntry> files = channelSftp.ls(remoteDir);
            if ((files != null) && !files.isEmpty()) {
                for (ChannelSftp.LsEntry entry : files) {
                    String fileName = entry.getFilename();

                    // Skip directories
                    if (entry.getAttrs().isDir()) {
                        continue;
                    }

                    System.out.println("Found file: " + fileName);

                    // Extract sender name
                    String senderName = extractSenderName(fileName);
                    if (senderName == null) {
                        System.err.println("Skipping file (sender name not found): " + fileName);
                        continue;
                    }

                    // Create sender folder if not exists
                    File senderDir = new File(localDir, senderName);
                    if (!senderDir.exists()) {
                        senderDir.mkdirs();
                    }

                    File targetFile = new File(senderDir, fileName);

                    // Download and write to file
                    try (InputStream inputStream = channelSftp.get(fileName);
                         FileOutputStream outputStream = new FileOutputStream(targetFile)) {

                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = inputStream.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, length);
                        }

                        outputStream.flush();
                        System.out.println("Downloaded to: " + targetFile.getAbsolutePath());

                        // Delete file from server
                        try {
                            channelSftp.rm(fileName);
                            System.out.println("Deleted from server: " + fileName);
                        } catch (SftpException e) {
                            System.err.println("Error deleting file from server: " + fileName);
                            e.printStackTrace();
                        }

                    } catch (SftpException | IOException e) {
                        System.err.println("Error downloading file: " + fileName);
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (channelSftp != null && channelSftp.isConnected()) {
                    channelSftp.disconnect();
                }
                if (session != null && session.isConnected()) {
                    session.disconnect();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
/*
 public void putFileToServer(String fileName, ChannelSftp channelSftp) {
        File fileToUpload = new File(localDir, fileName);

        // Check if the file exists locally
        if (fileToUpload.exists()) {
            try (InputStream reUploadStream = new FileInputStream(fileToUpload)) {
                channelSftp.put(reUploadStream, fileName);
                System.out.println("Re-uploaded to server: " + fileName);
            } catch (SftpException | FileNotFoundException e) {
                System.err.println("Error re-uploading file: " + fileName);
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.err.println("File not found locally: " + fileName);
        }
    }*/
}
