package ext2API;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class BufferHelper {
	protected Buffer buffer;
	protected ByteBuffer data;
	protected Volume volume;
	
	/**
	 * This is the parent class of the read and write helpers; these allow greater control of the buffer used to read 
	 * from and write to the ByteBuffers that access the Volume.
	 * @param file This would be the file being read to or written from.
	 * @param block_size This would be the block size of the file system.
	 * @param vol This would be the file system volume.
	 */
	public BufferHelper (RandomAccessFile file, int block_size, Volume vol){
		buffer = new Buffer(file, block_size);
		data = buffer.get_ByteBuffer();
		volume = vol;
	}
	
	/**
	 * This will read n bytes into an array and output that array.
	 * @param size This is the number of bytes read in at a time.
	 * @return This would be an array of the bytes read in.
	 */
	protected byte[] readByteWord(int size){
		byte[] bytes = new byte[size];
		for (int i = 0; i < size; i++)
			bytes[i] = data.get();
		return bytes;
	}	
	
	/**
	 * This will output an inode object given an inode number
	 * @param inode_number This is the inode number of the inode to be returned
	 * @return The inode object associated with the given inode number.
	 */
	public Inode get_inode(int inode_number){
		Inode output_inode = null;
		try {
			if(buffer.read_block(volume.get_inode_block_number(inode_number))){
				short[] short_vals = new short[3];
				long file_size_val;
				int[] int_vals = new int[7];
				int[] direct_pointers = new int[12];
				int offset = ((inode_number - 1)%8) * 128;
 				data.position(offset);
 				
 				String file_mode = Util.byteArrayToHexString(readByteWord(2));
 				short_vals[0] = Util.byteArrayToShort(readByteWord(2));
 				byte[] file_size_lower = readByteWord(4);
 				int_vals[0] = Util.byteArrayToInt(readByteWord(4));
 				int_vals[1] = Util.byteArrayToInt(readByteWord(4));
 				int_vals[2] = Util.byteArrayToInt(readByteWord(4));
 				int_vals[3] = Util.byteArrayToInt(readByteWord(4));
 				short_vals[1] = Util.byteArrayToShort(readByteWord(2));
 				short_vals[2] = Util.byteArrayToShort(readByteWord(2));
 				data.position(data.position() + 12);
				for (int i = 0; i<12 ; i++)
					direct_pointers[i] = Util.byteArrayToInt(readByteWord(4));
				int_vals[4] = Util.byteArrayToInt(readByteWord(4));
				int_vals[5] = Util.byteArrayToInt(readByteWord(4));
				int_vals[6] = Util.byteArrayToInt(readByteWord(4));
				data.position(data.position() + 8);
				byte[] file_size_upper = readByteWord(4);
				byte[] file_size = new byte[8];
				for (int i = 0; i < 4; i++){
					file_size[i] = file_size_lower[i];
					file_size[i+4] = file_size_upper[i];
				}
				file_size_val = Util.byteArrayToLong(file_size);
				output_inode = new Inode(inode_number, file_mode, short_vals[0], file_size_val, int_vals[0], 
						int_vals[1], int_vals[2], int_vals[3], short_vals[1], short_vals[2],
						direct_pointers, int_vals[4], int_vals[5], int_vals[6]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return output_inode;
	}
	
	/**
	 * This will output an array list of the direct pointers of the inode associated with the given inode number
	 * @param inode_number The inode number associated with the direct pointers to be returned
	 * @return An array list of direct pointers.
	 */
	public ArrayList<Integer> get_direct_pointers (int inode_number){
		ArrayList<Integer> direct_pointers = new ArrayList<>();
		Inode directory_node = get_inode(inode_number);
		for (int i : directory_node.get_direct_pointers())
			if (i != 0) 
				direct_pointers.add(i);
		return direct_pointers;
	}
	
	/**
	 * This will output an array list of the direct pointers of the inode associated with the given inode number
	 * @param inode_number The inode number associated with the direct pointers to be returned
	 * @return An array list of direct pointers.
	 */
	public ArrayList<Integer> get_indirect_pointers (int inode_number){
		ArrayList<Integer> indirect_pointers = new ArrayList<>();
		Inode directory_node = get_inode(inode_number);
		int current_pointer = 0;
		try {
			if(buffer.read_block(directory_node.get_indirect_pointer()))
				while(data.hasRemaining()){
					current_pointer = Util.byteArrayToInt(readByteWord(4));
					if (current_pointer != 0) indirect_pointers.add(current_pointer);
				}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return indirect_pointers;
	}
	
	/**
	 * This will output an array list of the double indirect pointers of the inode associated with the given inode number
	 * @param inode_number The inode number associated with the double indirect pointers to be returned
	 * @return An array list of double indirect pointers.
	 */
	public ArrayList<Integer> get_double_indirect_pointers (int inode_number){
		ArrayList<Integer> double_indirect_pointers = new ArrayList<>();
		ArrayList<Integer> pointers_to_double_indirect_pointers = new ArrayList<>();
		Inode directory_node = get_inode(inode_number);
		int current_pointer = 0;
		try {
			if(buffer.read_block(directory_node.get_double_indirect_pointer()))
				while(data.hasRemaining()){
					current_pointer = Util.byteArrayToInt(readByteWord(4));
					if (current_pointer != 0) pointers_to_double_indirect_pointers.add(current_pointer);
				}
			for (int pointer : pointers_to_double_indirect_pointers)
				if(buffer.read_block(pointer))
					while(data.hasRemaining()){
						current_pointer = Util.byteArrayToInt(readByteWord(4));
						if (current_pointer != 0) double_indirect_pointers.add(current_pointer);
					}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return double_indirect_pointers;
	}
	
	/**
	 * This will output an array list of the triple indirect file pointers of the inode associated with the given inode number
	 * @param inode_number The inode number associated with the triple indirect pointers to be returned
	 * @return An array list of triple indirect pointers.
	 */
	public ArrayList<Integer> get_triple_indirect_pointers (int inode_number){
		ArrayList<Integer> triple_indirect_pointers = new ArrayList<>();
		ArrayList<Integer> pointers_to_triple_indirect_pointers = new ArrayList<>();
		ArrayList<Integer> pointers_to_pointers_to_triple_indirect_pointers = new ArrayList<>();
		Inode directory_node = get_inode(inode_number);
		int current_pointer = 0;
		try {
			if(buffer.read_block(directory_node.get_triple_indirect_pointer()))
				while(data.hasRemaining()){
					current_pointer = Util.byteArrayToInt(readByteWord(4));
					if (current_pointer != 0) pointers_to_pointers_to_triple_indirect_pointers.add(current_pointer);
				}
			for (int pointer : pointers_to_pointers_to_triple_indirect_pointers)
				if(buffer.read_block(pointer))
					while(data.hasRemaining()){
						current_pointer = Util.byteArrayToInt(readByteWord(4));
						if (current_pointer != 0) pointers_to_triple_indirect_pointers.add(current_pointer);
					}
			for (int pointer : pointers_to_triple_indirect_pointers)
				if(buffer.read_block(pointer))
					while(data.hasRemaining()){
						current_pointer = Util.byteArrayToInt(readByteWord(4));
						if (current_pointer != 0) triple_indirect_pointers.add(current_pointer);
					}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return triple_indirect_pointers;
	}
	
	/**
	 * This will output an array list of the file pointers of the inode associated with the given inode number
	 * @param inode_number The inode number associated with the all the pointers to be returned
	 * @return An array list of all file pointers.
	 */
	public ArrayList<Integer> get_all_pointers (int inode_number){
		ArrayList<Integer> all_pointers = new ArrayList<>();
		all_pointers.addAll(get_direct_pointers(inode_number));
		all_pointers.addAll(get_indirect_pointers(inode_number));
		all_pointers.addAll(get_double_indirect_pointers(inode_number));
		all_pointers.addAll(get_triple_indirect_pointers(inode_number));
		return all_pointers;
	}
}
