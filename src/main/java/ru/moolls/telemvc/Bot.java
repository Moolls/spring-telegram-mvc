package ru.moolls.telemvc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.moolls.telemvc.config.BotProperties;
import ru.moolls.telemvc.entity.BeanMethod;

import java.lang.reflect.InvocationTargetException;


@Component
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(BotProperties.class)
public class Bot extends TelegramLongPollingBot {

    private final HandlingMethodResolver handlingMethodResolver;

    private final MethodReturnedHandler methodReturnedHandler;

    private final BotProperties botProperties;

    @Override
    public void onUpdateReceived(Update update) {
        log.error(botProperties.toString());
        try {
            BeanMethod beanMethod = handlingMethodResolver.resolveHandlingMethod(update);
            Object methodResult = beanMethod.invoke(update);
            BotApiMethod resultMethod = methodReturnedHandler.handleResult(update, methodResult);
            sendApiMethod(resultMethod);
        } catch (InvocationTargetException | IllegalAccessException | TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return botProperties.getName();
    }

    @Override
    public String getBotToken() {
        return botProperties.getToken();
    }
}
