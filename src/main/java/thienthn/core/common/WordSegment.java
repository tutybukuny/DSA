package thienthn.core.common;

import thienthn.algorithm.WordPreprocessor;

import java.io.Serializable;
import java.util.ArrayList;

public class WordSegment implements Comparable<WordSegment>, Serializable {
    private int index;

    /**
     * the meaning word that this object contain
     */
    private String word;

    /**
     * indexes of all documents which contains this word
     */
    private ArrayList<Integer> documentIndexes;
    /**
     * indexes of all documents which contain word that is similar with this word (if this word is accent then the similar is non-accent, so on)
     */
    private ArrayList<Integer> subDocumentIndexes;
    /**
     * specify this word is accent or non-accent
     */
    private boolean isNonAccent;

    public WordSegment(int index, String word) {
        this.index = index;
        this.word = word;
        isNonAccent = word.compareTo(WordPreprocessor.getInstance().convertToNonAccentWord(word)) == 0;
        documentIndexes = new ArrayList<>();
        subDocumentIndexes = new ArrayList<>();
    }

    public ArrayList<Integer> getSubDocumentIndexes() {
        return subDocumentIndexes;
    }

    public void setSubDocumentIndexes(ArrayList<Integer> subDocumentIndexes) {
        this.subDocumentIndexes = subDocumentIndexes;
    }

    public boolean isNonAccent() {
        return isNonAccent;
    }

    public String getWord() {
        return word;
    }

    public ArrayList<Integer> getDocumentIndexes() {
        return documentIndexes;
    }

    public ArrayList<Integer> getAllDocumentIndexes() {
        if (subDocumentIndexes.size() > 0) {
            ArrayList<Integer> allDocumentIndexes = (ArrayList<Integer>) documentIndexes.clone();
            allDocumentIndexes.addAll(subDocumentIndexes);
            return allDocumentIndexes;
        }
        return documentIndexes;
    }

    public int getDocumentIndexesSize() {
        return this.documentIndexes.size();
    }

    public void addDocumentIndex(int documentIndex, boolean isPutToSub) {
        if (!isPutToSub && !this.documentIndexes.contains(documentIndex))
            this.documentIndexes.add(documentIndex);
        else if (isPutToSub && !this.subDocumentIndexes.contains(documentIndex))
            this.subDocumentIndexes.add(documentIndex);
    }

    public boolean existsInDocument(int documentIndex) {
        return this.documentIndexes.contains(documentIndex);
    }

    @Override
    public int compareTo(WordSegment o) {
        return this.documentIndexes.size() - o.getDocumentIndexesSize();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;

        WordSegment wordSegment = (WordSegment) obj;

        boolean equal = wordSegment.getDocumentIndexesSize() == documentIndexes.size() && wordSegment.getSubDocumentIndexes().size() == subDocumentIndexes.size() &&
                wordSegment.isNonAccent() == isNonAccent();

        if (equal) {
            ArrayList<Integer> allIndexes = getAllDocumentIndexes();
            ArrayList<Integer> objIndexes = wordSegment.getAllDocumentIndexes();
            for (int i = 0; i < allIndexes.size(); i++) {
                if (allIndexes.get(i) != objIndexes.get(i)) {
                    equal = false;
                    break;
                }
            }
        }

        return equal;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
