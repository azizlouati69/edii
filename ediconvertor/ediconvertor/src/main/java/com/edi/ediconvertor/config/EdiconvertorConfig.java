package com.edi.ediconvertor.config;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "config")
public class EdiconvertorConfig {

    private String inputPath;
    private String outputPath;
    private String mailreceiver;
    private MailConfig mailConfig;
    @XmlElement(name = "input-path")
    public String getInputPath() {
        return inputPath;
    }
    @XmlElement(name = "mail-receiver")
    public String getMailreceiver() {
        return mailreceiver;
    }
    public void setInputPath(String inputPath) {
        this.inputPath = inputPath;
    }

    @XmlElement(name = "output-path")
    public String getOutputPath() {
        return outputPath;
    }
    @XmlElement(name = "mail-config")
    public MailConfig getMailConfig() { return mailConfig; }
    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public void setMailreceiver(String mailreceiver) {
        this.mailreceiver = mailreceiver;
    }

    public void setMailConfig(MailConfig mailConfig) { this.mailConfig = mailConfig; }
}
