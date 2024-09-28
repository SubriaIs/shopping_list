package com.team.e.exceptions;

import lombok.Getter;

@Getter
public class SLServiceException extends RuntimeException {
  private int httpRequestCode;
  private String error;

  public SLServiceException(String message) {
    this(message, 500);

  }

  public SLServiceException(String message, int httpStatusCode) {

    this(message,httpStatusCode,"Internal Error");
  }

  public SLServiceException(String message, int httpStatusCode, String errorMessage) {
    super(message);
    httpRequestCode = httpStatusCode;
    error = errorMessage;
  }
}