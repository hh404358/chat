package com.hahaha.platform.common.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageInfo;
import com.hahaha.platform.common.exception.BaseException;
import com.hahaha.platform.common.utils.BeanCopyUtils;
import com.hahaha.platform.common.web.mapper.BaseDao;
import com.hahaha.platform.common.web.service.BaseService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 基础service实现层基类
 */
public class BaseServiceImpl<T> implements BaseService<T> {

    protected final static String EXIST_MSG = "数据不存在";

    private BaseDao<T> baseMapper;

    public void setBaseDao(BaseDao<T> baseMapper) {
        this.baseMapper = baseMapper;
    }

    private static final Integer batchCount = 1000;

    @Override
    public Integer add(T entity) {
        return baseMapper.insert(entity);
    }

    @Override
    public Integer deleteById(Long id) {
        return baseMapper.deleteById(id);
    }

    @Override
    public Integer deleteByIds(Long[] ids) {
        return baseMapper.deleteBatchIds(Arrays.asList(ids));
    }

    @Override
    public Integer deleteByIds(List<Long> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    @Override
    public Integer updateById(T entity) {
        return baseMapper.updateById(entity);
    }

    @Override
    public T getById(Long id) {
        return baseMapper.selectById(id);
    }

    /**
     * eq: Wrapper wrapper = Wrappers.<ChatFriend>lambdaUpdate().set(ChatFriend::getRemark, null).eq(ChatFriend::getId, friend.getId());
     */
    @Override
    public Integer update(Wrapper wrapper) {
        return baseMapper.update(null, wrapper);
    }

    @Override
    public T findById(Long id) {
        T t = getById(id);
        if (t == null) {
            throw new BaseException(EXIST_MSG);
        }
        return t;
    }

    @Override
    public List<T> getByIds(Collection<? extends Serializable> idList) {
        return baseMapper.selectBatchIds(idList);
    }

    @Override
    public Long queryCount(T t) {
        Wrapper<T> wrapper = new QueryWrapper<>(t);
        return queryCount(wrapper);
    }

    @Override
    public Long queryCount(Wrapper<T> wrapper) {
        return baseMapper.selectCount(wrapper).longValue();
    }

    @Override
    public List<T> queryList(T t) {
        Wrapper<T> wrapper = new QueryWrapper<>(t);
        return queryList(wrapper);
    }

    @Override
    public List<T> queryList(Wrapper<T> wrapper) {
        return baseMapper.selectList(wrapper);
    }

    @Override
    public T queryOne(T t) {
        Wrapper<T> wrapper = new QueryWrapper<>(t);
        return queryOne(wrapper);
    }

    @Override
    public T queryOne(Wrapper<T> wrapper) {
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public Integer batchAdd(List<T> list) {
        return batchAdd(list, batchCount);
    }

    @Transactional
    @Override
    public Integer batchAdd(List<T> list, Integer batchCount) {
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }
        List<T> batchList = new ArrayList<>();
        AtomicReference<Integer> result = new AtomicReference<>(0);
        list.forEach((o) -> {
            batchList.add(o);
            if (batchList.size() == batchCount) {
                result.updateAndGet(v -> v + baseMapper.insertBatchSomeColumn(batchList));
                batchList.clear();
            }
        });
        if (!CollectionUtils.isEmpty(batchList)) {
            result.updateAndGet(v -> v + baseMapper.insertBatchSomeColumn(batchList));
            batchList.clear();
        }
        return result.get();
    }

    /**
     * 循环工具类
     */
    public static <T> Consumer<T> forEach(BiConsumer<T, Integer> consumer) {
        class Obj {
            int i;
        }
        Obj obj = new Obj();
        return t -> {
            int index = obj.i++;
            consumer.accept(t, index);
        };
    }

    @Override
    public void changeStatus(T t, String... param) {
        if (param.length == 0) {
            this.updateById(BeanCopyUtils.include(t, "id", "status"));
        } else {
            this.updateById(BeanCopyUtils.include(t, param));
        }
    }

    /**
     * 响应请求分页数据
     */
    protected PageInfo getPageInfo(List<?> list, List<?> oldList) {
        Long total = new PageInfo(oldList).getTotal();
        return getPageInfo(list, total);
    }

    /**
     * 格式化分页
     */
    public PageInfo getPageInfo(List<?> list, long total) {
        PageInfo pageInfo = new PageInfo(list);
        pageInfo.setTotal(total);
        return pageInfo;
    }

}
