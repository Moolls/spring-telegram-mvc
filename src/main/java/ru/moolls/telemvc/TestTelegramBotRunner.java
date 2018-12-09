package ru.moolls.telemvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
public class TestTelegramBotRunner {

  public static void main(String[] args) {
    ApiContextInitializer.init();
    SpringApplication.run(TestTelegramBotRunner.class, args);
  }


}
