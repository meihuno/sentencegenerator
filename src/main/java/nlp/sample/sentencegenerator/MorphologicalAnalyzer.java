package nlp.sample.sentencegenerator;

import org.atilika.kuromoji.Token;
import org.atilika.kuromoji.Tokenizer;
import java.util.ArrayList;

/** 
 * 形態素解析機能を提供するクラス。kuromojiのみをサポート。
 * */

public class MorphologicalAnalyzer {
	private Tokenizer tokenizer;
	MorphologicalAnalyzer() {
		tokenizer = Tokenizer.builder().build();
	}
	
	/** 
	 * 文字列(入力文)から形態素解析結果のArrayListを返すメソッド
	 * @param text 入力文字列
	 * @return 形態素解析結果の区切り文字列が格納されたArrayList 
	 * */
	public ArrayList<String> retWordSeqArray(String text){
		ArrayList<String> wordseq = new ArrayList<String>();
		for(Token token : tokenizer.tokenize(text)) {
			wordseq.add(token.getSurfaceForm());
		}
		return wordseq;
	}
	
	private boolean checkDelimitor(String tmpsurface, String tmpmorph) {
		if( tmpmorph.equals("記号,句点,*,*") == true) {
			return true;
		} else if( tmpmorph.equals("名詞,サ変接続,*,*") && tmpsurface.equals("?")) {
			return true;
		} else if( tmpmorph.equals("記号,一般,*,*") &&  tmpsurface.equals("？") ) {
			return true;
		}
		return false;
	}
	
	private boolean checkSpace(String tmpsurface, String tmpmorph) {
		//System.out.println(tmpmorph);
		if( tmpmorph.equals("記号,空白,*,*") == true) {
			return true;
		}
		return false;
	}
	
	/** 
	 * 文字列（入力テキスト）から文ごとの形態素解析結果を返すメソッド。
	 * @param text 入力テキスト
	 * @return 文ごとの形態素解析結果を格納したArrayListを格納したArrayList 
	 * */
	public ArrayList<ArrayList<String>> retWordSentenceArray(String text){
		ArrayList<ArrayList<String>> wordseq = new ArrayList<ArrayList<String>>();
		ArrayList<String> tmpArray = new ArrayList<String>();
		String tmpsurface= "";
		String tmpmorph = "";
		
		for(Token token : tokenizer.tokenize(text)) {
			tmpsurface = token.getSurfaceForm();
			tmpmorph = token.getPartOfSpeech();
			
			if(checkSpace(tmpsurface, tmpmorph) == false) {
				tmpArray.add(tmpsurface);
			}
			
			if(checkDelimitor(tmpsurface, tmpmorph) == true ) {
				wordseq.add(tmpArray);
				tmpArray = new ArrayList<String>();
			}
		}
		return wordseq;
	}
}
