package cn.nianzx.mbg.base;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

/**
 * Service基类
 *
 * @author nianzx
 * @date 2019/09/06
 */
public abstract class BaseServiceImpl<T, PK> implements BaseService<T, PK> {

    /**
     * 用来为BaseServiceImpl指定mapper，实现方法重用
     *
     * @return 给BaseServiceImpl类调用的mapper，由继承者传入实现
     */
    public abstract BaseDao<T, PK> getBaseDao();

    @Override
    public int deleteByPrimaryKey(PK pk) {
        return getBaseDao().deleteByPrimaryKey(pk);
    }

    @Override
    public T selectByPrimaryKey(PK pk) {
        return getBaseDao().selectByPrimaryKey(pk);
    }

    @Override
    public List<T> selectAll() {
        return getBaseDao().selectAll();
    }

    @Override
    public int insertSelective(T record) {
        return getBaseDao().insertSelective(record);
    }

    @Override
    public int insertBatch(Collection<? extends T> record) {
        return getBaseDao().insertBatch(record);
    }

    @Override
    public int deleteBatchByPrimaryKey(Collection<PK> record) {
        return getBaseDao().deleteBatchByPrimaryKey(record);
    }

    @Override
    public int deleteBySelective(T record) {
        if (isAllFieldNull(record)) {
            //这边的异常由各系统自行实现异常，这边为了通用，使用RuntimeException
            throw new RuntimeException("传入实体为空，不允许执行deleteBySelective方法，会导致全表删除");
        }
        return getBaseDao().deleteBySelective(record);
    }

    @Override
    public int updateByPrimaryKeySelective(T record) {
        return getBaseDao().updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateBatchByPrimaryKeySelective(Collection<? extends T> record) {
        return getBaseDao().updateBatchByPrimaryKeySelective(record);
    }

    @Override
    public List<T> getListByPrimaryKeys(Collection<PK> record) {
        return getBaseDao().getListByPrimaryKeys(record);
    }

    @Override
    public List<T> getList(T record) {
        return getBaseDao().getList(record);
    }

    @Override
    public int getCount(T record) {
        return getBaseDao().getCount(record);
    }

    /**
     * 用来判断实体里面是否全部为空值
     *
     * @param obj 要判断的实体
     * @return 是否空值
     */
    public static boolean isAllFieldNull(Object obj) {
        // 得到类对象
        Class<?> clazz = obj.getClass();
        //得到属性集合
        Field[] fs = clazz.getDeclaredFields();
        boolean flag = true;
        //遍历属性
        for (Field f : fs) {
            // 设置属性是可以访问的(私有的也可以)
            f.setAccessible(true);
            //跳过serialVersionUID属性
            if ("serialVersionUID".equals(f.getName())) {
                continue;
            }
            // 得到此属性的值
            Object val;
            try {
                val = f.get(obj);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("得不到属性值，请检查");
            }
            //只要有1个属性不为空,那么就不是所有的属性值都为空
            if (val != null) {
                flag = false;
                break;
            }
        }
        return flag;
    }
}