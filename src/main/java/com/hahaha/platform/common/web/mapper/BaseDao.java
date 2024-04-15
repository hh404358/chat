package com.hahaha.platform.common.web.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * Mapper基类
 */
public interface BaseDao<T> extends BaseMapper<T> {

    /**
     * 批量插入 （仅适用于mysql）
     */
    Integer insertBatchSomeColumn(Collection<T> entityList);

    /**
     * 注意：使用此方法时，sql末尾需要增加${ew.customSqlSegment}
     * 支持单表、和联查
     */
    List<T> search(@Param("ew") Wrapper<T> queryWrapper);

}
