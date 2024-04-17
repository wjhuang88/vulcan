package io.vulcan.bean.impl.helper.map2bean;

import io.vulcan.bean.impl.helper.ValidStackManipulation;
import io.vulcan.bean.impl.helper.translator.Tools;
import io.vulcan.bean.impl.helper.translator.Translators;
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

class MapToBeanMethodAppender implements ByteCodeAppender {

    private final Class<?> clazz;

    MapToBeanMethodAppender(Class<?> clazz) {
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
                    if (methodName.startsWith("set") && parameterTypes.length == 1) {
                        transferField(mv, ic, methodName, parameterTypes[0], Type.getMethodDescriptor(method));
                    }
                }
                mv.visitVarInsn(Opcodes.ALOAD, 2);
                return new StackManipulation.Size(0, 5);
            });
            manipulations.add(MethodReturn.of(instrumentedMethod.getReturnType()));
        }

        StackManipulation.Size operandStackSize = new StackManipulation.Compound(manipulations)
                .apply(methodVisitor, implementationContext);
        return new Size(operandStackSize.getMaximalSize(),
                instrumentedMethod.getStackSize());
    }

    private void transferField(MethodVisitor mv, Context ic, String methodName, Class<?> methodClass,
            String methodDescriptor) {
        final String fieldName = FieldNameMapper.mapToField(methodName);
        final Method translateMethod = Tools.getTranslateMethod(methodClass);
        final String translateMethodName = translateMethod.getName();
        final boolean hasClassParams = translateMethodName.equals("translate");

        mv.visitVarInsn(Opcodes.ALOAD, 2); // 1 - instance
        mv.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(clazz)); // 1
        mv.visitVarInsn(Opcodes.ALOAD, 0); // 2 - this
        mv.visitFieldInsn(Opcodes.GETFIELD, ic.getInstrumentedType().getInternalName(),
                "translators", Type.getDescriptor(Translators.class)); // 2

        if (hasClassParams) {
            mv.visitLdcInsn(Type.getType(methodClass)); // 3
        }

        mv.visitVarInsn(Opcodes.ALOAD, 1); // 4 - map
        mv.visitLdcInsn(fieldName); // 5 - fieldName
        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE,
                Type.getInternalName(Map.class),
                "get",
                Type.getMethodDescriptor(Type.getType(Object.class), Type.getType(Object.class)),
                true); // 4
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Translators.class),
                translateMethodName, Type.getMethodDescriptor(translateMethod), false); // 2
        if (!methodClass.isPrimitive()) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(methodClass)); // 2
        }

        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                Type.getInternalName(clazz),
                methodName,
                methodDescriptor,
                false); // 1
    }
}
