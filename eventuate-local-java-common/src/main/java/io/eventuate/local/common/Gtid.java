package io.eventuate.local.common;

import org.apache.commons.lang.builder.EqualsBuilder;

import java.util.Objects;

public class Gtid {
  private String value;

  public Gtid() {
  }

  public Gtid(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public long getLastId() {
    String id = value.split(":")[1];

    if (id.contains("-")) {
      id = id.split("-")[1];
    }

    return Long.parseLong(id);
  }

  @Override
  public boolean equals(Object o) {
    return EqualsBuilder.reflectionEquals(this, o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return value;
  }
}
