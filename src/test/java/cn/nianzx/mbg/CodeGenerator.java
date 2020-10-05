package cn.nianzx.mbg;

import cn.nianzx.mbg.config.JavaTypeResolverDefault;
import cn.nianzx.mbg.config.ProjectConstant;
import cn.nianzx.mbg.config.comment.CustomCommentGenerator;
import cn.nianzx.mbg.config.plugin.EntityPlugin;
import cn.nianzx.mbg.config.plugin.MapperPlugin;
import cn.nianzx.mbg.config.plugin.ServicePlugin;
import cn.nianzx.mbg.config.plugin.XmlPlugin;
import com.google.common.base.CaseFormat;
import freemarker.template.TemplateExceptionHandler;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.*;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 代码生成器，根据数据表名称生成对应的Model、Mapper、Service、Controller简化开发。
 */
public class CodeGenerator {


    public static void main(String[] args) {
        //genCodeByCustomModelName("输入表名","输入自定义Model名称");
        if (StringUtils.isEmpty(ProjectConstant.tableName)) {
            for (String table : getAllTableNames()) {
                genCode(table);
            }
        } else {
            String[] tableNames = ProjectConstant.tableName.split(",");
            for (String tableName : tableNames) {
                genCodeByCustomModelName(tableName, null);
            }

        }
        System.out.println("生成成功！");
    }

    /**
     * 通过数据表名称生成代码，Model 名称通过解析数据表名称获得，下划线转大驼峰的形式。
     * 如输入表名称 "t_user_detail" 将生成 TUserDetail、TUserDetailMapper、TUserDetailService ...
     *
     * @param tableNames 数据表名称...
     */
    private static void genCode(String... tableNames) {
        for (String tableName : tableNames) {
            genCodeByCustomModelName(tableName, null);
        }
    }

    /**
     * 通过数据表名称，和自定义的 Model 名称生成代码
     * 如输入表名称 "t_user_detail" 和自定义的 Model 名称 "User" 将生成 User、UserMapper、UserService ...
     *
     * @param tableName 数据表名称
     * @param modelName 自定义的 Model 名称
     */
    private static void genCodeByCustomModelName(String tableName, String modelName) {
        genModelAndMapper(tableName, modelName);
        //genService(tableName, modelName);
    }


    /**
     * 自动生成model和mapper
     *
     * @param tableName 数据表名称
     * @param modelName 自定义的 Model 名称
     */
    private static void genModelAndMapper(String tableName, String modelName) {
        Context context = new Context(ModelType.FLAT);
        context.setId("Potato");
        context.setTargetRuntime("MyBatis3Simple");

        //mysql转义字符
        context.addProperty(PropertyRegistry.CONTEXT_BEGINNING_DELIMITER, "`");
        context.addProperty(PropertyRegistry.CONTEXT_ENDING_DELIMITER, "`");

        //数据库连接
        JDBCConnectionConfiguration jdbcConnectionConfiguration = new JDBCConnectionConfiguration();
        jdbcConnectionConfiguration.setConnectionURL(ProjectConstant.JDBC_URL);
        jdbcConnectionConfiguration.setUserId(ProjectConstant.JDBC_USERNAME);
        jdbcConnectionConfiguration.setPassword(ProjectConstant.JDBC_PASSWORD);
        jdbcConnectionConfiguration.setDriverClass(ProjectConstant.JDBC_DIVER_CLASS_NAME);
        context.setJdbcConnectionConfiguration(jdbcConnectionConfiguration);


        //生成实体类
        JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();
        //实际路径
        javaModelGeneratorConfiguration.setTargetProject(ProjectConstant.JAVA_PATH);
        //包名（相对路径）
        javaModelGeneratorConfiguration.setTargetPackage(ProjectConstant.ENTITY_PACKAGE);
        context.setJavaModelGeneratorConfiguration(javaModelGeneratorConfiguration);

        //自定义实体类生成规则(是否使用lombok和swagger)
        PluginConfiguration entityPlugin = new PluginConfiguration();
        entityPlugin.setConfigurationType(EntityPlugin.class.getName());
        entityPlugin.addProperty("useLombok", ProjectConstant.USE_LOMBOK);
        entityPlugin.addProperty("useSwagger", ProjectConstant.USE_SWAGGER);
        context.addPluginConfiguration(entityPlugin);


        //生成xml文件
        SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = new SqlMapGeneratorConfiguration();
        //实际路径
        sqlMapGeneratorConfiguration.setTargetProject(ProjectConstant.RESOURCES_PATH);
        //包名（相对路径）
        sqlMapGeneratorConfiguration.setTargetPackage(ProjectConstant.XML_PACKAGE);
        context.setSqlMapGeneratorConfiguration(sqlMapGeneratorConfiguration);

        //自定义xml文件生成规则(自定义sql)
        PluginConfiguration xmlPlugin = new PluginConfiguration();
        xmlPlugin.setConfigurationType(XmlPlugin.class.getName());
        context.addPluginConfiguration(xmlPlugin);


        //DAO文件
        JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = new JavaClientGeneratorConfiguration();
        //实际路径
        javaClientGeneratorConfiguration.setTargetProject(ProjectConstant.JAVA_PATH);
        //包名（相对路径）
        javaClientGeneratorConfiguration.setTargetPackage(ProjectConstant.DAO_PACKAGE);
        //固定值
        javaClientGeneratorConfiguration.setConfigurationType("XMLMAPPER");
        context.setJavaClientGeneratorConfiguration(javaClientGeneratorConfiguration);

        //自定义DAO文件生成规则
        PluginConfiguration mapperPlugin = new PluginConfiguration();
        mapperPlugin.setConfigurationType(MapperPlugin.class.getName());
        context.addPluginConfiguration(mapperPlugin);

        //自定义Service文件生成
        PluginConfiguration servicePlugin = new PluginConfiguration();
        servicePlugin.setConfigurationType(ServicePlugin.class.getName());
        context.addPluginConfiguration(servicePlugin);

        //指定要生成的表
        TableConfiguration tableConfiguration = new TableConfiguration(context);
        tableConfiguration.setTableName(tableName);
        //设置生成的类别名，默认是下划线转驼峰
        if (!StringUtils.isEmpty(modelName)) tableConfiguration.setDomainObjectName(modelName);
        //为xml的插入增加keyColumn、keyProperty、useGeneratedKeys="true"属性，为了插入后返回自增id
        GeneratedKey gk = new GeneratedKey(getAutoIncrementPrimaryKeyName(tableName), "JDBC", true, null);
        tableConfiguration.setGeneratedKey(gk);
        context.addTableConfiguration(tableConfiguration);

        //自定义注释
        CommentGeneratorConfiguration comment = new CommentGeneratorConfiguration();
        comment.setConfigurationType(CustomCommentGenerator.class.getName());
        context.setCommentGeneratorConfiguration(comment);

        // 自定义类型转换 JavaTypeResolverDefault
        JavaTypeResolverConfiguration javaTypeResolverConfiguration = new JavaTypeResolverConfiguration();
        javaTypeResolverConfiguration.setConfigurationType(JavaTypeResolverDefault.class.getName());
        context.setJavaTypeResolverConfiguration(javaTypeResolverConfiguration);

        List<String> warnings;
        MyBatisGenerator generator;
        try {
            Configuration config = new Configuration();
            config.addContext(context);
            config.validate();

            DefaultShellCallback callback = new DefaultShellCallback(true);
            warnings = new ArrayList<>();
            generator = new MyBatisGenerator(config, callback, warnings);
            generator.generate(null);
        } catch (Exception e) {
            throw new RuntimeException("生成Model和Mapper失败", e);
        }
    }


    /**
     * 获取表中自动递增的主键名称，用来生成xml时，插入成功后返回自动递增的主键
     *
     * @param table 表名
     * @return 自动递增的主键名称
     */
    private static String getAutoIncrementPrimaryKeyName(String table) {
        try {
            Class.forName(ProjectConstant.JDBC_DIVER_CLASS_NAME).newInstance();
            Connection conn = DriverManager.getConnection(ProjectConstant.JDBC_URL, ProjectConstant.JDBC_USERNAME, ProjectConstant.JDBC_PASSWORD);
            PreparedStatement ps = conn.prepareStatement("desc " + table);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                //列名称
                String columnName = rs.getString("Field");
                //是否主键标志
                String pri = rs.getString("Key");
                //附加的，用来判断是否自动递增
                String extra = rs.getString("Extra");
                //是自动递增的主键
                if ("PRI".equals(pri) && "auto_increment".equals(extra)) {
                    return columnName;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 根据模板生成各个表的service
     */
    private static void genService(String tableName, String modelName) {
        try {
            freemarker.template.Configuration cfg = getConfiguration();

            Map<String, Object> data = new HashMap<>();
            data.put("date", ProjectConstant.DATE);
            data.put("author", ProjectConstant.AUTHOR);
            //实体类名（生成的Service名字为实体Service，例如User的实体生成UserService）
            String entityName = StringUtils.isEmpty(modelName) ? tableNameConvertUpperCamel(tableName) : modelName;
            //实体包名
            data.put("entityPackage", ProjectConstant.ENTITY_PACKAGE);
            data.put("entityName", entityName);

            data.put("servicePackage", ProjectConstant.SERVICE_PACKAGE);
            //Service路径（绝对路径）
            File file = new File(
                    ProjectConstant.PROJECT_PATH +
                            ProjectConstant.JAVA_PATH +
                            "/" +
                            ProjectConstant.SERVICE_PACKAGE.replace(".", "/") +
                            "/" +
                            entityName + "Service.java");
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                cfg.getTemplate("service.ftl").process(data, new FileWriter(file));
            }
            //生成ServiceImpl路径（绝对路径）
            File file1 = new File(
                    ProjectConstant.PROJECT_PATH +
                            ProjectConstant.JAVA_PATH +
                            "/" +
                            ProjectConstant.SERVICE_PACKAGE.replace(".", "/") +
                            "/impl/" +
                            entityName + "ServiceImpl.java");
            if (!file1.getParentFile().exists()) {
                file1.getParentFile().mkdirs();
            }
            if (!file1.exists()) {
                cfg.getTemplate("service-impl.ftl").process(data, new FileWriter(file1));
            }
        } catch (Exception e) {
            throw new RuntimeException("生成Service失败", e);
        }
    }

    /**
     * 获取freemarker模板配置
     *
     * @return freemarker模板配置
     * @throws IOException io异常
     */
    private static freemarker.template.Configuration getConfiguration() throws IOException {
        freemarker.template.Configuration cfg = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_30);
        cfg.setDirectoryForTemplateLoading(new File(ProjectConstant.TEMPLATE_FILE_PATH));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
        return cfg;
    }

    private static String tableNameConvertUpperCamel(String tableName) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, tableName.toLowerCase());
    }

    /**
     * 通过show tables来获取数据库中的表名称
     *
     * @return 表名称list
     */
    private static List<String> getAllTableNames() {
        List<String> list = new ArrayList<>();
        try {
            Class.forName(ProjectConstant.JDBC_DIVER_CLASS_NAME).newInstance();
            Connection conn = DriverManager.getConnection(ProjectConstant.JDBC_URL, ProjectConstant.JDBC_USERNAME, ProjectConstant.JDBC_PASSWORD);
            PreparedStatement ps = conn.prepareStatement("show tables");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String columnName = rs.getString(1);
                list.add(columnName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
