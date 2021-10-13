package io.vulcan.bean.helper.bean2bean;

import io.vulcan.bean.helper.ValidStackManipulation;
import io.vulcan.bean.helper.translator.Tools;
import io.vulcan.bean.helper.translator.Translators;
import io.vulcan.bean.helper.translator.bytecode.PrimitiveBoxing;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.implementation.Implementation.Context;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.jar.asm.Type;

class BeanToBeanMethodAppender implements ByteCodeAppender {

    private final Class<?> srcClass;
    private final Class<?> distClass;

    BeanToBeanMethodAppender(Class<?> srcClass, Class<?> distClass) {
        this.srcClass = srcClass;
        this.distClass = distClass;
    }

    @Override
    public Size apply(MethodVisitor methodVisitor, Context implementationContext,
            MethodDescription instrumentedMethod) {

        final List<StackManipulation> manipulations = new ArrayList<>();

        final Method[] declaredMethods = distClass.getMethods();
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
        final String fieldNameSuffix = methodName.substring(4);
        final String upperFieldName = methodName.charAt(3) + fieldNameSuffix;

        Method getterMethod;
        try {
            // 尝试获取源类型的getter方法
            getterMethod = srcClass.getMethod("get" + upperFieldName);
        } catch (NoSuchMethodException e) {
            // boolean型可能是is开头的方法
            if (methodClass == Boolean.class || methodClass == Boolean.TYPE) {
                try {
                    getterMethod = srcClass.getMethod("is" + upperFieldName);
                } catch (NoSuchMethodException ex) {
                    // 没有找到getter方法，不处理该字段
                    return;
                }
            } else {
                // 没有找到getter方法，不处理该字段
                return;
            }
        }
        Class<?> getterClass = getterMethod.getReturnType();
        final boolean sameType = getterClass == methodClass;

        mv.visitVarInsn(Opcodes.ALOAD, 2); // 1 - dist instance
        mv.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(distClass)); // 1 - cast dist instance

        final Method translateMethod = Tools.getTranslateMethod(methodClass);
        final String translateMethodName = translateMethod.getName();
        if (!sameType) {
            final boolean hasClassParams = translateMethodName.equals("translate");
            mv.visitVarInsn(Opcodes.ALOAD, 0); // 2 - this
            mv.visitFieldInsn(Opcodes.GETFIELD, ic.getInstrumentedType().getInternalName(),
                    "translators", Type.getDescriptor(Translators.class)); // 2

            if (hasClassParams) {
                mv.visitLdcInsn(Type.getType(methodClass)); // 3
            }
        }

        mv.visitVarInsn(Opcodes.ALOAD, 1); // 4 - src instance
        mv.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(srcClass)); // 4
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                Type.getInternalName(srcClass),
                getterMethod.getName(),
                Type.getMethodDescriptor(getterMethod),
                false); // 4
        if (!sameType) {
            if (getterClass.isPrimitive()) {
                PrimitiveBoxing.apply(getterClass, mv);
            }
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Translators.class),
                    translateMethodName, Type.getMethodDescriptor(translateMethod), false); // 2
        }
        if (!methodClass.isPrimitive()) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(methodClass)); // 2
        }

        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                Type.getInternalName(distClass),
                methodName,
                methodDescriptor,
                false); // 1
    }
}
