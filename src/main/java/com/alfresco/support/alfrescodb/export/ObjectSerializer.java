package com.alfresco.support.alfrescodb.export;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

public class ObjectSerializer {
    public static final String EXPORT_CSV = "csv";
    public static final String EXPORT_JSON = "json";

    public static void serialize(Object object, String outputPath, String format) throws IOException {
        switch (format.toLowerCase()) {
            case EXPORT_JSON:
                serializeToJson(object, outputPath);
                break;
            case EXPORT_CSV:
                if (object instanceof List && !((List<?>) object).isEmpty()) {
                    // Assume every element has the same type
                    serializeListToCsv((List<?>) object, outputPath);
                } else if (object instanceof Map ) {
                    throw new IllegalArgumentException("Unsupported Map to CSV!");
                } else {
                    serializeToCsv(object, outputPath);
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported format: " + format);
        }
    }
    
    private static void serializeToJson(Object object, String outputPath) throws IOException {
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_DEFAULT); //Remove null and default values from serialization
        jsonMapper.enable(SerializationFeature.INDENT_OUTPUT); // Pretty print
        jsonMapper.writeValue(new File(outputPath), object);
    }

    private static void serializeListToCsv(List<?> list, String outputPath) throws IOException {
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema schema = csvMapper.schemaFor(list.get(0).getClass()).withHeader();
        csvMapper.writer(schema).writeValue(new File(outputPath), list);
    }

    private static void serializeToCsv(Object object, String outputPath) throws IOException {
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema schema = csvMapper.schemaFor(object.getClass()).withoutHeader(); // Object MUST have public getters
        csvMapper.writer(schema).writeValue(new File(outputPath), object);
    }
}

