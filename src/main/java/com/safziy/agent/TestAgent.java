package com.safziy.agent;

import java.lang.instrument.Instrumentation;

public class TestAgent {
	public static void premain(String agentArgument, Instrumentation instrumentation) {
		System.out.println("Test Java Agent");
	}
}
