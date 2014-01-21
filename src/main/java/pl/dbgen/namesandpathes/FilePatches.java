package pl.dbgen.namesandpathes;

/**
 * @author Lukasz Raduj
 */
public enum FilePatches {
    FIRST_NAMES("C:\\Users\\raduy\\IdeaProjects\\db-generator\\src\\main\\resources\\first_names"),
    LAST_NAMES("C:\\Users\\raduy\\IdeaProjects\\db-generator\\src\\main\\resources\\last_names"),
    COMPANY_NAMES("C:\\Users\\raduy\\IdeaProjects\\db-generator\\src\\main\\resources\\company_names"),
    COUNTRIES("C:\\Users\\raduy\\IdeaProjects\\db-generator\\src\\main\\resources\\countries"),
    CITIES("C:\\Users\\raduy\\IdeaProjects\\db-generator\\src\\main\\resources\\cities"),
    STREETS("C:\\Users\\raduy\\IdeaProjects\\db-generator\\src\\main\\resources\\streets"),
    DESCRIPTIONS("C:\\Users\\raduy\\IdeaProjects\\db-generator\\src\\main\\resources\\descriptions");

    private String filePath;
    private FilePatches(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return filePath;
    }
}
