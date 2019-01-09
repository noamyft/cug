package serialization;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class MethodDeclarationSerializer implements JsonSerializer<MethodDeclaration> {
    @Override
    public JsonElement serialize(MethodDeclaration methodDeclaration, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(methodDeclaration.toString());
    }
}
