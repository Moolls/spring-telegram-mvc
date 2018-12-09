package ru.moolls.telemvc;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class MethodReturnedHandler {

    public BotApiMethod handleResult(Update update, Object methodResult) {

        if (methodResult instanceof BotApiMethod) {
            return (BotApiMethod) methodResult;
        } else if (methodResult instanceof String) {
            return prepareSendMessageMethod((String) methodResult, update);
        }
        return null;
    }


    private SendMessage prepareSendMessageMethod(String msg, Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText(msg);
        return sendMessage;
    }
}
