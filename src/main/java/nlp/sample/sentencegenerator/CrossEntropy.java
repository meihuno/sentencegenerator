package nlp.sample.sentencegenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * CrossEntoryp は NGramモデルのクロスエントロピーを計算するクラス。
 * */

public class CrossEntropy {
	
	/**
     * クロスエントロピーを計算するメソッド。
     * dirname下のテキストファイルのうち半分をモデルを作成し、残り半分の評価用としてクロスエントロピーを計算する。
     * @param dirname テキストファイルが格納されたディレクトリ。
     * @param smoothingType NGramのタイプ。"ml"は最尤推定、"laplase"はラプラススムージング、"backoff"はkatzのBackoffスムージングを行う。
     */
    public static void showCrossEntropy(String dirname, String smoothingType) {
    	
    	ArrayList<String> fileContentStringArray;
		fileContentStringArray = FindFile.retFileContentStringArray(dirname);
		WordFilter wordfilter = new WordFilter(new MorphologicalAnalyzer());
		ArrayList<ArrayList<String>> sensenArray = new ArrayList<ArrayList<String>>();
		
		for(String fileContent: fileContentStringArray) {
			 // System.out.println(fileContent);
			 ArrayList<ArrayList<String>> sensentmpArray =  wordfilter.retSentenceArrayArray(fileContent);
			 for(ArrayList<String> wordArray: sensentmpArray) {
				 sensenArray.add(wordArray);
			 }
		 }
		
		// 2分割なので、index は 0 と 1 のはず
		HashMap<Integer, ArrayList<ArrayList<String>> > senmap = DataContainer.retSplitSenMap(sensenArray,2);
		ArrayList<ArrayList<String>> trainSenArray, testSenArray;
		trainSenArray = senmap.get(0);
		testSenArray = senmap.get(1);
		
		try {
			printSenArray(trainSenArray, "train.txt");
			printSenArray(testSenArray, "test.txt");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// System.out.println(senmap);
		NGram ngrammodel1 = new NGram(trainSenArray, smoothingType);
		NGram ngrammodel22 = new NGram(testSenArray, "ml");
	
		double tmplogprob_model = 0.0;
		double tmplogprob_lang = 0.0;
		double tmpprob = 0.0;
		double valueM = 0.0;
		double valueL = 0.0;
		
		// H(L)とH(L,M)を計算する
		for(ArrayList<String> senArray: testSenArray) {
				
			tmplogprob_model = ngrammodel1.retGenerationalLogProb(senArray);
			tmplogprob_lang = ngrammodel22.retGenerationalLogProb(senArray);
			tmpprob = Math.exp(tmplogprob_lang);
	
			//System.out.println(senArray);
			//System.out.println(tmplogprob_lang);
			//System.out.println(tmplogprob_model);
			
			valueL = valueL + (tmpprob * tmplogprob_lang);
			valueM = valueM + (tmpprob * tmplogprob_model);
		}
		
		double ce = retEntropyValue(valueM, valueL);
		String ceLine = retEntoropyPrintLine(smoothingType, valueM, valueL, ce); 
		System.out.println(ceLine);
		
		// ngrammodel1.showModel();
    }
    
    private static double retEntropyValue(double valueM, double valueL) {
    	double ce = 0.0;
    	ce = ( -1.0 * valueM ) - ( -1.0 * valueL);
    	return ce;
    }
    
    private static String retEntoropyPrintLine(String smoothingType, double valueM, double valueL, double ce) {
    	String rstr = "CrossEntropy is H(L,M) ";
    	rstr += "(" + String.valueOf(valueM) + ")";
    	rstr += " -H(L) (" + String.valueOf(valueL) + ")";
    	rstr += " = " + String.valueOf(ce);
    	return rstr;
    }
    
    private static void printSenArray(ArrayList<ArrayList<String>> sensenArray, String file_name) throws Exception {
    	
		File file = new File(file_name);
		PrintWriter pw = null;
		pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		for(ArrayList<String> senArray: sensenArray) {
			for(String str: senArray) { 
				pw.print(str);
				pw.print(" ");
			}
		}
		pw.close();
    }

}
