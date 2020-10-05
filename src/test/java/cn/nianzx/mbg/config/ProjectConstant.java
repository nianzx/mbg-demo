package cn.nianzx.mbg.config;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 常量配置
 */
public final class ProjectConstant {

    //JDBC配置，请修改为你项目的实际配置
    public static final String JDBC_URL = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useInformationSchema=true&nullCatalogMeansCurrent=true";
    public static final String JDBC_USERNAME = "root";
    public static final String JDBC_PASSWORD = "root";
    public static final String JDBC_DIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";

    //要生成的表名(全部生成放空) 支持多个表一起生成，用逗号隔开即可
    public static final String tableName = "t_test";
    //模块名(如果不为空，以.开头)
    public static final String MODULE_NAME = "";

    //基础包名
    private static final String BASE_PACKAGE = "cn.nianzx.mbg";
    //是否使用lombok
    public static final String USE_LOMBOK = "false";
    //是否使用swagger
    public static final String USE_SWAGGER = "false";

    //当前项目在硬盘上的基础路径(可以为空，为空时使用相对项目的路径，不为空时最后/结尾)
    public static final String PROJECT_PATH = "";


    //以下两个路径可以为物理绝对路径，适用于有子模块的情况下使用(例如JAVA_PATH = PROJECT_PATH + "/模块名" + "/src/main/java")
    //java文件路径
    public static final String JAVA_PATH = PROJECT_PATH + "src/main/java";
    //资源文件路径
    public static final String RESOURCES_PATH = PROJECT_PATH + "src/main/resources";
    //实体类生成包名[路径]（相对JAVA_PATH路径）
    public static final String ENTITY_PACKAGE = BASE_PACKAGE + ".domain" + MODULE_NAME;
    //DAO文件生成包名[路径]（相对JAVA_PATH路径）
    public static final String DAO_PACKAGE = BASE_PACKAGE + ".dao" + MODULE_NAME;
    //Service文件生成包名[路径]（相对JAVA_PATH路径）
    public static final String SERVICE_PACKAGE = BASE_PACKAGE + ".service" + MODULE_NAME;
    //xml文件生成包名[路径]（相对RESOURCES_PATH路径）
    public static final String XML_PACKAGE = "mapper" + MODULE_NAME;

    //作者
    public static final String AUTHOR = "nianzx";
    //模板位置
    public static final String TEMPLATE_FILE_PATH = PROJECT_PATH + "src/test/resources/generator/template";
    //当前时间
    public static final String DATE = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
}
