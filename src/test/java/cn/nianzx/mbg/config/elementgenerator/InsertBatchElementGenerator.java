package cn.nianzx.mbg.config.elementgenerator;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;
import org.mybatis.generator.config.GeneratedKey;

import java.util.List;

/**
 * 批量插入
 */
public class InsertBatchElementGenerator extends AbstractXmlElementGenerator {

    @Override
    public void addElements(XmlElement parentElement) {
        //最外层的insert
        XmlElement insert = new XmlElement("insert");
        insert.addAttribute(new Attribute("id", "insertBatch"));
        insert.addAttribute(new Attribute("parameterType", "java.util.Collection"));

        context.getCommentGenerator().addComment(insert);

        GeneratedKey gk = introspectedTable.getGeneratedKey();
        if (gk != null) {
            introspectedTable.getColumn(gk.getColumn()).ifPresent(introspectedColumn -> {
                if (gk.isJdbcStandard()) {
                    insert.addAttribute(new Attribute("useGeneratedKeys", "true"));
                    insert.addAttribute(
                            new Attribute("keyProperty", introspectedColumn.getJavaProperty()));
                    insert.addAttribute(
                            new Attribute("keyColumn", introspectedColumn.getActualColumnName()));
                } else {
                    insert.addElement(getSelectKey(introspectedColumn, gk));
                }
            });
        }

        //sql语句 insert into
        StringBuilder insertClause = new StringBuilder();
        insertClause.append("insert into ");
        //表名
        insertClause.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());

        //（字段名，字段名）
        insertClause.append("(");
        List<IntrospectedColumn> columns = introspectedTable.getAllColumns();
        for (int i = 0; i < columns.size(); i++) {
            IntrospectedColumn introspectedColumn = columns.get(i);
            insertClause.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
            if (i + 1 < columns.size()) {
                insertClause.append(", ");
            }
        }
        insertClause.append(")");
        //把sql放入xml里
        insert.addElement(new TextElement(insertClause.toString()));
        insert.addElement(new TextElement("values "));

        //forEach
        XmlElement foreach = new XmlElement("foreach");
        foreach.addAttribute(new Attribute("collection", "collection"));
        foreach.addAttribute(new Attribute("item", "item"));
        foreach.addAttribute(new Attribute("index", "index"));
        foreach.addAttribute(new Attribute("separator", ","));

        //<trim prefix="(" suffix=")" suffixOverrides=","></trim>
        XmlElement trim = new XmlElement("trim");
        trim.addAttribute(new Attribute("prefix", "("));
        trim.addAttribute(new Attribute("suffix", ")"));
        trim.addAttribute(new Attribute("suffixOverrides", ","));

        //otherwise
        XmlElement otherwise = new XmlElement("otherwise");
        otherwise.addElement(new TextElement("default,"));

        //拼接参数值
        for (IntrospectedColumn introspectedColumn : columns) {
            //choose
            XmlElement choose = new XmlElement("choose");
            //when
            XmlElement when = new XmlElement("when");
            when.addAttribute(new Attribute("test", introspectedColumn.getJavaProperty("item.") + " != null"));
            when.addElement(new TextElement(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, "item.") + ","));

            choose.addElement(when);
            choose.addElement(otherwise);

            trim.addElement(choose);
        }

        foreach.addElement(trim);
        insert.addElement(foreach);
        parentElement.addElement(insert);
    }
}
