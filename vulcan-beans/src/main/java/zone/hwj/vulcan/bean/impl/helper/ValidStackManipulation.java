package zone.hwj.vulcan.bean.impl.helper;

import net.bytebuddy.implementation.bytecode.StackManipulation;

@FunctionalInterface
public interface ValidStackManipulation extends StackManipulation {

    @Override
    default boolean isValid() {
        return true;
    }
}
