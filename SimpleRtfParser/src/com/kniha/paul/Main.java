package com.kniha.paul;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

import com.kniha.paul.readers.RtfStreamReader;

public class Main {
	public static void main(String[] args) throws IOException {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter full path of destination directory to save test result\n"
				+ "Example: C:\\\n");

		String filePath = scanner.nextLine();
		testRawParser(filePath);
		System.out.println("OK");

	}

	public static void testRawParser(String folder) throws IOException {
		System.out.println("converting....");
		File tempFile = File.createTempFile("testRtfConverter", ".rtf",
				new File(folder));
//		tempFile.deleteOnExit();
		
		RtfToTextConverter tc = new RtfToTextConverter();

		try (InputStream is = RtfToTextConverter.class.getResourceAsStream("test.rtf");
				OutputStream os = new FileOutputStream(tempFile)){
			tc.convert(new RtfStreamReader(is), os, "UTF-8");
		}
	}

}
