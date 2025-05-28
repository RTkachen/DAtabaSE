package com.example.application;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Точка входа в Spring Boot + Vaadin приложение.
 * Аннотация @PWA делает его устанавливаемым на мобильных/десктопных браузерах.
 */
@SpringBootApplication
@Theme("my-app")
@PWA(
        name = "Cinema App",
        shortName = "Cinema",
        iconPath = "icons/icon-192.png", // путь до иконки вашей PWA
        backgroundColor = "#ffffff",
        themeColor = "#1C2B3A"
)
public class Application implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
