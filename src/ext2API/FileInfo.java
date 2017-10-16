package ext2API;

public class FileInfo {
	private String[][] values ;
	private int entries;
	private String filepath;
	
	/**
	 * This would be the file information associated with all the contents of a directory.
	 * @param vals This would be an array of all the information associated with the contents of the directory.
	 * @param length This would be the number of entries (vectors) in the array.
	 * @param path This would be the file path to the directory.
	 */
	public FileInfo(String[][] vals, int length, String path){
		values = vals;
		entries = length;
		filepath = path;
	}
	
	/**
	 * This will print the information associated with the directory object.
	 */
	public void display_file_info(){
		if (entries == 0)
			return;
		
  	System.out.printf("%-15s %-15s %-15s %-15s %-15s %-15s %-30s %-20s", "Inode", "Access Mode ", "Hard Links", "Owner ID", "Group ID", "File Size", "Last Accessed", "File Name");
  	for (int i = 0; i < entries; i ++){
  		if (i == entries - 1 )
    	  System.out.printf("\n%-15s %-15s %-15s %-15s %-15s %-15s %-30s %-20s\n",values[0][i], values[1][i], values[2][i], values[3][i], values[4][i], values[5][i], values[6][i], values[7][i]);
  		else
  			System.out.printf("\n%-15s %-15s %-15s %-15s %-15s %-15s %-30s %-20s",values[0][i], values[1][i], values[2][i], values[3][i], values[4][i], values[5][i], values[6][i], values[7][i]);
  	}
  	System.out.println("");

	}
}
