package nlp.sample.sentencegenerator;

import java.util.ArrayList;

/**
 * SentenceGenerator は NGramモデルにより文生成を行うクラス。文確率の計算も行う。
 * @author meihuno.huno.san@gmail.com
 * @version 0.0.1
 * */

public class SentenceGenerator {

	private NGram langmodel;
	
	/**
	 * コンストラクタ。public。
	 * @param NGramのモデルを受け取る。
	 * */
	public SentenceGenerator(NGram ngmodel) {
		langmodel = ngmodel;
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
    	String rstr = langmodel.retRandomSentence(max_length, headmax, seqmax);
		return rstr;
	}
	        
    /**
     * NGramモデルを生成するメソッド。 
     * @param dirname テキストファイルが格納されたディレクトリ。
     * @param smoothingType NGramのタイプ。"ml"は最尤推定、"laplase"はラプラススムージング、"backoff"はkatzのBackoffスムージングを行う。
     * */
	public void createNGramModel(String dirname, String smoothingType) throws Exception {
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
	}
	
	/**
	 * モデルをファイルにダンプするメソッド。
	 * @param filename ダンプするファイル名。 
	 * */
	public void dumpModel(String filename) {
		if(langmodel != null) {
			langmodel.dumpModel(filename);
		}
	}
	
	/**
	 * モデルを表示するメソッド。　
	 * */
	public void showModel() {
		if(langmodel != null) {
			langmodel.showModel();
		}
	}
	
	/**
	 * 入力文の生成対数確率を返すメソッド。
	 * @param inputstr 入力文字列。
	 * @return 対数確率値。
	 * */
	public double retGenerationalLogProb(String inputstr) {
		WordFilter wordfilter = new WordFilter(new MorphologicalAnalyzer());
		ArrayList<String> wordArray = wordfilter.retSentenceLineArray(inputstr);
		double prob = langmodel.retGenerationalLogProb(wordArray);
		return prob;
	}
}
