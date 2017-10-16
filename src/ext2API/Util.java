package ext2API;

import java.util.ArrayList;
import static java.lang.Math.toIntExact;

public abstract class Util {
	public final static int MASK = 0xff;
	
	public static int byteArrayToInt (byte[] bytes){	
		int x = java.nio.ByteBuffer.wrap(bytes).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();
		return x;
	}
	
	public static String byteArrayToHexString(byte[] bytes){
		StringBuilder sb = new StringBuilder();
    for (byte b : bytes)
        sb.append(String.format("%02X", b));
		return sb.toString();
	}
	
	public static String byteToHexString(byte b){
		StringBuilder sb = new StringBuilder();
    sb.append(String.format("%02X", b));
		return sb.toString();
	}
	
	public static String byteArrayToString(byte[] bytes){
		StringBuilder sb = new StringBuilder();
    for (byte b : bytes)
        sb.append((char) b);
		return sb.toString();
	}
	
	public static short byteArrayToShort (byte[] bytes){
		short x = java.nio.ByteBuffer.wrap(bytes).order(java.nio.ByteOrder.LITTLE_ENDIAN).getShort();
		return x;
	}
	
	public static char byteArrayToChar (byte[] bytes){
		char x = java.nio.ByteBuffer.wrap(bytes).order(java.nio.ByteOrder.LITTLE_ENDIAN).getChar();
		return x;
	}
	
	public static int longToInt(long foo){
		return toIntExact(foo);
	}
	
	public static long byteArrayToLong (byte[] bytes){
		long x = java.nio.ByteBuffer.wrap(bytes).order(java.nio.ByteOrder.LITTLE_ENDIAN).getLong();
		return x;
	}
	
	public static int byteToInt(byte b){
		return MASK &b;
	}
	
	public static String get_file_name_from_filePath(String file_path, ArrayList<Integer> key_points){
		return file_path.substring(key_points.get(key_points.size()-2)+1, key_points.get(key_points.size()-1));
	}
	
	public static String get_file_name_from_filePath_Ext2(String file_path){
		int dot_index = 0;
		for (int i = file_path.length()-1; i >=0; i--)
			if (file_path.charAt(i) == '/'){
				dot_index = i+1;
				break;
			}
		return file_path.substring(dot_index, file_path.length());
	}
}
