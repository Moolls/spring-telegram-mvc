package ru.moolls.telemvc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;

@Slf4j
@SpringBootApplication
public class TelegramBotApplication {

    public static void main(String[] args) {
        log.info("Version: {}", TelegramBotApplication.class.getPackage().getImplementationVersion());
        ApiContextInitializer.init();
        SpringApplication.run(TelegramBotApplication.class, args);
    }

}
