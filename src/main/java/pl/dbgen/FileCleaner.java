package pl.dbgen;

import pl.dbgen.namesandpathes.FilePatches;

import java.io.*;

/**
 * @author Lukasz Raduj
 */
public class FileCleaner {

    public static void deleteApostrophes(String path) {
        File cleanedFile = new File(path + "cleaned");
        File input = new File(path);

        StringBuilder source = new StringBuilder();

        try(BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while((line = reader.readLine()) != null) {
                source.append(line).append("\n");
            }
        } catch (IOException e) {
            System.out.println("File not found");
        }

        String cleanedSource = source.toString().replaceAll("'", "");


        try(BufferedWriter writer = new BufferedWriter(new FileWriter(path + "cleaned"))) {
            writer.write(cleanedSource);
        } catch (IOException e) {
            System.out.println("Error during writing to file");
        }
    }

    public static void main(String[] args) {
        FileCleaner.deleteApostrophes("C:\\Users\\raduy\\IdeaProjects\\db-generator\\src\\main\\resources\\descriptions");
    }
}
