package serialization;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class MethodDeclarationDeserializer implements JsonDeserializer<MethodDeclaration> {

    final String classPrefix = "public class Test {";
    final String classSuffix = "}";

    @Override
    public MethodDeclaration deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        String methodString = classPrefix + jsonElement.getAsJsonPrimitive().getAsString()  + classSuffix;
        return (MethodDeclaration)JavaParser.parse(methodString).getType(0).getMember(0);
    }
}
