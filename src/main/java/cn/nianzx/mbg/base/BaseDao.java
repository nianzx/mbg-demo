package cn.nianzx.mbg.base;

import java.util.Collection;
import java.util.List;

/**
 * DAO基类
 *
 * @param <T>
 * @param <PK>
 * @author nianzx
 * @date 2019/10/18
 */
public interface BaseDao<T, PK> {

    /**
     * 根据主键删除
     *
     * @param pk 主键（多个主键时PK为Map）
     * @return 影响条数
     */
    int deleteByPrimaryKey(PK pk);

    /**
     * 根据主键查询
     *
     * @param pk 主键（多个主键时PK为Map）
     * @return 返回实体
     */
    T selectByPrimaryKey(PK pk);

    /**
     * 查询所有
     *
     * @return 所有实体
     */
    List<T> selectAll();


    /**
     * 选择性插入
     *
     * @param record 要插入的实体
     * @return 影响条数
     */
    int insertSelective(T record);

    /**
     * 批量插入
     *
     * @param record 要插入的实体集合
     * @return 影响条数
     */
    int insertBatch(Collection<? extends T> record);

    /**
     * 根据主键批量删除
     *
     * @param record 主键集合（多个主键时PK为Map）
     * @return 影响条数
     */
    int deleteBatchByPrimaryKey(Collection<PK> record);

    /**
     * 选择性删除
     *
     * @param record 实体集合
     * @return 影响条数
     */
    int deleteBySelective(T record);

    /**
     * 根据主键选择性更新
     *
     * @param record 实体
     * @return 影响条数
     */
    int updateByPrimaryKeySelective(T record);

    /**
     * 根据主键批量选择性更新
     *
     * @param record 实体集合
     * @return 影响条数
     */
    int updateBatchByPrimaryKeySelective(Collection<? extends T> record);

    /**
     * 根据主键集合查询
     *
     * @param record 主键集合
     * @return 实体集合
     */
    List<T> getListByPrimaryKeys(Collection<PK> record);

    /**
     * 选择性查询
     *
     * @param record 实体
     * @return 实体集合
     */
    List<T> getList(T record);

    /**
     * 选择性查询合计数据
     *
     * @param record 实体
     * @return 共有多少条记录
     */
    int getCount(T record);

}