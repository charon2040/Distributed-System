package com.distributed.seckill.util;

import org.springframework.stereotype.Component;

@Component
public class SnowflakeIdGenerator {
  private final long machineId = 1L;
  private final long epoch = 1704067200000L;
  private long sequence = 0L;
  private long lastTimestamp = -1L;

  public synchronized long nextId() {
    long current = System.currentTimeMillis();
    if (current < lastTimestamp) {
      current = lastTimestamp;
    }
    if (current == lastTimestamp) {
      sequence = (sequence + 1) & 0xFFF;
      if (sequence == 0) {
        while (current <= lastTimestamp) {
          current = System.currentTimeMillis();
        }
      }
    } else {
      sequence = 0L;
    }
    lastTimestamp = current;
    return ((current - epoch) << 22) | (machineId << 12) | sequence;
  }
}
