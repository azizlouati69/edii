package com.edi.ediconvertor.config;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class MailConfig {

    private String host;
    private int port;
    private String username;
    private String password;

    @XmlElement(name = "properties")
    private MailProperties properties;

    // Getters and setters
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public MailProperties getProperties() { return properties; }
    public void setProperties(MailProperties properties) { this.properties = properties; }
}
