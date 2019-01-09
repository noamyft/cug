package serialization;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import common.MethodMutantData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class MutantFileIO {


    public void saveMutantData(MethodMutantData methodMutantData, Path path) throws IOException {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(MethodDeclaration.class, new MethodDeclarationSerializer())
                .create();

        String json = gson.toJson(methodMutantData);

        Files.write(path, json.getBytes());

    }

    public MethodMutantData loadMutantData(Path Path) throws IOException {

        String content = new String(Files.readAllBytes(Path));
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(MethodDeclaration.class, new MethodDeclarationDeserializer())
                .create();

        MethodMutantData result = gson.fromJson(content, MethodMutantData.class);

        return result;
    }
}
