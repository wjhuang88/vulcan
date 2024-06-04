package zone.hwj.vulcan.bean.impl.helper.map2bean;

final class FieldNameMapper {
    static String mapToField(String methodName) {
        int len = methodName.length();
        assert len > 3;
        for (int i = 3; i < len; i++) {
            char c = methodName.charAt(i);
            if (Character.isLowerCase(c)) {
                return Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
            }
        }
        return methodName.substring(3);
    }
}
