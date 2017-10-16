package ext2API;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class WriteBufferHelper extends BufferHelper {
	
	/**
	 * This Buffer Helper facilitates the buffered writing of the Volume's random access file's data to a file.
	 * @param vol This is the Random Access File associated with the Volume
	 * @param volume This would be the Volume of the file system.
	 */
	public WriteBufferHelper (RandomAccessFile vol, Volume volume){
		super(vol, Volume.BLOCK_SIZE, volume);
	}
	
	/**
	 * This will write all the contents of an inode associated with a file to the Ext2 File itself.
	 * @param inode_number The inode number associated with the file.
	 * @param file This is the file to be written to.
	 */
	public void write_inode_to_file(int inode_number, Ext2File file) {
		for(int block_number : get_all_pointers(inode_number)){
			try {
				if(block_number!=0){
					if(buffer.read_block(block_number)){
							file.write(cleave(data));			
					}
				}
			} catch (IOException e) {
					e.printStackTrace();
			}
		}
	}
	
	private ByteBuffer cleave (ByteBuffer data){
		ArrayList<Byte> buf_list = new ArrayList<>();
		byte current_byte;
		while (data.hasRemaining()){
			current_byte = data.get();		
			if(!Util.byteToHexString(current_byte).equals("00"))
				buf_list.add(current_byte);
		}
		byte[] buf_array = new byte[buf_list.size()];
		for (int i = 0 ; i < buf_list.size(); i++)
			buf_array[i] = buf_list.get(i);		
		return ByteBuffer.wrap(buf_array);
	}
}
