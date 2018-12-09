package ru.moolls.telemvc.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

@Builder
@Getter
@Setter
@EqualsAndHashCode(of = {"object", "method"})
public class BeanMethod {

    private Object object;
    private Method method;

    public Object invoke(Update messageDate)
            throws InvocationTargetException, IllegalAccessException {

        if (isMethodHasModelParam(method)) {
            Model requestModel = new Model();
            return method.invoke(object, messageDate, requestModel);
        }
        return method.invoke(object, messageDate);
    }


    private boolean isMethodHasModelParam(Method method) {
        return Arrays.stream(method.getParameterTypes())
                .anyMatch(aClass -> aClass == Model.class);
    }

}
