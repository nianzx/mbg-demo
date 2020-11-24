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
}