package io.vulcan.bean.impl.helper.map2bean;

import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;

public class MapToBeanMethodImpl implements Implementation {

    private final Class<?> clazz;

    public MapToBeanMethodImpl(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public ByteCodeAppender appender(Target implementationTarget) {
        return new MapToBeanMethodAppender(clazz);
    }

    @Override
    public InstrumentedType prepare(InstrumentedType instrumentedType) {
        return instrumentedType;
    }
}
