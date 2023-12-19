package io.xzxj.canal.core.config;

import io.xzxj.canal.core.convertor.IColumnConvertor;
import io.xzxj.canal.core.convertor.impl.BigDecimalColumnConvertor;
import io.xzxj.canal.core.convertor.impl.BooleanColumnConvertor;
import io.xzxj.canal.core.convertor.impl.ByteArrayColumnConvertor;
import io.xzxj.canal.core.convertor.impl.DateColumnConvertor;
import io.xzxj.canal.core.convertor.impl.DoubleColumnConvertor;
import io.xzxj.canal.core.convertor.impl.FloatColumnConvertor;
import io.xzxj.canal.core.convertor.impl.IntegerColumnConvertor;
import io.xzxj.canal.core.convertor.impl.LongColumnConvertor;
import io.xzxj.canal.core.convertor.impl.SqlDateColumnConvertor;
import io.xzxj.canal.core.convertor.impl.StringListColumnConvertor;

import javax.annotation.Nullable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings({"rawtypes"})
public final class CanalEntityConvertConfig {

    private static CanalEntityConvertConfig instance;

    /**
     * 日期格式规则集合
     */
    private final Set<String> datePatternSet = new HashSet<>();

    /**
     * 属性类型转换器map
     * key: 全类名; value: 类型转换器实现
     */
    private final Map<String, IColumnConvertor<?>> columnConvertorMap = new ConcurrentHashMap<>();

    private CanalEntityConvertConfig() {
        this.initDatePatterns();
        this.initDefaultConvertors();
    }

    public static CanalEntityConvertConfig getInstance() {
        if (instance == null) {
            instance = new CanalEntityConvertConfig();
        }
        return instance;
    }

    /**
     * 初始化默认日期格式化规则
     */
    private void initDatePatterns() {
        Collections.addAll(datePatternSet, "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd HH:mm", "yyyy-MM", "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss",
                "yyyy/MM/dd HH:mm", "yyyy/MM", "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss",
                "yyyy.MM.dd HH:mm", "yyyy.MM");
    }

    /**
     * 获取日期格式化规则
     *
     * @return 所有日期格式化规则数组
     */
    public String[] getDateParsePatterns() {
        return datePatternSet.toArray(new String[]{});
    }

    /**
     * 添加日期格式化规则
     *
     * @param datePattern 日期格式化规则
     */
    public void addDateParsePattern(String datePattern) {
        datePatternSet.add(datePattern);
    }

    /**
     * 初始化默认属性类型转换器
     */
    private void initDefaultConvertors() {
        List<IColumnConvertor<?>> convertorList = Arrays.asList(
                new IntegerColumnConvertor(),
                new LongColumnConvertor(),
                new FloatColumnConvertor(),
                new DoubleColumnConvertor(),
                new BigDecimalColumnConvertor(),
                new BooleanColumnConvertor(),
                new DateColumnConvertor(),
                new SqlDateColumnConvertor(),
                new StringListColumnConvertor(),
                new ByteArrayColumnConvertor()
        );
        this.putAllColumnConvertor(convertorList);
    }

    public void putAllColumnConvertor(List<IColumnConvertor<?>> convertorList) {
        convertorList.forEach(this::putColumnConvertor);
    }

    /**
     * 新增一个属性类型转换器
     *
     * @param convertor 属性类型转换器实现类
     */
    public void putColumnConvertor(IColumnConvertor<?> convertor) {
        Class<? extends IColumnConvertor> clazz = convertor.getClass();
        Type[] interfacesTypes = clazz.getGenericInterfaces();
        for (Type type : interfacesTypes) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Class<?> c = (Class<?>) parameterizedType.getRawType();
            if (c.equals(IColumnConvertor.class)) {
                String typeName = parameterizedType.getActualTypeArguments()[0].getTypeName();
                columnConvertorMap.put(typeName, convertor);
            }
        }
    }

    @Nullable
    public IColumnConvertor<?> getColumnConvertor(String typeName) {
        return this.columnConvertorMap.get(typeName);
    }

}
