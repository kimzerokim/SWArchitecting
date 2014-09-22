package com.google.android.gcm.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Sender
{
  protected static final String UTF8 = "UTF-8";
  protected static final int BACKOFF_INITIAL_DELAY = 1000;
  protected static final int MAX_BACKOFF_DELAY = 1024000;
  protected final Random random = new Random();
  protected final Logger logger = Logger.getLogger(getClass().getName());
  private final String key;

  public Sender(String key)
  {
    this.key = ((String)nonNull(key));
  }

  public Result send(Message message, String registrationId, int retries)
    throws IOException
  {
    int attempt = 0;
    Result result = null;
    int backoff = 1000;
    boolean tryAgain;
    do
    {
      attempt++;
      if (this.logger.isLoggable(Level.FINE)) {
        this.logger.fine("Attempt #" + attempt + " to send message " + message + " to regIds " + registrationId);
      }

      result = sendNoRetry(message, registrationId);
      tryAgain = (result == null) && (attempt <= retries);
      if (tryAgain) {
        int sleepTime = backoff / 2 + this.random.nextInt(backoff);
        sleep(sleepTime);
        if (2 * backoff < 1024000)
          backoff *= 2;
      }
    }
    while (tryAgain);
    if (result == null) {
      throw new IOException("Could not send message after " + attempt + " attempts");
    }

    return result;
  }

  public Result sendNoRetry(Message message, String registrationId)
    throws IOException
  {
    StringBuilder body = newBody("registration_id", registrationId);
    Boolean delayWhileIdle = message.isDelayWhileIdle();
    if (delayWhileIdle != null) {
      addParameter(body, "delay_while_idle", delayWhileIdle.booleanValue() ? "1" : "0");
    }
    String collapseKey = message.getCollapseKey();
    if (collapseKey != null) {
      addParameter(body, "collapse_key", collapseKey);
    }
    Integer timeToLive = message.getTimeToLive();
    if (timeToLive != null) {
      addParameter(body, "time_to_live", Integer.toString(timeToLive.intValue()));
    }
    for (Map.Entry entry : message.getData().entrySet()) {
      String key = "data." + (String)entry.getKey();
      String value = (String)entry.getValue();
      addParameter(body, key, URLEncoder.encode(value, "UTF-8"));
    }
    String requestBody = body.toString();
    this.logger.finest("Request body: " + requestBody);
    HttpURLConnection conn = post("https://android.googleapis.com/gcm/send", requestBody);
    int status = conn.getResponseCode();
    if (status == 503) {
      this.logger.fine("GCM service is unavailable");
      return null;
    }
    if (status != 200)
      throw new InvalidRequestException(status);
    try
    {
      BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      try
      {
        String line = reader.readLine();

        if ((line == null) || (line.equals(""))) {
          throw new IOException("Received empty response from GCM service.");
        }
        String[] responseParts = split(line);
        String token = responseParts[0];
        String value = responseParts[1];
        Result.Builder builder;
        if (token.equals("id")) {
          builder = new Result.Builder().messageId(value);

          line = reader.readLine();
          if (line != null) {
            responseParts = split(line);
            token = responseParts[0];
            value = responseParts[1];
            if (token.equals("registration_id"))
              builder.canonicalRegistrationId(value);
            else {
              this.logger.warning("Received invalid second line from GCM: " + line);
            }
          }

          Result result = builder.build();
          if (this.logger.isLoggable(Level.FINE)) {
            this.logger.fine("Message created succesfully (" + result + ")");
          }
          return result;
        }if (token.equals("Error")) {
          return new Result.Builder().errorCode(value).build();
        }
        throw new IOException("Received invalid response from GCM: " + line);
      }
      finally {
      }
    }
    finally {
      conn.disconnect();
    }
  }

  public MulticastResult send(Message message, List<String> regIds, int retries)
    throws IOException
  {
    int attempt = 0;
    MulticastResult multicastResult = null;
    int backoff = 1000;

    Map results = new HashMap();
    List unsentRegIds = new ArrayList(regIds);

    List multicastIds = new ArrayList();
    boolean tryAgain;
    do
    {
      attempt++;
      if (this.logger.isLoggable(Level.FINE)) {
        this.logger.fine("Attempt #" + attempt + " to send message " + message + " to regIds " + unsentRegIds);
      }

      multicastResult = sendNoRetry(message, unsentRegIds);
      long multicastId = multicastResult.getMulticastId();
      this.logger.fine("multicast_id on attempt # " + attempt + ": " + multicastId);

      multicastIds.add(Long.valueOf(multicastId));
      unsentRegIds = updateStatus(unsentRegIds, results, multicastResult);
      tryAgain = (!unsentRegIds.isEmpty()) && (attempt <= retries);
      if (tryAgain) {
        int sleepTime = backoff / 2 + this.random.nextInt(backoff);
        sleep(sleepTime);
        if (2 * backoff < 1024000)
          backoff *= 2;
      }
    }
    while (tryAgain);

    int success = 0; int failure = 0; int canonicalIds = 0;
    for (Result result : results.values()) {
      if (result.getMessageId() != null) {
        success++;
        if (result.getCanonicalRegistrationId() != null)
          canonicalIds++;
      }
      else {
        failure++;
      }
    }

    long multicastId = ((Long)multicastIds.remove(0)).longValue();
    MulticastResult.Builder builder = new MulticastResult.Builder(success, failure, canonicalIds, multicastId).retryMulticastIds(multicastIds);

    for (String regId : regIds) {
      Result result = (Result)results.get(regId);
      builder.addResult(result);
    }
    return builder.build();
  }

  private List<String> updateStatus(List<String> unsentRegIds, Map<String, Result> allResults, MulticastResult multicastResult)
  {
    List results = multicastResult.getResults();
    if (results.size() != unsentRegIds.size())
    {
      throw new RuntimeException("Internal error: sizes do not match. currentResults: " + results + "; unsentRegIds: " + unsentRegIds);
    }

    List newUnsentRegIds = new ArrayList();
    for (int i = 0; i < unsentRegIds.size(); i++) {
      String regId = (String)unsentRegIds.get(i);
      Result result = (Result)results.get(i);
      allResults.put(regId, result);
      String error = result.getErrorCodeName();
      if ((error != null) && (error.equals("Unavailable"))) {
        newUnsentRegIds.add(regId);
      }
    }
    return newUnsentRegIds;
  }

  public MulticastResult sendNoRetry(Message message, List<String> registrationIds)
    throws IOException
  {
    if (((List)nonNull(registrationIds)).isEmpty()) {
      throw new IllegalArgumentException("registrationIds cannot be empty");
    }
    Map jsonRequest = new HashMap();
    setJsonField(jsonRequest, "time_to_live", message.getTimeToLive());
    setJsonField(jsonRequest, "collapse_key", message.getCollapseKey());
    setJsonField(jsonRequest, "delay_while_idle", message.isDelayWhileIdle());

    jsonRequest.put("registration_ids", registrationIds);
    Map payload = message.getData();
    if (!payload.isEmpty()) {
      jsonRequest.put("data", payload);
    }
    String requestBody = JSONValue.toJSONString(jsonRequest);
    this.logger.finest("JSON request: " + requestBody);
    HttpURLConnection conn = post("https://android.googleapis.com/gcm/send", "application/json", requestBody);

    int status = conn.getResponseCode();
    String responseBody = getString(conn.getInputStream());
    this.logger.finest("JSON response: " + responseBody);
    if (status != 200) {
      throw new InvalidRequestException(status, responseBody);
    }
    JSONParser parser = new JSONParser();
    try
    {
      JSONObject jsonResponse = (JSONObject)parser.parse(responseBody);
      int success = getNumber(jsonResponse, "success").intValue();
      int failure = getNumber(jsonResponse, "failure").intValue();
      int canonicalIds = getNumber(jsonResponse, "canonical_ids").intValue();
      long multicastId = getNumber(jsonResponse, "multicast_id").longValue();
      MulticastResult.Builder builder = new MulticastResult.Builder(success, failure, canonicalIds, multicastId);

      List results = (List)jsonResponse.get("results");

      if (results != null) {
        for (Map jsonResult : results) {
          String messageId = (String)jsonResult.get("message_id");
          String canonicalRegId = (String)jsonResult.get("registration_id");

          String error = (String)jsonResult.get("error");
          Result result = new Result.Builder().messageId(messageId).canonicalRegistrationId(canonicalRegId).errorCode(error).build();

          builder.addResult(result);
        }
      }
      return builder.build();
    }
    catch (ParseException e) {
      throw newIoException(responseBody, e);
    } catch (CustomParserException e) {
      throw newIoException(responseBody, e);
    }
  }

  private IOException newIoException(String responseBody, Exception e)
  {
    String msg = "Error parsing JSON response (" + responseBody + ")";
    this.logger.log(Level.WARNING, msg, e);
    return new IOException(msg + ":" + e);
  }

  private void setJsonField(Map<Object, Object> json, String field, Object value)
  {
    if (value != null)
      json.put(field, value);
  }

  private Number getNumber(Map<?, ?> json, String field)
  {
    Object value = json.get(field);
    if (value == null) {
      throw new CustomParserException("Missing field: " + field);
    }
    if (!(value instanceof Number)) {
      throw new CustomParserException("Field " + field + " does not contain a number: " + value);
    }

    return (Number)value;
  }

  private String[] split(String line)
    throws IOException
  {
    String[] split = line.split("=", 2);
    if (split.length != 2) {
      throw new IOException("Received invalid response line from GCM: " + line);
    }
    return split;
  }

  protected HttpURLConnection post(String url, String body)
    throws IOException
  {
    return post(url, "application/x-www-form-urlencoded;charset=UTF-8", body);
  }

  protected HttpURLConnection post(String url, String contentType, String body) throws IOException
  {
    if ((url == null) || (body == null)) {
      throw new IllegalArgumentException("arguments cannot be null");
    }
    if (!url.startsWith("https://")) {
      this.logger.warning("URL does not use https: " + url);
    }
    this.logger.fine("Sending POST to " + url);
    this.logger.finest("POST body: " + body);
    byte[] bytes = body.getBytes();
    HttpURLConnection conn = getConnection(url);
    conn.setDoOutput(true);
    conn.setUseCaches(false);
    conn.setFixedLengthStreamingMode(bytes.length);
    conn.setRequestMethod("POST");
    conn.setRequestProperty("Content-Type", contentType);
    conn.setRequestProperty("Authorization", "key=" + this.key);
    OutputStream out = conn.getOutputStream();
    out.write(bytes);
    out.close();
    return conn;
  }

  protected static final Map<String, String> newKeyValues(String key, String value)
  {
    Map keyValues = new HashMap(1);
    keyValues.put(nonNull(key), nonNull(value));
    return keyValues;
  }

  protected static StringBuilder newBody(String name, String value)
  {
    return new StringBuilder((String)nonNull(name)).append('=').append((String)nonNull(value));
  }

  protected static void addParameter(StringBuilder body, String name, String value)
  {
    ((StringBuilder)nonNull(body)).append('&').append((String)nonNull(name)).append('=').append((String)nonNull(value));
  }

  protected HttpURLConnection getConnection(String url)
    throws IOException
  {
    HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
    return conn;
  }

  protected static String getString(InputStream stream)
    throws IOException
  {
    BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream)nonNull(stream)));

    StringBuilder content = new StringBuilder();
    String newLine;
    do
    {
      newLine = reader.readLine();
      if (newLine != null)
        content.append(newLine).append('\n');
    }
    while (newLine != null);
    if (content.length() > 0)
    {
      content.setLength(content.length() - 1);
    }
    return content.toString();
  }

  static <T> T nonNull(T argument) {
    if (argument == null) {
      throw new IllegalArgumentException("argument cannot be null");
    }
    return argument;
  }

  void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  class CustomParserException extends RuntimeException
  {
    CustomParserException(String message)
    {
      super();
    }
  }
}