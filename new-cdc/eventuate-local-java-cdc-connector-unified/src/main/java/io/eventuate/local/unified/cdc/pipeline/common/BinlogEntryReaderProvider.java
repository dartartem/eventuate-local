package io.eventuate.local.unified.cdc.pipeline.common;

import io.eventuate.local.common.BinlogEntryReader;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BinlogEntryReaderProvider {

  private Map<String, BinlogEntryReader> clients = new HashMap<>();

  public void addReader(String name, BinlogEntryReader reader) {
    clients.put(name.toLowerCase(), reader);
  }

  public BinlogEntryReader getReader(String name) {
    return clients.get(name.toLowerCase());
  }

  public BinlogEntryReader getReaderById(long id) {
    return clients
            .values()
            .stream()
            .filter(reader -> reader.getBinlogClientUniqueId() == id)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(String.format("reader with id %s not found", id)));
  }

  public void start() {
    clients.values().forEach(BinlogEntryReader::start);
  }

  public Collection<BinlogEntryReader> getAllReaders() {
    return clients.values();
  }

  public void stop() {
    clients.values().forEach(BinlogEntryReader::stop);
  }
}
