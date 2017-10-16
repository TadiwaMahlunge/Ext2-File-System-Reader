package ext2API;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class Buffer {
	private RandomAccessFile aFile;
	private FileChannel inChannel;
	private ByteBuffer data;
	
	/**
	 * This object would wrap around the ByteBuffer such that it would facilitate the reading of a Volume.
	 * @param volume This is the volume to be read.
	 * @param buffer_size This would be the size of the buffer.
	 */
	public Buffer (RandomAccessFile volume , int buffer_size){
		aFile = volume;
		inChannel = aFile.getChannel();
		data = ByteBuffer.allocate(buffer_size);
		data.position(0);
	}
	
	/**
	 * This would read a block from the given Volume block to the buffer.
	 * @param directory_block_number This would be the block to be read to.
	 * @return Returns true if the block is read successfully.
	 * @throws IOException This would throw this exception if the file to be read is not found or there is an underflow
	 * associated with the file.
	 */
	public boolean read_block(int directory_block_number) throws IOException{
		seek(directory_block_number);
		data.clear();
		int bytesRead;
		bytesRead = inChannel.read(data);
		if (bytesRead != -1) {
			data.flip();
			data.position(0);		
			return true;
		}
		else return false;
	}
	
	private void seek(int block){
		try {
			inChannel.position(Volume.BLOCK_SIZE * block);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return Returns the buffer associated with this object.
	 */
	public ByteBuffer get_ByteBuffer() {
		return data;
	}
}
