package nlp.sample.sentencegenerator;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import nlp.sample.sentencegenerator.DataContainer;
import org.junit.Test;

public class DataContainerTest {

	@Test
	public void testDividedSentenceHashMap() throws Exception {
		String test_path = System.getProperty("user.dir"); 
		String dirname = test_path + "/data/sample3";
		HashMap<Integer, ArrayList<String>> map = DataContainer.dividedSentenceHashMap(dirname, 2);
		int size = map.size();
		
		for(Entry<Integer, ArrayList<String>> s: map.entrySet()) {
    		int cvnum = s.getKey();
    		ArrayList<String> value = s.getValue();
    		for(String ss: value) {
    			System.out.print(cvnum);
    			System.out.print(":");
    			System.out.println(ss);
    		}
		}
		System.out.println(map);
		assertEquals(size, 2);
	}

}
