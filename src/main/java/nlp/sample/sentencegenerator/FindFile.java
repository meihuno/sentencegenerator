package nlp.sample.sentencegenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;

/** 
 * テキストを格納したファイルに対する入出力機能を提供するクラス。
 * */
public class FindFile {

	/** 
	 * ディレクトリ下のテキストファイルを格納したArrayListを返すメソッド
	 * @param Dir ディレクトリのパス
	 * @return ファイルパスの文字列を格納したArrayList
	 * */
	public static ArrayList<String> retTextFileList(String Dir) {
		File file = new File(Dir);
		String[] fileList1 = file.list();
		if(fileList1 == null) {
			System.err.println("File Not Found.");
		}
		ArrayList<String> fileList2 = new ArrayList<String>();
		String filename;
		for(int i = 0; i < fileList1.length; i++){
			filename = fileList1[i];
			if (filename.endsWith(".txt") == true) {
				fileList2.add(filename);
			} 
		}
		return fileList2;
    }	
	
	/** 
	 * ディレクトリ下のテキストファイルを格納したArrayListを返すメソッド
	 * @param Dir ディレクトリのパス
	 * @return ファイルパスの文字列を格納したArrayList
	 * */
	public static String retFileContentString(String FileName) {
		String s, rstr;
		rstr = "";
		try {
			BufferedReader in = new BufferedReader(new FileReader(FileName));
			while((s=in.readLine()) != null) {
				rstr += s;
			}
			in.close();
		} catch(FileNotFoundException e) {
			System.err.println("FileNotFoundException");
		} catch (IOException e) {
			System.err.println("IOException");
		}
		rstr = rstr.replace(":", "<colon>");
		rstr = rstr.replace("@", "<atmark>");
		return rstr;
	}
	
	public static ArrayList<String> retFileContentStringArray(String Dir) {
		
		ArrayList<String> fileList = retTextFileList(Dir);
		ArrayList<String> sentence_array = new ArrayList<String>();
		
		for(String filename: fileList) {
	   		 sentence_array.add(retFileContentString(Dir + '/' + filename));
	   	 }
		return sentence_array;
	
	}
	
}
