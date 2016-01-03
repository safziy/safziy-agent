package com.safziy.agent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.instrument.UnmodifiableClassException;

import org.apache.log4j.Logger;

public class ClassReloaderManager {
	private static final Logger log = Logger.getLogger(ClassReloaderManager.class);

	private String classPath = "/usr/local/services/reload/";

	private static ClassReloaderManager instance = new ClassReloaderManager();

	public static ClassReloaderManager getInstance() {
		return instance;
	}

	private ClassReloaderManager() {
		if (System.getProperty("hlwl.reloadPath") != null)
			this.classPath = System.getProperty("hlwl.reloadPath");
	}

	public String reloadClass() {
		String res = "";
		try {
			String[] classNames = ClassHelper.getFullyQualifiedClassNamesFromDir(this.classPath);

			if (classNames == null) {
				res = "热加载代码时,找不到文件!，请检查启动参数-Dhlwl.reloadPath是否正确";
				log.error(res);
			} else {
				String[] arrayOfString1;
				int j = (arrayOfString1 = classNames).length;
				for (int i = 0; i < j; i++) {
					String name = arrayOfString1[i];

					String className = name.replaceAll("/", "\\.");
					String fileName = className.replaceAll(".+\\.", "");

					File classFile = new File(this.classPath + File.separator + fileName + ".class");

					if (classFile.exists()) {
						res = res + reload(classFile, className) + "\r\n";
					}
				}

				if (res.contains("ProblemProcesser"))
					try {
						res = res + "\r\nProblemProcesser: " + new ProblemProcesser().execute();
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
		} catch (FileNotFoundException e) {
			log.error("热加载代码时,找不到文件!", e);
			res = res + e.getMessage();
		} catch (IOException e) {
			log.error("热加载代码时,读取文件出错!", e);
			res = res + e.getMessage();
		}

		System.err.println(res);

		return res;
	}

	@SuppressWarnings("rawtypes")
	private String reload(File file, String className) {
		String error;
		try {
			Class cls = Class.forName(className);
			return ComplexClassLoader.reload(cls, file);
		} catch (IOException e) {
			log.error("热加载代码时,加载文件出错!", e);
			error = e.getMessage();
		} catch (UnmodifiableClassException e) {
			log.error("热加载代码时,加载代码出错!", e);
			error = e.getMessage();
		} catch (ClassNotFoundException e) {
			log.error("热加载代码时,找不到类!", e);
			error = e.getMessage();
		}

		return error;
	}
}
