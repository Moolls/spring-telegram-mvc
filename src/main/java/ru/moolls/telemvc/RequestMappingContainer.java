package ru.moolls.telemvc;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.moolls.telemvc.annotation.BotController;
import ru.moolls.telemvc.annotation.BotMapping;
import ru.moolls.telemvc.entity.BeanMethod;
import ru.moolls.telemvc.entity.MethodType;
import ru.moolls.telemvc.entity.RequestMetaData;


@Component
public class RequestMappingContainer implements ApplicationListener<ApplicationReadyEvent> {

  private HashMap<RequestMetaData, BeanMethod> requestMethodMap = new HashMap<>();

  HashMap<RequestMetaData, BeanMethod> getRequestMethodMap() {
    return requestMethodMap;
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    ConfigurableApplicationContext ctx = event.getApplicationContext();
    Map<String, Object> beansWithAnnotation = ctx.getBeansWithAnnotation(BotController.class);

    for (String s : beansWithAnnotation.keySet()) {
      Object bean = beansWithAnnotation.get(s);
      for (Method method : bean.getClass().getMethods()) {
        if (method.isAnnotationPresent(BotMapping.class)) {
          BotMapping botMappingAnnotation = method.getAnnotation(BotMapping.class);
          if (!isMethodHasUpdateParam(method)) {
            throw new RuntimeException(
                "Method " + method.getName() + " doesn't have Update param.");
          }
          RequestMetaData requestMapping = prepareRequestMetaData(botMappingAnnotation);
          putRequestMethod(requestMapping, bean, method);
        }
      }
    }

  }

  private RequestMetaData prepareRequestMetaData(BotMapping botMappingAnnotation) {
    MethodType methodType = botMappingAnnotation.methodType();
    String msgPath = botMappingAnnotation.msgPath();
    String callbackPath = botMappingAnnotation.callbackPath();
    String replyOn = botMappingAnnotation.replyOn();

    return buildRequestDataMapping(methodType, msgPath, callbackPath, replyOn);
  }

  private void putRequestMethod(RequestMetaData requestMapping, Object bean, Method method) {
    BeanMethod beanMethod = BeanMethod.builder()
        .object(bean)
        .method(method).build();

    requestMethodMap.put(requestMapping, beanMethod);

  }

  private boolean isMethodHasUpdateParam(Method method) {
    Parameter[] methodParam = method.getParameters();
    return methodParam.length == 1 && methodParam[0].getType() == Update.class;
  }


  private RequestMetaData buildRequestDataMapping(MethodType methodType, String msg,
      String callbackPath, String replyOn) {

    return RequestMetaData.builder()
        .methodType(methodType)
        .msgPath(msg)
        .replyOn(replyOn)
        .callbackPath(callbackPath)
        .build();
  }


}
