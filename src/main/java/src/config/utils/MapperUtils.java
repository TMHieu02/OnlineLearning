package src.config.utils;

import java.lang.reflect.Field;

public class MapperUtils<T, D> {
    public static <T, D> void toDto(T src, D des) {
        try {
            Field[] fields = src.getClass().getSuperclass().getDeclaredFields();
            for (Field field : fields) {
                if (field.get(src) != null && !field.getName().contains("id")) {
                    Field sd = des.getClass().getDeclaredField(field.getName());
                    sd.setAccessible(true);
                    sd.set(des, field.get(src));
                }
            }
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

//    public static Object getFieldValueByName(Object obj, String fieldName) {
//        try {
//            Field field = obj.getClass().getDeclaredField(fieldName);
//            field.setAccessible(true);
//            return field.get(obj);
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//    private static List<Field> getAllFields(Class<?> clazz) {
//        List<Field> fields = new ArrayList<>();
//        Class<?> currentClass = clazz;
//        while (currentClass != null) {
//            for (Field field : currentClass.getDeclaredFields()) {
//                fields.add(field);
//            }
//            currentClass = currentClass.getSuperclass();
//        }
//        return fields;
//    }
}
