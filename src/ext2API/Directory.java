package ext2API;

import java.io.File;
import java.util.ArrayList;

public class Directory{
  String filepath;
  File file;
  Volume volume;
  int inode_number;
  
  /**
   * This is the object associated with a file system directory.
   * @param path This is the file path to the file system directory.
   * @param inode_num This is the inode number associated with the file system directory.
   * @param vol This is the Volume to which the directory belongs
   */
  public Directory(String path, int inode_num, Volume vol){
  	inode_number = inode_num;
  	filepath = path;
  	volume = vol;
  	file = new File("src/File System"+ path);
  	if (file.mkdirs())
  		System.out.println("Directory : " + path + " created successfully...");	
  }
  /**
   * This returns an object holding file information for any directory
   * @return The file information of the directory.
   */
  public FileInfo getFileInfo(){
  	ReadBufferHelper helper = new ReadBufferHelper(volume.get_Random_Access_File(), Volume.BLOCK_SIZE, volume);
  	Inode file_inode;
  	ArrayList<String> subdirectory_names = volume.get_all_filenames(inode_number);
  	ArrayList<Integer> subdirectory_inode_numbers = volume.get_subdirectory_inode_numbers_for_all_files(inode_number);
  	String[][] values = new String[8][subdirectory_inode_numbers.size()];
  	
  	for (int i = 0; i < subdirectory_inode_numbers.size(); i ++){
  		file_inode = helper.get_inode(subdirectory_inode_numbers.get(i)); 
  		if (helper.get_inode(inode_number).get_file_mode().equals("C041")){
  			System.out.println("Access denied to : " + filepath + "\n");
  			return null;
  		}
  		values[0][i] = new String(""+ subdirectory_inode_numbers.get(i));
  		values[1][i] = file_inode.get_file_mode();
  		values[2][i] = new String("" + file_inode.get_hard_links());
  		values[3][i] = new String("" + file_inode.get_user_id());
  		values[4][i] = new String("" + file_inode.get_group_id());
  		values[5][i] = new String("" + file_inode.get_file_size());
  		values[6][i] = new String("" + file_inode.get_last_modified_time());
  		values[7][i] = subdirectory_names.get(i);
  	}
  		
  	FileInfo file_info = new FileInfo (values, subdirectory_inode_numbers.size(), filepath);
  	return file_info;
  }
  
  /**
   * @return This is the file path to the directory
   */
  public String get_filepath(){
  	return filepath;
  }
	
  /**
   * @return The names of all files that are within this directory.
   */
	public ArrayList<String> get_file_names() {
		ArrayList<String> output_names = new ArrayList<>();
		ReadBufferHelper helper = new ReadBufferHelper(volume.get_Random_Access_File(), Volume.BLOCK_SIZE, volume);
		if(helper.get_inode(inode_number).get_file_mode().equals("C041"))
			return output_names;
  	ArrayList<String> subdirectory_names = volume.get_all_filenames(inode_number);
  	ArrayList<Integer> subdirectory_inode_numbers = volume.get_subdirectory_inode_numbers_for_all_files(inode_number);
  	for (int i = 0; i < subdirectory_inode_numbers.size(); i ++)
  		if (helper.get_inode(subdirectory_inode_numbers.get(i)).get_file_mode().equals("A481"))
  			output_names.add(subdirectory_names.get(i));
  	return output_names;
	}
}
