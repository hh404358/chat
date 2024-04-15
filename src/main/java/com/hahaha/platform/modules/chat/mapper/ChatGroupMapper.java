package com.hahaha.platform.modules.chat.mapper;

import com.hahaha.platform.common.web.mapper.BaseDao;
import com.hahaha.platform.modules.chat.domain.ChatGroup;
import com.hahaha.platform.modules.push.vo.PushParamVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 群组 数据库访问层
 *
 * </p>
 */
@Repository
public interface ChatGroupMapper extends BaseDao<ChatGroup> {

    /**
     * 查询列表
     */
    List<ChatGroup> queryList(ChatGroup chatGroup);

    /**
     * 查询用户
     */
    List<PushParamVo> queryFriendPushFrom(@Param("groupId") Long groupId, @Param("userId") Long userId);

}
