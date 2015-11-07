package com.feng;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

public class RangeQueryBasicTest {
	private RangeContainerFactory rf;

	@Before
	public void setUp() {
		rf = new SortedArrayRangeContainerFactory();
	}

	@Test
	public void runARangeQuery() {
		RangeContainer rc = rf.createContainer(new long[] { 10, 12, 17, 21, 2, 14, 16 });

		Ids ids = rc.findIdsInRange(14, 17, true, true);
		assertEquals(2, ids.nextId());
		assertEquals(5, ids.nextId());
		assertEquals(6, ids.nextId());
		assertEquals(Ids.END_OF_IDS, ids.nextId());
		ids = rc.findIdsInRange(14, 17, true, false);
		assertEquals(5, ids.nextId());
		assertEquals(6, ids.nextId());
		assertEquals(Ids.END_OF_IDS, ids.nextId());
		ids = rc.findIdsInRange(20, Long.MAX_VALUE, false, true);
		assertEquals(3, ids.nextId());
		assertEquals(Ids.END_OF_IDS, ids.nextId());
	}
	
	@Test
	public void testWithDuplicatedData() {
		RangeContainer rc = rf.createContainer(new long[] { 10, 14, 12, 17, 21, 2, 14, 15, 17, 16 });

		Ids ids = rc.findIdsInRange(14, 17, true, true);
		assertEquals(1, ids.nextId());
		assertEquals(3, ids.nextId());
		assertEquals(6, ids.nextId());
		assertEquals(7, ids.nextId());
		assertEquals(8, ids.nextId());
		assertEquals(9, ids.nextId());
		assertEquals(Ids.END_OF_IDS, ids.nextId());
		
		ids = rc.findIdsInRange(14, 17, false, false);
		assertEquals(7, ids.nextId());
		assertEquals(9, ids.nextId());
		assertEquals(Ids.END_OF_IDS, ids.nextId());
		
	}
	
	@Test
	public void testEmptyData() {
		RangeContainer rc = rf.createContainer(new long[] { });
		Ids ids = rc.findIdsInRange(14, 17, true, true);
		assertEquals(Ids.END_OF_IDS, ids.nextId());
	}
	
	@Test
	public void testSingleElementData() {
		RangeContainer rc = rf.createContainer(new long[] { 17 });
		Ids ids = rc.findIdsInRange(14, 17, true, true);
		assertEquals(0, ids.nextId());
		assertEquals(Ids.END_OF_IDS, ids.nextId());

		ids = rc.findIdsInRange(14, 17, false, false);
		assertEquals(Ids.END_OF_IDS, ids.nextId());
		
		ids = rc.findIdsInRange(14, 17, false, true);
		assertEquals(0, ids.nextId());
		assertEquals(Ids.END_OF_IDS, ids.nextId());
	}
	
	@Test
	public void testSameElementsData() {
		RangeContainer rc = rf.createContainer(new long[] { 17, 17 });
		Ids ids = rc.findIdsInRange(14, 17, true, true);
		assertEquals(0, ids.nextId());
		assertEquals(1, ids.nextId());
		assertEquals(Ids.END_OF_IDS, ids.nextId());

		ids = rc.findIdsInRange(14, 17, false, false);
		assertEquals(Ids.END_OF_IDS, ids.nextId());
		
		ids = rc.findIdsInRange(14, 17, false, true);
		assertEquals(0, ids.nextId());
		assertEquals(1, ids.nextId());
		assertEquals(Ids.END_OF_IDS, ids.nextId());

		ids = rc.findIdsInRange(17, 30, true, false);
		assertEquals(0, ids.nextId());
		assertEquals(1, ids.nextId());
		assertEquals(Ids.END_OF_IDS, ids.nextId());

	}
	
	@Test
	public void testWithInvalidRange() {
		RangeContainer rc = rf.createContainer(new long[] { 10, 12, 17, 21, 2, 14, 16 });

		Ids ids = rc.findIdsInRange(17, 14, true, true);
		assertEquals(Ids.END_OF_IDS, ids.nextId());
	}
	
	@Test
	public void testFromAndToAreEqual() {
		RangeContainer rc = rf.createContainer(new long[] { 10, 12, 17, 21, 17, 2, 14, 16 });

		Ids ids = rc.findIdsInRange(17, 17, true, true);
		assertEquals(2, ids.nextId());
		assertEquals(4, ids.nextId());
		assertEquals(Ids.END_OF_IDS, ids.nextId());
	}
	
	@Test
	public void testMinAndMaxMatch() {
		RangeContainer rc = rf.createContainer(new long[] { 6, 21, 7, 6, 21 });
		Ids ids = rc.findIdsInRange(6, 21, true, true);
		assertEquals(0, ids.nextId());
		assertEquals(1, ids.nextId());
		assertEquals(2, ids.nextId());
		assertEquals(3, ids.nextId());
		assertEquals(4, ids.nextId());
		assertEquals(Ids.END_OF_IDS, ids.nextId());

		ids = rc.findIdsInRange(6, 21, false, false);
		assertEquals(2, ids.nextId());
		assertEquals(Ids.END_OF_IDS, ids.nextId());
		
		ids = rc.findIdsInRange(Long.MIN_VALUE, Long.MAX_VALUE, true, true);
		assertEquals(0, ids.nextId());
		assertEquals(1, ids.nextId());
		assertEquals(2, ids.nextId());
		assertEquals(3, ids.nextId());
		assertEquals(4, ids.nextId());
		assertEquals(Ids.END_OF_IDS, ids.nextId());
	}
	
	public static void main(String [] argv) {
		RangeContainerFactory factory1 = new SimpleRangeContainerFactory();
		RangeContainerFactory factory2 = new SortedArrayRangeContainerFactory();
		RangeContainerFactory factory3 = new RangeContainerFacotoryImpl();
		
		long [] testData = generateRandomData();

		RangeContainer rc2 = factory2.createContainer(testData);
		RangeContainer rc1 = factory1.createContainer(testData);
		RangeContainer rc3 = factory3.createContainer(testData);

		int[][] ranges = generateRandomRange();
		
		long begin;
		long end;
		
		begin = System.currentTimeMillis();
		runLoadTest(testData, rc1, ranges);
		end = System.currentTimeMillis();
		System.out.println(rc1.getClass() + " took " + (end - begin) + "ms");

		begin = System.currentTimeMillis();
		runLoadTest(testData, rc2, ranges);
		end = System.currentTimeMillis();
		System.out.println(rc2.getClass() + " took " + (end - begin) + "ms");

		begin = System.currentTimeMillis();
		runLoadTest(testData, rc3, ranges);
		end = System.currentTimeMillis();
		System.out.println(rc3.getClass() + " took " + (end - begin) + "ms");

	}

	private static void runLoadTest(long[] testData, RangeContainer rc1, int[][] ranges) {
		int count=0;
		for(int i = 0; i < ranges.length; i++) {
			
			Ids ids = null;
			long value1 = testData[ranges[i][0]];
			long value2 = testData[ranges[i][1]];

			if(value1 > value2) {
				ids = rc1.findIdsInRange(value2, value1, true, true);
			} else {
				ids = rc1.findIdsInRange(value1, value2, true, true);
			}
			
			while(ids.nextId() != Ids.END_OF_IDS) {
				count++;
			}
		}
		System.out.println("find " + count + " matches");
	}
	
	private static long[] generateRandomData() {
		Random generator = new Random();
		long [] data = new long[32000];
		for(short i = 0; i < data.length; i++) {
			data[i] = generator.nextLong();
		}
		return data;
	}
	
	private static int[][] generateRandomRange() {
		Random generator = new Random();

		int numberOfRecords = 100000;
		int[][] ranges = new int[numberOfRecords][2];
		for(int i = 0; i < numberOfRecords; i++) {
			ranges[i][0] = generator.nextInt(32000); 
			ranges[i][1] = generator.nextInt(32000); 
		}
		
		return ranges;
	}
}