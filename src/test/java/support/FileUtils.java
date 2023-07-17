package support;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.RegistrationSchema100;
import uk.gov.dwp.health.pip.application.manager.model.registration.data.RegistrationSchema110;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class FileUtils {

  public static RegistrationSchema100 getRegistrationDataFromFile(String registrationDataJsonFile)
      throws IOException {
    Map<String, Object> registrationDataJson = readTestFile(registrationDataJsonFile);
    return parseJson(registrationDataJson);
  }

  public static RegistrationSchema110 getRegistrationSchema110DataFromFile(String registrationDataJsonFile)
      throws IOException {
    Map<String, Object> registrationDataJson = readTestFile(registrationDataJsonFile);
    return parseJsonSchema110(registrationDataJson);
  }

  public static Map<String, Object> readTestFile(String file) throws IOException {
    String fileAsString = readTestFromFileAsString(file);

    TypeReference<HashMap<String, Object>> typeRef = new TypeReference<>() {};

    return new ObjectMapper().readValue(fileAsString, typeRef);
  }

  public static Object readTestFileAsObject(String file) throws IOException {
    String fileAsString = readTestFromFileAsString(file);

    return new ObjectMapper().readValue(fileAsString, Object.class);
  }

  private static String readTestFromFileAsString(String file) throws IOException {
    InputStream resource = new ClassPathResource(file).getInputStream();

    String json;
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource))) {
      json = reader.lines().collect(Collectors.joining("\n"));
    }
    return json;
  }

  private static RegistrationSchema100 parseJson(Map<String, Object> registrationDataJson) {
    return parseJson(registrationDataJson, RegistrationSchema100.class);
  }

  private static RegistrationSchema110 parseJsonSchema110(Map<String, Object> registrationDataJson) {
    return parseJson(registrationDataJson, RegistrationSchema110.class);
  }

  private static <T> T parseJson(Map<String, Object> registrationDataJson, Class<T> clazz) {
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.convertValue(registrationDataJson, clazz);
  }
}
