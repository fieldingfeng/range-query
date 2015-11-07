package com.feng;

public class SimpleRangeContainerFactory implements RangeContainerFactory {
	private static final Ids EMPTY_IDS = new EmptyIds();
	
	@Override
	public RangeContainer createContainer(long[] data) {
		return new SimpleRangeContainer(data);
	}

	private static class SimpleRangeContainer implements RangeContainer {
		final long [] data;
		
		private SimpleRangeContainer(long[] data) {
			this.data = new long[data.length];
			
			for(int i = 0; i < data.length; i++) {
				this.data[i] = data[i];
			}
		}
		
		@Override
		public Ids findIdsInRange(long fromValue, long toValue,
				boolean fromInclusive, boolean toInclusive) {
			if(fromValue > toValue) {
				return EMPTY_IDS;
			}
			
			return new IdIterator(fromInclusive, toInclusive, fromValue, toValue, data);
		}
		
	}
	
	private static class EmptyIds implements Ids {

		@Override
		public short nextId() {
			return END_OF_IDS;
		}
		
	}
	
	private static class IdIterator implements Ids {
		private final boolean fromInclusive;
		private final boolean toInclusive;
		private final long fromValue;
		private final long toValue;
		private final long[] data;
		
		private short index = 0;
		
		public IdIterator(boolean fromInclusive, boolean toInclusive,
				long fromValue, long toValue, long[] data) {
			super();
			this.fromInclusive = fromInclusive;
			this.toInclusive = toInclusive;
			this.fromValue = fromValue;
			this.toValue = toValue;
			this.data = data;
		}


		@Override
		public short nextId() {
			while(index < data.length) {
				long curVal = data[index];
				
				if((curVal == fromValue && fromInclusive) ||
						(curVal == toValue && toInclusive)) {
					return index++;
				} else if (curVal > fromValue && curVal < toValue){
					return index++;
				}
				
				index++;
			}
			
			return END_OF_IDS;
		}
		
	}
}
