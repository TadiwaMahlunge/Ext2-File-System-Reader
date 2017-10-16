package ext2API;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class ReadBufferHelper extends BufferHelper {
	
	/**
	 * This Buffer Helper facilitates the buffered reading of the Volume's random access file.
	 * @param file This is the file associated with the Volume
	 * @param block_size This is the block size of the file system
	 * @param volume This would be the Volume of the file system.
	 */
	public ReadBufferHelper (RandomAccessFile file, int block_size, Volume volume){
		super(file, block_size, volume);
	}
	
	/**
	 * @return This is the SuperBlock object associated with the first copy of the SuperBlock of the file system.
	 */
	public Superblock get_superblock(){
		Superblock output_block = new Superblock(1,1,1,1,1);
		int[] super_block = new int[5];
		try {
			if(buffer.read_block(1)){
				super_block[0] = Util.byteArrayToInt(readByteWord(4));
				data.position(4);
				super_block[1] = Util.byteArrayToInt(readByteWord(4));
				data.position(32);
				super_block[2] = Util.byteArrayToInt(readByteWord(4));
				data.position(40);
				super_block[3] = Util.byteArrayToInt(readByteWord(4));
				data.position(88);
				super_block[4] = Util.byteArrayToInt(readByteWord(4));
				output_block = new Superblock(super_block[0], super_block[1], super_block[2], super_block[3], super_block[4]);
				data.position(120);
				char[] volume_name = new char[16];
				byte[] vol_name_bytes = readByteWord(16);
				for (int i = 0; i < 16 ;i++)
					volume_name[i] = (char) vol_name_bytes[i];
				output_block.set_volume_name(String.valueOf(volume_name));
			}
		} catch (IOException e) {
			output_block.set_volume_name("Failed");
			e.printStackTrace();
		}
		return output_block;
	}
	
	/**
	 * @param num_groups This is the number of groups within the file system.
	 * @return This is the Group_Descriptor object associated with the first copy of the Group Descriptor of the file system.
	 */
	public Group_Descriptor[] get_descriptor_table(int num_groups){
		Group_Descriptor[] output_table = new Group_Descriptor[num_groups];
		try {
			if(buffer.read_block(2)){
				for(int i = 0; i < num_groups; i++){
					data.position(i*32 + 8);
					int inode_table_block;
					short used_dirs_count;
					inode_table_block = Util.byteArrayToInt(readByteWord(4));
					data.position(data.position() + 4);
					used_dirs_count = Util.byteArrayToShort(readByteWord(2));
					output_table[i] = new Group_Descriptor(i, inode_table_block, used_dirs_count);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return output_table;
	}
	
	/**
	 * This will return the inode numbers of the contents of the directory that are themselves also directories.
	 * @param directory_block_number This is the block number of the directory.
	 * @return This will be a list of all the inode numbers of the directory that are themselves also directories.
	 */
	public ArrayList<Integer> get_subdirectory_inode_numbers(int directory_block_number) {
		int current_entry = 0;	
		int entries_read = 0;
		int current_entry_length = 0;
		ArrayList<Integer> subdirectory_inode_numbers = new ArrayList<>();
		try {
			if(buffer.read_block(directory_block_number)){		
				while (data.hasRemaining()){
					if (entries_read > 1){
						data.position(current_entry + 7);
						if (Util.byteToInt(readByteWord(1)[0]) == 2){
							data.position(current_entry);
							subdirectory_inode_numbers.add(Util.byteArrayToInt(readByteWord(4)));
						}
					}
					data.position(current_entry + 4);
					current_entry_length = (int) Util.byteArrayToShort(readByteWord(2));
					current_entry = current_entry + current_entry_length;
					data.position(current_entry);
					entries_read++;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return subdirectory_inode_numbers;
	}
	
	/**
	 * This will return the names of the contents of the directory that are themselves also directories.
	 * @param directory_block_number This is the block number of the directory.
	 * @return This will be a list of all the names of the directory that are themselves also directories.
	 */
	public ArrayList<String>  get_subdirectory_names(int directory_block_number) {
		int current_entry = 0;	
		int entries_read = 0;
		int current_entry_length = 0;
		int current_name_length = 0;
		ArrayList<String> subdirectory_names = new ArrayList<>();
		try {
			if(buffer.read_block(directory_block_number)){	
				while (data.hasRemaining()){
					if (entries_read > 1){
						data.position(current_entry + 7);
						if (Util.byteToInt(readByteWord(1)[0]) == 2){
							data.position(current_entry + 6);
							current_name_length =  Util.byteToInt(readByteWord(1)[0]);
							data.position(current_entry + 8 );
							String name = Util.byteArrayToString(readByteWord(current_name_length));
							subdirectory_names.add(name);
						}						
					}
					data.position(current_entry + 4);
					current_entry_length = (int) Util.byteArrayToShort(readByteWord(2));
					current_entry = current_entry + current_entry_length;
					data.position(current_entry);
					entries_read++;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return subdirectory_names;
	}
	
	/**
	 * This will return the inode numbers of the contents of the directory.
	 * @param directory_block_number This is the block number of the directory.
	 * @return This will be a list of all the inode numbers of the directory.
	 */
	public ArrayList<Integer> get_all_sub_inode_numbers(int directory_block_number) {
		int current_entry = 0;	
		int current_entry_length = 0;
		ArrayList<Integer> subdirectory_inode_numbers = new ArrayList<>();
		try {
			if(buffer.read_block(directory_block_number)){
				while (data.hasRemaining()){
					subdirectory_inode_numbers.add(Util.byteArrayToInt(readByteWord(4)));
					data.position(current_entry + 4);
					current_entry_length = (int) Util.byteArrayToShort(readByteWord(2));
					current_entry = current_entry + current_entry_length;
					data.position(current_entry);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return subdirectory_inode_numbers;
	}
	
	/**
	 * This will return the names of the contents of the directory.
	 * @param directory_block_number This is the block number of the directory.
	 * @return This will be a list of all the names of the directory.
	 */
	public ArrayList<String> get_all_sub_filenames(int directory_block_number) {
		int current_entry = 0;	
		int current_entry_length = 0;
		int current_name_length = 0;
		ArrayList<String> subdirectory_names = new ArrayList<>();
		try {
			if(buffer.read_block(directory_block_number)){
				while (data.hasRemaining()){
					data.position(current_entry + 6);
					current_name_length =  Util.byteToInt(readByteWord(1)[0]);
					data.position(current_entry + 8);
					String current_name = Util.byteArrayToString(readByteWord(current_name_length));
					subdirectory_names.add(current_name);		
					data.position(current_entry + 4);
					current_entry_length = (int) Util.byteArrayToShort(readByteWord(2));
					current_entry = current_entry + current_entry_length;
					data.position(current_entry);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return subdirectory_names;
	}
	
	/**
	 * This will read data from an Ext2File from the start to the offset of the said file into an array of bytes.
	 * @param start The byte one would like to begin reading from
	 * @param offset The byte at which one would like to finish reading
	 * @return An array of all read bytes
	 */
	public byte[] read(long start, long offset){
		byte[] output_array = new byte[Util.longToInt(offset - start)];
		try {
			buffer.read_block(0);
			data.position(Util.longToInt(start));
			for (int i = 0 ; i < (offset-start); i++)
				output_array[i] = data.get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return output_array;
	}

}
