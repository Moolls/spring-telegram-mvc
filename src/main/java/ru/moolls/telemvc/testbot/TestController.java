package ru.moolls.telemvc.testbot;


import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import ru.moolls.telemvc.annotation.BotController;
import ru.moolls.telemvc.annotation.BotMapping;
import ru.moolls.telemvc.entity.MethodType;
import ru.moolls.telemvc.entity.Model;

@BotController
public class TestController {

    @BotMapping(msgPath = "/start", methodType = MethodType.MSG)
    public String tetx(Update update, Model model) {
        return "Hi, guys!!!";
    }

    @BotMapping(msgPath = "/testReply", methodType = MethodType.MSG)
    public BotApiMethod test2(Update update) {
        SendMessage sendMessageMethod = new SendMessage();

        sendMessageMethod.setChatId(update.getMessage().getChatId());
        sendMessageMethod.setText("Reply here");
        sendMessageMethod.setReplyMarkup(new ForceReplyKeyboard());

        return sendMessageMethod;
    }

    @BotMapping(replyOn = "Reply here", methodType = MethodType.REPLY)
    public String testReply(Update update) {
        return "Reply is done!!!";
    }

}
