package de.hakuyamu.skybee.votesystem.util;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {

    @Nullable
    public static File getFileIfExists(String dir, String filename) {
        File file = new File(dir, filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        return file;
    }

    public static File getFileAndCreate(String dir, String filename) {
        File file = new File(dir, filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static JSONObject readJsonObject(File file) throws IOException, ParseException {
        FileReader reader = new FileReader(file);
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(reader);
    }

    public static JSONArray readJsonArray(File file) throws IOException, ParseException {
        FileReader reader = new FileReader(file);
        JSONParser parser = new JSONParser();
        return (JSONArray) parser.parse(reader);
    }

    public static void writeJsonToFile(JSONObject object, @NotNull File file) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(object.toJSONString());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeJsonToFile(JSONArray object, @NotNull File file) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(object.toJSONString());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
