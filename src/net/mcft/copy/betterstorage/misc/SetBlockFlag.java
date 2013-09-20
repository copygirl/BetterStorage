package net.mcft.copy.betterstorage.misc;

public final class SetBlockFlag {
	
	/** When set, causes a block update to the surrounding blocks. */
	public static final int BLOCK_UPDATE   = 0x1;
	/** When set on server, sends the block change to the client. */
	public static final int SEND_TO_CLIENT = 0x2;
	/** When set on client, will not render the chunk again. */
	public static final int DONT_RERENDER  = 0x4;
	
	/** The default setting, will cause a block
	 *  update and send the change to the client. */
	public static final int DEFAULT = BLOCK_UPDATE | SEND_TO_CLIENT;
	
	private SetBlockFlag() {  }
	
}
