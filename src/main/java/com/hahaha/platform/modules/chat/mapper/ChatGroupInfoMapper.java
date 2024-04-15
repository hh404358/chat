package com.hahaha.platform.modules.chat.mapper;

import com.hahaha.platform.common.web.mapper.BaseDao;
import com.hahaha.platform.modules.chat.domain.ChatGroupInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 数据库访问层
 *
 * </p>
 */
@Repository
public interface ChatGroupInfoMapper extends BaseDao<ChatGroupInfo> {

    /**
     * 查询列表
     */
    List<ChatGroupInfo> queryList(ChatGroupInfo chatGroupInfo);

}
