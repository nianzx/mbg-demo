package cn.nianzx.mbg.config.elementgenerator;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;

import java.util.List;

/**
 * 根据主键批量删除sql生成
 */
public class DeleteBatchByPrimaryKeyElementGenerator extends AbstractXmlElementGenerator {

    @Override
    public void addElements(XmlElement parentElement) {
        //表名
        String tableName = introspectedTable.getFullyQualifiedTableNameAtRuntime();
        //主键名
        StringBuilder key;
        List<IntrospectedColumn> introspectedColumnList = introspectedTable.getPrimaryKeyColumns();
        if (introspectedColumnList.size() > 1) {
            key = new StringBuilder("(");
            for (int i = 0; i < introspectedColumnList.size(); i++) {
                key.append(introspectedColumnList.get(i).getActualColumnName());
                if (i != introspectedColumnList.size() - 1) {
                    key.append(",");
                }
            }
            key.append(")");
        } else {
            key = new StringBuilder(introspectedColumnList.get(0).getActualColumnName());
        }

        String baseSql = String.format("delete from %s where %s in ", tableName, key.toString());

        XmlElement deleteElement = new XmlElement("delete");
        deleteElement.addAttribute(new Attribute("id", "deleteBatchByPrimaryKey"));
        deleteElement.addAttribute(new Attribute("parameterType", "java.util.Collection"));

        context.getCommentGenerator().addComment(deleteElement);

        XmlElement foreach = new XmlElement("foreach");
        foreach.addAttribute(new Attribute("open", "("));
        foreach.addAttribute(new Attribute("close", ")"));
        foreach.addAttribute(new Attribute("collection", "collection"));
        foreach.addAttribute(new Attribute("item", "item"));
        foreach.addAttribute(new Attribute("index", "index"));
        foreach.addAttribute(new Attribute("separator", ","));

        if (introspectedColumnList.size() > 1) {
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            for (int i = 0; i < introspectedColumnList.size(); i++) {
                sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumnList.get(i), "item."));
                if (i != introspectedColumnList.size() - 1) {
                    sb.append(",");
                }
            }
            sb.append(")");
            foreach.addElement(new TextElement(sb.toString()));
        } else {
            foreach.addElement(new TextElement("#{item}"));
        }

        deleteElement.addElement(new TextElement(baseSql));
        deleteElement.addElement(foreach);
        parentElement.addElement(deleteElement);
    }
}
