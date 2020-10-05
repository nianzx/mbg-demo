package cn.nianzx.mbg.config.plugin;

import cn.nianzx.mbg.base.BaseDao;
import cn.nianzx.mbg.base.BaseService;
import cn.nianzx.mbg.config.ProjectConstant;
import com.google.common.base.CaseFormat;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.config.TableConfiguration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServicePlugin extends PluginAdapter {

    //要创建的service名字（不带service后缀）
    String createTableService;

    @Override
    public boolean validate(List<String> warnings) {
        //所有的表表名
        List<TableConfiguration> tableList = context.getTableConfigurations();
        TableConfiguration tableConfiguration = tableList.get(0);
        //类别名
        String EntityName = tableConfiguration.getDomainObjectName();
        if (EntityName == null || EntityName.trim().length() == 0) {
            //没有填别名的情况下，从表名获取，转换成类名
            EntityName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, tableConfiguration.getTableName().toLowerCase());
        }

        //不存在才去生成
        File file = getServiceFile(EntityName, 0);
        if (!file.exists()) {
            createTableService = EntityName;
            return true;
        }
        return false;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        try {
            Configuration cfg = getConfiguration();

            Map<String, Object> data = new HashMap<>();
            data.put("date", ProjectConstant.DATE);
            data.put("author", ProjectConstant.AUTHOR);
            //实体包名
            data.put("entityPackage", ProjectConstant.ENTITY_PACKAGE);
            //service包名
            data.put("servicePackage", ProjectConstant.SERVICE_PACKAGE);
            data.put("baseServicePackage", BaseService.class.getPackage().getName() + ".BaseService");

            //生成service
            List<IntrospectedColumn> primaryKeyColumnList = introspectedTable.getPrimaryKeyColumns();
            String pk = primaryKeyColumnList.get(0).getFullyQualifiedJavaType().getShortName();
            //如果主键不止1个，PK传Map
            if (primaryKeyColumnList.size() > 1) {
                pk = "Map<String, Object>";
            }
            data.put("EntityName", createTableService);
            data.put("PK", pk);
            File file = getServiceFile(createTableService, 0);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                cfg.getTemplate("service.ftl").process(data, new FileWriter(file));
            }

            data.put("baseDaoPackage", BaseDao.class.getPackage().getName() + ".BaseDao");
            data.put("entityName", Character.toLowerCase(createTableService.charAt(0)) + createTableService.substring(1));

            String mapperType = introspectedTable.getMyBatis3JavaMapperType();
            String mapperName = mapperType.substring(mapperType.lastIndexOf("."));
            String mapperPackage = mapperType.substring(0, mapperType.lastIndexOf("."));
            data.put("mapperExtPackage", mapperPackage + ".extend" + mapperName + "Ext");

            //生成serviceImpl
            File implFile = getServiceFile(createTableService, 1);
            if (!implFile.getParentFile().exists()) {
                implFile.getParentFile().mkdirs();
            }
            if (!implFile.exists()) {
                cfg.getTemplate("service-impl.ftl").process(data, new FileWriter(implFile));
            }


        } catch (Exception e) {
            throw new RuntimeException("生成Service失败", e);
        }


        return super.contextGenerateAdditionalJavaFiles(introspectedTable);
    }


    /**
     * 返回service文件的file
     *
     * @param EntityName service文件名（不带service.java或serviceImpl.java的名字）
     * @param flag       0 service 1 impl
     * @return file
     */
    private File getServiceFile(String EntityName, int flag) {
        StringBuilder filePath = new StringBuilder();
        filePath.append(ProjectConstant.JAVA_PATH).append("/").append(ProjectConstant.SERVICE_PACKAGE.replace(".", "/")).append("/");
        if (flag == 0) {
            filePath.append(EntityName).append("Service.java");
        } else {
            filePath.append("impl/").append(EntityName).append("ServiceImpl.java");
        }
        return new File(filePath.toString());
    }


    /**
     * 获取freemarker模板配置
     *
     * @return freemarker模板配置
     * @throws IOException io异常
     */
    private static Configuration getConfiguration() throws IOException {
        Configuration cfg = new Configuration(freemarker.template.Configuration.VERSION_2_3_30);
        cfg.setDirectoryForTemplateLoading(new File(ProjectConstant.TEMPLATE_FILE_PATH));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
        return cfg;
    }
}
