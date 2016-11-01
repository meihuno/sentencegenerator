package nlp.sample.sentencegenerator;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ BackOffSmoothingTest.class, DataContainerTest.class, FindFileTest.class, MapUtilTest.class,
		MorphologicalAnalyzerTest.class, NGramTest.class, SentenceGeneratorTest.class, SGLaplaseSmoothingTest.class,
		WordSequenceProbTest.class })
public class AllTests {

}
