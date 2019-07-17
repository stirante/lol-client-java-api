package com.stirante.utils;

import com.stirante.lolclient.ClientApi;
import io.swagger.oas.models.PathItem;
import io.swagger.oas.models.media.ArraySchema;
import io.swagger.oas.models.media.Schema;
import io.swagger.parser.OpenAPIParser;
import io.swagger.parser.models.ParseOptions;
import io.swagger.parser.models.SwaggerParseResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Generates all classes from OpenAPI scheme. League of Legends client should be opened while running generator.
 */
@SuppressWarnings("deprecation")
public class ClassGenerator {

    /**
     * List of reserved keywords, which can't be part of the class name or field name. It's not complete, but works
     * for now.
     */
    private static final List<String> RESERVED =
            Arrays.asList("implements", "int", "long", "short", "extends", "super", "char", "byte", "private", "protected", "public", "class", "default");
    private static final String PATH = "src/main/java/generated/";

    public static void main(String[] args) throws IOException {
        ClientApi.setLegacyMode(true);
        ClientApi api = new ClientApi();
        String openapiJson = api.getOpenapiJson();
        File f = new File(PATH);
        // create folder just in case
        f.mkdir();
        // delete all previously generated classes
        Arrays.stream(Objects.requireNonNull(f.listFiles())).forEach(File::delete);
        SwaggerParseResult swagger =
                new OpenAPIParser().readContents(openapiJson, new ArrayList<>(), new ParseOptions());
        Map<String, Schema> schemas = swagger.getOpenAPI().getComponents().getSchemas();
        for (String s : schemas.keySet()) {
            boolean importList = false;
            boolean importSerializedName = false;
            Schema schema = schemas.get(s);
            if (schema.getType().equalsIgnoreCase("object")) {
                StringBuilder b = new StringBuilder();
                b
                        .append("package generated;")
                        .append("%imports%")
                        .append("\n")
                        .append("\npublic class ").append(s).append(" {")
                        .append("\n");
                Map<String, Schema> properties = schema.getProperties();
                for (String s1 : properties.keySet()) {
                    Schema prop = properties.get(s1);
                    String type = getType(prop, true);
                    if (type.startsWith("List")) {
                        importList = true;
                    }
                    if (isValidName(s1)) {
                        b.append("\n\tpublic ").append(type).append(" ").append(s1).append(";");
                    }
                    else {
                        importSerializedName = true;
                        b.append("\n\t@SerializedName(\"").append(s1).append("\")");
                        b.append("\n\tpublic ").append(type).append(" ").append(toValidName(s1)).append(";");
                    }
                }
                b
                        .append("\n")
                        .append("\n")
                        .append("}");
                StringBuilder imports = new StringBuilder();
                if (importList || importSerializedName) {
                    imports.append("\n");
                }
                if (importList) {
                    imports.append("\nimport java.util.List;");
                }
                if (importSerializedName) {
                    imports.append("\nimport com.google.gson.annotations.SerializedName;");
                }
                saveFile(s, b.toString().replace("%imports%", imports));
            }
            else if (schema.getType().equalsIgnoreCase("string") && schema.getEnum() != null &&
                    !schema.getEnum().isEmpty()) {
                StringBuilder b = new StringBuilder();
                b
                        .append("package generated;")
                        .append("\n")
                        .append("\nimport com.google.gson.annotations.SerializedName;")
                        .append("\n")
                        .append("\npublic enum ").append(s).append(" {")
                        .append("\n");
                boolean first = true;
                for (Object o : schema.getEnum()) {
                    if (!first) {
                        b.append(",");
                    }
                    else {
                        first = false;
                    }
                    b.append("\n\t@SerializedName(\"").append(o.toString()).append("\")");
                    b.append("\n\t").append(toEnumName(o.toString()));
                }
                b
                        .append("\n")
                        .append("\n")
                        .append("}");
                saveFile(s, b.toString());
            }
        }
        // generate uri map, so we can match class by it's URI (for receiving live events from client)
        StringBuilder b = new StringBuilder();
        b
                .append("package generated;")
                .append("\n")
                .append("\nimport java.util.HashMap;")
                .append("\n")
                .append("\npublic class UriMap {")
                .append("\n")
                .append("\n\tpublic static final HashMap<String, Class> toClass = new HashMap<>();")
                .append("\n")
                .append("\n\tstatic {");
        for (String path : swagger.getOpenAPI().getPaths().keySet()) {
            PathItem item = swagger.getOpenAPI().getPaths().get(path);
            if (item.getGet() != null && item.getGet().getResponses().containsKey("200") &&
                    item.getGet().getResponses().get("200").getContent().containsKey("application/json")) {
                Schema schema =
                        item.getGet().getResponses().get("200").getContent().get("application/json").getSchema();
                if (schema == null) {
                    continue;
                }
                b.append("\n\t\ttoClass.put(\"")
                        .append(toRegex(path))
                        .append("\", ")
                        .append(getType(schema, false))
                        .append(".class);");
            }
        }
        b
                .append("\n\t}")
                .append("\n")
                .append("\n}");
        saveFile("UriMap", b.toString());
    }

    /**
     * Turns URI with parameters into regex
     * Example:
     * '/voice-chat/v2/sessions/{sessionId}/participants/{participantId}'
     *                       is turned into
     * '\/voice-chat\/v2\/sessions\/[^/]+\/participants\/[^/]+'
     * @param str path with parameters
     * @return regex string
     */
    private static String toRegex(String str) {
        System.out.println(str);
        return str.replaceAll("/", "\\\\\\\\/").replaceAll("\\{[^/]+\\}", "[^/]+");
    }

    /**
     * Saves class to file
     * @param s class name
     * @param b class contents
     */
    private static void saveFile(String s, String b) throws IOException {
        File file = new File(PATH + s + ".java");
        FileWriter fw = new FileWriter(file);
        fw.write(b);
        fw.flush();
        fw.close();
        System.out.println(s + ".java saved!");
    }

    /**
     * Returns java type name based on type from scheme
     * @param schema schema
     * @param asList should type be list instead of an array
     * @return java type or empty string if schema type is null
     */
    private static String getType(Schema schema, boolean asList) {
        String type = "Object";
        if (schema.getType() == null && schema.get$ref() == null) {
            System.out.println(schema);
            return "";
        }
        if (schema.get$ref() != null && !schema.get$ref().isEmpty()) {
            type = schema.get$ref().replace("#/components/schemas/", "");
        }
        else if (schema.getType().equalsIgnoreCase("integer")) {
            if (schema.getFormat().equalsIgnoreCase("int64")) {
                type = "Long";
            }
            else {
                type = "Integer";
            }
        }
        else if (schema.getType().equalsIgnoreCase("string")) {
            type = "String";
        }
        else if (schema.getType().equalsIgnoreCase("array")) {
            ArraySchema as = (ArraySchema) schema;
            if (asList) {
                type = "List<" + getType(as.getItems(), true) + ">";
            }
            else {
                type = getType(as.getItems(), false) + "[]";
            }
        }
        else if (schema.getType().equalsIgnoreCase("boolean")) {
            type = "Boolean";
        }
        else if (schema.getType().equalsIgnoreCase("number")) {
            type = "Double";
        }
        return type;
    }

    /**
     * Is name valid for java. Probably can be improved
     * @param s name
     * @return true if name is valid
     */
    private static boolean isValidName(String s) {
        if (RESERVED.contains(s)) {
            return false;
        }
        if (s.contains("-")) {
            return false;
        }
        return true;
    }

    /**
     * Turns name into valid java name
     * @param s name
     * @return valid java name
     */
    private static String toValidName(String s) {
        if (RESERVED.contains(s)) {
            return s + "Field";
        }
        return s.replaceAll("-", "_");
    }

    /**
     * Turns name into more java-like enum name. Could be probably improved.
     * Example: 'enum-name' into 'ENUM_NAME'
     * @param s name
     * @return java enum name
     */
    private static String toEnumName(String s) {
        return s.replaceAll("-", "_").toUpperCase();
    }

}
