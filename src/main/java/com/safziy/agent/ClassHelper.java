package com.safziy.agent;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClassHelper {
	public static final int MAGIC = -889275714;

	public static String[] getFullyQualifiedClassNamesFromDir(String path) throws IOException {
		File dir = new File(path);
		List names = new ArrayList();
		try {
			if ((!dir.exists()) || (!dir.isDirectory()))
				return null;
			File[] arrayOfFile;
			int j = (arrayOfFile = dir.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.endsWith(".class");
				}
			})).length;
			for (int i = 0; i < j; i++) {
				File file = arrayOfFile[i];

				String name = null;
				DataInputStream in = new DataInputStream(new FileInputStream(file));
				in.readInt();

				in.readUnsignedShort();
				in.readUnsignedShort();
				in.readUnsignedShort();
				in.readByte();
				in.readUnsignedShort();
				in.readByte();
				name = in.readUTF();
				in.close();

				if (name != null)
					names.add(name);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return (String[]) names.toArray(new String[names.size()]);
	}
}
