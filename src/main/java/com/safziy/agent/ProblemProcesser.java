package com.safziy.agent;

import java.io.PrintStream;

public class ProblemProcesser
{
  public String execute()
  {
    long cur = System.currentTimeMillis();
    System.err.println(cur);

    return "返回处理结果";
  }
}
