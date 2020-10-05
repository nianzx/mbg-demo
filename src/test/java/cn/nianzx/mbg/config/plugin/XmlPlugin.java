package cn.nianzx.mbg.config.plugin;

import cn.nianzx.mbg.config.elementgenerator.*;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.XmlConstants;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.BaseColumnListElementGenerator;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.InsertSelectiveElementGenerator;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.UpdateByPrimaryKeySelectiveElementGenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * 自定义xml文件生成规则(自定义sql语句)
 */
public class XmlPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
    }

    @Override
    public boolean sqlMapGenerated(GeneratedXmlFile sqlMap, IntrospectedTable introspectedTable) {
        //多次生成时,xml覆盖原来的文件
        sqlMap.setMergeable(false);
        return super.sqlMapGenerated(sqlMap, introspectedTable);
    }

    /**
     * 自定义sql生成
     */
    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        XmlElement parentElement = document.getRootElement();

        //Base_Column_List
        BaseColumnListElementGenerator baseColumnList = new BaseColumnListElementGenerator();
        baseColumnList.setContext(context);
        baseColumnList.setIntrospectedTable(introspectedTable);
        baseColumnList.addElements(parentElement);

        /* insertSelective方法 */
        InsertSelectiveElementGenerator insertSelect = new InsertSelectiveElementGenerator();
        insertSelect.setContext(context);
        insertSelect.setIntrospectedTable(introspectedTable);
        insertSelect.addElements(parentElement);

        /* 批量插入 insertBatch 方法*/
        InsertBatchElementGenerator insertBatch = new InsertBatchElementGenerator();
        insertBatch.setContext(context);
        insertBatch.setIntrospectedTable(introspectedTable);
        insertBatch.addElements(parentElement);

        /* 根据主键批量删除 deleteBatchByPrimaryKey 方法*/
        DeleteBatchByPrimaryKeyElementGenerator deleteBatchByPrimaryKey = new DeleteBatchByPrimaryKeyElementGenerator();
        deleteBatchByPrimaryKey.setContext(context);
        deleteBatchByPrimaryKey.setIntrospectedTable(introspectedTable);
        deleteBatchByPrimaryKey.addElements(parentElement);

        /* 根据条件删除 deleteBySelective 方法 */
        DeleteSelectiveElementGenerator deleteBySelective = new DeleteSelectiveElementGenerator();
        deleteBySelective.setContext(context);
        deleteBySelective.setIntrospectedTable(introspectedTable);
        deleteBySelective.addElements(parentElement);

        /* updateByPrimaryKeySelective 方法*/
        UpdateByPrimaryKeySelectiveElementGenerator updateByPrimaryKey = new UpdateByPrimaryKeySelectiveElementGenerator();
        updateByPrimaryKey.setContext(context);
        updateByPrimaryKey.setIntrospectedTable(introspectedTable);
        updateByPrimaryKey.addElements(parentElement);

        /* updateBatchByPrimaryKeySelective 方法*/
        UpdateBatchByPrimaryKeySelectiveElementGenerator updateBatchByPrimaryKey = new UpdateBatchByPrimaryKeySelectiveElementGenerator();
        updateBatchByPrimaryKey.setContext(context);
        updateBatchByPrimaryKey.setIntrospectedTable(introspectedTable);
        updateBatchByPrimaryKey.addElements(parentElement);

        /* getListByPrimaryKeys */
        GetListByPrimaryKeysElementGenerator getListByPrimaryKeys = new GetListByPrimaryKeysElementGenerator();
        getListByPrimaryKeys.setContext(context);
        getListByPrimaryKeys.setIntrospectedTable(introspectedTable);
        getListByPrimaryKeys.addElements(parentElement);

        /* getList */
        GetListElementGenerator getList = new GetListElementGenerator();
        getList.setContext(context);
        getList.setIntrospectedTable(introspectedTable);
        getList.addElements(parentElement);

        /* getCount */
        GetCountElementGenerator getCount = new GetCountElementGenerator();
        getCount.setContext(context);
        getCount.setIntrospectedTable(introspectedTable);
        getCount.addElements(parentElement);

        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    /**
     * 去掉默认的insert方法（使用insertSelective代替）
     */
    @Override
    public boolean sqlMapInsertElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    /**
     * 去掉默认的updateByPrimaryKey方法（用updateByPrimaryKeySelective代替）
     */
    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    /**
     * 生成自定义的mapper.xml
     */
    @Override
    public List<GeneratedXmlFile> contextGenerateAdditionalXmlFiles(IntrospectedTable introspectedTable) {
        //表映射出来的mapper.xml名称
        String[] splitFile = introspectedTable.getMyBatis3XmlMapperFileName().split("\\.");
        String fileNameExt = null;
        if (splitFile[0] != null) {
            fileNameExt = splitFile[0] + "Ext.xml";
        }
        //已存在不重新生成
        if (isExistExtFile(context.getSqlMapGeneratorConfiguration().getTargetProject(), introspectedTable.getMyBatis3XmlMapperPackage() + ".extend", fileNameExt)) {
            return super.contextGenerateAdditionalXmlFiles(introspectedTable);
        }

        Document document = new Document(XmlConstants.MYBATIS3_MAPPER_PUBLIC_ID, XmlConstants.MYBATIS3_MAPPER_SYSTEM_ID);

        XmlElement root = new XmlElement("mapper");

        String[] namespaces = introspectedTable.getMyBatis3SqlMapNamespace().split("\\.") ;

        StringBuilder namespace = new StringBuilder();
        for (int i = 0; i < namespaces.length; i++) {
            namespace.append(namespaces[i]);
            if (i != namespaces.length - 1) {
                namespace.append(".");
            }
            if (i == namespaces.length - 2) {
                namespace.append("extend.");
            }
        }

        root.addAttribute(new Attribute("namespace", namespace+ "Ext"));
        //避免闭合直接在后面用/>,加上空行
        root.addElement(new TextElement(""));
        document.setRootElement(root);

        GeneratedXmlFile gxf = new GeneratedXmlFile(document, fileNameExt,
                introspectedTable.getMyBatis3XmlMapperPackage() + ".extend",
                context.getSqlMapGeneratorConfiguration().getTargetProject(), false, context.getXmlFormatter());

        List<GeneratedXmlFile> answer = new ArrayList<>(2);
        answer.add(gxf);
        return answer;
    }

    /**
     * 用来判断文件是否存在
     *
     * @param targetProject 文件来源
     * @param targetPackage 文件包名
     * @param fileName      文件名
     * @return 存在与否
     */
    private boolean isExistExtFile(String targetProject, String targetPackage, String fileName) {
        File project = new File(targetProject);
        if (!project.isDirectory()) {
            return true;
        }
        StringBuilder sb = new StringBuilder();
        StringTokenizer st = new StringTokenizer(targetPackage, ".");
        while (st.hasMoreTokens()) {
            sb.append(st.nextToken());
            sb.append(File.separatorChar);
        }

        File directory = new File(project, sb.toString());
        if (!directory.isDirectory()) {
            boolean rc = directory.mkdirs();
            if (!rc) {
                return true;
            }
        }

        File testFile = new File(directory, fileName);
        return testFile.exists();
    }
}
