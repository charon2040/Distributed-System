package com.distributed.seckill.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
/**
 * 为每个响应写入实例标识，便于负载均衡验证时观察请求落点。
 */
public class InstanceHeaderFilter extends OncePerRequestFilter {
  private final String instanceName;

  public InstanceHeaderFilter(@Value("${INSTANCE_NAME:backend}") String instanceName) {
    this.instanceName = instanceName;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {
    // 在响应头中附带当前后端实例名
    response.setHeader("X-Instance", instanceName);
    filterChain.doFilter(request, response);
  }
}
