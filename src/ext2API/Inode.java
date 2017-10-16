package ext2API;

import java.util.Date;

public class Inode {
	private long file_size;
	private String file_mode;
	private short user_id, group_id, hard_links;
	private int inode_num, indirect_pointer, double_indirect_pointer, triple_indirect_pointer;
	private Date last_accessed_time, deleted_time, creation_time, last_modified_time;
	private int[] direct_pointers = new int [12];
	
	/**
	 * This is the inode object representing a file system inode.
	 * @param inode_num The inode number of the inode.
	 * @param file_mode The access mode of the inode.
	 * @param user_id The id of the user owning the inode.
	 * @param file_size The memory allocated to the inode's pointers.
	 * @param accessed_time The last access time of the inode.
	 * @param creation_time The creation time of the inode.
	 * @param modified_time The last time of modification of the inode.
	 * @param delete_time The deletion time of the inode.
	 * @param group_id The group id of the owner of the inode.
	 * @param hard_links The number of hard links to the inode.
	 * @param direct_pointers A list of the direct pointers to the inode's files.
	 * @param indirect_pointer A pointer to the indirect pointers of the inode.
	 * @param double_indirect_pointer A pointer to the double indirect pointers of the inode.
	 * @param triple_indirect_pointer A pointer to the triple indirect pointers of the inode.
	 */
	public Inode(int inode_num, String file_mode, short user_id, long file_size,  int accessed_time, int creation_time, 
		int modified_time, int delete_time, short group_id, short hard_links,
		int[] direct_pointers, int indirect_pointer, int double_indirect_pointer, int triple_indirect_pointer )
		{
			this.file_mode = file_mode;
			this.inode_num = inode_num;
			this.user_id = user_id;
			this.group_id = group_id;
			this.hard_links = hard_links;
			this.file_size = file_size;
			this.indirect_pointer = indirect_pointer;
			this.double_indirect_pointer = double_indirect_pointer;
			this.triple_indirect_pointer = triple_indirect_pointer;
			this.direct_pointers = direct_pointers;
			this.last_accessed_time = (accessed_time == 0) ? null : new Date((long) accessed_time * 1000);
			this.creation_time = (creation_time == 0) ? null : new Date((long) creation_time * 1000);
			this.deleted_time =  (delete_time == 0) ? null : new Date((long) delete_time * 1000);
			this.last_modified_time = (modified_time == 0) ? null : new Date((long) modified_time * 1000);
		}
	
	/**
	 * This will check if all the pointers associated with the inode object are empty.
	 * @return Returns true if the Inode has no pointers.
	 */
	public boolean isEmpty(){
		boolean empty = true;
		for (int i : direct_pointers)
			if (i!=0)
				return false;
		if (indirect_pointer!=0 || double_indirect_pointer!=0 || triple_indirect_pointer!=0)
			return false;
		return empty;
	}
	
	/**
	 * This will print the information associated with the inode object.
	 */
	public void print_inode(){
		System.out.println("\nInode : " + inode_num);
		System.out.println("File mode : " + file_mode);
		System.out.println("User ID : " + user_id);
		System.out.println("File size : " + file_size);
		System.out.println("Last access time : " + this.last_accessed_time);
		System.out.println("Creation time : " + this.creation_time);
		System.out.println("Last modified time : " + this.last_modified_time);
		System.out.println("Deleted : " + this.deleted_time);
		System.out.println("Group id : " + this.group_id);
		System.out.println("Hard links : " + this.hard_links);
		for (int i= 0; i < 12 ; i++)
			if (direct_pointers[i] != 0)
				System.out.println("Direct pointer " + (i+1) + " : " + direct_pointers[i]);
		if (indirect_pointer != 0)
			System.out.println("indirect pointer : " + indirect_pointer);
		if (double_indirect_pointer != 0)
			System.out.println("Double indirect pointer : " + this.double_indirect_pointer);
		if (triple_indirect_pointer != 0)
			System.out.println("Triple indirect pointer : " + this.triple_indirect_pointer);
		System.out.println("");
	}

	public String get_file_mode(){
		return file_mode;
	}
	
	public short get_user_id(){
		return user_id;
	}
	
	public short get_group_id(){
		return group_id;
	}
	
	public short get_hard_links(){
		return hard_links;
	}
	
	public Date get_last_accessed_time(){
		return last_accessed_time;
	}
	
	public Date get_deleted_time(){
		return deleted_time;
	}
	
	public Date get_creation_time(){
		return creation_time;
	}
	
	public Date get_last_modified_time(){
		return last_modified_time;
	}
	
	public long get_file_size(){
		return file_size;
	}
	
	public int get_indirect_pointer(){
		return indirect_pointer;
	}
	public int get_double_indirect_pointer(){
		return double_indirect_pointer;	
	}
		
	public int get_triple_indirect_pointer(){
		return triple_indirect_pointer;
	}
	
	public int[] get_direct_pointers(){
		return direct_pointers;
	}
}
