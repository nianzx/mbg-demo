package ${servicePackage}.impl;

import ${baseDaoPackage};
import ${mapperExtPackage};
import ${entityPackage}.${EntityName};
import ${baseServicePackage}Impl;

import ${servicePackage}.${EntityName}Service;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.Map;

/**
 * 自动生成的service实现类
 *
 * @author ${author}
 * @date ${date}
 */
@Service
public class ${EntityName}ServiceImpl extends BaseServiceImpl<${EntityName}, ${PK}> implements ${EntityName}Service {

    @Resource
    private ${EntityName}MapperExt ${entityName}Mapper;

    @Override
    public BaseDao<${EntityName}, ${PK}> getBaseDao() {
        return this.${entityName}Mapper;
    }
}