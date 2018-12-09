package ru.moolls.telemvc.entity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Update;

@Builder
@Getter
@Setter
@EqualsAndHashCode(of = {"object", "method"})
public class BeanMethod {

  private Object object;
  private Method method;

  public Object invoke(Update messageDate)
      throws InvocationTargetException, IllegalAccessException {

    Object methodResult = method.invoke(object, messageDate);
    return methodResult;
  }

}
