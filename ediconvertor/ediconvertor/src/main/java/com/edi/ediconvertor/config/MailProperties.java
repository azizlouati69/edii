package com.edi.ediconvertor.config;


import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class MailProperties {

    private boolean auth;
    private boolean starttls;

    // Getters and setters
    public boolean isAuth() { return auth; }
    public void setAuth(boolean auth) { this.auth = auth; }

    public boolean isStarttls() { return starttls; }
    public void setStarttls(boolean starttls) { this.starttls = starttls; }
}