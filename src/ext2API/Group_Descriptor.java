package ext2API;

public class Group_Descriptor {
	private int group;
	private int inode_table_block;
	private short used_dirs_count;
	
	/**
	 *  This object represents a copy of the Group Descriptor of the block group of the file system
	 *  Redundant Desctriptor information in the context of file reading has been excluded.
	 * @param grp This is the group number of the block group.
	 * @param inode_table This is the block at which the inode table begins
	 * @param dirs This is the number of used directories of the group.
	 */
	public Group_Descriptor(int grp, int inode_table, short dirs){
		group = grp;
		inode_table_block = inode_table;
		used_dirs_count = dirs;
	}
	
	/**
	 * This will print the information associated with the Super Block object.
	 */
	public void print_group_descriptor(){
		System.out.println("----  Group : " + group + "  ----");
		System.out.println("Inode table begins at block : " + inode_table_block);
		System.out.println("Used directory count : " + used_dirs_count);
		System.out.println(" ");
	}
	
	public int get_group(){
		return group;
	}
	
	public int get_inode_table_block(){
		return inode_table_block;
	}
	
	public short get_used_dirs_count(){
		return used_dirs_count;
	}
}
