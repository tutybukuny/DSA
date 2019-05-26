package thienthn.core.common;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class IOManager {
    private FileInputStream fileInputStream = null;
    private ObjectInputStream objectInputStream = null;
    private FileOutputStream fileOutputStream = null;
    private ObjectOutputStream objectOutputStream = null;

    /**
     * path to file that stores objects, particularly in this application, this path is path of trained model file
     */
    private String pathToFile;

    /**
     * path to trained model file
     *
     * @param pathToFile
     */
    public IOManager(String pathToFile) {
        this.pathToFile = pathToFile;
    }

    /**
     * open input stream to reading objects
     *
     * @throws IOException
     */
    public void openInputStream() throws IOException {
        closeOutputStream();
        fileInputStream = new FileInputStream(pathToFile);
        objectInputStream = new ObjectInputStream(fileInputStream);
    }

    /**
     * close input stream after using it
     *
     * @throws IOException
     */
    public void closeInputStream() throws IOException {
        if (objectInputStream != null)
            objectInputStream.close();
        if (fileInputStream != null)
            fileInputStream.close();
    }

    public boolean isEOF() throws IOException {
        return objectInputStream.available() == 0;
    }

    /**
     * read object from input stream
     *
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Object readInput() throws IOException, ClassNotFoundException {
        if (fileInputStream == null || objectInputStream == null)
            throw new NullPointerException("you didnt open file to read!");
        return objectInputStream.readObject();
    }

    /**
     * open output stream to write objects through it
     *
     * @throws IOException
     */
    public void openOutputStream() throws IOException {
        closeInputStream();
        File file = new File(pathToFile);
        if(!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        fileOutputStream = new FileOutputStream(pathToFile);
        objectOutputStream = new ObjectOutputStream(fileOutputStream);
    }

    /**
     * flush data in the output stream
     *
     * @throws IOException
     */
    public void flushOutputStream() throws IOException {
        if (objectOutputStream != null)
            objectOutputStream.flush();
        if (fileOutputStream != null)
            fileOutputStream.flush();
    }

    /**
     * close output stream after writing
     *
     * @throws IOException
     */
    public void closeOutputStream() throws IOException {
        if (objectOutputStream != null)
            objectOutputStream.close();
        if (fileOutputStream != null)
            fileOutputStream.close();
    }

    /**
     * write object by using output stream
     *
     * @param object
     * @throws IOException
     */
    public void writeOutput(Object object) throws IOException {
        if (fileOutputStream == null || objectOutputStream == null)
            throw new NullPointerException("you didnt open file to write!");
        objectOutputStream.writeObject(object);
    }

    /**
     * read all lines from a text file
     *
     * @param pathToFile
     * @return
     * @throws FileNotFoundException
     */
    public ArrayList<String> readLines(String pathToFile) throws FileNotFoundException {
        ArrayList<String> lines = new ArrayList<>();
        Scanner inp = new Scanner(new File(pathToFile));
        while (inp.hasNext()) {
            lines.add(inp.nextLine());
        }

        return lines;
    }

    /**
     * write specific lines to utf-8 text file
     *
     * @param lines
     * @param pathToDestinationFile
     * @throws IOException
     */
    public void writeTextToFile(ArrayList<String> lines, String pathToDestinationFile) throws IOException {
        File file = new File(pathToDestinationFile);
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"));
        for (String line : lines) {
            writer.append(line + "\r\n");
        }
        writer.flush();
        writer.close();
    }

    public String getPathToFile() {
        return pathToFile;
    }

    public void setPathToFile(String pathToFile) {
        this.pathToFile = pathToFile;
    }
}
