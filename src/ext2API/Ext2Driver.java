package ext2API;

public class Ext2Driver {

	public static void main(String[] args) {
		Volume volume = new Volume("src/res/ext2fs.jpg", 1024, true);
		Ext2File file = new Ext2File(volume, "/root/two-cities.txt");
		Ext2File file1 = new Ext2File(volume, "/root/files/dbl-ind-e.txt");
		Ext2File file2 = new Ext2File(volume, "/root/files/dir-s.txt");
		Ext2File file3 = new Ext2File(volume, "/root/files/trpl-ind-e.txt");
		OutputHelper outputter = new OutputHelper();
		//outputter.dumpHexBytes(file.read(150));
		
	}
}
