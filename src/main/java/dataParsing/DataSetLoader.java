package dataParsing;

import org.jetbrains.annotations.Contract;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Template for loading in a set of data files.
 */
public abstract class DataSetLoader {

    /**
     * Loads all files from a given directory, then calls for parsing each one.
     * @param dataPath The given directory path
     */
    @Contract(pure = true)
    void loadDataFiles(String dataPath) {
        try (Stream<Path> paths = Files.walk(Paths.get(dataPath))) {
            paths.filter(Files::isRegularFile).forEach(path -> {
                try {
                    //Parses every file
                    parseDataFile(new FileReader(path.toFile()));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parses every line of a given data file into a desirable format.
     * @param fileReader The given data file
     */
    protected abstract void parseDataFile(FileReader fileReader);
}
