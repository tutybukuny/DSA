package thienthn.algorithm;

import org.apache.log4j.Logger;
import thienthn.core.common.WordSegment;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WordPreprocessor {
    private static final Logger LOGGER = Logger.getLogger(WordPreprocessor.class);
    private static WordPreprocessor WORD_PREPROCESSOR = null;

    public static WordPreprocessor getInstance() {
        if (WORD_PREPROCESSOR == null)
            WORD_PREPROCESSOR = new WordPreprocessor();
        return WORD_PREPROCESSOR;
    }

    /**
     * turn a accent word to non accent word
     *
     * @param str
     * @return non accent word
     */
    public String convertToNonAccentWord(String str) {
        try {
            String temp = Normalizer.normalize(str, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            return pattern.matcher(temp).replaceAll("").replaceAll("đ", "d").replaceAll("Đ", "D");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    /**
     * create the map of word segments from particular sentences.
     *
     * @param sentences
     * @return HashMap<word               ,                               counted                               properties                               of                               word>
     */
    public HashMap<String, WordSegment> createWordSegmentDictionary(ArrayList<String> sentences) {
        HashMap<String, WordSegment> map = new HashMap<>();
        try {
            for (int i = 0; i < sentences.size(); i++) {
                ArrayList<String> words = getWords(sentences.get(i));
                for (String word : words) {
                    putToMap(map, word, i, false);
                    // turn the word to non-accent word and count for it too
                    if (word.compareTo(convertToNonAccentWord(word)) != 0)
                        putToMap(map, convertToNonAccentWord(word), i, true);
                }
            }
        } catch (NullPointerException ex) {
            LOGGER.error(ex);
        }

        for (String word : map.keySet()) {
            WordSegment wordSegment = map.get(word);
            if (!wordSegment.isNonAccent()) {
                WordSegment nonAccentWordSegment = map.get(WordPreprocessor.getInstance().convertToNonAccentWord(word));
                List<Integer> subIndexes = nonAccentWordSegment.getDocumentIndexes().stream().filter(integer -> !wordSegment.existsInDocument(integer)).collect(Collectors.toList());
                wordSegment.setSubDocumentIndexes(new ArrayList<>(subIndexes));
            }
        }

        return map;
    }

    /**
     * add or update a WordSegment into a HashMap
     *
     * @param map
     * @param word
     * @param documentIndex
     */
    private void putToMap(HashMap<String, WordSegment> map, String word, int documentIndex, boolean isPutToSub) {
        try {
            WordSegment wordSegment = map.get(word);
            if (wordSegment == null) {
                wordSegment = new WordSegment(map.size(), word);
            }
            wordSegment.addDocumentIndex(documentIndex, isPutToSub);
            map.put(word, wordSegment);
        } catch (NullPointerException ex) {
            LOGGER.error(ex);
        }
    }

    /**
     * get all words in a sentence
     *
     * @param sentence
     * @return list of words
     */
    public ArrayList<String> getWords(String sentence) {
        ArrayList<String> wordList = new ArrayList<>();
        String[] words = sentence.split("\\s+");
        for (int j = 0; j < words.length; j++) {
            // replacing special characters and force word to lower case
            String word = words[j].replaceAll("[!@#$%^&*(),.?\":\\{\\}|<>\\-+/]", " ").trim().toLowerCase();
            if (!word.isEmpty())
                wordList.add(word);
        }

        return wordList;
    }
}
