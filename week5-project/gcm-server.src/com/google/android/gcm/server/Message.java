package com.google.android.gcm.server;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class Message
  implements Serializable
{
  private final String collapseKey;
  private final Boolean delayWhileIdle;
  private final Integer timeToLive;
  private final Map<String, String> data;

  private Message(Builder builder)
  {
    this.collapseKey = builder.collapseKey;
    this.delayWhileIdle = builder.delayWhileIdle;
    this.data = Collections.unmodifiableMap(builder.data);
    this.timeToLive = builder.timeToLive;
  }

  public String getCollapseKey()
  {
    return this.collapseKey;
  }

  public Boolean isDelayWhileIdle()
  {
    return this.delayWhileIdle;
  }

  public Integer getTimeToLive()
  {
    return this.timeToLive;
  }

  public Map<String, String> getData()
  {
    return this.data;
  }

  public String toString()
  {
    StringBuilder builder = new StringBuilder("Message(");
    if (this.collapseKey != null) {
      builder.append("collapseKey=").append(this.collapseKey).append(", ");
    }
    if (this.timeToLive != null) {
      builder.append("timeToLive=").append(this.timeToLive).append(", ");
    }
    if (this.delayWhileIdle != null) {
      builder.append("delayWhileIdle=").append(this.delayWhileIdle).append(", ");
    }
    if (!this.data.isEmpty()) {
      builder.append("data: {");
      for (Map.Entry entry : this.data.entrySet()) {
        builder.append((String)entry.getKey()).append("=").append((String)entry.getValue()).append(",");
      }

      builder.delete(builder.length() - 1, builder.length());
      builder.append("}");
    }
    if (builder.charAt(builder.length() - 1) == ' ') {
      builder.delete(builder.length() - 2, builder.length());
    }
    builder.append(")");
    return builder.toString();
  }

  public static final class Builder
  {
    private final Map<String, String> data;
    private String collapseKey;
    private Boolean delayWhileIdle;
    private Integer timeToLive;

    public Builder()
    {
      this.data = new LinkedHashMap();
    }

    public Builder collapseKey(String value)
    {
      this.collapseKey = value;
      return this;
    }

    public Builder delayWhileIdle(boolean value)
    {
      this.delayWhileIdle = Boolean.valueOf(value);
      return this;
    }

    public Builder timeToLive(int value)
    {
      this.timeToLive = Integer.valueOf(value);
      return this;
    }

    public Builder addData(String key, String value)
    {
      this.data.put(key, value);
      return this;
    }

    public Message build() {
      return new Message(this, null);
    }
  }
}