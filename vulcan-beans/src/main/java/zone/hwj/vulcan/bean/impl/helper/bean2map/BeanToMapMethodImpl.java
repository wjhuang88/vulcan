package zone.hwj.vulcan.bean.impl.helper.bean2map;

import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;

public class BeanToMapMethodImpl implements Implementation {

    private final Class<?> clazz;

    public BeanToMapMethodImpl(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public ByteCodeAppender appender(Target implementationTarget) {
        return new BeanToMapMethodAppender(clazz);
    }

    @Override
    public InstrumentedType prepare(InstrumentedType instrumentedType) {
        return instrumentedType;
    }
}
