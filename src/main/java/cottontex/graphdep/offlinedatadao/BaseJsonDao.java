package cottontex.graphdep.offlinedatadao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import cottontex.graphdep.constants.JsonPaths;
import cottontex.graphdep.utils.EncryptionUtil;
import cottontex.graphdep.utils.LoggerUtility;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class BaseJsonDao<T> implements GenericDao<T> {
    protected final ObjectMapper objectMapper;
    protected final String fileName;
    protected final TypeReference<List<T>> typeReference;

    public BaseJsonDao(String fileName, TypeReference<List<T>> typeReference) {
        LoggerUtility.info("BaseJsonDao constructor called with fileName: " + fileName);

        File baseDir = new File(JsonPaths.BASE_PATH);
        LoggerUtility.info("Base directory: " + baseDir.getAbsolutePath());
        if (!baseDir.isAbsolute()) {
            baseDir = new File(System.getProperty("user.dir"), JsonPaths.BASE_PATH);
            LoggerUtility.info("Adjusted base directory: " + baseDir.getAbsolutePath());
        }
        if (fileName.startsWith(JsonPaths.BASE_PATH)) {
            fileName = fileName.substring(JsonPaths.BASE_PATH.length());
        }
        this.fileName = new File(baseDir, fileName).getAbsolutePath();
        LoggerUtility.info("JSON file path set to: " + this.fileName);
        this.typeReference = typeReference;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public List<T> findAll() {
        List<T> entities = readFromFile();
        return entities != null ? entities : new ArrayList<>();
    }

    @Override
    public abstract Optional<T> findById(int id);

    @Override
    public void save(T entity) {
        List<T> entities = findAll();
        entities.add(entity);
        writeToFile(entities);
    }

    @Override
    public abstract void update(T entity);

    @Override
    public abstract void delete(int id);

    protected List<T> readFromFile() {
        File file = new File(fileName);
        LoggerUtility.info("Reading from JSON file: " + file.getAbsolutePath());
        if (!file.exists()) {
            LoggerUtility.info("File does not exist, returning empty list");
            return new ArrayList<>();
        }
        try {
            String encrypted = new String(java.nio.file.Files.readAllBytes(file.toPath()));
            String decrypted = EncryptionUtil.decrypt(encrypted);
            List<T> result = objectMapper.readValue(decrypted, typeReference);
            LoggerUtility.info("Successfully read " + result.size() + " entries from JSON file");
            return result;
        } catch (Exception e) {
            LoggerUtility.error("Error reading from JSON file", e);
            return new ArrayList<>();
        }
    }

    protected void writeToFile(List<T> entities) {
        LoggerUtility.info("Writing " + entities.size() + " entities to JSON file: " + fileName);
        try {
            String json = objectMapper.writeValueAsString(entities);
            String encrypted = EncryptionUtil.encrypt(json);
            java.nio.file.Files.write(new File(fileName).toPath(), encrypted.getBytes());
            LoggerUtility.info("Successfully wrote entities to JSON file");
        } catch (Exception e) {
            LoggerUtility.error("Error writing to JSON file", e);
        }
    }
}