package ext2API;

public class Superblock {
	private int total_inodes = 0;
	private int total_blocks = 0;
	private int blocks_per_group = 0;
	private int inodes_per_group = 0;
	private int inodes_per_block = 0;
	private int total_groups = 0;
	private int inode_size = 0;
	private String volume_name;
	
	/**
	 *  This object represents the a copy of SuperBlock of the file system
	 * @param total_inodes This would be the total number of inodes within the file system
	 * @param total_blocks This would be the total number of blocks within the file system
	 * @param blocks_per_group This would be the total number of blocks per group within the file system
	 * @param inodes_per_group This would be the total number of inodes per group within the file system
	 * @param inode_size This would be the size of inodes within the file system
	 */
	public Superblock(int total_inodes, int total_blocks, int blocks_per_group, int inodes_per_group, int inode_size){
		this.total_inodes = total_inodes;
		this.total_blocks = total_blocks;
		this.blocks_per_group = blocks_per_group;
		this.inodes_per_group = inodes_per_group;
		this.inode_size = inode_size;
		inodes_per_block = Volume.BLOCK_SIZE/inode_size;
		total_groups = total_inodes/inodes_per_group;
	}
	
	/**
	 * This will print the information associated with the Super Block object.
	 */
	public void print_Superblock(){
		System.out.println("---------  GROUP 0 SUPER BLOCK --------- ");
		System.out.println("Total inodes : " + total_inodes);
		System.out.println("Total blocks : " + total_blocks);
		System.out.println("Blocks per group : " + blocks_per_group);
		System.out.println("Inodes per group : " + inodes_per_group);
		System.out.println("Inodes per block : " + inodes_per_block);
		System.out.println("Total groups : " + total_groups);
		System.out.println("Inode size : " + inode_size);
		System.out.println("Volume name : " + volume_name + "\n");
	}
	
	public void set_total_inodes(int i){
		total_inodes = i;
	}
	
	public int get_total_inodes(){
		return total_inodes;
	}
	
	public void set_total_blocks(int i){
		total_blocks = i;
	}
	
	public int get_total_blocks(){
		return total_blocks;
	}
	
	public void set_blocks_per_group(int i){
		blocks_per_group = i;
	}
	
	public int get_blocks_per_group(){
		return blocks_per_group;
	}
	
	public void set_inodes_per_group(int i){
		inodes_per_group = i;
	}
	
	public int get_inodes_per_group(){
		return inodes_per_group;
	}
	
	public void set_inodes_per_block(int i){
		inodes_per_block = i;
	}
	
	public int get_inodes_per_block(){
		return inodes_per_block;
	}
	
	public void set_total_groups(int i){
		total_groups = i;
	}
	
	public int get_total_groups(){
		return total_groups;
	}
	
	public void set_inode_size(int i){
		inode_size = i;
	}
	
	public int get_inode_size(){
		return inode_size;
	}
	
	public void set_volume_name(String i){
		volume_name = i;
	}
	
	public String get_volume_name(){
		return volume_name;
	}
	
}
