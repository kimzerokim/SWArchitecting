package com.google.android.gcm.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MulticastResult
  implements Serializable
{
  private final int success;
  private final int failure;
  private final int canonicalIds;
  private final long multicastId;
  private final List<Result> results;
  private final List<Long> retryMulticastIds;

  private MulticastResult(Builder builder)
  {
    this.success = builder.success;
    this.failure = builder.failure;
    this.canonicalIds = builder.canonicalIds;
    this.multicastId = builder.multicastId;
    this.results = Collections.unmodifiableList(builder.results);
    List tmpList = builder.retryMulticastIds;
    if (tmpList == null) {
      tmpList = Collections.emptyList();
    }
    this.retryMulticastIds = Collections.unmodifiableList(tmpList);
  }

  public long getMulticastId()
  {
    return this.multicastId;
  }

  public int getSuccess()
  {
    return this.success;
  }

  public int getTotal()
  {
    return this.success + this.failure;
  }

  public int getFailure()
  {
    return this.failure;
  }

  public int getCanonicalIds()
  {
    return this.canonicalIds;
  }

  public List<Result> getResults()
  {
    return this.results;
  }

  public List<Long> getRetryMulticastIds()
  {
    return this.retryMulticastIds;
  }

  public String toString()
  {
    StringBuilder builder = new StringBuilder("MulticastResult(").append("multicast_id=").append(this.multicastId).append(",").append("total=").append(getTotal()).append(",").append("success=").append(this.success).append(",").append("failure=").append(this.failure).append(",").append("canonical_ids=").append(this.canonicalIds).append(",");

    if (!this.results.isEmpty()) {
      builder.append("results: " + this.results);
    }
    return builder.toString();
  }

  static final class Builder
  {
    private final List<Result> results = new ArrayList();
    private final int success;
    private final int failure;
    private final int canonicalIds;
    private final long multicastId;
    private List<Long> retryMulticastIds;

    public Builder(int success, int failure, int canonicalIds, long multicastId)
    {
      this.success = success;
      this.failure = failure;
      this.canonicalIds = canonicalIds;
      this.multicastId = multicastId;
    }

    public Builder addResult(Result result) {
      this.results.add(result);
      return this;
    }

    public Builder retryMulticastIds(List<Long> retryMulticastIds) {
      this.retryMulticastIds = retryMulticastIds;
      return this;
    }

    public MulticastResult build() {
      return new MulticastResult(this, null);
    }
  }
}