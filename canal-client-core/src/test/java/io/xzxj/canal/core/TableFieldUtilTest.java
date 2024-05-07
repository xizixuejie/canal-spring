package io.xzxj.canal.core;

import io.xzxj.canal.core.entity.Example;
import io.xzxj.canal.core.util.TableFieldUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TableFieldUtilTest {

    @Test
    void testSetFiledValue() throws NoSuchFieldException, IllegalAccessException {
        Example example = new Example();
        final String filedName = "id";
        final String value = "123";
        TableFieldUtil.setFieldValue(example, filedName, value);
        assertEquals(value, example.getId());
    }

    @Test
    void testSetFiledValueNoField() {
        Example example = new Example();
        final String filedName = "xxx";
        final String value = "123";
        assertThrows(NoSuchFieldException.class, () -> TableFieldUtil.setFieldValue(example, filedName, value));
    }

}
