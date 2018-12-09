package ru.moolls.telemvc;

import java.lang.reflect.InvocationTargetException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.moolls.telemvc.entity.BeanMethod;


@Component
public class Bot extends TelegramLongPollingBot {


  @Autowired
  private HandlingMethodResolver handlingMethodResolver;

  @Autowired
  private MethodReturnedHandler methodReturnedHandler;

  @Value("${bot.name}")
  private String botName;

  @Value("${bot.token}")
  private String botToken;


  @Override
  public void onUpdateReceived(Update update) {
    try {
      BeanMethod beanMethod = handlingMethodResolver.resolveHandlingMethod(update);
      Object methodResult = beanMethod.invoke(update);
      BotApiMethod resultMethod = methodReturnedHandler.handleResult(update, methodResult);
      sendApiMethod(resultMethod);
    } catch (InvocationTargetException | IllegalAccessException | TelegramApiException e) {
      e.printStackTrace();
    }
  }


  @Override
  public String getBotUsername() {
    return botName;
  }

  @Override
  public String getBotToken() {
    return botToken;
  }
}
