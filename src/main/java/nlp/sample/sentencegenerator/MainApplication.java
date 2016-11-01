package nlp.sample.sentencegenerator;

import nlp.sample.sentencegenerator.SentenceGenerator;
import nlp.sample.sentencegenerator.NGram;

public class MainApplication {

	public static void main(String[] args) throws Exception {		
		
		
		String dirname = "/Users/sugihara/void/data/aozora_bunko/dazai_osamu";
		String smoothingType = "backoff";
		SentenceGenerator sg = new SentenceGenerator( NGram.retNGramModel(dirname, smoothingType) );
		//sg.createNGramModel(dirname, smoothingType);
		/**/
		System.out.println("This is a test line.");
		
		System.out.println(sg.retRandomSentence(100, true, true));
		System.out.println(sg.retRandomSentence(100, true, false));
		System.out.println(sg.retRandomSentence(100, false, false));
		
	}

}
