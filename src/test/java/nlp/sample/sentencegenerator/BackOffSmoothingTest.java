package nlp.sample.sentencegenerator;

import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/** Back off スムージングクラス機能用 テストクラス */
public class BackOffSmoothingTest {
	
	private static SentenceGenerator sg;
	private static String dirname;
	
	/** "メロスは激怒した。"の1文のみをセットアップ。*/
	@BeforeClass
	public static void startUp() {
		String test_path = System.getProperty("user.dir");  
		String smoothingType = "backoff";
		dirname = test_path + "/data/sample1";
		sg = new SentenceGenerator( NGram.retNGramModel(dirname, smoothingType) ); 
		
	}
	
	/** 文生成確率の動作確認用テストメソッド。
	 * 特に、未知語を含む単語列の生成確率が0.0 でないことを確認する。
	 * */
	@Test
	public void testRetGenerationalProb() {
		
		// Backoff smoothing で未知語を含む単語列の生成確率が0.0 でないことを確認する。
		// メロスは激怒したのであって、感激はしていない。
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
		String cstr = sg.retRandomSentence(20, false, false);
		System.out.println("Random:" + "S" + cstr + "</S>");
		assertEquals(expstr, cstr);
		
	}
	
	
}
