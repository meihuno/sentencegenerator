package nlp.sample.sentencegenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

/** 
 * テストデータの分割機能を提供するクラス。Cross Validation用のテストデータ分割を行う。
 * */
public class DataContainer {
	
	/**
	 * ディレクトリ下のテキストファイルのテキストをテスト用に分割してHashMapに格納して返すメソッド
	 * @param dirname ディレクトリのパス
	 * @param cvnum Cross Validation のN分割のN数
	 * @return 分割したテキストデータを格納したHashMap。分割されたテキストデータをキーの数字で関連づけたHashMapを返す。
	 * cvnumが2の場合テキストデータを2つに分割し、分割したテキストデータを0と1とで関連づけてHashMapに格納する。
	 * */
	public static HashMap<Integer, ArrayList<String>> dividedSentenceHashMap(String dirname, int cvnum) throws Exception {
		 
		HashMap<Integer, ArrayList<String>> senmap = new HashMap<Integer, ArrayList<String>>();
		 
		ArrayList<String> fileContentStringArray = FindFile.retFileContentStringArray(dirname);
		ArrayList<String> tmpSentenceArray = new ArrayList<String>();
		System.out.println(fileContentStringArray);
		for(String fileContent: fileContentStringArray) {
			ArrayList<String> sentence_array = retSplitSentenceArray(fileContent);
			 for(String sentenceString: sentence_array) {
				 if(sentenceString.length() != 0) {
					 String instr = sentenceString + "。";
					 tmpSentenceArray.add(instr);
				 }
			 }
		 }
		 
		 int size = tmpSentenceArray.size();
		 int num = size / cvnum;
		 
		 int count = 0;
		 ArrayList<String> tmpArray = new ArrayList<String>(); 
		 for(String sentenceString: tmpSentenceArray) {
			 tmpArray.add(sentenceString);
			 if (tmpArray.size() == num) {
				 senmap.put(count, tmpArray);
				 count += 1;
				 tmpArray = new ArrayList<String>();  
			 }
		 }
		 if(tmpArray.size() != 0) {
			 senmap.put(count, tmpArray);
		 }
		 return senmap;
	}
	
	/**
	 * 形態素解析結果を格納したArrayListをテスト用に分割してHashMapに格納して返すメソッド
	 * @param senArray 形態素解析結果を格納したArrayListを格納したArrayList
	 * @param Cross Validation のN分割のN数
	 * @return 分割した形態素解析結果を格納したHashMap。分割の数字関連づけたHashMapを返す。
	 * cvnumが2の場合テキストデータを2つに分割し、分割したテキストデータを0と1とで関連づけてHashMapに格納する。
	 * */
	public static HashMap<Integer, ArrayList<ArrayList<String>> > retSplitSenMap( ArrayList<ArrayList<String>> senArray,  int cvnum) {
		 
		 HashMap<Integer, ArrayList< ArrayList<String> >> senmap = new HashMap<Integer, ArrayList<ArrayList<String>> >();
		 ArrayList<ArrayList<String>> tmpArray = new ArrayList<ArrayList<String>>();
		 
		 int size = senArray.size();
		 int num = size / cvnum;
		 int count = 0;
		 
		 for(ArrayList<String> wordArray: senArray) {
			 tmpArray.add(wordArray);
			 size = tmpArray.size();
			 
			 if (tmpArray.size() == num) {
				 senmap.put(count, tmpArray);
				 count += 1;
				 tmpArray = new ArrayList<ArrayList<String>>();  
			 }
			 
		 }
		 
		 if(tmpArray.size() != 0) {
			 senmap.put(count, tmpArray);
		 }
		 return senmap;
	}
	
	/** 
	 * テキストを文分割するメソッド
	 * @param sentences テキスト
	 * @return テキストの文字列を句点で分割したArrayList
	 * */
	public static ArrayList<String> retSplitSentenceArray(String sentences) {
		ArrayList<String> sentence_array = new ArrayList<String>();
		Pattern p = Pattern.compile("。");
		String[] lines = p.split(sentences);
		for(String sentence: lines) {
	   		 sentence_array.add(sentence);
	   	 }
		return sentence_array; 
	}
	
}
