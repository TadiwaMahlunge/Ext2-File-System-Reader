package ext2API;

public class OutputHelper {
	/**
	 * This will print all the input bytes as a standard format Hex dump.
	 * @param bytes This is the array of bytes one would like to print.
	 */
	void dumpHexBytes(byte[] bytes){
		System.out.println(" ------ HEX DUMP -----");
		int length = bytes.length;
		int reached = 0;
		int condition = 1;
		while (condition != 0){
			condition = print_line(bytes , reached , (length-reached)); 
			reached += condition;
		}
		int final_reached_value = reached;
		System.out.printf("\n");
		for (int i = reached ; i < length; i++){
			if (i == 8)
				System.out.printf("  ");
			System.out.printf("%2s ", Util.byteToHexString(bytes[i]));
			reached++;
		}
		
		while (reached % 16 !=0){
			if (reached % 8 == 0)
				System.out.printf("  ");
			System.out.printf("%2s ", "XX");
			reached++;
		}
		
		for (int j = final_reached_value ; j < length ; j++){
			if (j % 8 == 0)
				System.out.printf("  ");		
			if (Util.byteToHexString(bytes[j]).equals("0A"))
				System.out.printf("%2c", ' ');
			else
				System.out.printf("%2c ", (char)(bytes[j]));
		}
	}
	
	private int print_line (byte[] bytes , int i, int left){
		System.out.printf("\n");
		int condition = (left < 16) ? 0 : 16 ;
		for (int j = 0 ; j < condition ; j++){
			if (j == 8)
				System.out.printf("  ");
			System.out.printf("%2s ", Util.byteToHexString(bytes[i+j]));
		}
		for (int j = 0 ; j < condition ; j++){
			if (j == 8 || j == 0)
				System.out.printf("  ");		
			if (Util.byteToHexString(bytes[i+j]).equals("0A"))
				System.out.printf("%2c ", ' ');
			else
				System.out.printf("%2c ", (char) Util.byteToInt(bytes[i+j]));
		}
		return condition;
	}
}
