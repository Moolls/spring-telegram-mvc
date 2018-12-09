package ru.moolls.telemvc;

import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.moolls.telemvc.entity.BeanMethod;
import ru.moolls.telemvc.entity.MethodType;
import ru.moolls.telemvc.entity.RequestMetaData;

@Component
public class HandlingMethodResolver {


  private HashMap<RequestMetaData, BeanMethod> requestMethodMap = new HashMap<>();

  @Autowired
  private void loadRequestMapping(RequestMappingContainer mappingKeeper) {
    requestMethodMap = mappingKeeper.getRequestMethodMap();
  }


  public BeanMethod resolveHandlingMethod(Update messageData) {
    RequestMetaData requestMetaData = prepareRequestMetaData(messageData);
    BeanMethod beanMethod = requestMethodMap.get(requestMetaData);
    return beanMethod;
  }


  private RequestMetaData prepareRequestMetaData(Update messageData) {
    String msg = "";
    String callbackPath = "";
    String replyOn = "";

    MethodType methodType = resolveRequestType(messageData);
    if (methodType == MethodType.MSG) {
      msg = messageData.getMessage().getText();
    } else if (methodType == MethodType.CALLBACK) {
      //TODO Impl. callback path resolving
      callbackPath = messageData.getCallbackQuery().getData();
    } else {
      replyOn = messageData.getMessage().getReplyToMessage().getText();
    }

    RequestMetaData requestMapping = RequestMetaData.builder()
        .callbackPath(callbackPath)
        .methodType(methodType)
        .replyOn(replyOn)
        .msgPath(msg).build();

    return requestMapping;
  }

  private MethodType resolveRequestType(Update messageData) {
    if (messageData.hasCallbackQuery()) {
      return MethodType.CALLBACK;
    } else if (messageData.getMessage().isReply()) {
      return MethodType.REPLY;
    } else {
      return MethodType.MSG;
    }
  }
}
