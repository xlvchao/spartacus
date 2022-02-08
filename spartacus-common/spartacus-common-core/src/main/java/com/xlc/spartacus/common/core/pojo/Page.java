package com.xlc.spartacus.common.core.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 只接受前4个必要的属性，其他4个属性由计算、转化得到
 *
 * @author xlc, since 2021
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Page<T> {
	// 由页面传参
	private int currentPage; // 当前页
	private int pageSize; // 页大小
	// 查询的结果
	private Object records; // 本页数据
	private int total; // 总记录数

	// 由计算、转化得到
	private int totalPages; // 总页数
	private int beginPageIndex; // 页码列表的开始索引（包含）
	private int endPageIndex; // 页码列表的结束索引（包含）
//	private List<Article> articles = new ArrayList<Article>();
	
	
	public Page(int currentPage, int pageSize, int total, List<Map<String, Object>> records) {
		this.currentPage = currentPage;
		this.pageSize = pageSize;
		this.records = records;
		this.total = total;

		// 转化ES结果
		/*for (Map<String, Object> map : records) {
			Article article = new Article();
			BeanUtils.map2Bean(map, article);
			articles.add(article);
		}*/

		// 计算总页码
		totalPages = (total + pageSize - 1) / pageSize;

		// 计算 beginPageIndex 和 endPageIndex
		// >> 总页数不多于10页，则全部显示
		if (totalPages <= 10) {
			beginPageIndex = 1;
			endPageIndex = totalPages;
		}
		// >> 总页数多于10页，则显示当前页附近的共10个页码
		else {
			// 当前页附近的共10个页码（前4个 + 当前页 + 后5个）
			beginPageIndex = currentPage - 4;
			endPageIndex = currentPage + 5;
			// 当前面的页码不足4个时，则显示前10个页码
			if (beginPageIndex < 1) {
				beginPageIndex = 1;
				endPageIndex = 10;
			}
			// 当后面的页码不足5个时，则显示后10个页码
			if (endPageIndex > totalPages) {
				endPageIndex = totalPages;
				beginPageIndex = totalPages - 10 + 1;
			}
		}
	}

}
