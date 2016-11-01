package nlp.sample.sentencegenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nlp.sample.sentencegenerator.MorphologicalAnalyzer;

/** テキストの整形機能を提供するクラス */
public class WordFilter {
	private static MorphologicalAnalyzer tokenizer;
	WordFilter(MorphologicalAnalyzer morphologicalanalyzer) {
		tokenizer = morphologicalanalyzer;
	}
	
	/** 
	 * 文字列をデリミタで分割するメソッド。
	 * @param str 入力文字列
	 * @param delimitor デリミタ
	 * @return 分割された文字列を格納するArrayList
	 * */
	public ArrayList<String> splitToList(String str, String delimitor) {
		String[] items = str.split(delimitor);
		ArrayList<String> itemList = new ArrayList<String>(Arrays.asList(items));
		return itemList;
	}
	
	/**
	 * 青空文庫のルビを修正するメソッド。
	 * @param text 入力テキスト
	 * @return 修正後の文字列
	 * */
	public String retReplacedRubyText(String text) {
		ArrayList<String> lines = splitToList(text, "\n");
		String rstr = "";
		
		Pattern p1 = Pattern.compile("^-+$");
		Pattern p2 = Pattern.compile("^底本：.*");
		Pattern p4 = Pattern.compile(".+[0-9a-zA-Zぁ-んァ-ヶ一-龠々ー]*.+$");
		
		boolean inoutflag = true;
		for(String line: lines) {
			
			Matcher m1 = p1.matcher(line);
			Matcher m2 = p2.matcher(line);
			Matcher m4 = p4.matcher(line);
			
			if( m2.matches() == true) {
				break;
			}
			
			if( m1.matches() == true && inoutflag == true) {
				inoutflag = false;
			} else if ( m1.matches() == true && inoutflag == false ) {
				inoutflag = true;
			} else if ( m1.matches() == false && inoutflag == true ) {
				line = line.replaceAll("《.+?》", "");
				if ( m4.matches() == true) {
					rstr += line + "\n";	
				} else {
					rstr += line + "。\n";
				}
			}	
		}	
		return rstr;
	}
	
	/** 
	 * 入力文に形態素解析処理を行うメソッド。単語区切りのみを行う。
	 * @param sentence 入力文
	 * @return 形態素解析結果（単語区切り結果）を格納したArrayList
	 * */
	public ArrayList<String> retSentenceLineArray(String sentence) {
		return WordFilter.tokenizer.retWordSeqArray(sentence);
	}
	
	/** 
	 * テキストに形態素解析処理を行うメソッド。単語区切りのみを行う。
	 * @param sentence テキスト（文の集まりを想定）
	 * @return 形態素解析結果（単語区切り結果）を格納したArrayListを格納したArrayList
	 * */
	public ArrayList<ArrayList<String>> retSentenceArrayArray(String line) {
		String line1 = retReplacedRubyText(line);
		
		ArrayList<ArrayList<String>> lines = new ArrayList<ArrayList<String>>();
		lines = WordFilter.tokenizer.retWordSentenceArray(line1);
		return lines;
	}
	
}
