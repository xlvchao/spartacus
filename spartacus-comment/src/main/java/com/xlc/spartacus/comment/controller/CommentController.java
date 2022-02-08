package com.xlc.spartacus.comment.controller;

import com.xlc.spartacus.comment.exception.GlobalException;
import com.xlc.spartacus.comment.pojo.Comment;
import com.xlc.spartacus.comment.pojo.CommentForbid;
import com.xlc.spartacus.comment.service.CommentService;
import com.xlc.spartacus.common.core.pojo.BaseRequest;
import com.xlc.spartacus.common.core.pojo.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 接口
 *
 * @author xlc, since 2021
 */
@RestController
@RequestMapping("/comment")
public class CommentController {
		
	@Autowired
	private CommentService commentService;
	

	@RequestMapping("/findByStatus")
	public BaseResponse findByStatus(@ModelAttribute BaseRequest baseRequest) throws GlobalException {
		return commentService.findByStatus(baseRequest.getStatus(), baseRequest.getCurrentPage(), baseRequest.getPageSize());
	}

	@RequestMapping("/findByArticleId")
	public BaseResponse findByArticleId(@ModelAttribute BaseRequest baseRequest) throws GlobalException {
		return commentService.findByArticleIdAndStatus(baseRequest.getArticleId(), 1, baseRequest.getCurrentPage(), baseRequest.getPageSize());
	}
	
	@RequestMapping("/findByArticleIdAndStatus")
	public BaseResponse findByArticleIdAndStatus(@ModelAttribute BaseRequest baseRequest) throws GlobalException {
		return commentService.findByArticleIdAndStatus(baseRequest.getArticleId(), baseRequest.getStatus(), baseRequest.getCurrentPage(), baseRequest.getPageSize());
	}
	
	@RequestMapping(value = "/submitComment", method = RequestMethod.POST)
	public BaseResponse submitComment(@ModelAttribute Comment comment, HttpServletRequest request) throws GlobalException {
		return commentService.submitComment(comment, request);
	}

	@RequestMapping(value = "/adminSubmitComment", method = RequestMethod.POST)
	public BaseResponse adminSubmitComment(@ModelAttribute Comment comment, HttpServletRequest request) throws GlobalException {
		return commentService.adminSubmitComment(comment, request);
	}
	
	@RequestMapping(value = "/adminReplyComment", method = RequestMethod.POST)
	public BaseResponse adminReplyComment(@ModelAttribute Comment comment, HttpServletRequest request) throws GlobalException {
		return commentService.adminReplyComment(comment, request);
	}
	
	@RequestMapping("/getRecentComments")
	public BaseResponse getRecentComments() throws GlobalException {
	    return commentService.getRecentComments();
	}
	
	@RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    public BaseResponse updateStatus(Long articleId, Long commentId, Long frontId, Integer level, Integer oldStatus, Integer newStatus) throws GlobalException {
        return commentService.updateStatus(articleId, commentId, frontId, level, oldStatus, newStatus);
    }
	
	@RequestMapping(value = "/batchUpdateStatus", method = RequestMethod.POST)
    public BaseResponse batchUpdateStatus(String jsonParams) throws GlobalException {
        return commentService.batchUpdateStatus(jsonParams);
    }
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
    public BaseResponse delete(Long articleId, Long commentId, Integer status, Integer level) throws GlobalException {
        return commentService.delete(articleId, commentId, status, level);
    }
	
	@RequestMapping(value = "/batchDelete", method = RequestMethod.POST)
    public BaseResponse batchDelete(String jsonParams) throws GlobalException {
        return commentService.batchDelete(jsonParams);
    }
	
	@RequestMapping(value = "/forbid", method = RequestMethod.POST)
	public BaseResponse forbid(@ModelAttribute CommentForbid forbid) throws GlobalException {
		return commentService.forbid(forbid);
	}
	
	@RequestMapping(value = "/unForbid", method = RequestMethod.POST)
	public BaseResponse unForbid(@ModelAttribute CommentForbid forbid) throws GlobalException {
		return commentService.unForbid(forbid);
	}

	@RequestMapping(value = "/batchUnForbid", method = RequestMethod.POST)
	public BaseResponse batchUnForbid(String jsonParams) throws GlobalException {
		return commentService.batchUnForbid(jsonParams);
	}
	
	@RequestMapping(value = "/getForbids", method = RequestMethod.POST)
	public BaseResponse getForbids(Integer forbidType, Integer currentPage, Integer pageSize) throws GlobalException {
		return commentService.getForbids(forbidType, currentPage, pageSize);
	}

}
