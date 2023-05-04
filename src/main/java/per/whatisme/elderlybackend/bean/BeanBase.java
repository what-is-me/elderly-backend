package per.whatisme.elderlybackend.bean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Data
public class BeanBase {
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = null;
        try {
            jsonString = mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(this);
        } catch (JsonProcessingException ignore) {
        }
        return jsonString;
    }

    public void SetProperty(String pro, Object val) throws NoSuchFieldException, IllegalAccessException {
        Class<?> clazz = this.getClass();
        Field field = clazz.getDeclaredField(pro);
        field.setAccessible(true);
        field.set(this, val);
    }
    public Object GetProperty(String pro) throws NoSuchFieldException, IllegalAccessException {
        Class<?> clazz = this.getClass();
        Field field = clazz.getDeclaredField(pro);
        field.setAccessible(true);
        return field.get(this);
    }
    public Class<?> propertyClass(String pro) throws NoSuchFieldException {
        Class<?> clazz = this.getClass();
        Field field = clazz.getDeclaredField(pro);
        return field.getType();
    }
    public void SetProperties(Map<String, Object> map) {
        map.forEach((key, value) -> {
            try {
                SetProperty(key, value);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public Map<String, Object> toMap() throws IllegalAccessException {
        Map<String, Object> ret = new HashMap<>();
        Class<?> clazz = this.getClass();
        Field[] fields = clazz.getFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Object val = field.get(this);
            if (val == null) continue;
            ret.put(field.getName(), val);
        }
        return ret;
    }
}
