package com.safziy.agent;

import java.lang.instrument.Instrumentation;

public class TestAgent {
	public static Instrumentation inst = null;
	
	public static void premain(String agentArgument, Instrumentation instrumentation) {
		System.out.println("Test Java Agent");
		
		inst = instrumentation;
	}
}
