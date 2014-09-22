package com.google.android.gcm.server;

import java.io.Serializable;

public final class Result
  implements Serializable
{
  private final String messageId;
  private final String canonicalRegistrationId;
  private final String errorCode;

  private Result(Builder builder)
  {
    this.canonicalRegistrationId = builder.canonicalRegistrationId;
    this.messageId = builder.messageId;
    this.errorCode = builder.errorCode;
  }

  public String getMessageId()
  {
    return this.messageId;
  }

  public String getCanonicalRegistrationId()
  {
    return this.canonicalRegistrationId;
  }

  public String getErrorCodeName()
  {
    return this.errorCode;
  }

  public String toString()
  {
    StringBuilder builder = new StringBuilder("[");
    if (this.messageId != null) {
      builder.append(" messageId=").append(this.messageId);
    }
    if (this.canonicalRegistrationId != null) {
      builder.append(" canonicalRegistrationId=").append(this.canonicalRegistrationId);
    }

    if (this.errorCode != null) {
      builder.append(" errorCode=").append(this.errorCode);
    }
    return " ]";
  }

  static final class Builder
  {
    private String messageId;
    private String canonicalRegistrationId;
    private String errorCode;

    public Builder canonicalRegistrationId(String value)
    {
      this.canonicalRegistrationId = value;
      return this;
    }

    public Builder messageId(String value) {
      this.messageId = value;
      return this;
    }

    public Builder errorCode(String value) {
      this.errorCode = value;
      return this;
    }

    public Result build() {
      return new Result(this, null);
    }
  }
}