package ext2API;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class Ext2File {
	private File file;
	private String filepath;
	private FileChannel channel;
	private RandomAccessFile ext2File;
	private long file_size = 0;
	private long current_position = 0;
	private ReadBufferHelper read_helper;
	
	@SuppressWarnings("resource")
	/**
	 * This is the object associated with the Ext2File within the file system.
	 * @param vol This would be the Volume within which the file exists.
	 * @param path This would be the path to the file.
	 */
	public Ext2File(Volume vol, String path){
		file = new File("src/File System/" + path);
		filepath = path;
		try {
			channel = new FileOutputStream(file).getChannel();	
			vol.fill_Ext2File(this);
			ext2File = new RandomAccessFile(file, "r");
			read_helper = new ReadBufferHelper(ext2File, Util.longToInt(file_size), vol);
			read_helper.buffer.read_block(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		//System.out.println("file : " + path + " created successfully...");
	}
	
	/**
	 * This would write the a Buffer's worth of data to the file
	 * @param data The data to be written.
	 */
	public void write(ByteBuffer data){
		try {
			channel.write(data);
			file_size += Volume.BLOCK_SIZE;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This would read a selection of bytes from the file.
	 * @param startByte The byte at which one would like to begin reading
	 * @param length The amount of bytes one would like to read
	 * @return An array of the bytes read from the file
	 * @throws IllegalArgumentException This would arise if one tries to read more bytes than are in the file
	 */
	public byte[] read(long startByte, long length) throws IllegalArgumentException {
		if ((startByte + length) >= file_size)
			throw new IllegalArgumentException("Starting byte + Length exceed file_size; seek a new position.");
		
		return read_helper.read(startByte, (startByte + length));
	}
	
	/**
	 * This would read a selection of bytes from the file from the current byte.
	 * @param length The amount of bytes one would like to read
	 * @return 	 * @return An array of the bytes read from the file
	 * @throws IllegalArgumentException This would arise if one tries to read more bytes than are in the file
	 */
	public byte[] read(long length) throws IllegalArgumentException {
		if ((current_position + length) >= file_size)
			throw new IllegalArgumentException("Length exceed file_size; seek a new position.");
		
		return read_helper.read(current_position, (current_position + length));
	}
	
	/**
	 * Moves the read pointer to the input position.
	 * @param position The position one would like to move the read pointer to.
	 */
	public void seek (long position){
		current_position = position;
	}
	
	/**
	 * Returns the current position of the read pointer
	 * @return The current position of the read pointer.
	 */
	public long position(){
		return current_position;
	}
	
	/**
	 * Returns the size of the file
	 * @return The size of the file
	 */
	public long size(){
		return file_size;
	}
	
	/**
	 * Returns the path to the file
	 * @return The path to the file.
	 */
	public String get_filepath(){
		return filepath;
	}
}
