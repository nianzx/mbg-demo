package cn.nianzx.mbg.config.plugin;

import cn.nianzx.mbg.base.PrimaryKey;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.plugins.SerializablePlugin;

import java.util.List;
import java.util.Properties;

/**
 * 实体类生成规则
 *
 * @author nianzx
 */
public class EntityPlugin extends SerializablePlugin {

    //是否使用lombok
    private Boolean useLombok;
    //是否使用swagger
    private Boolean useSwagger;

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        useLombok = Boolean.valueOf(properties.getProperty("useLombok"));
        useSwagger = Boolean.valueOf(properties.getProperty("useSwagger"));
    }

    /**
     * 生成实体中每个属性的get方法 如果不想生成getter 返回false
     */
    @Override
    public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return !useLombok;
    }

    /**
     * 生成实体中每个属性的set方法 如果不想生成setter 返回false
     */
    @Override
    public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return !useLombok;
    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        if (useSwagger) {
            // 追加ApiModelProperty注解
            topLevelClass.addImportedType("io.swagger.annotations.ApiModelProperty");
            field.addAnnotation("@ApiModelProperty" + "(value = \"" + introspectedColumn.getRemarks() + "\", name = \"" + introspectedColumn.getJavaProperty() + "\", dataType = \"" + introspectedColumn.getFullyQualifiedJavaType() + "\")");
        }

        //指示主键是哪个
        topLevelClass.addImportedType(PrimaryKey.class.getPackage().getName() + ".PrimaryKey");
        List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
        for (IntrospectedColumn primaryKeyColumn : primaryKeyColumns) {
            String fieldName = field.getName();
            if (primaryKeyColumn.getJavaProperty().equals(fieldName)) {
                field.addAnnotation("@PrimaryKey");
            }
        }
        return super.modelFieldGenerated(field, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
    }

    /**
     * 生成实体
     */
    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        if (useLombok) {
            //向实体添加一个@Setter和@Getter注解,这在使用Lombok时可用,上面两个函数返回false,然后打开下列两行
            topLevelClass.addImportedType("lombok.Getter");
            topLevelClass.addImportedType("lombok.Setter");
            topLevelClass.addAnnotation("@Getter");
            topLevelClass.addAnnotation("@Setter");
        }
        if (useSwagger) {
            topLevelClass.addImportedType("io.swagger.annotations.ApiModel");
            topLevelClass.addAnnotation("@ApiModel(value = \"" + introspectedTable.getRemarks() + "\")");
        }

        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }


}
