package nlp.sample.sentencegenerator;

import static org.junit.Assert.*;
import java.util.ArrayList;
import org.junit.Test;

/** 
 * FindFileクラスのテストクラス。
 * */ 

public class FindFileTest {

	/**
	 * FindFileのFindFileRetTextFileListメソッドのテストメソッド。
	 * retTextFileListは指定ディレクトリしたのファイル名を格納したArrayを返す。
	 * テストメソッドでは、user.dir の data/sample1に特定のファイル名が含まれているかをチェックしている。
	 * */
	@Test
	public void testFindFileRetTextFileList() {
		
		String test_path = System.getProperty("user.dir"); 
		String dir = test_path + "/data/sample1";
		ArrayList<String> filelist = FindFile.retTextFileList(dir);
		
		int index = filelist.indexOf("sample.txt");
		boolean flag = false;
		if(index != -1) {
			flag = true;
		}
		assertEquals(flag, true);
	}

}
