package com.google.gson;

import com.google.gson.internal..Gson.Preconditions;
import com.google.gson.internal.StringMap;
import java.util.Map.Entry;
import java.util.Set;

public final class JsonObject extends JsonElement
{
  private final StringMap<JsonElement> members = new StringMap();

  public void add(String property, JsonElement value)
  {
    if (value == null) {
      value = JsonNull.INSTANCE;
    }
    this.members.put((String).Gson.Preconditions.checkNotNull(property), value);
  }

  public JsonElement remove(String property)
  {
    return (JsonElement)this.members.remove(property);
  }

  public void addProperty(String property, String value)
  {
    add(property, createJsonElement(value));
  }

  public void addProperty(String property, Number value)
  {
    add(property, createJsonElement(value));
  }

  public void addProperty(String property, Boolean value)
  {
    add(property, createJsonElement(value));
  }

  public void addProperty(String property, Character value)
  {
    add(property, createJsonElement(value));
  }

  private JsonElement createJsonElement(Object value)
  {
    return value == null ? JsonNull.INSTANCE : new JsonPrimitive(value);
  }

  public Set<Map.Entry<String, JsonElement>> entrySet()
  {
    return this.members.entrySet();
  }

  public boolean has(String memberName)
  {
    return this.members.containsKey(memberName);
  }

  public JsonElement get(String memberName)
  {
    if (this.members.containsKey(memberName)) {
      JsonElement member = (JsonElement)this.members.get(memberName);
      return member == null ? JsonNull.INSTANCE : member;
    }
    return null;
  }

  public JsonPrimitive getAsJsonPrimitive(String memberName)
  {
    return (JsonPrimitive)this.members.get(memberName);
  }

  public JsonArray getAsJsonArray(String memberName)
  {
    return (JsonArray)this.members.get(memberName);
  }

  public JsonObject getAsJsonObject(String memberName)
  {
    return (JsonObject)this.members.get(memberName);
  }

  public boolean equals(Object o)
  {
    return (o == this) || (((o instanceof JsonObject)) && (((JsonObject)o).members.equals(this.members)));
  }

  public int hashCode()
  {
    return this.members.hashCode();
  }
}