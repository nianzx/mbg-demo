package cn.nianzx.mbg.config.plugin;

import cn.nianzx.mbg.base.BaseDao;
import cn.nianzx.mbg.config.ProjectConstant;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.config.PropertyRegistry;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * 自定义DAO文件生成规则(Mapper.java)
 */
public class MapperPlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    /**
     * mapper.java继承BaseDao基类
     */
    @Override
    public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
        //清空所有默认接口方法
        interfaze.getMethods().clear();
        //清空所有的import
        interfaze.getImportedTypes().clear();

        //主键列表
        List<IntrospectedColumn> primaryKeyColumnList = introspectedTable.getPrimaryKeyColumns();
        String pk = primaryKeyColumnList.get(0).getFullyQualifiedJavaType().getFullyQualifiedName();
        //如果主键不止1个，PK传Map
        if (primaryKeyColumnList.size() > 1) {
            interfaze.addImportedType(new FullyQualifiedJavaType(Map.class.getName()));
            pk = "Map<String,Object>";
        }

        // 添加 extends BaseDao<User,Integer>
        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType("BaseDao<" + introspectedTable.getBaseRecordType() + "," + pk + ">");
        interfaze.addSuperInterface(fqjt);

        //BaseDao的路径import
        FullyQualifiedJavaType imp = new FullyQualifiedJavaType(BaseDao.class.getName());
        interfaze.addImportedType(imp);

        //实体类import
        FullyQualifiedJavaType recordType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        interfaze.addImportedType(recordType);

        //Mapper.java文件注解
        interfaze.addJavaDocLine("/**");
        interfaze.addJavaDocLine(" * table : " + introspectedTable.getFullyQualifiedTable());
        interfaze.addJavaDocLine(" * 此代码为MyBatis Generator自动生成,请勿修改");
        interfaze.addJavaDocLine(" * ");
        interfaze.addJavaDocLine(" * @author " + ProjectConstant.AUTHOR);
        interfaze.addJavaDocLine(" */");

        return super.clientGenerated(interfaze, introspectedTable);
    }


    /**
     * 生成自定义的mapper.java
     */
    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {

        String[] mapperTypes = introspectedTable.getMyBatis3JavaMapperType().split("\\.");

        StringBuilder fullTypeSpecification = new StringBuilder();
        for (int i = 0; i < mapperTypes.length; i++) {
            fullTypeSpecification.append(mapperTypes[i]);
            if (i != mapperTypes.length - 1) {
                fullTypeSpecification.append(".");
            }
            if (i == mapperTypes.length - 2) {
                fullTypeSpecification.append("extend.");
            }
        }

        FullyQualifiedJavaType type = new FullyQualifiedJavaType(fullTypeSpecification + "Ext");
        Interface interfaze = new Interface(type);

        interfaze.setVisibility(JavaVisibility.PUBLIC);
        context.getCommentGenerator().addJavaFileComment(interfaze);

        //继承extends
        FullyQualifiedJavaType baseInterfaze = new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType());
        interfaze.addSuperInterface(baseInterfaze);
        FullyQualifiedJavaType imp = new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType());
        interfaze.addImportedType(imp);

        //加上@Mapper注解
        FullyQualifiedJavaType annotation = new FullyQualifiedJavaType("org.apache.ibatis.annotations.Mapper");
        interfaze.addAnnotation("@Mapper");
        interfaze.addImportedType(annotation);

        //MapperExt.java文件注解
        interfaze.addJavaDocLine("/**");
        interfaze.addJavaDocLine(" * table : " + introspectedTable.getFullyQualifiedTable());
        interfaze.addJavaDocLine(" * 自定义sql请写在这里，该文件只会生成一次，多次生成不会覆盖");
        interfaze.addJavaDocLine(" * ");
        interfaze.addJavaDocLine(" * @author " + ProjectConstant.AUTHOR);
        interfaze.addJavaDocLine(" */");


        GeneratedJavaFile generatedJavaFile = new GeneratedJavaFile(
                interfaze,
                context.getJavaModelGeneratorConfiguration().getTargetProject(),
                context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
                context.getJavaFormatter());

        //避免多次生成
        if (isExistExtFile(generatedJavaFile.getTargetProject(), generatedJavaFile.getTargetPackage(), generatedJavaFile.getFileName())) {
            return super.contextGenerateAdditionalJavaFiles(introspectedTable);
        }

        List<GeneratedJavaFile> generatedJavaFiles = new ArrayList<>(2);
        generatedJavaFiles.add(generatedJavaFile);
        return generatedJavaFiles;
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
