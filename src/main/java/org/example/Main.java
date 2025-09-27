package org.example;

import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.models.chat.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static String HOST = "http://localhost:11434/";
    private static String MODEL = "tinyllama:latest";
    private static String DATABASE_PATH = "Database";

    public static void main(String[] args) throws Exception {
        OllamaAPI ollamaAPI = new OllamaAPI(HOST);
        ollamaAPI.setVerbose(true);
        ollamaAPI.ping();
        ollamaAPI.pullModel(MODEL);

        StringBuilder messageBuilder = new StringBuilder().append("If neccesary, use any of the following files as context to provide a simple answer to the question at the end.\n");
        BufferedReader cliReader = new BufferedReader(new InputStreamReader(System.in));
        for(String name : new File(DATABASE_PATH).list()) {
            String path = DATABASE_PATH + "/" + name;
            long size = new File(path).length();
            messageBuilder.append(new File(path).getName());
            messageBuilder.append(":\n");
            BufferedReader dataReader = new BufferedReader(new FileReader(path));
            char[] buf = new char[(int) size];
            dataReader.read(buf);
            messageBuilder.append(buf);
            messageBuilder.append("\n\n");
        }
        messageBuilder.append("Question: ");
        messageBuilder.append(cliReader.readLine());
        System.out.println(messageBuilder.toString());


        List<OllamaChatMessage> messages = new ArrayList<>();
        messages.add(new OllamaChatMessage(OllamaChatMessageRole.USER, messageBuilder.toString()));
        OllamaChatResult chatResult = ollamaAPI.chat(MODEL, messages);
        System.out.println(
                "Model answer: " + chatResult.getResponseModel().getMessage().getContent());
    }
}