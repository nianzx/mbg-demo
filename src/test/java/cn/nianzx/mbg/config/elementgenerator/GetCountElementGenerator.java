package cn.nianzx.mbg.config.elementgenerator;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;

public class GetCountElementGenerator extends AbstractXmlElementGenerator {
    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement count = new XmlElement("select");
        count.addAttribute(new Attribute("id", "getCount"));
        count.addAttribute(new Attribute("resultType", "java.lang.Integer"));
        count.addAttribute(new Attribute("parameterType", introspectedTable.getBaseRecordType()));

        context.getCommentGenerator().addComment(count);

        count.addElement(new TextElement("select count(1)"));
        count.addElement(new TextElement("from " + introspectedTable.getFullyQualifiedTableNameAtRuntime()));

        XmlElement where = new XmlElement("where");
        StringBuilder sb = new StringBuilder();

        //<if test=" != null">
        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            XmlElement isNotNullElement = new XmlElement("if");
            sb.setLength(0);
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(" != null");
            isNotNullElement.addAttribute(new Attribute("test", sb.toString()));
            where.addElement(isNotNullElement);

            sb.setLength(0);
            sb.append(" and ");
            sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
            sb.append(" = ");
            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
            isNotNullElement.addElement(new TextElement(sb.toString()));
        }

        count.addElement(where);

        parentElement.addElement(count);
    }
}
