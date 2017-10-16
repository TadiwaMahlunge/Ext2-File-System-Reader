package ext2API;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class Volume {
	public static int BLOCK_SIZE;
	
	private int[] INODE_TABLE_GROUP_UPPERBOUNDS;
	private Superblock super_block;
	private Group_Descriptor[] group_descriptor_table;
	private RandomAccessFile volume;
	private ReadBufferHelper read_helper;
	private WriteBufferHelper write_helper;
	private ArrayList<Directory> directories = new ArrayList<>();
	
	/**
	 * This creates an object representing the Volume of the file system image, including a copy of its Group Descriptor
	 * an its super block. I advise one put their file system image in the res folder to separate it from the .java files.
	 * The directories and files will be loaded.
	 * @param path This is the file path to the file system image you wish to upload. 
	 * e.g. src/res/ext2fs.jpg would access the image within the res folder.
	 * @param block_size This is the block size of the file system you wish to upload. This number will be a power of 2.
	 * @param fill_files If true, this Volume will load all files to the folders created in the src folder.
	 */
	public Volume(String path, int block_size, boolean fill_files){
		BLOCK_SIZE = block_size; 
		File file = new File(path);
		try {
			volume = new RandomAccessFile(file, "r");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		read_helper = new ReadBufferHelper(volume, BLOCK_SIZE, this);
		write_helper = new WriteBufferHelper(volume, this);
		
		super_block = read_helper.get_superblock();
		super_block.print_Superblock();
		
		group_descriptor_table = read_helper.get_descriptor_table(super_block.get_total_groups());
		get_inode_table_upperbounds(group_descriptor_table);
		
		for (Group_Descriptor g : group_descriptor_table)
			g.print_group_descriptor();		
		
		fill_directories();
		for (Directory d: directories){
			FileInfo info = d.getFileInfo();
			if (info != null)
				info.display_file_info();
		}
		
		if(fill_files)
			fill_directories_with_files(directories);
	}
	
	/**
	 * This returns the RandomAccessFile needed to read this Volume
	 * @return This is the RandomAccessFile from which the Volume is loaded.
	 */
	public RandomAccessFile get_Random_Access_File(){
		return volume;
	}
	
	private void get_inode_table_upperbounds(Group_Descriptor[] group_descriptor_table){
		INODE_TABLE_GROUP_UPPERBOUNDS = new int[group_descriptor_table.length];
		for (int i = 0; i < INODE_TABLE_GROUP_UPPERBOUNDS.length; i++)
			INODE_TABLE_GROUP_UPPERBOUNDS[i] = (i+1) * super_block.get_inodes_per_group(); 
	}
	
	private int get_inode_group (int inode_number){
		int group=0;
		for (int i = 0; i < INODE_TABLE_GROUP_UPPERBOUNDS.length ; i++)
			if (inode_number < INODE_TABLE_GROUP_UPPERBOUNDS[i]){
				group = i;
				break;
			}
		return group;
	}
	
	public int get_inode_block_number(int inode_number){
		int block_num = group_descriptor_table[get_inode_group(inode_number)].get_inode_table_block() +
				((inode_number-1)%super_block.get_inodes_per_group())/super_block.get_inodes_per_block();
		return block_num;		
	}
	
	private void fill_directories(){
		String filepath = new String("/root");
		fill_subdirectories(filepath, 2);
	}
	
	private void fill_subdirectories(String filepath , int inode_number) {
		directories.add(new Directory(filepath, inode_number , this));
		
		ArrayList<Integer> subdirectory_inode_numbers = get_subdirectory_inode_numbers(inode_number);
		ArrayList<String> subdirectory_names = get_subdirectory_names(inode_number);
		
		for(int i = 0; i < subdirectory_names.size(); i++)
			fill_subdirectories(new String(filepath + "/"+ subdirectory_names.get(i) ) , subdirectory_inode_numbers.get(i));
	}

	private ArrayList<Integer> get_subdirectory_inode_numbers(int inode_number) {
		ArrayList<Integer> subdirectory_inode_numbers = new ArrayList<>();
		for (int block_number : read_helper.get_all_pointers(inode_number)){
			subdirectory_inode_numbers.addAll(read_helper.get_subdirectory_inode_numbers(block_number));
		}
		return subdirectory_inode_numbers;
	}
	
	private ArrayList<String> get_subdirectory_names(int inode_number) {
		ArrayList<String> subdirectory_names = new ArrayList<>();
		for (int block_number : read_helper.get_all_pointers(inode_number)){
			subdirectory_names.addAll(read_helper.get_subdirectory_names(block_number));
		}
		return subdirectory_names;
	}
	
	private int get_file_inode_number (String file_path){
		int inode_number = 0;
		
		ArrayList<Integer> key_points = new ArrayList<>();
		for (int i = 0; i < file_path.length() -1; i++)
			if(file_path.charAt(i) == '.' || file_path.charAt(i) == '/')
				key_points.add(i);
		
		int num_directories;
		if(!file_path.substring(key_points.get(key_points.size()-1)).equals(".txt")){
			key_points.add(file_path.length());
			num_directories = key_points.size()-1;
		}
		else{
			num_directories = key_points.size()-1;
		}
		
		String[] directories = new String[num_directories];
		for(int i = 0; i < num_directories; i++)
			directories[i] = file_path.substring(key_points.get(i)+1, key_points.get(i+1));
		
		inode_number = get_subdirectory_inode_path(directories, num_directories-1);
		return inode_number;
	}
	
	private int get_subdirectory_inode_path(String directories[], int i){
		int current_inode_number = 0;
		int return_inode_number = 0;
		if (!directories[i].equals("root")){
			current_inode_number = get_subdirectory_inode_path(directories, i-1);		
			if(i < directories.length - 1){
				ArrayList<Integer> subdirectory_inode_numbers = get_subdirectory_inode_numbers_for_all_files(current_inode_number);
				ArrayList<String> subdirectory_names = get_all_filenames(current_inode_number);
				for (int index = 0 ; index < subdirectory_names.size(); index++)
					if (subdirectory_names.get(index).equals(directories[i+1])){
						return_inode_number = subdirectory_inode_numbers.get(index);
						break;
				}
			}
			else return current_inode_number;
		}
		else {	
			ArrayList<Integer> subdirectory_inode_numbers = get_subdirectory_inode_numbers_for_all_files(2);
			ArrayList<String> subdirectory_names = get_all_filenames(2);
			for (int index = 0 ; index < subdirectory_names.size(); index++)
				if (subdirectory_names.get(index).equals(directories[i+1]))
					return subdirectory_inode_numbers.get(index);
		}
		return return_inode_number;
	}
	
	/**
	 * This will return all the inode numbers of all the files within the directory associated with the inode give.
	 * @param inode_number This is the inode pointing to the directory from which the inode numbers will be read
	 * @return All the inode numbers derived from this directory
	 */
	public ArrayList<Integer> get_subdirectory_inode_numbers_for_all_files(int inode_number) {
		ArrayList<Integer> subdirectory_inode_numbers = new ArrayList<>();
		for (int block_number : read_helper.get_all_pointers(inode_number)){
			subdirectory_inode_numbers.addAll(read_helper.get_all_sub_inode_numbers(block_number));
		}
		return subdirectory_inode_numbers;
	}
	
	/**
	 * This will return all the file names of all the files within the directory associated with the inode give.
	 * @param inode_number This is the inode pointing to the directory from which the names will be read
	 * @return All the filenames derived from this directory
	 */
	public ArrayList<String> get_all_filenames(int inode_number) {
		ArrayList<String> subdirectory_names = new ArrayList<>();
		for (int block_number : read_helper.get_all_pointers(inode_number)){
			subdirectory_names.addAll(read_helper.get_all_sub_filenames(block_number));
		}
		return subdirectory_names;
	}
	
	/**
	 * This will read all file data into the Ext2File from its associated blocks
	 * @param file This is the file we are reading into.
	 */
	public void fill_Ext2File(Ext2File file){
		int file_inode_number = get_file_inode_number(file.get_filepath());
		write_helper.write_inode_to_file(file_inode_number, file);
	}
	
	private void fill_directories_with_files(ArrayList<Directory> directories){
		Ext2File file;
		for (Directory d : directories){
			ArrayList<String> subdirectory_names = d.get_file_names();
			for(int i = 0; i < subdirectory_names.size(); i++)
				file = new Ext2File(this, (d.get_filepath() + "/" + subdirectory_names.get(i) + ".txt"));
		}
	}
}
