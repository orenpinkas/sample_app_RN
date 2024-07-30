package com.outbrain.tests;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;


public class TestsUtils {

    public static String readJsonFromFile(String fileName) throws Exception {
        JSONParser parser = new JSONParser();
        String filePath = ClassLoader.getSystemClassLoader().getResource(fileName).getFile();
        System.out.println("--> " + filePath);
        org.json.simple.JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(filePath));
        return jsonObject.toString();
    }

    public static String readFileToString(String path)  throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, StandardCharsets.US_ASCII);
    }
}
