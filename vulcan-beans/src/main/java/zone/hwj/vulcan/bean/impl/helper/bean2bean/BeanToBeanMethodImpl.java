package zone.hwj.vulcan.bean.impl.helper.bean2bean;

import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;

public class BeanToBeanMethodImpl implements Implementation {

    private final Class<?> srcClass;
    private final Class<?> distClass;

    public BeanToBeanMethodImpl(Class<?> srcClass, Class<?> distClass) {
        this.srcClass = srcClass;
        this.distClass = distClass;
    }

    @Override
    public ByteCodeAppender appender(Target implementationTarget) {
        return new BeanToBeanMethodAppender(srcClass, distClass);
    }

    @Override
    public InstrumentedType prepare(InstrumentedType instrumentedType) {
        return instrumentedType;
    }
}
