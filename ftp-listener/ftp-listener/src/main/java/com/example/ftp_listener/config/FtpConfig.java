package com.example.ftp_listener.config;

 import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "config")
public class FtpConfig {
    private String directoryPath;

    @XmlElement(name = "directory-path")
    public String getDirectoryPath() {
        return directoryPath;
    }

    public void setDirectoryPath(String directoryPath) {
        this.directoryPath = directoryPath;
    }
}
