package nlp.sample.sentencegenerator;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

/**
 * 形態素解析機能の動作確認用テストクラス。全てのメソッドのテストは網羅していない。
 * */
public class MorphologicalAnalyzerTest {

	/**
	 * 文区切り機能のテストメソッド。
	 * */
	@Test
	public void testRetWordSentenceArray() {
		MorphologicalAnalyzer box = new MorphologicalAnalyzer();
		String text = "日本語のサンプルです。文区切りをしたいと思っています。OK？";		
		ArrayList<ArrayList<String>> lines = box.retWordSentenceArray(text);
		int size = lines.size();
		assertEquals(size, 3);
	}

}
