package com.feng;

/**
 * This class will create a {@link RangeContainer} which is back by a sorted array
 */
public class SortedArrayRangeContainerFactory implements RangeContainerFactory {

	@Override
	public RangeContainer createContainer(long[] data) {
		return new SortedArrayRangeContainer(data);
	}
	
	

}
