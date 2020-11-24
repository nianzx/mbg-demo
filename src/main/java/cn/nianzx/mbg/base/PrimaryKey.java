package cn.nianzx.mbg.base;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

/**
 * MBG生成的实体类中，指明哪个字段是主键
 *
 * @author nianzx
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {FIELD})
public @interface PrimaryKey {
}
