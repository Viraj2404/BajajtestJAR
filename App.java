package Bajajtest.Bajajtest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class App {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar DestinationFinder.jar <PRN Number> <Path to JSON File>");
            return;
        }

        String prnNumber = args[0].toLowerCase().replaceAll("\\s+", "");
        String jsonFilePath = args[1];

        File jsonFile = new File(jsonFilePath);

        if (!jsonFile.exists()) {
            System.out.println("The JSON file does not exist.");
            return;
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonFile);

            String destination = findDestination(rootNode);
            if (destination != null) {
                String randomString = generateRandomString(8);
                String concatenatedValue = prnNumber + destination + randomString;
                String hashedValue = generateMD5Hash(concatenatedValue);
                System.out.println(hashedValue + ";" + randomString);
            } else {
                System.out.println("No destination key found in the JSON file.");
            }

        } catch (IOException | NoSuchAlgorithmException e) {
            System.out.println("An error occurred while processing the JSON file.");
            e.printStackTrace();
        }
    }

    private static String findDestination(JsonNode node) {
        if (node.isObject()) {
            if (node.has("destination")) {
                return node.get("destination").asText();
            }
            for (JsonNode child : node) {
                String result = findDestination(child);
                if (result != null) {
                    return result;
                }
            }
        } else if (node.isArray()) {
            for (JsonNode child : node) {
                String result = findDestination(child);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }

        return sb.toString();
    }

    private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();

        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }
}
