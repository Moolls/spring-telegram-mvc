package ru.moolls.telemvc;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.moolls.telemvc.annotation.BotController;
import ru.moolls.telemvc.annotation.BotMapping;
import ru.moolls.telemvc.entity.BeanMethod;
import ru.moolls.telemvc.entity.MethodType;
import ru.moolls.telemvc.entity.RequestMetaData;
import ru.moolls.telemvc.exception.IncompatibleMethod;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class HandlerMethodMapping implements InitializingBean, ApplicationContextAware {

    private ApplicationContext ctx;
    private HashMap<RequestMetaData, BeanMethod> requestMethodMap = new HashMap<>();


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.ctx = applicationContext;
    }

    @Override
    public void afterPropertiesSet() {
        Map<String, Object> controllerBeans = getControllerBeans();
        Collection<Object> controllerObjects = controllerBeans.values();
        controllerObjects.forEach(this::putControllerMappings);
    }

    private void putControllerMappings(Object controllerObject) {
        Method[] methods = controllerObject.getClass().getMethods();
        Arrays.stream(methods)
                .filter(method -> method.isAnnotationPresent(BotMapping.class))
                .forEach(method -> {
                    BotMapping botMappingAnnotation = method.getAnnotation(BotMapping.class);
                    if (!isMethodHasUpdateParam(method)) {
                        throw new IncompatibleMethod("Method " + method.getName() + " doesn't have Update param.");
                    }
                    RequestMetaData requestMapping = prepareRequestMetaData(botMappingAnnotation);
                    putMapping(requestMapping, controllerObject, method);
                });
    }

    private Map<String, Object> getControllerBeans() {
        Map<String, Object> beansWithAnnotation = ctx.getBeansWithAnnotation(BotController.class);
        return beansWithAnnotation;
    }

    private boolean isMethodHasUpdateParam(Method method) {
        Parameter[] methodParam = method.getParameters();
        return methodParam.length == 1 && methodParam[0].getType() == Update.class;
    }

    private RequestMetaData prepareRequestMetaData(BotMapping botMappingAnnotation) {
        MethodType methodType = botMappingAnnotation.methodType();
        String msgPath = botMappingAnnotation.msgPath();
        String callbackPath = botMappingAnnotation.callbackPath();
        String replyOn = botMappingAnnotation.replyOn();

        return buildRequestDataMapping(methodType, msgPath, callbackPath, replyOn);
    }

    private void putMapping(RequestMetaData requestMapping, Object bean, Method method) {
        BeanMethod beanMethod = BeanMethod.builder()
                .object(bean)
                .method(method).build();

        requestMethodMap.put(requestMapping, beanMethod);

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
