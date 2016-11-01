package nlp.sample.sentencegenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Mapオブジェクト関係のよく使う操作メソッドを提供するクラス。
 * */
public class MapUtil {
	
	/**
	 * MapのValue実数値でソートしたMap.Entryを返すメソッド。
	 * @param map 入力Map。キーは文字列で実数値のvalueを持つ。
	 * @return Valueの実数値で降順でソートされたMap.Entryを格納したList。
	 * */
	public static List<Map.Entry<String, Double>> retSortedMapDouble(Map<String, Double> map) {
    	// List 生成 (ソート用)
    	List<Map.Entry<String,Double>> entries = new ArrayList<Map.Entry<String,Double>>(map.entrySet());
    	Collections.sort(entries, new Comparator<Map.Entry<String,Double>>() {
    		public int compare(Entry<String,Double> entry1, Entry<String,Double> entry2) {
    			return ((Double)entry2.getValue()).compareTo((Double)entry1.getValue());
            }
        });
    	
    	return entries;
	}
	
	/**
	 * MapのIntegerキーでソートしたMap.Entryを返すメソッド。
	 * @param map 入力Map。キーはIntegerでIntegerのvalueを持つ。
	 * @return キーのInteger値で降順でソートされたMap.Entryを格納したList。
	 * */
	public static List<Map.Entry<Integer, Integer>> retSortedMapIntegerKey(Map<Integer, Integer> map) {
    	List<Map.Entry<Integer,Integer>> entries = new ArrayList<Map.Entry<Integer,Integer>>(map.entrySet());
    	Collections.sort(entries, new Comparator<Map.Entry<Integer,Integer>>() {
    		public int compare(Entry<Integer,Integer> entry1, Entry<Integer,Integer> entry2) {
    			return ((Integer)entry2.getKey()).compareTo((Integer)entry1.getKey());
            }
        });
    	return entries;
	}
	
	/**
	 * MapのStringキーでソートしたMap.Entryを返すメソッド。
	 * @param map 入力Map。キーはStringで単語列オブジェクト(WordSequence)のvalueを持つ。
	 * @return キーの文字列で降順でソートされたMap.Entryを格納したList。
	 * */
	public static List<Map.Entry<String, WordSequence>> retSortedMapStringKey(Map<String, WordSequence> map) {
    	List<Map.Entry<String,WordSequence>> entries = new ArrayList<Map.Entry<String,WordSequence>>(map.entrySet());
    	Collections.sort(entries, new Comparator<Map.Entry<String, WordSequence>>() {
    		public int compare(Entry<String, WordSequence> entry1, Entry<String, WordSequence> entry2) {
    			return ((String)entry2.getKey()).compareTo((String)entry1.getKey());
            }
        });
    	
    	return entries;
	}

	/**
	 * MapのStringキーでソートしたMap.Entryを返すメソッド。
	 * @param map 入力Map。キーはStringで単語列オブジェクト(WordSequence)のvalueを持つ。
	 * @return キーの文字列で降順でソートされたMap.Entryを格納したList。
	 * */
	public static ArrayList<String> retMaxValueStrings(List<Map.Entry<String, Double>> word2prob) {
		ArrayList<String> rlist = new ArrayList<String>(); 
		
		double pre_value = -10000.0;
    	boolean start_flag = false;
    	
    	for(Entry<String, Double> s: word2prob) {
    		String tstr = s.getKey();
    		Double value = s.getValue();
    		
    		if(value < pre_value && start_flag == true) { 
    			break;
    		} else {
    			rlist.add(tstr);
    		}
    		start_flag = true;
    		pre_value = value;
    	}
		return rlist;
	}
	
}
