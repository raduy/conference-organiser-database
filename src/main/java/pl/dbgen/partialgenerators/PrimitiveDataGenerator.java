package pl.dbgen.partialgenerators;

import pl.dbgen.namesandpathes.FilePatches;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

/**
 * @author Lukasz Raduj
 */
public class PrimitiveDataGenerator {
    private static final Logger LOGGER = Logger.getLogger(PrimitiveDataGenerator.class.getName());
    private final static Random random = new Random();
    private final static PrimitiveDataGenerator instance = new PrimitiveDataGenerator();

    public static PrimitiveDataGenerator getInstance() { return instance; }

    private PrimitiveDataGenerator() {
        loadDataFromFile();
    }

    private void loadDataFromFile() {
        loadFirstNames();
        loadLastNames();
        loadCompanyNames();
        loadCountries();
        loadCities();
        loadStreets();
        loadDescriptions();
    }

    private List<String> firstNames = new ArrayList<>();
    private void loadFirstNames() {
        try(BufferedReader reader = new BufferedReader(new FileReader(FilePatches.FIRST_NAMES.toString()))) {
            String line;
            while((line = reader.readLine()) != null){
                firstNames.add(line);
            }
        } catch (IOException e) {
            LOGGER.severe("Cannot read first names file");
        }
    }

    private List<String> lastNames = new ArrayList<>();
    private void loadLastNames() {
        try(BufferedReader reader = new BufferedReader(new FileReader(FilePatches.LAST_NAMES.toString()))) {
            String line;
            while((line = reader.readLine()) != null) {
                lastNames.add(line);
            }
        } catch (IOException e) {
            LOGGER.severe("Cannot read last names file");
        }
    }

    private List<String> companyNames = new ArrayList<>();
    private void loadCompanyNames() {
        try(BufferedReader reader = new BufferedReader(new FileReader(FilePatches.COMPANY_NAMES.toString()))) {
            String line;
            while((line = reader.readLine()) != null) {
                companyNames.add(line);
            }
        } catch (IOException e) {
            LOGGER.severe("Cannot read company names file");
        }
    }

    private List<String> countries = new ArrayList<>();
    private void loadCountries() {
        try(BufferedReader reader = new BufferedReader(new FileReader(FilePatches.COUNTRIES.toString()))) {
            String line;
            while((line = reader.readLine()) != null) {
                countries.add(line);
            }
        } catch (IOException e) {
            LOGGER.severe("Cannot read countries file");
        }
    }

    private List<String> cities = new ArrayList<>();
    private void loadCities() {
        try(BufferedReader reader = new BufferedReader(new FileReader(FilePatches.CITIES.toString()))) {
            String line;
            while((line = reader.readLine()) != null) {
                cities.add(line);
            }
        } catch (IOException e) {
            LOGGER.severe("Cannot read cities file");
        }
    }

    private List<String> streets = new ArrayList<>();
    private void loadStreets() {
        try(BufferedReader reader = new BufferedReader(new FileReader(FilePatches.STREETS.toString()))) {
            String line;
            while((line = reader.readLine()) != null) {
                streets.add(line);
            }
        } catch (IOException e) {
            LOGGER.severe("Cannot read streets file");
        }
    }

    private List<String> descriptions = new ArrayList<>();
    private void loadDescriptions() {
        try(BufferedReader reader = new BufferedReader(new FileReader(FilePatches.DESCRIPTIONS.toString()))) {
            String line;
            while((line = reader.readLine()) != null) {
                descriptions.add(line);
            }
        } catch (IOException e) {
            LOGGER.severe("Cannot read descriptions file");
        }
    }

    public String nextCompanyName() {
        return companyNames.get(random.nextInt(companyNames.size()));
    }


    public String nextFirstName() {
        return firstNames.get(random.nextInt(firstNames.size()));
    }

    public String nextLastName() {
        return lastNames.get(random.nextInt(lastNames.size()));
    }

    public String nextSinglePersonName() {
        return nextFirstName() + " " + nextLastName();
    }

    public String nextCountry() {
        return countries.get(random.nextInt(countries.size()));
    }

    public String nextCity() {
        return cities.get(random.nextInt(cities.size()));
    }

    public String nextStreet() {
        return streets.get(random.nextInt(streets.size()));
    }

    public String nextPhone() {
        return String.valueOf(random.nextInt(1000000000) + 100000000);
    }

    public String nextDescription() {
        return descriptions.get(random.nextInt(descriptions.size()));
    }

    public int nextInt() {
        return random.nextInt();
    }

    public int nextInt(int bound) {
        return random.nextInt(bound);
    }

    public boolean nextBoolean() {
        return random.nextBoolean();
    }

    public Integer nextStudentIdentyficatorOrNull() {
        boolean returnNull = nextBoolean(); //50% of participants aren't students
        if (returnNull) {
            return null;
        }
        return nextInt(1000000) + 100000;
    }

    public static void main(String[] args) {
        PrimitiveDataGenerator generator = PrimitiveDataGenerator.getInstance();

        System.out.println(generator.nextCompanyName());
        System.out.println(generator.nextSinglePersonName());
        System.out.println(generator.nextCountry());
        System.out.println(generator.nextCity());
        System.out.println(generator.nextStreet());
        System.out.println(generator.nextPhone());
        System.out.println(generator.nextDescription());
        System.out.println(generator.nextStudentIdentyficatorOrNull());
    }
}
