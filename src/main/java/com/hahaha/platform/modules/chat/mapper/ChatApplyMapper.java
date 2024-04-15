package com.hahaha.platform.modules.chat.mapper;

import com.hahaha.platform.common.web.mapper.BaseDao;
import com.hahaha.platform.modules.chat.domain.ChatApply;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 好友申请表 数据库访问层
 *
 * </p>
 */
@Repository
public interface ChatApplyMapper extends BaseDao<ChatApply> {

    /**
     * 查询列表
     */
    List<ChatApply> queryList(ChatApply chatApply);

}
