package nlp.sample.sentencegenerator;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

/** 
 * SentenceGeneratorクラスのテストクラス。
 * */
public class SentenceGeneratorTest {

	private static SentenceGenerator sg;
	/**
	 * startUpメソッド。
	 * "メロスは激怒した。"の1文のみをセットアップ。
	 * NGramのスムージングはなし。確率値の計算には単純に最尤推定を用いる。
	 * */
	@BeforeClass
	public static void startUp() {
		String test_path = System.getProperty("user.dir");  
		String smoothingType = "none";
		String dirname = test_path + "/data/sample1";
		sg = new SentenceGenerator( NGram.retNGramModel(dirname, smoothingType) ); 		
	}
	
	/** 文生成確率の動作確認用テストメソッド。
	 * 未知語を含まない単語列の生成確率が0.0 でないことを確認する。
	 * 未知語を含む単語列の生成確率が0.0 であることを確認する。
	 * */
	@Test
	public void testRetGenerationalProb() {
		
		double value = 0.0;
		String str = "メロスは激怒した。";
		value = sg.retGenerationalLogProb(str);
		boolean flag = false;
		value = Math.exp(value);
		System.out.print(str + ":");
		System.out.println(value);
		if(value == 1.0) {
			flag = true;
		}
		assertEquals(flag, true);
		
		String str2 = "メロスは感激した。";
		
		value = sg.retGenerationalLogProb(str2);
		flag = false;
		double value2 = Math.exp(value);
		System.out.print(str2 + ":");
		System.out.println(value2);
		if(value2 == 0.0) {
			flag = true;
		}
		System.out.println(flag);
		assertEquals(flag, true);
	}

	/** 文生成機能の動作確認用テストメソッド。 
	 * ちゃんとメロスが激怒しているかを確認する。
	 * */
	@Test
	public void testRetRandomSentence() {
		String expstr = "<S>メロスは激怒した。</S>";
		String cstr = sg.retRandomSentence(20, true, true);
		System.out.println("Random:" + cstr);
		assertEquals(expstr, cstr);
	}
	
}
