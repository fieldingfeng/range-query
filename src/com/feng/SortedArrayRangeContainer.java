package com.feng;

import java.util.Arrays;
import java.util.Comparator;


public class SortedArrayRangeContainer implements RangeContainer {

	private static final Ids EMPTY_IDS = new EmptyIds();

	private final Entry [] entries;

	private final long [] data;
	
	public SortedArrayRangeContainer(long [] data) {
		if(data == null) {
			this.data = new long [] {};
			entries = new Entry [] {};
 		} else {
 			// make a copy of data
			this.data = new long[data.length];
			for(int i = 0; i < data.length; i++) {
				this.data[i] = data[i];
			}
 			
 			entries = new Entry [data.length];
 			
 			for(short i = 0; i < data.length; i++) {
 				entries[i] = new Entry(i, data[i]);
 			}
 			
 			Arrays.sort(entries, new EntryComparator());
 		}

	}
	
	@Override
	public Ids findIdsInRange(long fromValue, long toValue,
			boolean fromInclusive, boolean toInclusive) {
		
		if(toValue < fromValue || entries.length == 0) {
			return EMPTY_IDS;
		}
		
		if(notInRange(fromValue, toValue, fromInclusive, toInclusive)) {
			return EMPTY_IDS;
		}

		Range range = findRange(0, entries.length - 1, fromValue, toValue, fromInclusive, toInclusive);
		
		int size = range.end - range.start;
		short [] ids = new short[size+1];
		for(int i = 0; i <= size; i++) {
			ids[i] = entries[i+range.start].id;
		}
		
		Arrays.sort(ids);
		
		return new ArrayIdsIterator(ids);
	}

	private boolean notInRange(long fromValue, long toValue,
			boolean fromInclusive, boolean toInclusive) {
		
		return fromValue > entries[entries.length - 1].value || 
				(fromValue == entries[entries.length - 1].value && ! fromInclusive) ||
				toValue < entries[0].value || 
				(toValue == entries[0].value && ! toInclusive);
		
	}

	private Range findRange(int start, int end, long fromValue, long toValue, boolean fromInclusive, boolean toInclusive) {
		if( (entries[start].value > fromValue || (entries[start].value == fromValue && fromInclusive)) && 
				(entries[end].value < toValue || (entries[end].value == toValue && toInclusive))) {
			return new Range(start, end);
		} 
		
		if(start + 1 == end) {
			if( (entries[start].value > fromValue || entries[start].value == fromValue && fromInclusive) && 
					(entries[start].value < toValue || entries[start].value == toValue && toInclusive)) {
				return new Range(start, start);
			} else {
				return new Range(end, end);
			}
		}
		
		int mid = (start + end) >>> 1;
		
		if(entries[mid].value < fromValue || (entries[mid].value == fromValue && ! fromInclusive)) {
			return findRange(mid, end, fromValue, toValue, fromInclusive, toInclusive);
		} else if(entries[mid].value > toValue || (entries[mid].value == toValue && !toInclusive)) {
			return findRange(start, mid, fromValue, toValue, fromInclusive, toInclusive);
		} else {
			Range r1 = findRange(start, mid, fromValue, toValue, fromInclusive, toInclusive);
			Range r2 = findRange(mid, end, fromValue, toValue, fromInclusive, toInclusive);
			
			r1.end = r2.end;
			return r1;
		}
	}	

	private static class Range {
		int start;
		int end;

		public Range(int start, int end) {
			super();
			this.start = start;
			this.end = end;
		}
	}
	
	private static class Entry {
		short id;
		long value;
		
		public Entry(short id, long value) {
			super();
			this.id = id;
			this.value = value;
		}
		
	}
	
	private class EntryComparator implements Comparator<Entry> {

		@Override
		public int compare(Entry o1, Entry o2) {
			if(o1.value > o2.value) {
				return 1;
			} else if(o1.value < o2.value) {
				return -1;
			} 
			
			return 0;
		}
		
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
}
