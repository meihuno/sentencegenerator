package nlp.sample.sentencegenerator;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.BeforeClass;
import org.junit.Test;

/** 
 * NGramの確率値計算用クラスのテストクラス。
 * voidのメソッドのテストはさぼっている。
 * */
public class WordSequenceProbTest {
	
	private static SentenceGenerator sg;
	private static String dirname;
	private static WordSequenceProb wsp; 
	
	/**
	 * startUpメソッド。
	 * "メロスは激怒した。"の1文のみをセットアップ。
	 * NGramのスムージングはなし。確率値の計算には単純に最尤推定を用いる。
	 * */
	@BeforeClass
	public static void startUp() {
		String test_path = System.getProperty("user.dir");  
		String smoothingType = "ml";
		ArrayList<ArrayList<String>> sensenArray = new ArrayList<ArrayList<String>>();
		ArrayList<String> senArray = new ArrayList<String>();
		
		//senArray.add("<S>");
		senArray.add("メロス");
		senArray.add("は");
		senArray.add("激怒");
		senArray.add("し");
		senArray.add("た");
		senArray.add("。");
		//senArray.add("</S>");
		
		sensenArray.add(senArray);
		
		dirname = test_path + "/data/sample1";
		
		wsp = new WordSequenceProb(sensenArray);
		sg = new SentenceGenerator( NGram.retNGramModel(dirname, smoothingType) ); 
		
	}
	
	/** 文生成確率の動作確認用テストメソッド。
	 * 未知語を含まない単語列の生成確率が0.0 でないことを確認する。
	 * 未知語を含む単語列の生成確率が0.0 であることを確認する。
	 * */
	@Test
	public void testRetGenerationalProb() {
		
		// 未知語を含む単語列の生成確率が0.0 になる。
		String str = "メロスは感激した。";
		
		double value = sg.retGenerationalLogProb(str);
		boolean flag = false;
		value = Math.exp(value);
				
		if(value == 0.0) {
			flag = true;
		}
		
		assertEquals(flag, true);
	
		// 学習データそのもの生成確率は1.0となる。
		String str2 = "メロスは激怒した。";
		double value2 = sg.retGenerationalLogProb(str2);
		flag = false;
		value2 = Math.exp(value2);
				
		if(value2 == 1.0) {
			flag = true;
		}
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
	
	/** retPreKyのテストメソッド。  */
	@Test
	public void testRetPreKey() {
		String str = "2@東京:都";
		String expstr = "1@東京";
		String cstr = wsp.retPreKey(str);
		assertEquals(expstr, cstr);
		
	}
	
	/** retNgramLogProbのテストメソッド。  */
	@Test
	public void testRetNGramProb() {
		String str = "3@メロス:は:激怒";
		
		// 対数
		double value = Math.log(1.0);
		double cvalue = wsp.retNgramLogProb(str, 3);
		
		// 3Gramは1種類づつしかないので確率値は1.0(対数では0.0)
		assertEquals(cvalue, value, 0.0001);
		
		str = "1@メロス";
		// 対数
		value = Math.log(1.0/8.0);
		cvalue = wsp.retNgramLogProb(str, 1);
		
		// 3Gramは1種類づつしかないので確率値は1.0(対数では0.0)
		assertEquals(cvalue, value, 0.0001);
		
	}

	/** retUnigramProbのテストメソッド。  */
	@Test
	public void testRetUnigramProb() {	
		String str = "1@メロス";
		// 対数ではない
		double value = 1.0/8.0;
		double cvalue = wsp.retUnigramProb(str);
		
		assertEquals(cvalue, value, 0.0001);
	}
	
	/** retNgramProbのテストメソッド。  */
	@Test
	public void testRetNgramProb() {
		String str = "3@メロス:は:激怒";
		double value = 1.0;
		double cvalue = wsp.retNgramProb(str, 3);
		
		assertEquals(cvalue, value, 0.0001);
		
	}
	
	/** retGenerationalProbNgram のテストメソッド。 */
	@Test
	public void testRetGenerationalProbNgram() {
		
		ArrayList<String> senArray = new ArrayList<String>();
		senArray.add("メロス");
		senArray.add("は");
		senArray.add("激怒");
		senArray.add("し");
		senArray.add("た");
		senArray.add("。");
		double value = Math.log(1.0);
		double cvalue = wsp.retGenerationalProbNgram(senArray, 3);
		System.out.println(cvalue);
		assertEquals(cvalue, value, 0.0001);
		
		ArrayList<String> senArray2 = new ArrayList<String>();
		senArray2.add("メロス");
		senArray2.add("は");
		senArray2.add("感激");
		senArray2.add("し");
		senArray2.add("た");
		senArray2.add("。");
		value = 0.0;
		cvalue = wsp.retGenerationalProbNgram(senArray2, 3);
		cvalue = Math.exp(cvalue);
		System.out.println(cvalue);
		assertEquals(cvalue, value, 0.0001);
	}
	
	/** retPostStringCandidateHashMapのテストメソッド。 */
	@Test
	public void testRetPostStringCandidateHashMap() {
		 String str1 = "メロス";
		 boolean flag = false;
		 HashMap<String, Double> map = wsp.retPostStringCandidateHashMap(str1);
		 flag = map.containsKey("は");
		 assertEquals(flag, true);
		 
		 flag = false;
		 String str2 = "は";
		 HashMap<String, Double> map2 = wsp.retPostStringCandidateHashMap(str1, str2);
		 flag = map2.containsKey("激怒");
		 assertEquals(flag, true);
	}
	
	/** retPostStringCandidateHashArrayListのテストメソッド。 */
	@Test
	public void testRetPostStringCandidateArrayList() {
		 String str1 = "メロス";
		 boolean flag = false;
		 ArrayList<String> array = wsp.retPostStringCandidateArrayList(str1);
		 flag = array.contains("は");
		 assertEquals(flag, true);
		 
		 flag = false;
		 String str2 = "は";
		 ArrayList<String> array2 = wsp.retPostStringCandidateArrayList(str1, str2);
		 flag = array2.contains("激怒");
		 assertEquals(flag, true);
	}
	
}
