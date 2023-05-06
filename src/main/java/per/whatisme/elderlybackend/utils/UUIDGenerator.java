package per.whatisme.elderlybackend.utils;

import com.fasterxml.uuid.Generators;

public class UUIDGenerator {
    public static String generate() {
        return Generators.timeBasedGenerator().generate().toString();
    }
}
