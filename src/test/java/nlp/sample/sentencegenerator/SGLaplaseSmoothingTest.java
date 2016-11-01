package nlp.sample.sentencegenerator;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

/** Laplaseスムージングクラス機能用 テストクラス */
public class SGLaplaseSmoothingTest {

	private static SentenceGenerator sg;
	private static String dirname;
	
	@BeforeClass
	public static void startUp() {
		String test_path = System.getProperty("user.dir");  
		String smoothingType = "laplase";
		dirname = test_path + "/data/sample1";
		sg = new SentenceGenerator( NGram.retNGramModel(dirname, smoothingType) ); 
		
	}
	
	/** 文生成確率の動作確認用テストメソッド。
	 * 特に、未知語を含む単語列の生成確率が0.0 でないことを確認する。
	 * */
	@Test
	public void testRetGenerationalProb() {
		
		// Laplase smoothing で未知語を含む単語列の生成確率が0.0 でないことを確認する。
		String str = "メロスは感激した。";
		
		double value = sg.retGenerationalLogProb(str);
		boolean flag = false;
		value = Math.exp(value);
		
		System.out.print(str + ":");
		System.out.println(value);
		
		if(value != 0.0) {
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
