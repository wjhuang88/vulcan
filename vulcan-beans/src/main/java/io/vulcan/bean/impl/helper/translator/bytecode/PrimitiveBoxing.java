package io.vulcan.bean.impl.helper.translator.bytecode;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;

public enum PrimitiveBoxing {

    BOOLEAN(Boolean.class, "valueOf", "(Z)Ljava/lang/Boolean;"),

    BYTE(Byte.class, "valueOf", "(B)Ljava/lang/Byte;"),

    SHORT(Short.class, "valueOf", "(S)Ljava/lang/Short;"),

    CHARACTER(Character.class, "valueOf", "(C)Ljava/lang/Character;"),

    INTEGER(Integer.class, "valueOf", "(I)Ljava/lang/Integer;"),

    LONG(Long.class, "valueOf", "(J)Ljava/lang/Long;"),

    FLOAT(Float.class, "valueOf", "(F)Ljava/lang/Float;"),

    DOUBLE(Double.class, "valueOf", "(D)Ljava/lang/Double;");
    
    PrimitiveBoxing(Class<?> wrapperType,
        String boxingMethodName,
        String boxingMethodDescriptor) {
            this.wrapperType = TypeDescription.ForLoadedType.of(wrapperType);
            this.boxingMethodName = boxingMethodName;
            this.boxingMethodDescriptor = boxingMethodDescriptor;
    }

    private final TypeDescription wrapperType;

    private final String boxingMethodName;

    private final String boxingMethodDescriptor;

    static PrimitiveBoxing forPrimitive(final Class<?> clazz) {
        if (clazz == boolean.class) {
            return BOOLEAN;
        } else if (clazz == byte.class) {
            return BYTE;
        } else if (clazz == short.class) {
            return SHORT;
        } else if (clazz == char.class) {
            return CHARACTER;
        } else if (clazz == int.class) {
            return INTEGER;
        } else if (clazz == long.class) {
            return LONG;
        } else if (clazz == float.class) {
            return FLOAT;
        } else if (clazz == double.class) {
            return DOUBLE;
        } else {
            throw new IllegalArgumentException("Not a non-void, primitive type: " + clazz);
        }
    }

    public static void apply(final Class<?> clazz, final MethodVisitor mv) {
        final PrimitiveBoxing instance = PrimitiveBoxing.forPrimitive(clazz);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                instance.wrapperType.getInternalName(),
                instance.boxingMethodName,
                instance.boxingMethodDescriptor,
                false);
    }
}
