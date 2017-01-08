package nlp.sample.sentencegenerator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.FileWriter;

/**
 * NGramモデルの確率値、単語の条件付き確率値を計算するクラス。
 * */
public class WordSequenceProb {
	
	protected WordSequenceMap wordsequencemap;
	private static int GRAMNUM = 3;
	/**
	 * WordSequneceProbクラスのコンストラクタ。
	 * @param 単語列を格納したArrayListを格納したArrayList。単語のArrayListに先頭には文頭記号を、末尾に文末記号を挿入している。
	 * */
	WordSequenceProb(ArrayList<ArrayList<String>> sensenArray) {
		
		wordsequencemap = new WordSequenceMap();
		for(ArrayList<String> wordArray: sensenArray) {
			if(wordArray.size() != 0 ) {
				wordArray.add(0, "<S>");
				wordArray.add("</S>");
					setWordSequenceMap(wordsequencemap, wordArray);
			}
		}
		 
		wordsequencemap.setNfreqMap();
		setWordSequenceProb(wordsequencemap);
		//wordsequencemap.showWordSeqMap();
	}
	
	public void showModel() {
		wordsequencemap.showWordSeqMap();
	}
	
	/**
	 * NGramモデルをARPA形式でファイルに書き込むメソッド。
	 * @param filename ファイル名。
	 * */
	public void printARPAFormat(String filename) throws Exception{
		
		File file = new File(filename);
		FileWriter out = new FileWriter(file);
		
		String outputString = retARPAFormatString();
		
		out.write(outputString);
		out.close();
	}
	
	private String retOutputHeaderString() {
		
		String outputString = "";
		outputString += "[n]\n";
		outputString += String.valueOf(GRAMNUM) + "\n\n";
		
		outputString += "[smoother]\n";
		outputString += getClass().getName() + "\n\n";
		
		outputString += "\\data\\\n";
				
		for(Entry<Integer, Integer> s: wordsequencemap.freqNmap.entrySet()) {
			int key = s.getKey();
			int value = s.getValue();
			outputString += "ngram";
			outputString += String.valueOf(key);
			outputString += " = ";
			outputString += String.valueOf(value);
			outputString += "\n";
			//wsqm.showWordSequenceInfo();
		}
		return outputString;
	}
	
	private String retARPAFormatString() {
		String outputString = "";
		
		outputString += retOutputHeaderString();
		outputString += retOutputDataString();
				
		return outputString;
	};
	
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
			outputString += "\n";
		}
		return outputString;
	}
	
	/**
	 * NGramモデルをARPA形式で標準出力に表示するメソッド。
	 * */
	public void printARPAFormat() throws Exception{
		String outputString = retARPAFormatString();
		System.out.print(outputString);
	}
	
	private void setWordSequenceMap(WordSequenceMap wsqm, ArrayList<String> words) {
		
		for(int i = 0; i < words.size(); i++) {
			String word = words.get(i);
			
			// Unigram
			wsqm.setWordSequenceMap(1, word,1);
			
			// Bigram
			if(i < words.size()-1) {
				String word2 = word + ":" + words.get(i+1);
				wsqm.setWordSequenceMap(2, word2, 1);
				// Trigram
				if(i < words.size()-2) {
					String word3 = word2 + ":" + words.get(i+2);
					wsqm.setWordSequenceMap(3, word3, 1);
				}
			}
		} 
	}
	
	/**
	 * 単語オブジェクトに確率値をセットするメソッド。
	 * @param wsqm 単語列オブジェクト（WordSequenceMapオブジェクト）。Gramごとの単語列が格納されている。
	 * */
	protected void setWordSequenceProb(WordSequenceMap wsqm) {
		for(Entry<String, WordSequence> s: wsqm.wordseqmap.entrySet()) {
			String key = s.getKey();
			WordSequence wsq = s.getValue();
			int gram_n = wsq.gram_n;
			wsq.prob = retNgramLogProb(key, gram_n);
		}
	}	
	
	/**
	 * 単語列の先行文脈部分のキーを返すメソッド。例)2@東京:都から1@東京を返す。
	 * @param str 単語列のキー（Gram@:区切り文字列）
	 * */
	protected String retPreKey(String str) {
		String[] pairs = str.split("@")[1].split(":");
		int gram_n = Integer.parseInt(str.split("@")[0]);
		String prekey = "";
		if(gram_n == 3) {
			prekey = String.valueOf(2) + "@" + pairs[0] + ":" + pairs[1];
		} else if(gram_n == 2) {
			prekey = String.valueOf(1) + "@" + pairs[0];
		}
		if(prekey == "") {
			System.exit(-1);
		}
		return prekey;
	}
	
	private double retConditionalLogProb(String str) {
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
	
	/**
	 * NGramの条件付き確率を返すメソッド。
	 * @param str 単語列のキー（Gram数@:区切り文字列）
	 * @param gram_n Gram数
	 * @return 確率対数値
	 * */
	protected double retNgramLogProb(String str, int gram_num) {
		double value = Math.log(0.0);
		
		if(gram_num == 2 || gram_num == 3) {
			value = retConditionalLogProb(str);
		} else if(gram_num == 1 ) {
			
		    value = retUnigramLogProb(str);	
		}
		return value;
	}
	
	private double retUnigramLogProb(String str) {
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
	
	/**
	 * Unigram確率を返すメソッド。
	 * @param str 単語列のキー（Gram数@:区切り文字列）
	 * @return 確率値(0から1までの値)
	 * */
	protected double retUnigramProb(String str) {
		double value = retUnigramLogProb(str);
		return Math.exp(value);
	}
	
	/**
	 * NGramの条件付き確率を返すメソッド。
	 * @param str 単語列のキー（Gram数@:区切り文字列）
	 * @return 確率値(0から1までの値)
	 * */
	protected double retNgramProb(String str, int gram_n) {
		double value = retNgramLogProb(str, gram_n);
		return Math.exp(value);
	}
	
	/**
	 * 単語列の生成確率を返すメソッド。
	 * @param wordArray 単語列の格納された配列
	 * @param 生成確率計算に用いるNGramの最大数（現在1,2,3までをサポート）
	 * @return 対数確率値
	 * */
	protected double retGenerationalProbNgram(ArrayList<String> wordArray, int gram_n) {
		
		double prob = 0.0;
		double tmpprob = 0.0;
		
		if(wordArray.size() != 0) {
			if(wordArray.get(0).equals("<S>") != true ) {
				wordArray.add(0, "<S>");
			}
			int lastidx = wordArray.size() - 1;
			if(wordArray.get(lastidx).equals("</S>") != true ) {
				wordArray.add("</S>");
			}	
		}
		
		List<String> list = wordArray;
		List<String> sublist;
		
		for(int i = 1; i < list.size(); i++) {
			sublist = retSubListFromList(list, i, gram_n);
			String key = wordsequencemap.retKey(sublist);
			if(wordsequencemap.hasKey(key) == true) {
				tmpprob = wordsequencemap.retCurrentWordSequenceProb(sublist);
				
				//System.out.println(sublist);
				//System.out.println(tmpprob);
				
			} else {
				tmpprob = retNgramLogProb(key, gram_n);
			}
			prob = prob + tmpprob;
		}
		
		if(prob == 0.0) {
			return Math.log(1.0);	
		} else {
			return prob;
		}
		
	}
	
	private List<String> retSubListFromList(List<String> list, int i, int gram_n) {
		List<String> sublist;
		if(i == 0 ) { 
			sublist = list.subList(0,1);
		} else if (i == 1) {
			sublist = list.subList(0,2);
		} else {
			sublist = list.subList(i-(gram_n-1),i+1);
		}
		return sublist;
	}
	
	/**
	 * ある単語に後続する単語とそのBiGram確率値を格納したHashMapを返すメソッド。
	 * @param str 単語文字列
	 * @return 後続の単語と確率値を格納したHashMap。後続する単語が複数ある場合はキーの単語も複数格納される。
	 * */
	protected HashMap<String, Double>retPostStringCandidateHashMap(String str) {
		HashMap<String, Double> rhash = new HashMap<String, Double>();
		HashMap<String, WordSequence> wordseqmap = wordsequencemap.wordseqmap;
		for(Entry<String, WordSequence> s: wordseqmap.entrySet()) {
			WordSequence wsq = s.getValue();
			if(wsq.gram_n == 2) {
				String[] pairs = wsq.words.split(":");
				if (pairs[0].equals(str) == true) {
					String nstr = pairs[1];
					rhash.put(nstr, wsq.prob);
				}
			}
		}
		return rhash;
	}
	
	/**
	 * 単語列(2単語)に後続する単語とそのTriGram確率値を格納したHashMapを返すメソッド。
	 * @param str1 1つめの単語文字列
	 * @param str2 2つめの単語文字列
	 * @return 後続の単語と確率値を格納したHashMap。後続する単語が複数ある場合はキーの単語も複数格納される。
	 * */
	protected HashMap<String, Double>retPostStringCandidateHashMap(String str1, String str2) {
		HashMap<String, Double> rhash = new HashMap<String, Double>();
		HashMap<String, WordSequence> wordseqmap = wordsequencemap.wordseqmap;
		for(Entry<String, WordSequence> s: wordseqmap.entrySet()) {
			WordSequence wsq = s.getValue();
			if(wsq.gram_n == 3) {
				String[] pairs = wsq.words.split(":");
				if (pairs[0].equals(str1) == true && pairs[1].equals(str2) == true) {
					String nstr = pairs[2];
					rhash.put(nstr, wsq.prob);
				}
			}
		}
		return rhash;
	}
	
	/**
	 * ある単語に後続する単語群を格納したArrayListを返すメソッド。
	 * @param str 単語文字列
	 * @return 後続の単語群を格納したArrayList
	 * */
	protected ArrayList<String>retPostStringCandidateArrayList(String str) {
		ArrayList<String> rlist = new ArrayList<String>();
		HashMap<String, WordSequence> wordseqmap = wordsequencemap.wordseqmap;
		for(Entry<String, WordSequence> s: wordseqmap.entrySet()) {
			WordSequence wsq = s.getValue();
			if(wsq.gram_n == 2) {
				String[] pairs = wsq.words.split(":");
				if (pairs[0].equals(str) == true) {
					String nstr = pairs[1];
					rlist.add(nstr);
				}
			}
		}
		return rlist;
	}
	
	/**
	 * 単語列(2単語)に後続する単語群を格納したArrayListを返すメソッド。
	 * @param str1 1つめの単語文字列
	 * @param str2 2つめの単語文字列
	 * @return 後続の単語群を格納したArrayList
	 * */
	protected ArrayList<String>retPostStringCandidateArrayList(String str1, String str2) {
		ArrayList<String> rlist = new ArrayList<String>();
		HashMap<String, WordSequence> wordseqmap = wordsequencemap.wordseqmap;
		for(Entry<String, WordSequence> s: wordseqmap.entrySet()) {
			WordSequence wsq = s.getValue();
			if(wsq.gram_n == 3) {
				String[] pairs = wsq.words.split(":");
				if (pairs[0].equals(str1) == true && pairs[1].equals(str2) == true) {
					String nstr = pairs[2];
					rlist.add(nstr);
				}
			}
		}
		return rlist;
	}
}
