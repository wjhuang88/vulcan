package zone.hwj.vulcan.bean.impl.helper.translator;

public interface Translator<T> {

    /**
     * 将源字段对象转换为目标类型T的转换逻辑实现
     * @param value 源字段对象
     * @return 转换结果
     */
    T translate(Object value);
}
