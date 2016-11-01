package nlp.sample.sentencegenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/** 
 * NGramモデルの各種操作を行うクラス。
 * */
public class NGram {
	
	private String smoothingType;
	// 各種スムージングアルゴリズムオブジェクトへの参照
	private WordSequenceProb wordsequenceprob;
	
	/** 
	 * コンストラクタ。
	 * @param sensenArray は単語列を格納したArrayListを格納したArrayList
	 * @param smoothingにはスムージングのタイプをセットする。
	 * */
	public NGram(ArrayList<ArrayList<String>> sensenArray, String smoothing) {
		smoothingType = smoothing;	
		setProb(sensenArray);
	}
	
	/** 
	 * NGramモデルを生成するメソッド。 
     * @param dirname テキストファイルが格納されたディレクトリ。
     * @param smoothingType NGramのタイプ。"ml"は最尤推定、"laplase"はラプラススムージング、"backoff"はkatzのBackoffスムージングを行う。
     * */
	
	public static NGram retNGramModel(String dirname, String smoothingType) {
		NGram langmodel = null;
		try {	
			ArrayList<String> fileContentStringArray;
			fileContentStringArray = FindFile.retFileContentStringArray(dirname);
			WordFilter wordfilter = new WordFilter(new MorphologicalAnalyzer());
			ArrayList<ArrayList<String>> sensenArray = new ArrayList<ArrayList<String>>();
			
			for(String fileContent: fileContentStringArray) {
				 // System.out.println(fileContent);
				 ArrayList<ArrayList<String>> sensentmpArray =  wordfilter.retSentenceArrayArray(fileContent);
				 for(ArrayList<String> wordArray: sensentmpArray) {
					 sensenArray.add(wordArray);
				 }
			 }
			langmodel = new NGram(sensenArray, smoothingType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return langmodel;
	}
	
	/** 
	 * NGramの確率値を設定するメソッド。スムージングのタイプごとに確率値の設定方法が異なる。
	 * @param sensenArray は単語列を格納したArrayListを格納したArrayList
	 * @param smoothingにはスムージングのタイプをセットする。
	 * "laplase"を指定するとラプラススムージング、"backoff"を指定するとKatzのBack off スムージング、それ以外は最尤推定で確率値をセットする。
	 * */
	public void setProb(ArrayList<ArrayList<String>> sensenArray) {
		if(smoothingType.equals("laplase")) {
			wordsequenceprob = new LaplaseSmoothing(sensenArray);
		} else if(smoothingType.equals("backoff")) {
			wordsequenceprob = new BackOffSmoothing(sensenArray);
		} else {
			wordsequenceprob = new WordSequenceProb(sensenArray);
		}
	}
	
	/** 
	 * NGramの内容をARPA形式でファイルに書き込むメソッド。
	 * @param filename 書き込むファイル名。
	 * */
	public void dumpModel(String filename) {
		try {
			wordsequenceprob.printARPAFormat(filename);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/** 
	 * NGramの内容をARPA形式で標準出力に表示するメソッド。
	 * */
	public void showModel() {
		try {
			wordsequenceprob.printARPAFormat();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * NGramに基づいて文生成を行うメソッド。文頭記号の&gl;S&gt;からNGramの条件付き確率値P(b|&gl;S&gt;)の値で
	 * を用いて形態素bを生成、以後はP(c|a,b)値を使って形態素列を生成し、以後句点または最大形態素長に
	 * 至るまで形態素列を生成することで文を生成する。
	 * @param max_length 生成文時の最大形態素長。制約となる。
	 * @param headmax trueが与えられると、P(b|&gl;S&gt;)が最大となるbが選択される。
	 * @param seqmax trueが与えられると文生成確率が最大となるような形態素が選択される。
	 * @return 生成された文文字列
	 * */
	public String retRandomSentence(int max_length, boolean headmax, boolean seqmax) {
		
		String prestr = "<S>";
		ArrayList<String> str_array = new ArrayList<String>();
		String tmpstr = retRandomString(prestr, headmax);
		String current_str = "";
		String rstr = prestr + tmpstr;
		
		str_array.add(prestr);
		str_array.add(tmpstr);
		int count = 1;
		
		while(tmpstr.equals("。") == false) {
			prestr = str_array.get(str_array.size() - 2);
			tmpstr = str_array.get(str_array.size() - 1);
			current_str = retRandomString(prestr, tmpstr, seqmax);
			str_array.add(current_str);
			
			rstr = rstr + current_str;
			
			if(count == max_length) {
				break;
			}
			count += 1;
		}
		return rstr;		
	}
	
	private String retRandomString(String target, boolean headmax) {
		ArrayList<String> elem;
		if(headmax == true) {
			elem = relavantStringsMaxProb(target);
		} else {
			elem = relavantStrings(target);
		}
		return retShuffledResult(elem);
	}
		
	private String retRandomString(String target1, String target2, boolean headmax) {
		ArrayList<String> elem;
		if(headmax == true) {
			elem = relavantStringsMaxProb(target1, target2);
		} else {
			elem = relavantStrings(target1, target2);
		}
		return retShuffledResult(elem);
	}
	
	private String retShuffledResult(ArrayList<String> elem) {
		Collections.shuffle(elem);
		String key = elem.get(0);
		return key;
	}
	
	private ArrayList<String> relavantStringsMaxProb(String str) {
    	// List 生成 (ソート用)
		HashMap<String, Double> map = wordsequenceprob.retPostStringCandidateHashMap(str);
		List<Map.Entry<String, Double>> word2prob = MapUtil.retSortedMapDouble(map);
    	return MapUtil.retMaxValueStrings(word2prob);
	}
	
	private ArrayList<String> relavantStrings(String str) {
    	ArrayList<String> rlist = wordsequenceprob.retPostStringCandidateArrayList(str);
		if( rlist.isEmpty() == true ) {
			rlist.add("。");
			return rlist;
		}
		return rlist;
	}
	private ArrayList<String> relavantStrings(String str1, String str2) {
    	// List 生成 (ソート用)
		ArrayList<String> rlist = wordsequenceprob.retPostStringCandidateArrayList(str1, str2);	
		if( rlist.isEmpty() == true ) {
			rlist = wordsequenceprob.retPostStringCandidateArrayList(str2);
		} 
		
		if( rlist.isEmpty() == true ) {
			rlist.add("。");
			return rlist;
		}
		return rlist;
	}
	
	private ArrayList<String> relavantStringsMaxProb(String str1, String str2) {
		
		HashMap<String, Double> map = wordsequenceprob.retPostStringCandidateHashMap(str1, str2);
		
		if( map.isEmpty() == true ) {
			map = wordsequenceprob.retPostStringCandidateHashMap(str2);
		} 
		
		if( map.isEmpty() == true ) {
			ArrayList<String> rlist = new ArrayList<String>();
			rlist.add("</S>");
			return rlist;
		}
		
		List<Map.Entry<String, Double>> word2prob = MapUtil.retSortedMapDouble(map);
		
    	return MapUtil.retMaxValueStrings(word2prob);
	}
	
	/** 
	 * 単語列の生成確率を返すメソッド。
	 * @param wordArray 単語列文字列が格納されたArrayList
	 * @return 対数確率値
	 * */
	public double retGenerationalLogProb(ArrayList<String> wordArray) {
		double value = Math.log(0.0);
		value = wordsequenceprob.retGenerationalProbNgram(wordArray, 3);
		return value;
	}
	
}
