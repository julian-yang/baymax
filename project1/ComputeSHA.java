// UCLA CS144
// Nathan Tung (004-059-195)

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ComputeSHA {
	
	public static void main(String[] args) {
		
		// set up file path and file contents strings
		String filePath = "";
		String contents = "";
		String hash = null;
		byte[] data = null;
		
		// check if file path is provided as argument
		if(args.length>0) {
			filePath = args[0];
		}
		else {
			System.out.println("Error: missing arguments!");
			return;
		}
		
		// convert file to byte array (instead of using BufferedReader to read each line into contents variable)
		// catch all exceptions
		try {
			
			/*
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			
			// while reader's next line is still non-null, concat it into contents
			// we need to use read() instead of readLine(), since the latter ignores last line \n (treats it solely as EOF)
						
			int value = 0;
			for(value = reader.read(); value!=-1; value = reader.read()) {
				char c = (char)value;
				contents+=c;
			}
			
			reader.close();
			*/
			
			Path path = Paths.get(filePath);
			data = Files.readAllBytes(path);
			
		} catch (FileNotFoundException e) {
			System.out.println("Error: no such file found!");
			return;
		} catch (IOException e) {
			System.out.println("Error: couldn't read from file!");
			return;
		}
		
		// create MessageDigest object for generating SHA1 from contents string
		try {
			MessageDigest md = MessageDigest.getInstance("SHA1");

			// convert md.digest() byte array output to hex string
			hash = hexFromByteArray(md.digest(data)); //hash = hexFromByteArray(md.digest(contents.getBytes()));
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Error: SHA1 algorithm doesn't exist!");
			return;
		}
		
		if(hash!=null) {
			System.out.println(hash);
		}
		
	}
	
	// use String.format() and StringBuilder to convert and append bytes as hex
	// used to convert md.digest() byte array result into hex string
	public static String hexFromByteArray(byte[] arr) {
		final StringBuilder sb = new StringBuilder();
		
		for(byte b : arr)
			sb.append(String.format("%02x", b));
		
		return sb.toString();
	}
}
