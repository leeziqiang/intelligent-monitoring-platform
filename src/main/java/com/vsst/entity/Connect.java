package com.vsst.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "tcp.connect")
public class Connect {
    private String host;
    private int port;
    private String address;
    private String sendFile;
    private String receiveFile;
}
