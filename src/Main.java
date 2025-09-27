

import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.models.chat.OllamaChatMessage;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final String HOST = "http://localhost:11434/";
    private static final String MODEL = "tinyllama:latest";
    private static final String DATABASE_PATH = "Database";

    public static void main(String[] args) throws Exception {
        // Ollama & Ollama4j setup
        OllamaAPI ollamaAPI = new OllamaAPI(HOST);
        ollamaAPI.setVerbose(true);
        ollamaAPI.ping();
        ollamaAPI.pullModel(MODEL);

        StringBuilder modifiedQueryBuilder = new StringBuilder().append("If neccesary, use any of the following files as context to provide a simple answer to the question at the end.\n\n");
        BufferedReader cliReader = new BufferedReader(new InputStreamReader(System.in));

        String[] files = new File(DATABASE_PATH).list();
        if (files != null) {
            for (String name : files) {
                String path = DATABASE_PATH + "/" + name;
                File file = new File(path);
                int size = (int) file.length();

                modifiedQueryBuilder.append(file.getName());
                modifiedQueryBuilder.append(":\n");

                BufferedReader dataReader = new BufferedReader(new FileReader(path));
                char[] buf = new char[size];
                dataReader.read(buf);
                modifiedQueryBuilder.append(buf);
                modifiedQueryBuilder.append("\n\n");
            }
        }
        // Adds user's query to modified Query
        modifiedQueryBuilder.append("question: ");
        modifiedQueryBuilder.append(cliReader.readLine());
        modifiedQueryBuilder.append("\n");
        System.out.println(modifiedQueryBuilder);

        // Send Modified query to Ollama
        List<OllamaChatMessage> messages = new ArrayList<>();
        messages.add(new OllamaChatMessage(OllamaChatMessageRole.USER, modifiedQueryBuilder.toString()));
        OllamaChatResult chatResult = ollamaAPI.chat(MODEL, messages);

        System.out.println(MODEL + ": " + chatResult.getResponseModel().getMessage().getContent());
    }
}