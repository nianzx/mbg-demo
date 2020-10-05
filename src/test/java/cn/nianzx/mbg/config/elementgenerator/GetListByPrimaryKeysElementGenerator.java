package cn.nianzx.mbg.config.elementgenerator;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;

import java.util.List;

public class GetListByPrimaryKeysElementGenerator extends AbstractXmlElementGenerator {
    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement select = new XmlElement("select");
        select.addAttribute(new Attribute("id", "getListByPrimaryKeys"));
        select.addAttribute(new Attribute("resultMap", "BaseResultMap"));
        select.addAttribute(new Attribute("parameterType", "java.util.Collection"));

        context.getCommentGenerator().addComment(select);

        select.addElement(new TextElement(" select <include refid=\"Base_Column_List\" /> "));
        select.addElement(new TextElement(" from " + introspectedTable.getFullyQualifiedTableNameAtRuntime()));

        List<IntrospectedColumn> primaryKeyColumnList = introspectedTable.getPrimaryKeyColumns();

        StringBuilder k = new StringBuilder();
        StringBuilder v = new StringBuilder();
        if (primaryKeyColumnList.size() > 1) {
            k.append("(");
            v.append("(");
        }
        for (int i = 0; i < primaryKeyColumnList.size(); i++) {
            IntrospectedColumn introspectedColumn = primaryKeyColumnList.get(i);
            k.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));

            if (primaryKeyColumnList.size() > 1) {
                v.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, "item."));
            }else{
                v.append("#{item}");
            }


            if (i != primaryKeyColumnList.size() - 1) {
                k.append(",");
                v.append(",");
            }
        }
        if (primaryKeyColumnList.size() > 1) {
            k.append(")");
            v.append(")");
        }

        TextElement where = new TextElement("where " + k.toString() + " in ");
        XmlElement foreach = new XmlElement("foreach");
        foreach.addAttribute(new Attribute("open", "("));
        foreach.addAttribute(new Attribute("close", ")"));

        foreach.addAttribute(new Attribute("collection", "collection"));
        foreach.addAttribute(new Attribute("item", "item"));
        foreach.addAttribute(new Attribute("index", "index"));
        foreach.addAttribute(new Attribute("separator", ","));
        foreach.addElement(new TextElement(v.toString()));

        select.addElement(where);
        select.addElement(foreach);

        parentElement.addElement(select);
    }
}
