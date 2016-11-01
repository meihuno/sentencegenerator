package nlp.sample.sentencegenerator;


import nlp.sample.sentencegenerator.WordSequenceProb;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.HashMap;

/**
 * WordSequenceProbクラスのNGramの基本機能を継承し、ラプラススムージングの機能を提供するクラス。
 * */
public class LaplaseSmoothing extends WordSequenceProb{
	
	private HashMap<Integer, Integer> vfreqmap;
	private double sigma = 0.5;
	
	LaplaseSmoothing(ArrayList<ArrayList<String>> sensenArray) {
		super(sensenArray);
	}
	
	private void incIntNum(int key, int freq, HashMap<Integer, Integer> map){
		
		if(map.containsKey(key)) {
			int value = map.get(key);
			map.put(key, value + freq); 
		} else {
			map.put(key, freq); 
		}
	}
	
	public void setWordSequenceProb(WordSequenceMap wordsequencemap) {
		vFreqMap();
		for(Entry<String, WordSequence> s: wordsequencemap.wordseqmap.entrySet()) {
			String key = s.getKey();
			WordSequence wsq = s.getValue();
			int gram_n = wsq.gram_n;
			wsq.prob = retNgramLogProb(key, gram_n);
		}
	}	
	
	private void vFreqMap() {
		vfreqmap = new HashMap<Integer, Integer>();
		for(Entry<String, WordSequence> s: wordsequencemap.wordseqmap.entrySet()) {
			WordSequence wsq = s.getValue();
			int gram_n = wsq.gram_n;
			incIntNum(gram_n, 1, vfreqmap);
		}
	} 
	
	/**
	 * NGramの条件付き確率を返すメソッド。
	 * @param str 語または形態素列のキー（Gram数@:区切り文字列）
	 * @param gram_n Gram数
	 * @return 確率対数値
	 * */
	public double retNgramLogProb(String str, int gram_num) {
		double value = Math.log(0.0);
		
		if(gram_num == 2 || gram_num == 3) {
			value = retConditionalLogProb(gram_num, str);
		} else if(gram_num == 1 ) {
		    value = retUnigramLogProb(str);	
		}
		return value;
	}
	
	private double retConditionalLogProb(int gram_num, String str) {
		double value = Math.log(0.0);
		String prekey = retPreKey(str);
		double V = sigma * (double)vfreqmap.get(gram_num);
		double freq = sigma * 1.0;
		
		if (wordsequencemap.hasKey(prekey) == true) {
			
			WordSequence wpsqm = wordsequencemap.retValue(prekey);
			V = V + (double)wpsqm.frequency;
			
			if (wordsequencemap.wordseqmap.containsKey(str) == true ) {
				WordSequence wpsqm2 = wordsequencemap.wordseqmap.get(str);
				freq = freq + (double)wpsqm2.frequency;
			}
		}
		
		value = Math.log(freq) - Math.log(V);
		return value;
	}

	private double retUnigramLogProb(String str) {
		double value = Math.log(0.0);
		
		double V = sigma * (double)vfreqmap.get(1);
		double freq = sigma * 1.0;
		
		V = V + (double)wordsequencemap.freqNmap.get(1);
		
		if (V == 0.0) {
			return value;
		}
		
		if ( wordsequencemap.wordseqmap.containsKey(str) == true ) {
			WordSequence wpsqm = wordsequencemap.wordseqmap.get(str);
			freq = freq + (double)wpsqm.frequency;
		} 
		value = Math.log(freq) - Math.log(V);
		return value;
	}

}
