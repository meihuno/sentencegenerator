package nlp.sample.sentencegenerator;

import java.util.HashMap;
import java.util.Map.Entry;

/** 
 * GoodTuring推定値を計算するクラス。
 * */

public class GoodTuringEstimater {
	private HashMap<Integer, HashMap<Integer, Integer>> nrfreqmap;
	private WordSequenceMap wordsequencemap;
	private static int GRAMNUM = 3;
	private double m;
	private double b;
	
	GoodTuringEstimater(WordSequenceMap wsqm) {
		nrfreqmap = new HashMap<Integer, HashMap<Integer, Integer>>();
		wordsequencemap = wsqm;
		setNrfreqMap();
		setZipParameter();
	}
	
	/* Nが取得できない頻度rに対する補正値をlog(Nr) = -m* logr + b で与える。*/
	private void setZipParameter() {
		m = 0.5;
		b = - 0.1;
	}
	
	private void setNrfreqMap() {
		
		for(int i = 0; i < GRAMNUM + 1; i++ ) {
			nrfreqmap.put(i, new HashMap<Integer, Integer>());
		}
		
		HashMap<Integer, Integer> subhash;
		
		for(Entry<String, WordSequence> s: wordsequencemap.wordseqmap.entrySet()) {
			
			WordSequence wsq = s.getValue();
			int gram_n = wsq.gram_n;
			int rfreq = wsq.frequency;
			int rnum = 0;
			
			subhash = nrfreqmap.get(gram_n);
			if( subhash.containsKey(rfreq) == true ) {
				rnum = subhash.get(rfreq);
				subhash.put(rfreq, rnum+1);
				nrfreqmap.put(gram_n, subhash);
			} else {
				subhash.put(rfreq, 1);
				nrfreqmap.put(gram_n, subhash);
			}	
		}
	}
	
	/** 
	 * 出現回数rの頻度Nrを返すメソッド。
	 * @param r 頻度r
	 * @param gram_n Gram数
	 * @return 頻度rの頻度Nr
	 * */
	public int retNRfreq(int r, int gram_n) {
		int nr = 0;
		HashMap<Integer, Integer> nrfreqcontainer;
		if(nrfreqmap.containsKey(gram_n) == true ) {
			nrfreqcontainer = nrfreqmap.get(gram_n);
			if(nrfreqcontainer.containsKey(r) == true ) {
				nr = nrfreqcontainer.get(r);
			}
		}
		return nr;
	}
	/**
	 * 出現回数rの補正値r*を返すメソッド。 
	 * @param r 頻度r
	 * @param gram_n Gram数
	 * @return 頻度rの補正値 r*
	 * */	
	public double retSharpR(int r, int gram_n) {
		double sharp_r = 0.0;
		HashMap<Integer, Integer> nrfreqcontainer;
		int nr,nrpn;
		if(nrfreqmap.containsKey(gram_n) == true ) {
			nrfreqcontainer = nrfreqmap.get(gram_n);
			if(nrfreqcontainer.containsKey(r) == true ) {
				if(nrfreqcontainer.containsKey(r+1) == true ) {
					nrpn = nrfreqcontainer.get(r+1);
					nr = nrfreqcontainer.get(r);
					sharp_r = (double)(r+1) * ((double)nrpn/(double)nr);
				} 
			} 
		}
		
		if(sharp_r == 0.0) {
			//sharp_r = (double)r;
			sharp_r = Math.exp(-1.0 * m * Math.log(r) + b );
		}
		return sharp_r;
	}
	
}
