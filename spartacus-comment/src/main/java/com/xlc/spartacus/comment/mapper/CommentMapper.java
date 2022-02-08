package com.xlc.spartacus.comment.mapper;

import com.xlc.spartacus.comment.pojo.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * mapper
 *
 * @author xlc, since 2021
 */
@Mapper
public interface CommentMapper {

    Integer save(Comment comment);

    List<Comment> findByStatus(Integer status);

    List<Comment> findByCondition(Comment condition);

    List<Comment> findSubByCondition(Comment condition);

    Comment findById(Long id);

    Integer getStatusById(Long id);

    Integer setRearId(@Param("id") Long id, @Param("rearId")Long rearId);

    List<Comment> getRecentComments();

    Integer countByRefIdAndStatus(@Param("refId")Long refId, @Param("status")Integer status);

    Integer updateStatusById(@Param("id")Long id, @Param("status")Integer status);

    Integer updateStatusByRefId(@Param("refId")Long refId, @Param("status")Integer status);

    Integer deleteByFrontId(Long frontId);

    List<Long> getLinkedCommentIdListById(Long id);

    Integer modifyCommentNumber(@Param("id")Long id, @Param("increaseNumber")Integer increaseNumber);
}