package com.hahaha.platform.modules.chat.mapper;

import com.hahaha.platform.common.web.mapper.BaseDao;
import com.hahaha.platform.modules.chat.domain.ChatUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 用户表 数据库访问层
 *
 * </p>
 */
@Mapper
public interface ChatUserMapper extends BaseDao<ChatUser> {

    /**
     * 查询列表
     */
    List<ChatUser> queryList(ChatUser chatUser);

}
