package com.distributed.seckill.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 哈希工具类。
 */
public class HashUtil {
  public static String sha256(String input) {
    try {
      // 使用 SHA-256 计算摘要
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hashed = digest.digest(input.getBytes(StandardCharsets.UTF_8));
      StringBuilder builder = new StringBuilder();
      for (byte value : hashed) {
        // 每个字节转 2 位十六进制字符串
        String hex = Integer.toHexString(0xff & value);
        if (hex.length() == 1) {
          builder.append('0');
        }
        builder.append(hex);
      }
      return builder.toString();
    } catch (NoSuchAlgorithmException ex) {
      // JDK 内置算法理论上总是可用，异常时按不可恢复错误处理
      throw new IllegalStateException("HASH_ERROR");
    }
  }
}
