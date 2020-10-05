package cn.nianzx.mbg.config.elementgenerator;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;

import java.util.List;

public class UpdateBatchByPrimaryKeySelectiveElementGenerator extends AbstractXmlElementGenerator {
    @Override
    public void addElements(XmlElement xmlElement) {

        XmlElement answer = new XmlElement("update");
        answer.addAttribute(new Attribute("id", "updateBatchByPrimaryKeySelective"));
        answer.addAttribute(new Attribute("parameterType", "java.util.Collection"));

        context.getCommentGenerator().addComment(answer);

        XmlElement trim = new XmlElement("trim");
        trim.addAttribute(new Attribute("prefix", "set"));
        trim.addAttribute(new Attribute("suffixOverrides", ","));

        //表所有字段
        List<IntrospectedColumn> columns = ListUtilities.removeIdentityAndGeneratedAlwaysColumns(introspectedTable.getAllColumns());
        //表主键字段
        List<IntrospectedColumn> introspectedColumnList = introspectedTable.getPrimaryKeyColumns();

        StringBuilder sb = new StringBuilder();
        for (IntrospectedColumn column : columns) {
            XmlElement tmpTrim = new XmlElement("trim");

            tmpTrim.addAttribute(new Attribute("prefix", MyBatis3FormattingUtilities.getEscapedColumnName(column) + " = case "));
            tmpTrim.addAttribute(new Attribute("suffix", " else " + MyBatis3FormattingUtilities.getEscapedColumnName(column) + " end,"));
            //foreach
            XmlElement foreach = new XmlElement("foreach ");
            foreach.addAttribute(new Attribute("collection ", "collection"));
            foreach.addAttribute(new Attribute("item ", "item"));
            foreach.addAttribute(new Attribute("index ", "index"));

            XmlElement isNotNullElement = new XmlElement("if");
            sb.setLength(0);
            sb.append(column.getJavaProperty("item."));
            sb.append(" != null");
            isNotNullElement.addAttribute(new Attribute("test", sb.toString()));
            StringBuilder pkColumnContent = new StringBuilder();

            //拼接主键信息
            pkColumnContent.append("when ");
            for (int i = 0; i < introspectedColumnList.size(); i++) {
                IntrospectedColumn introspectedColumn = introspectedColumnList.get(i);
                pkColumnContent.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
                pkColumnContent.append(" = ");
                pkColumnContent.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, "item."));
                if (i != introspectedColumnList.size() - 1) {
                    pkColumnContent.append(" and ");
                }
            }
            pkColumnContent.append(" then ");

            isNotNullElement.addElement(new TextElement(pkColumnContent.toString() + MyBatis3FormattingUtilities.getParameterClause(column,"item.")));

            foreach.addElement(isNotNullElement);
            tmpTrim.addElement(foreach);
            trim.addElement(tmpTrim);
        }


        String updateClause = "update " + introspectedTable.getFullyQualifiedTableNameAtRuntime();
        answer.addElement(new TextElement(updateClause));
        answer.addElement(trim);

        XmlElement foreach = new XmlElement("foreach");
        foreach.addAttribute(new Attribute("collection", "collection"));
        foreach.addAttribute(new Attribute("separator", ","));
        foreach.addAttribute(new Attribute("open", "("));
        foreach.addAttribute(new Attribute("close", ")"));
        foreach.addAttribute(new Attribute("item", "item"));
        foreach.addAttribute(new Attribute("index", "index"));

        StringBuilder where1 = new StringBuilder();
        StringBuilder where2 = new StringBuilder();
        where1.append("where ");
        if (introspectedColumnList.size() > 1) {
            where1.append("(");
            where2.append("(");
        }

        for (int i = 0; i < introspectedColumnList.size(); i++) {
            IntrospectedColumn introspectedColumn = introspectedColumnList.get(i);

            where1.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
            where2.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, "item."));

            if (i != introspectedColumnList.size() - 1) {
                where1.append(",");
                where2.append(",");
            }
        }

        if (introspectedColumnList.size() > 1) {
            where1.append(")");
            where2.append(")");
        }
        where1.append(" in ");

        answer.addElement(new TextElement(where1.toString()));
        foreach.addElement(new TextElement(where2.toString()));

        answer.addElement(foreach);

        xmlElement.addElement(answer);
    }
}
