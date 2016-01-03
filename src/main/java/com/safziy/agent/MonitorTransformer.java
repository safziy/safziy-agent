package com.safziy.agent;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

public class MonitorTransformer implements ClassFileTransformer {

	final static String prefix = "long startTime = System.currentTimeMillis();\n";
	final static String postfix = "long endTime = System.currentTimeMillis();\n";

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		// 只监测自定义的类,系统类和第三方类 不检测
		if (className.startsWith("com/safziy")) {
			// 将'/'替换为'.' 比如com/safziy/hotswap/HotSwap 替换为
			// com.safziy.hotswap.HotSwap
			className = className.replaceAll("/", ".");
			CtClass ctClass = null;
			try {
				// 用于取得字节码类，必须在当前的classpath中，使用全称 ,这部分是关于javassist的知识
				ctClass = ClassPool.getDefault().get(className);
				// 循环一下，看看哪些方法需要加时间监测
				for (CtMethod ctMethod : ctClass.getMethods()) {
					// 只监测本类中的方法 从父类继承的方法也不需要监测
					if (ctMethod.getLongName().startsWith(className)) {
						// 获取方法名
						String methodName = ctMethod.getName();
						// 新定义一个方法叫做比如sayHello$impl
						String newMethodName = methodName + "$impl";
						// 原来的方法改个名字
						ctMethod.setName(newMethodName);
						// 创建新的方法，复制原来的方法 ，名字为原来的名字
						CtMethod newCtMethod = CtNewMethod.copy(ctMethod, methodName, ctClass, null);
						// 构建新的方法体
						StringBuilder bodyStr = new StringBuilder();
						bodyStr.append("{\n");
						bodyStr.append(prefix);
						// 调用原有代码，类似于method();($$)表示所有的参数
						bodyStr.append(newMethodName + "($$);\n");
						bodyStr.append(postfix);
						// 输出方法调用时间
						String outputStr = "System.out.println(\"this method " + methodName
								+ " cost:\" +(endTime - startTime) +\"ms.\");";
						bodyStr.append(outputStr);
						bodyStr.append("\n}");

						// 替换新方法
						newCtMethod.setBody(bodyStr.toString());
						// 增加新方法
						ctClass.addMethod(newCtMethod);
					}
				}
				return ctClass.toBytecode(); 
			} catch (NotFoundException e) {
				e.printStackTrace();
			} catch (CannotCompileException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
