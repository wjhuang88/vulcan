package io.vulcan.bean.impl.helper.translator.bytecode;

import io.vulcan.bean.impl.helper.ValidStackManipulation;
import io.vulcan.bean.impl.helper.translator.Translators;
import java.util.Collections;
import java.util.List;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.implementation.Implementation.Context;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.jar.asm.Type;

public class TranslatorAppender implements ByteCodeAppender {

    @Override
    public Size apply(MethodVisitor methodVisitor, Context implementationContext,
            MethodDescription instrumentedMethod) {

        List<StackManipulation> manipulations = Collections.singletonList(makeManipulation(implementationContext));
        StackManipulation.Size operandStackSize = new StackManipulation.Compound(manipulations)
                .apply(methodVisitor, implementationContext);
        return new Size(operandStackSize.getMaximalSize(),
                instrumentedMethod.getStackSize());
    }

    private StackManipulation makeManipulation(Context implementationContext) {
        return (ValidStackManipulation) (mv, ic) -> {
            mv.visitVarInsn(Opcodes.ALOAD, 0); // 1
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(Object.class),
                    "<init>", Type.getMethodDescriptor(Type.VOID_TYPE), false);
            mv.visitVarInsn(Opcodes.ALOAD, 0); // 1
            mv.visitFieldInsn(Opcodes.GETSTATIC, Type.getInternalName(Translators.class),
                    "INSTANCE", Type.getDescriptor(Translators.class)); // 2
            mv.visitFieldInsn(Opcodes.PUTFIELD, implementationContext.getInstrumentedType().getInternalName(),
                    "translators", Type.getDescriptor(Translators.class)); // 0
            mv.visitInsn(Opcodes.RETURN);
            return new StackManipulation.Size(0, 2);
        };
    }
}
