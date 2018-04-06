package com.ctmconnect;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Handler {
	
	private static final String ENV_PROGRAM_PATH = "MY_PROGRAM";
	private static final String PATH_SEPARATOR = "/";
	private static final String PROGRAM_ID = "MyProgram";
	
	static {
		 System.loadLibrary("mylib");
	 }
	
	/**
	 * Calls intermediate C program that calls mylib translated via GNU COBOL.
	 *
	 * 
	 */
	public static synchronized native String mylib(String request);
	
	public String getResponse(String request){
		String response = "Response test";
		mylib(toASCII(request));
		return response;
	}
	
	public String toASCII(String text) {
		Charset charset = StandardCharsets.US_ASCII;
		ByteBuffer byteBuffer = charset.encode(text);
		return new String(byteBuffer.array(), charset);
	}
}
