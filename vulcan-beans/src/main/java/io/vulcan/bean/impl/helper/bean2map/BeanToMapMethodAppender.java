package io.vulcan.bean.impl.helper.bean2map;

import io.vulcan.bean.impl.helper.ValidStackManipulation;
import io.vulcan.bean.impl.helper.translator.bytecode.PrimitiveBoxing;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.implementation.Implementation.Context;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.jar.asm.Type;

class BeanToMapMethodAppender implements ByteCodeAppender {

    private final Class<?> clazz;

    BeanToMapMethodAppender(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Size apply(MethodVisitor methodVisitor, Context implementationContext,
            MethodDescription instrumentedMethod) {

        final List<StackManipulation> manipulations = new ArrayList<>();

        final Method[] declaredMethods = clazz.getMethods();
        if (declaredMethods.length > 0) {
            manipulations.add((ValidStackManipulation) (mv, ic) -> {
                for (Method method : declaredMethods) {
                    final Class<?>[] parameterTypes = method.getParameterTypes();
                    final String methodName = method.getName();
                    final Class<?> getterClass = method.getReturnType();
                    if (isGetter(methodName, getterClass, parameterTypes.length)) {
                        transferField(mv, ic, methodName, getterClass, Type.getMethodDescriptor(method));
                    }
                }
                mv.visitVarInsn(Opcodes.ALOAD, 1);
                return new StackManipulation.Size(0, 4);
            });
            manipulations.add(MethodReturn.of(instrumentedMethod.getReturnType()));
        }

        StackManipulation.Size operandStackSize = new StackManipulation.Compound(manipulations)
                .apply(methodVisitor, implementationContext);
        return new Size(operandStackSize.getMaximalSize(),
                instrumentedMethod.getStackSize());
    }

    private boolean isGetter(String methodName, Class<?> returnType, int paramLen) {
        if (paramLen > 0) {
            return false;
        }

        if (methodName.startsWith("get") && !methodName.equals("getClass")) {
            return true;
        }

        return (returnType == Boolean.class || returnType == Boolean.TYPE) && methodName.startsWith("is");
    }

    private void transferField(MethodVisitor mv, Context ic, String methodName, Class<?> methodClass,
            String methodDescriptor) {
        final String fieldName = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);

        mv.visitVarInsn(Opcodes.ALOAD, 1); // 1 - map
        mv.visitLdcInsn(fieldName); // 2 - fieldName

        mv.visitVarInsn(Opcodes.ALOAD, 2); // 3 - instance
        mv.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(clazz)); // 3

        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                Type.getInternalName(clazz),
                methodName,
                methodDescriptor,
                false); // 3
        if (methodClass.isPrimitive()) {
            PrimitiveBoxing.apply(methodClass, mv); // 3
        }

        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE,
                Type.getInternalName(Map.class),
                "put",
                Type.getMethodDescriptor(Type.getType(Object.class), Type.getType(Object.class), Type.getType(Object.class)),
                true); // 1
        mv.visitInsn(Opcodes.POP);
    }
}
