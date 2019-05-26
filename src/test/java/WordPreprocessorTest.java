import org.junit.Assert;
import org.junit.Test;
import thienthn.algorithm.WordPreprocessor;
import thienthn.core.common.WordSegment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class WordPreprocessorTest {

    @Test
    public void testCreateWordSegmentDictionaryMethod() {
        ArrayList<String> sentences = new ArrayList<>(Arrays.asList("mười hai", "hái quả"));
        HashMap<String, WordSegment> map = WordPreprocessor.getInstance().createWordSegmentDictionary(sentences);
        HashMap<String, WordSegment> expectedMap = new HashMap<>();
        WordSegment ws = new WordSegment(0, "mười");
        ws.addDocumentIndex(0, false);
        expectedMap.put(ws.getWord(), ws);

        ws = new WordSegment(1,"muoi");
        ws.addDocumentIndex(0, true);
        expectedMap.put(ws.getWord(), ws);

        ws = new WordSegment(2,"hai");
        ws.addDocumentIndex(0, false);
        ws.addDocumentIndex(1, true);
        expectedMap.put(ws.getWord(), ws);

        ws = new WordSegment(3,"hái");
        ws.addDocumentIndex(1, false);
        ws.addDocumentIndex(0, true);
        expectedMap.put(ws.getWord(), ws);

        ws = new WordSegment(4,"quả");
        ws.addDocumentIndex(1, false);
        expectedMap.put(ws.getWord(), ws);

        ws = new WordSegment(5,"qua");
        ws.addDocumentIndex(1, true);
        expectedMap.put(ws.getWord(), ws);

        Assert.assertEquals(expectedMap.size(), map.size());
        for(String key : expectedMap.keySet()) {
            Assert.assertEquals(expectedMap.get(key), map.get(key));
        }
    }

    @Test
    public void testConvertToNonAccentWordMethod() {
        String originString = "Đoàn quân Việt Nam đi, chung lòng cứu quốc! Yêu lắm Việt Nam ơi :D!!!";
        String expectedString = "Doan quan Viet Nam di, chung long cuu quoc! Yeu lam Viet Nam oi :D!!!";
        String convertedString = WordPreprocessor.getInstance().convertToNonAccentWord(originString);

        Assert.assertEquals(expectedString, convertedString);
    }

    @Test
    public void testGetWordMethod() {
        String originString = "một 2 ba bốn ,.i, ,, n > ! {}";
        ArrayList<String> expectedList = new ArrayList<>(Arrays.asList("một", "2", "ba", "bốn", "i", "n"));
        ArrayList<String> list = WordPreprocessor.getInstance().getWords(originString);

        Assert.assertEquals(expectedList, list);
    }
}
