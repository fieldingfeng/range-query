package com.feng;

/**
 * This class represent an empty Ids iterator, which will always return END_OF_IDS on nextId()
 *
 */
public class EmptyIds implements Ids {

	@Override
	public short nextId() {
		return END_OF_IDS;
	}

}
