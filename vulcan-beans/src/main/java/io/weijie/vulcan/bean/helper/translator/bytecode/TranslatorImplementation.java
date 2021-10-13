package io.weijie.vulcan.bean.helper.translator.bytecode;

import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;

public class TranslatorImplementation implements Implementation {

    @Override
    public ByteCodeAppender appender(Target implementationTarget) {
        return new TranslatorAppender();
    }

    @Override
    public InstrumentedType prepare(InstrumentedType instrumentedType) {
        return instrumentedType;
    }
}
