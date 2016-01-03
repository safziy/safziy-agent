package com.safziy.agent;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ComplexClassLoader
{
  public static Instrumentation inst = null;

  static
  {
    System.out.println("ComplexClassLoader");
  }

  public static void premain(String agentArgs, Instrumentation ins)
  {
    inst = ins;
  }

  public static String reload(Class<?> cls, File file)
    throws IOException, ClassNotFoundException, UnmodifiableClassException
  {
    byte[] code = loadBytes(cls, file);
    if (code == null) {
      throw new IOException("Unknown File");
    }
    ClassDefinition def = new ClassDefinition(cls, code);
    inst.redefineClasses(new ClassDefinition[] { def });

    return cls.getName() + " reloaded";
  }

  private static byte[] loadBytes(Class<?> cls, File file) throws IOException, ClassNotFoundException {
    String name = file.getName();
    if (name.endsWith(".jar"))
      return loadBytesFromJarFile(cls, file);
    if (name.endsWith(".class")) {
      return loadBytesFromClassFile(file);
    }
    return null;
  }

  private static byte[] loadBytesFromClassFile(File classFile) throws IOException {
    byte[] buffer = new byte[(int)classFile.length()];
    FileInputStream fis = new FileInputStream(classFile);
    BufferedInputStream bis = new BufferedInputStream(fis);
    try {
      bis.read(buffer);
    } catch (IOException e) {
      throw e;
    } finally {
      try {
        bis.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    return buffer;
  }

  private static byte[] loadBytesFromJarFile(Class<?> cls, File file) throws IOException, ClassNotFoundException {
    JarFile jarFile = new JarFile(file);
    String name = cls.getName();
    name = name.replaceAll("\\.", "/") + ".class";
    JarEntry en = jarFile.getJarEntry(name);
    if (en == null)
      throw new ClassNotFoundException(name);
    byte[] buffer = new byte[(int)en.getSize()];
    BufferedInputStream bis = new BufferedInputStream(jarFile.getInputStream(en));
    try {
      bis.read(buffer);
    } catch (IOException e) {
      throw e;
    } finally {
      try {
        bis.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return buffer;
  }
}
