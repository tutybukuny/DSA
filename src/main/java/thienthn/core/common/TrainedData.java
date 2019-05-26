package thienthn.core.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TrainedData implements Serializable {
    /**
     * dictionary of a chunk
     */
    private Map<String, WordSegment> dictionary;

    /**
     * list of document belong to this chunk
     */
    private ArrayList<String> documents;

    /**
     * vector of all document after reduce by pca
     */
    private ArrayList<HashMap<String, Double>> documentVectors;

    public TrainedData() {
        dictionary = new HashMap<>();
        documents = new ArrayList<>();
        documentVectors = new ArrayList<>();
    }

    public Map<String, WordSegment> getDictionary() {
        return dictionary;
    }

    public void setDictionary(Map<String, WordSegment> dictionary) {
        this.dictionary = dictionary;
    }

    public ArrayList<String> getDocuments() {
        return documents;
    }

    public void setDocuments(ArrayList<String> documents) {
        this.documents = documents;
    }

    public ArrayList<HashMap<String, Double>> getDocumentVectors() {
        return documentVectors;
    }

    public void setDocumentVectors(ArrayList<HashMap<String, Double>> documentVectors) {
        this.documentVectors = documentVectors;
    }
}
