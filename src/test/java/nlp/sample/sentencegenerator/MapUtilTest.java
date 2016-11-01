package nlp.sample.sentencegenerator;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/**
 * MapUtilのテストメソッド。
 * */
public class MapUtilTest {

	/**
	 * 文字列キーと実数のValueを持つHashMapのValueによるソート機能の動作確認。
	 * */
	@Test
	public void testRetMaxValueStrings() {
		HashMap<String, Double> map = new HashMap<String, Double>();
        map.put("国語", 28.0);
        map.put("算数", 79.0);
        map.put("理科", 95.0);
        map.put("社会", 82.0);
        	
        List<Map.Entry<String, Double>> word2prob = MapUtil.retSortedMapDouble(map);
        
        ArrayList<String> array = MapUtil.retMaxValueStrings(word2prob);
		String first_sub = array.get(0);
		System.out.print(first_sub);
		assertEquals("理科", first_sub);  
	}

}
