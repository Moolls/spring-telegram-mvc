package ru.moolls.telemvc.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@EqualsAndHashCode(of = {"msgPath", "methodType", "replyOn", "methodType"})
public class RequestMetaData {

  private String msgPath;
  private String callbackPath;
  private String replyOn;
  private MethodType methodType;
}
