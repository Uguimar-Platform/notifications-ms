package com.uguimar.notificationsms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class NotificationsMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationsMsApplication.class, args);
    }

}
