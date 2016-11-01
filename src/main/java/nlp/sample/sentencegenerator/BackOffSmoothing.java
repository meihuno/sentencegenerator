package nlp.sample.sentencegenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * WordSequenceProbクラスのNGramの基本機能を継承し、KatzのBack Off スムージングの機能を提供するクラス。
 * */

public class BackOffSmoothing extends WordSequenceProb {

	private HashMap<String, Double> alphaMap;
	private HashMap<Integer, Integer> zerofreqmap;
	private static int GRAMNUM = 3;
	private GoodTuringEstimater goodturingvalue;
	
	/**
	 * コンストラクタにてalphaの計算とGoodTuring推定値による最尤推定値の修正を行っている。
	 * @param sensenArray 単語区切り結果を格納したArrayListを格納したArrayList
	 * */
	BackOffSmoothing(ArrayList<ArrayList<String>> sensenArray) {
		super(sensenArray);
		alphaMap = new HashMap<String, Double>();
		reductionSequenceProb(wordsequencemap);
		// Backoff の α の計算にはgoodTuringによる調整値を用いる。
		setFollowProbMap();
	}
	
	/**
	 * 表示用文字列を返すメソッド。
	 * @return 表示用文字列
	 * */
	@SuppressWarnings("unused")
	private String retOutputDataString() {
		String outputString = "";
		
		List<Map.Entry<String, WordSequence>> list = MapUtil.retSortedMapStringKey(wordsequencemap.wordseqmap);
		Collections.reverse(list);
		
		int lkey = list.size();
		for(int i = 0; i < lkey; i++) {
			Entry<String, WordSequence> s = list.get(i);
			String key = s.getKey();
			WordSequence wsq = wordsequencemap.wordseqmap.get(key);
			double prob = wsq.prob;
			prob = Math.exp(prob);
			outputString += key + " ";
			outputString += String.valueOf(prob);
			if(alphaMap.containsKey(key) == true) {
				double alphaValue = alphaMap.get(key);
				outputString += " " + String.valueOf(alphaValue);
			}
			outputString += "\n";
		}
		return outputString;
	}
	
	private ArrayList<String> splitToList(String str) {
		String[] items = str.split(":");
		ArrayList<String> itemList = new ArrayList<String>(Arrays.asList(items));
		return itemList;
	 }
	
	private String retFollowString1(String[] pairs, int gram_n) {
		String w1 = pairs[1];
		if(gram_n > 2) {
			for(int i = 2; i < gram_n; i++) {
				w1 = w1 + ":" + pairs[i];
			}
		}
		return w1;
	}
	
	private String retFollowString2(String[] pairs, int gram_n) {
		String w2 = pairs[0];
		if(gram_n > 2) {
			for(int i = 1; i < gram_n-1; i++) {
				w2 = w2 + ":" + pairs[i];
			}
		}
		return w2;
	}
	
	/*
	 * 単語列ごとの確率値の和をHashMapに格納するメソッド
	 * */
	private void setFollowProbSumMap(HashMap<String, Double> condprobmap, HashMap<String,Double> uniprobmap) {
		
		for(Entry<String, WordSequence> s: wordsequencemap.wordseqmap.entrySet()) {
			
			WordSequence wsq = s.getValue();
			int gram_n = wsq.gram_n;
			
			if(gram_n > 1) {
				
				String word = wsq.words;
				String[] pairs = word.split(":");
				
				String w1w2 = retFollowString2(pairs, gram_n);
				String w2w3 = retFollowString1(pairs, gram_n);
				String w3 = pairs[gram_n-1];
				
				String key1 = String.valueOf(gram_n-1) + "@" + w1w2;
				String key2 = String.valueOf(gram_n-1) + "@" + w2w3;
				String key3 = String.valueOf(1) + "@" + w3;
				
				// w1w2w3とあった場合に、単語列 w1w2ごとに確率値の和をとればよい
				// 分子の sum_{w3C(w1w2w3) P(w3|w2w1) の計算) の計算
				// w3|w1,w2の最尤推定値
				double prob = Math.exp(wsq.prob);
				double value = 0.0; // 対数値でないのは注意
				if (condprobmap.containsKey(key1) == true ) {
					value = condprobmap.get(key1);
					condprobmap.put(key1, value + prob);
				} else {
					condprobmap.put(key1, prob);
				}
				
				// w3|w2
				WordSequence wsq2;
				double prob2 = 0.0;
				if(wordsequencemap.hasKey(key2) == true ) {
					wsq2 = wordsequencemap.retValue(key2);
					prob2 = Math.exp(wsq2.prob);
				} else if (wordsequencemap.hasKey(key3) == true) {
					wsq2 = wordsequencemap.retValue(key3);
					prob2 = Math.exp(wsq2.prob);
				} 
								
				// 分母の sum_{w3C(w1w2w3) P(w3|w2) の計算
				if (uniprobmap.containsKey(key1) == true ) {
					value = uniprobmap.get(key1);
					uniprobmap.put(key1, value + prob2);
				} else {
					uniprobmap.put(key1, prob2);
				}
			}
		}
		
	}
	
	/*
	 * 単語列ごとの確率値の和を用いて単語列ごとのalphaの値を設定してHashMapに格納するメソッド
	 * */
	private void setFollowProbMap() {
		
		HashMap<String, Double> condprobmap = new HashMap<String,Double>();
		HashMap<String, Double> uniprobmap = new HashMap<String,Double>();
		setFollowProbSumMap(condprobmap, uniprobmap);
		double numerator = 0.0;
		double denominator = 0.0;
		
		for(Entry<String, WordSequence> s: wordsequencemap.wordseqmap.entrySet()) {
			WordSequence wsq = s.getValue();
			int gram_n = wsq.gram_n;
			if(gram_n > 1) {
				String word = wsq.words;
				String[] pairs = word.split(":");
				
				String w1w2 = retFollowString2(pairs, gram_n);
				String key1 = String.valueOf(gram_n-1) + "@" + w1w2;
				
				// w1w2w3とあった場合に、単語列 w1w2ごとに確率値の和をとればよい
				
				double value1, value2, alpha;
				value1 = condprobmap.get(key1);
				value2 = uniprobmap.get(key1);
				numerator = 1.0 - value1;
				denominator = 1.0 - value2;
				
				if(denominator == 0.0 || numerator == 0.0) {
					alpha = 1.0;
				} else {	
					alpha = numerator / denominator;	
				}
				
				if(Double.isNaN(alpha)){ 
					System.out.println(key1);
					System.out.println(value1);
					System.out.println(value2);
					System.out.println(numerator);
					System.out.println(denominator);
				}
				alphaMap.put(key1, alpha);
			}
		}
	}
	
	/*
	 * GoodTuring推定値と用いて最尤推定値を修正するメソッド。
	 * */
	private void reductionSequenceProb(WordSequenceMap wsqm) {
		goodturingvalue = new GoodTuringEstimater(wsqm);
		for(Entry<String, WordSequence> s: wsqm.wordseqmap.entrySet()) {
			WordSequence wsq = s.getValue();
			int gram_n = wsq.gram_n;
			int freq = wsq.frequency;
			double mllogprob = wsq.prob;
			
			// sharp_r は log ではない
			double sharp_r = goodturingvalue.retSharpR(freq, gram_n);
			double discount_value = (sharp_r)/(double)freq;
			double discounted_mlprob = Math.log(discount_value) + mllogprob;
			wsq.prob = discounted_mlprob;
			/*
			System.out.println(key);
			System.out.println(sharp_r);
			System.out.println(freq);
			System.out.println(mllogprob);
			System.out.println(discount_value);
			System.out.println(Math.log(discount_value));
			System.out.println(discounted_mlprob);
			*/	
		}
	}	
	
	private HashMap<Integer, Integer> initzerofreqmap() {
		zerofreqmap = new HashMap<Integer, Integer>();
		for(int i = 0; i < GRAMNUM; i++ ) {
			zerofreqmap.put(i+1, 0);
		}
		return zerofreqmap;
	}
	
	private void setZeroFreqMap(List<String> list) {
		zerofreqmap = initzerofreqmap();
		List<String> sublist;
		for(int i = 0; i < list.size(); i++) {
			sublist = list.subList(i, i+1);
			String key = wordsequencemap.retKey(sublist);
			if(wordsequencemap.hasKey(key) == false) {
				int zf = zerofreqmap.get(1);
				zerofreqmap.put(1, zf+1);
			}
			if(i > 1) {
				sublist = list.subList(i-1, i+1);
				key = wordsequencemap.retKey(sublist);
				if(wordsequencemap.hasKey(key) == false) {
					int zf = zerofreqmap.get(2);
					zerofreqmap.put(1, zf+1);
				}
				if(i > 2) {
					sublist = list.subList(i-2, i+1);
					key = wordsequencemap.retKey(sublist);
					if(wordsequencemap.hasKey(key) == false) {
						int zf = zerofreqmap.get(3);
						zerofreqmap.put(1, zf+1);
					}
				} 
			}
		}
	}
	
	/*
	 * NGramの確率値を返すメソッド
	 * @param wordArray 形態素解析結果を格納したArrayList
	 * @param gram_n Gram数
	 * @return 確率対数値
	 * */
	public double retGenerationalProbNgram(ArrayList<String> wordArray, int gram_n) {
		
		double prob = 0.0;
		double tmpprob = 0.0;
		
		List<String> list = wordArray;
		setZeroFreqMap(list); 
		
		if(wordArray.size() >= gram_n ) {
			for(int i = (gram_n-1); i < list.size(); i++) {
				
				List<String> sublist = list.subList(i-(gram_n-1),i+1);
				String key = wordsequencemap.retKey(sublist);
				
				if(wordsequencemap.hasKey(key) == true) {
					tmpprob = wordsequencemap.retCurrentWordSequenceProb(sublist);	
				} else {
					tmpprob = retAlphaProb(key, gram_n);
					
				}
				prob = prob + tmpprob;
			}
		}
		
		if(prob == 0.0) {
			return Math.log(1.0);	
		} else {
			return prob;
		}	
	}
	
	private String retTailString(ArrayList<String> pairs) {
		if(pairs.isEmpty() == true) {
			return "";
		}
		if(pairs.size() == 1 ) {
			return pairs.get(0);
		}
		String str = pairs.get(pairs.size()-1);
		for(int i = pairs.size() -2; i >= 1; i-- ) {
			str = pairs.get(i) + ":" + str;
		}
		return str;		
	}

	/*
	 * alpha値を返すメソッド
	 * @param 単語列。Gram数@:区切りの単語列形式
	 * @param gran_num Gram数
	 * */
	private double retAlphaProb(String str, int gram_num) {
		
		double value = Math.log(0.0);
		double prelogvalue = Math.log(0.0);
		
		double alpha = 1.0;
		
		String substr = str.split("@")[1];
		
		ArrayList<String> pairs = splitToList(substr);
		
		if( pairs.size() > 1 ) {
			String w2w3 = retTailString(pairs);
			
			// ここで再帰的に alpha(w1,w2) * P(w3|w2)を計算できるようにする
			// w2,w3がなければ、P(w3|w2) = alpha(w2) * P(w3)
			// C(w2) = 0 の場合は、P(w3|w2) = P(w3)
			String prekey2 = String.valueOf(gram_num-1) + "@" + w2w3;
			
			if (wordsequencemap.wordseqmap.containsKey(prekey2) == true ) {
				WordSequence wpsqm = wordsequencemap.wordseqmap.get(prekey2);
				prelogvalue = wpsqm.prob;
			} else {
				prelogvalue = retAlphaProb(prekey2, gram_num-1);
			}
			value = Math.log(alpha) + prelogvalue;
		} else {
			String prekey2 = pairs.get(0);
			if (wordsequencemap.wordseqmap.containsKey(prekey2) == true ) {
				WordSequence wpsqm = wordsequencemap.wordseqmap.get(prekey2);
				value = wpsqm.prob;
			} else {
				value = retZeroUniProb();
			}
			
			if(Double.isNaN(value)){
				System.out.println(prekey2);
			}
			
		}
		return value;
	}
	
	private double retZeroUniProb() {
		double value = 0.0;
		int nfreq = 1;
		if (wordsequencemap.freqNmap.containsKey(1) == true ) {
			nfreq = wordsequencemap.freqNmap.get(1);
		}
		int n1freq = goodturingvalue.retNRfreq(1,1);
		int n0freq = 1;
		if(zerofreqmap.containsKey(1) == true) {
			n0freq = zerofreqmap.get(1);
		}
		value = (double)n1freq/(double)(n0freq*nfreq);
		return value;
	}
	
	/*
	public double retUnigramLogProb2(String str) {
		double value = Math.log(0.0);
		int N1 = wordsequencemap.freqNmap.get(1);
		if (N1 == 0) {
			return value;
		}
		
		if ( wordsequencemap.wordseqmap.containsKey(str) == true ) {
			WordSequence wpsqm = wordsequencemap.wordseqmap.get(str);
			int freq = wpsqm.frequency;
			value = Math.log( (double)freq ) - Math.log( (double)N1 );
		} 
		return value;
	}

	public double retConditionalLogProb2(int gram_num, String str) {
		double value = Math.log(0.0);
		String prekey = retPreKey(str);
		if (wordsequencemap.hasKey(prekey) == true) {
			WordSequence wpsqm = wordsequencemap.retValue(prekey);
			int N = wpsqm.frequency;
			if (wordsequencemap.hasKey(str) == true ) {
				WordSequence wpsqm2 = wordsequencemap.retValue(str);
				int freq = wpsqm2.frequency;
				value = Math.log( (double)freq ) - Math.log( (double)N );
			}
		}
		return value;
	}
	
	*/
}
