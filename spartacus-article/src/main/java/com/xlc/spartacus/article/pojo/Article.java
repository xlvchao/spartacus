package com.xlc.spartacus.article.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import com.xlc.spartacus.common.core.pojo.BaseRequest;
import com.xlc.spartacus.common.core.utils.CommonUtils;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 实体
 *
 * @author xlc, since 2021
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class Article implements Serializable {
	private static final long serialVersionUID = 1L;

	@JsonSerialize(using= ToStringSerializer.class)
	private Long id;
	
	private String title;
	
	private String author;
	
	private String labels;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JSONField(format="yyyy-MM-dd HH:mm:ss") //fastJson序列化时的格式
	private Date publishTime;

	private Integer likeNumber = 0;

//	@Column(columnDefinition="INT default 0")
	private Integer commentNumber = 0;
	
//	@Column(columnDefinition="INT default 0")
	private Integer scanNumber = 0;
	
	private String cname;
	
	private String fromWhere; // 原创，摘抄，转载
	
//	@Column(columnDefinition="INT default 0")
	private Integer status = 0; // 0已发布，1存草稿，2已撤回，3已删除，4关于我
	
//	@Column(columnDefinition="INT default 0")
	private Integer isTop = 0; // 是否置顶，0不是，1是
	
//	@Column(columnDefinition="TEXT")
	private String brief;
	
	private String year;
	
	private String monthDay;

	private String pictures; // 插图
	
	private String content;
	
	
	public Article(Article article) {
		this.id = article.getId();
		this.title = article.getTitle();
		this.author = article.getAuthor();
		this.labels = article.getLabels();
		this.publishTime = article.getPublishTime();
		this.likeNumber = article.getLikeNumber();
		this.commentNumber = article.getCommentNumber();
		this.scanNumber = article.getScanNumber();
		this.cname = article.getCname();
		this.fromWhere = article.getFromWhere();
		this.status = article.getStatus();
		this.isTop = article.getIsTop();
		this.brief = article.getBrief();
		this.year = article.getYear();
		this.monthDay = article.getMonthDay();
		this.pictures = article.getPictures();
		this.content = "null";
	}
	
	
	public Article(BaseRequest req) {
		this.id = req.getId();
		this.title = req.getTitle();
		this.author = req.getAuthor();
		this.labels = req.getLabels();
		this.publishTime = req.getPublishTime();
		this.cname = req.getCname();
		this.fromWhere = req.getFromWhere();
		this.status = req.getStatus();
		this.content = req.getContent().trim();
		
		if(!CommonUtils.isNull(req.getContent())) {
			this.pictures = CommonUtils.getPictures(req.getContent().trim());
		}
		
		if(!CommonUtils.isNull(req.getBrief())) {
			this.brief = req.getBrief();
		} else if(!CommonUtils.isNull(req.getContent())) {
			this.brief = CommonUtils.getBrief(req.getContent().trim());
		}
		
		this.year = CommonUtils.getDateString("yyyy", req.getPublishTime());
		this.monthDay = CommonUtils.getDateString("MM.dd", req.getPublishTime());
	}
	
	
	public Article(Long id, String title, String author, String labels, Date publishTime, Integer likeNumber, Integer commentNumber,
			Integer scanNumber, String cname, String fromWhere, Integer status, Integer isTop, String brief,
			String year, String monthDay, String pictures) {
		this.id = id;
		this.title = title;
		this.author = author;
		this.labels = labels;
		this.publishTime = publishTime;
		this.likeNumber = likeNumber;
		this.commentNumber = commentNumber;
		this.scanNumber = scanNumber;
		this.cname = cname;
		this.fromWhere = fromWhere;
		this.status = status;
		this.isTop = isTop;
		this.brief = brief;
		this.year = year;
		this.monthDay = monthDay;
		this.pictures = pictures;
	}
	
}
