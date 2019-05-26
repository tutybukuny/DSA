package thienthn.algorithm;

import javafx.util.Pair;
import org.apache.log4j.Logger;
import org.ejml.data.Complex_F64;
import org.ejml.simple.SimpleEVD;
import org.ejml.simple.SimpleMatrix;
import thienthn.core.common.ConfigurationManager;
import thienthn.core.common.IOManager;
import thienthn.core.common.TrainedData;
import thienthn.core.common.WordSegment;

import java.io.EOFException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class PCAEngine {
    private static final Logger LOGGER = Logger.getLogger(PCAEngine.class);
    private IOManager ioManager;
    private ArrayList<TrainedData> trainedDatas;

    public PCAEngine() {
        ioManager = new IOManager(ConfigurationManager.MODEL_PATH);
        trainedDatas = new ArrayList<>();
    }

    /**
     * load model from model file
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void loadModel() throws IOException, ClassNotFoundException {
        trainedDatas.clear();
        ioManager.openInputStream();
        LOGGER.info("loading model!");
        while (true) {
            try {
                trainedDatas.add((TrainedData) ioManager.readInput());
            } catch (EOFException e) {
                break;
            }
        }
        LOGGER.info("finished loading model with " + trainedDatas.size() + " trained datas!");
    }

    /**
     * find all results for queries from query file and put those results to a specific folder
     *
     * @param pathToQueryFile
     * @param pathToResultFolder
     * @throws IOException
     */
    public void find(String pathToQueryFile, String pathToResultFolder) throws IOException {
        ArrayList<String> queries = ioManager.readLines(pathToQueryFile);
        for (String query : queries) {
            ArrayList<String> results = find(query);
            ioManager.writeTextToFile(results, pathToResultFolder + "/" + query + ".txt");
        }
    }

    /**
     * find results for a query
     *
     * @param query
     * @return list of result names
     */
    public ArrayList<String> find(String query) {
        ArrayList<Pair<String, Double>> results = new ArrayList<>();

        query = query.trim();
        ArrayList<String> words = WordPreprocessor.getInstance().getWords(query);
        ArrayList<String> nonAccentWords = null;
        String nonAccentQuery = WordPreprocessor.getInstance().convertToNonAccentWord(query);
        if (nonAccentQuery.compareTo(query) != 0)
            nonAccentWords = WordPreprocessor.getInstance().getWords(WordPreprocessor.getInstance().convertToNonAccentWord(query));
        for (TrainedData data : trainedDatas) {
            ArrayList<Pair<String, Double>> chunkResults = findInChunk(words, data);
            results.addAll(chunkResults);
            if (nonAccentWords != null) {
                chunkResults = findInChunk(nonAccentWords, data);
                results.addAll(chunkResults);
            }
        }

        results.sort((o1, o2) -> {
            if (o1.getValue() > o2.getValue())
                return 1;
            if (o1.getValue() < o2.getValue())
                return -1;
            return 0;
        });

        ArrayList<String> stringResults = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            stringResults.add(results.get(i).getKey());
        }

        return stringResults;
    }

    /**
     * find results in a chunk
     *
     * @param words
     * @param data
     * @return list results in a chunk Pair<result string, score>>
     */
    private ArrayList<Pair<String, Double>> findInChunk(ArrayList<String> words, TrainedData data) {
        if (words == null || data == null) {
            LOGGER.error("input is null");
        }
        ArrayList<Pair<String, Double>> results = new ArrayList<>();
        Map<String, WordSegment> dic = data.getDictionary();
        HashMap<String, Double> vector = new HashMap<>();
        ArrayList<String> chunkDocuments = data.getDocuments();
        int chunkSize = chunkDocuments.size();
        for (String word : words) {
            if (!dic.containsKey(word)) continue;
            if (vector.containsKey(word))
                continue;
            double tfIdf = calculateTfIdf(dic.get(word), word, words, chunkSize);
            vector.put(word, tfIdf);
        }

        ArrayList<HashMap<String, Double>> chunkVectors = data.getDocumentVectors();
        for (int i = 0; i < chunkVectors.size(); i++) {
            double distance = calculateVectorsDistance(dic, vector, chunkVectors.get(i));
            results.add(new Pair<>(chunkDocuments.get(i), distance));
        }
        return results;
    }

    /**
     * calculate distance of 2 vectors
     *
     * @param dic
     * @param vector1
     * @param vector2
     * @return distance
     */
    private double calculateVectorsDistance(Map<String, WordSegment> dic, HashMap<String, Double> vector1, HashMap<String, Double> vector2) {
        double result = 0;
        boolean isInfinity = true;
        for (String word : dic.keySet()) {
            double a = 0;
            double b = 0;
            if (vector1.containsKey(word))
                a = vector1.get(word);
            if (vector2.containsKey(word))
                b = vector2.get(word);
            if (a != 0 || b != 0)
                isInfinity = false;
            result += (a - b) * (a - b);
        }

        return isInfinity ? Double.MAX_VALUE : Math.sqrt(result);
    }

    /**
     * calculate tf-idf
     *
     * @param wordSegment
     * @param word
     * @param words
     * @param chunkSize
     * @return tf-idf value
     */
    private double calculateTfIdf(WordSegment wordSegment, String word, ArrayList<String> words, double chunkSize) {
        if (wordSegment == null || words == null)
            throw new NullPointerException("input data of calculateTfIdf is null");
        double tf = (double) words.stream().filter(s -> s.compareTo(word) == 0).count() / (double) words.size();
        double dHasT = wordSegment.getDocumentIndexesSize();
        if (wordSegment.isNonAccent())
            dHasT += wordSegment.getSubDocumentIndexes().size();
        double idf = Math.log(chunkSize / (dHasT + 1));

        return tf * idf;
    }

    public boolean isTrained() {
        return !trainedDatas.isEmpty();
    }

    /**
     * train engine from data
     * @param dataPath
     * @return true if success, false if fail
     */
    public boolean train(String dataPath) {
        try {
            ioManager.openOutputStream();
            ArrayList<String> lines = ioManager.readLines(dataPath);
            LOGGER.info("loaded " + lines.size() + " lines");

            int lineCount = 0;
            ArrayList<String> lineChunk = new ArrayList<>();
            for (String line : lines) {
                ++lineCount;
                lineChunk.add(line);

                // I have problem with the size of dictionary and the number of documents
                // they are too big so that I can't process them at one time
                // I split data to smaller data and I can handle it
                // I know this way might be dramatically wrong and the result would be terrifyingly bad but I have'nt gotten other ideas yet
                // or may be my approach was mistaken at very first time
                if (lineCount % 5000 == 0) {
                    LOGGER.info("start training the chunk " + (lineCount / 5000));
                    startCalculateChunk(lineChunk);
                    lineChunk.clear();
                    LOGGER.info("finished training the chunk " + (lineCount / 5000));
                }
            }

            ioManager.closeOutputStream();

            return true;
        } catch (IOException e) {
            LOGGER.error(e);
        }

        return false;
    }

    /**
     * train for a chunk
     *
     * @param lineChunk
     * @throws IOException
     */
    private void startCalculateChunk(ArrayList<String> lineChunk) throws IOException {
        ArrayList<HashMap<Integer, Double>> dataMatrix = new ArrayList<>();
        HashMap<String, WordSegment> wordSegmentMap = WordPreprocessor.getInstance().createWordSegmentDictionary(lineChunk);
        ArrayList<String> dataLines = new ArrayList<>();
        for (String line : lineChunk) {
            dataLines.add(line);
            ArrayList<String> words = WordPreprocessor.getInstance().getWords(line);
            HashMap<Integer, Double> lineVector = new HashMap<>();
            for (String word : words) {
                if (wordSegmentMap.containsKey(word)) {
                    WordSegment wordSegment = wordSegmentMap.get(word);
                    if (lineVector.containsKey(wordSegment.getIndex()))
                        continue;

                    lineVector.put(wordSegment.getIndex(), calculateTfIdf(wordSegment, word, words, lineChunk.size()));
                }
            }
            dataMatrix.add(lineVector);
        }
        LOGGER.info("prepared data matrix");
        calculatePCA(wordSegmentMap, dataMatrix, dataLines);
    }

    /**
     * implement pca algorithm
     *
     * @param dic
     * @param dataMatrix
     * @param dataLines
     * @throws IOException
     */
    private void calculatePCA(HashMap<String, WordSegment> dic, ArrayList<HashMap<Integer, Double>> dataMatrix, ArrayList<String> dataLines) throws IOException {
        Double[] g = new Double[dic.size()];
        for (String word : dic.keySet()) {
            int index = dic.get(word).getIndex();
            g[index] = (double) 0; // 1 / n * sum[1-n]gi
            for (HashMap<Integer, Double> lineVector : dataMatrix) {
                if (lineVector.containsKey(index)) {
                    g[index] += lineVector.get(index);
                }
            }
            g[index] /= dataMatrix.size();
        }

        LOGGER.info("finished calculating g");

        Double[] sigma = new Double[dic.size()];
        for (String word : dic.keySet()) {
            int index = dic.get(word).getIndex();
            sigma[index] = (double) 0; // 1 / n * sum[1-n]Xi
            for (HashMap<Integer, Double> lineVector : dataMatrix) {
                if (lineVector.containsKey(index)) {
                    sigma[index] += Math.pow(lineVector.get(index) - g[index], 2);
                } else
                    sigma[index] += Math.pow(g[index], 2);
            }
            sigma[index] /= (dataMatrix.size() - 1);
            sigma[index] = Math.sqrt(sigma[index]);
        }

        LOGGER.info("finished calculating sigma");

        ArrayList<HashMap<Integer, Double>> standardDataMatrix = new ArrayList<>();
        for (HashMap<Integer, Double> lineVector : dataMatrix) {
            HashMap<Integer, Double> standardLineVector = new HashMap<>();
            for (Integer index : lineVector.keySet()) {
                double value = lineVector.get(index);
                double xij = (value - g[index]) / (Math.sqrt(dataMatrix.size()) * sigma[index]);
                standardLineVector.put(index, xij);
            }
            standardDataMatrix.add(standardLineVector);
        }

        LOGGER.info("finished calculating standard matrix");

        SimpleMatrix matrix = new SimpleMatrix(standardDataMatrix.size(), dic.size());
        for (int i = 0; i < matrix.numRows(); i++) {
            for (int j = 0; j < matrix.numCols(); j++) {
                if (standardDataMatrix.get(i).containsKey(j))
                    matrix.set(i, j, standardDataMatrix.get(i).get(j));
                else
                    matrix.set(i, j, 0);
            }
        }
        LOGGER.info("finished create simple matrix");

        SimpleMatrix matrixT = matrix.transpose();
        SimpleMatrix v = matrix.mult(matrixT).divide(dataMatrix.size());
        LOGGER.info(v.numRows() + " " + v.numCols());
        SimpleEVD eigs = v.eig();
        System.out.println("finished calculating eigs");
        List<Complex_F64> eigvs = eigs.getEigenvalues();
        ArrayList<Pair<Integer, Complex_F64>> sortedIndex = new ArrayList<>();
        for (int i = 0; i < eigvs.size(); i++) {
            sortedIndex.add(new Pair<>(i, eigvs.get(i)));
        }

        Collections.sort(sortedIndex, (o1, o2) -> {
            if (o1.getValue().getReal() > o2.getValue().getReal())
                return 1;
            if (o1.getValue().getReal() < o2.getValue().getReal())
                return -1;
            return 0;
        });

        ArrayList<Integer> indexDic = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            indexDic.add(sortedIndex.get(i).getKey());
            LOGGER.info("important index " + sortedIndex.get(i).getKey());
        }

        Map<String, WordSegment> map = dic.entrySet().stream().filter(e -> indexDic.contains(e.getValue().getIndex())).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
        TrainedData data = new TrainedData();
        data.setDictionary(map);
        data.setDocuments(dataLines);
        ArrayList<HashMap<String, Double>> lineVectors = new ArrayList<>();
        for (int i = 0; i < dataLines.size(); i++) {
            ArrayList<String> words = WordPreprocessor.getInstance().getWords(dataLines.get(i));
            HashMap<String, Double> vector = new HashMap<>();
            for (String word : words) {
                if (dic.containsKey(word)) {
                    Double value = dataMatrix.get(i).get(dic.get(word).getIndex());
                    vector.put(word, value);
                }
            }
            lineVectors.add(vector);
        }
        data.setDocumentVectors(lineVectors);

        ioManager.writeOutput(data);
        ioManager.flushOutputStream();
        System.out.println("finished " + dataLines.size() + " lines");
    }
}
