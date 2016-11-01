package nlp.sample.sentencegenerator;

/** 
 * 単語列を表すクラス。
 * */

public class WordSequence {
	
	/**  Gram 数 */
	public int gram_n;
	/**  Gram数@:区切り単語列 */
	public String words;
	/**  頻度 */
	public int frequency;
	/**  確率値 */
	public double prob;
	
	WordSequence() {
		gram_n = 0;
		words = "";
		frequency = 0;
		prob = 0.0;
	}
	
	/**  属性値を標準出力に表示するメソッド。 */
	public void showWordSequenceInfo() {
		System.out.print("wordseq: ");
		System.out.print(words);
		System.out.print(" ");
		System.out.print(frequency);
		System.out.print(" ");
		System.out.println(prob);
	}
}
