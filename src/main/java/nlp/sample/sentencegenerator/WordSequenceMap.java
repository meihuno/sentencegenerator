package nlp.sample.sentencegenerator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/** 
 * WordSequenceMapクラスは形態素連接を表すクラス。
 * @author meihuno.huno.san@gmail.com
 * @version 0.0.1
 * */

public class WordSequenceMap {
	/** 
	 * 形態素連接を格納するHashMap。keyは「Gram数@:区切りの形態素列」の形式
	 * */
	public HashMap<String, WordSequence> wordseqmap;
	
	/** 
	 * NGramごとの頻度を格納するHashMap
	 * */
	public HashMap<Integer, Integer> freqNmap;
	
	/** 
	 * 形態素連接を格納するHashMapとNGramごとの頻度を格納するHashMapを初期化する。
	 * */
	WordSequenceMap() {
		wordseqmap = new HashMap<String, WordSequence>();
		freqNmap = new HashMap<Integer, Integer>();
	}
	
	/** 
	 * 形態素連接を格納するHashMapであるwordseqmapに入力キー(Gram数@:区切り形態素列の形式)があるか否かを返すメソッド。
	 * @param key「Gram数@:区切りの形態素列」の形式
	 * @return 真偽値
	 * */
	public boolean hasKey(String key) {
		if(wordseqmap.containsKey(key) == true) {
			return true;
		}
		return false;
	}
	
	/**
	 * 形態素列を表示するメソッド。wordseqmapの各値の表示メソッドを呼び出す。
	 * */
	public void showWordSeqMap() {
		for(Entry<String, WordSequence> s: wordseqmap.entrySet()) {
			WordSequence wsqm = s.getValue();
			wsqm.showWordSequenceInfo();
		}
	}
	
	/**
	 * 形態素列がwordseqmapに含まれているかをチェックするメソッド。
	 * @param wordArray 形態素文字列を格納したList。
	 * @return 真偽値
	 * */
	public boolean hasSequence(List<String> wordArray) {
		String key = retKey(wordArray);
		if(wordseqmap.containsKey(key) == true) {
			return true;
		}
		return false;
	}
	
	/**
	 * 形態素連接を格納するHashMapにキーで問い合わせを行い、その値を返すメソッド。
	 * @param key key「Gram数@:区切りの形態素列」の形式
	 * @return 形態素列を表すWordSequenceオブジェクト。
	 * */
	public WordSequence retValue(String key) {
		return wordseqmap.get(key);
	}
	
	/**
	 * 形態素連接を格納するHashMapにキーで問い合わせを行い値を返すメソッド。
	 * @param key key「Gram数@:区切りの形態素列」の形式
	 * @return 形態素列を表すWordSequenceオブジェクト。
	 * */
	public void setNfreqMap(){
		for(Entry<String, WordSequence> s: wordseqmap.entrySet()) {
     		WordSequence wsdq = s.getValue();
     		int gram_n = wsdq.gram_n;
     		int freq = wsdq.frequency;
     		incNfreqMap(gram_n, freq);
		}
	}
	
	private void incNfreqMap(int key, int freq){
		if(freqNmap.containsKey(key)) {
			int value = freqNmap.get(key);
			freqNmap.put(key, value + freq); 
		} else {
			freqNmap.put(key, freq); 
		}
	}
	
	/**
	 * 形態素連接を格納するHashMapを設定するメソッド。
	 * @param gram_num Gram数
	 * @param str :区切りの形態素列文字列
	 * @param freq 頻度
	 * */
	public void setWordSequenceMap(int gram_n, String str, int freq) {	
		setWordSequenceMapSub(wordseqmap, gram_n, str, freq); 
	}
	
	private void setWordSequenceMapSub(HashMap<String,WordSequence> map,int n,String str, int fq) {
		String key = String.valueOf(n) + "@" + str;
		if(map.containsKey(key)) {
			WordSequence tmpwsq = map.get(key);
			tmpwsq.frequency = tmpwsq.frequency + fq;
			map.put(key, tmpwsq); 
		} else {
			WordSequence tmpwsq = new WordSequence();
			tmpwsq.gram_n = n;
			tmpwsq.frequency = fq;
			tmpwsq.words = str;
			map.put(key, tmpwsq); 
		}
	}
	
	/**
	 * 形態素を格納したListから"Gram数@:区切り形態素列"の文字列を返すメソッド。
	 * @param wordArray 形態素文字列を格納した配列
	 * @return "Gram数@:区切り形態素列"の文字列
	 * */
	public String retKey(List<String> wordArray) {
		int size = wordArray.size();
		String subkey = retJoinedString(wordArray, ":");
		String key = String.valueOf(size) + "@" + subkey;
		return key;
	}

	private boolean hasWordSequence(List<String> wordArray) {
		String key = retKey(wordArray);
		if(wordseqmap.containsKey(key) == true) {
			return true;
		}
		return false;
	}
	
	/** 
	 * 形態素連接確率を返すメソッド。
	 * @param wordArray 形態素列の配列
	 * @return 確率対数値
	 * */
	public double retCurrentWordSequenceProb(List<String> wordArray) {
		if(hasWordSequence(wordArray) == true ) {
			String key = retKey(wordArray);
			WordSequence wsq = wordseqmap.get(key);
			return wsq.prob;
			
		} else {
			return Math.log(0.0);
		}
	}
	
	private String retJoinedString(List<String> wordArray, String deli) {
		String rstr = "";
		int count = 0;
		for(String s: wordArray) {
			if(count == 0) {
				rstr = s;
			} else {
				rstr = rstr + deli + s;
			}
			count += 1;
		}
		return rstr;
	}
	
}
