package com.feng;

public class RangeContainerFacotoryImpl implements RangeContainerFactory {

	@Override
	public RangeContainer createContainer(long[] data) {
		return new RangeContainerImpl(data);
	}

}
