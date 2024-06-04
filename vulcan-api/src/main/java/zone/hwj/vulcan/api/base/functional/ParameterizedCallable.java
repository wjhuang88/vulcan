package zone.hwj.vulcan.api.base.functional;

@FunctionalInterface
public interface ParameterizedCallable<IN, R> {
    R call(IN input) throws Exception;
}
