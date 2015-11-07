package com.feng;

import java.util.Arrays;
import java.util.Comparator;

public class RangeContainerImpl implements RangeContainer {
	private static final Ids EMPTY_IDS = new EmptyIds();

	private final Entity [] entities;
	
	RangeContainerImpl(long [] data) {
		if(data == null) {
			entities = new Entity [] {};
 		} else {
 			entities = new Entity [data.length];
 			
 			for(short i = 0; i < data.length; i++) {
 				entities[i] = new Entity(i, data[i]);
 			}
 			
 			Arrays.sort(entities, new EntityComparator());
 		}
	}
	
	@Override
	public Ids findIdsInRange(long fromValue, long toValue,
			boolean fromInclusive, boolean toInclusive) {

		if(toValue < fromValue || entities.length == 0) {
			return EMPTY_IDS;
		}
		
		if(notInRange(fromValue, toValue, fromInclusive, toInclusive)) {
			return EMPTY_IDS;
		}
		
		int low = findLow(fromValue, fromInclusive);
		int high = findHigh(toValue, low, toInclusive);
		
		int size = high - low;
		short [] ids = new short[size+1];
		for(int i = 0; i <= size; i++) {
			ids[i] = entities[i+low].id;
		}
		
		Arrays.sort(ids);
		
		return new ArrayIdsIterator(ids);
	}

	private boolean notInRange(long fromValue, long toValue,
			boolean fromInclusive, boolean toInclusive) {
		
		return fromValue > entities[entities.length - 1].value || 
				(fromValue == entities[entities.length - 1].value && ! fromInclusive) ||
				toValue < entities[0].value || 
				(toValue == entities[0].value && ! toInclusive);
		
	}

	private int findHigh(long toValue, int from, boolean inclusive) {
		int low = from;
		int high = entities.length - 1;
		
		while(low+1 < high) {
			int mid = (low + high) >>> 1;
			if(entities[mid].value < toValue) {
				low = mid;
			} else if (entities[mid].value > toValue){
				high = mid;
			} else if(inclusive){
				int idx = mid + 1;
				while(idx <= high && entities[idx].value == entities[mid].value) {
					mid = idx;
					idx = mid + 1;
				}
				return mid;
			} else {
				high = mid;
			}
		}
		
		if( (entities[high].value < toValue) || 
				entities[high].value == toValue && inclusive) {
			return high;
		} 
		
		return low;
	}

	private int findLow(long fromValue, boolean inclusive) {
		int low = 0; 
		int high = entities.length - 1;
		
		while(low+1 < high) {
			int mid = (low + high) >>> 1;
			long midValue = entities[mid].value;
			
			if(midValue < fromValue) {
				low = mid;
			} else if (midValue > fromValue){
				high = mid;
			} else if(inclusive){
				int idx = mid - 1;
				while(idx >= low && entities[idx].value == entities[mid].value) {
					mid = idx;
					idx = mid - 1;
				}
				return mid;
			} else {
				low = mid;
			}
		}

		if( (entities[low].value > fromValue) || 
				entities[low].value == fromValue && inclusive) {
			return low;
		} 

		return high;
	}

	private static class ArrayIdsIterator implements Ids {
		private int index = 0;
		private final short [] ids;
		
		public ArrayIdsIterator(short[] ids) {
			super();
			this.ids = ids;
		}


		@Override
		public short nextId() {
			if(index < ids.length) {
				return ids[index++];
			}
			return END_OF_IDS;
		}
		
	}
	
	private class Entity {
		short id;
		long value;
		
		public Entity(short id, long value) {
			super();
			this.id = id;
			this.value = value;
		}
		
	}
	
	private class EntityComparator implements Comparator<Entity> {

		@Override
		public int compare(Entity o1, Entity o2) {
			if(o1.value > o2.value) {
				return 1;
			} else if(o1.value < o2.value) {
				return -1;
			} 
			
			return 0;
		}
		
	}
}
