package com.xlc.spartacus.monitor.pojo;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 分时段分析的结果的实体类型
 *
 * @author xlc, since 2021
 */
@Data
public class Result {
	Integer count = 0; //该时段内次数
	Float avgInterval = 0.0f; //平均间隔时间
	Boolean isRobot = false; //true是机器，false是人
	
	List<Date> dateList = new ArrayList<>();

	//根据时间排序
	public List<Date> getDateList() {
		Collections.sort(dateList, (date1, date2) -> {
			try {
				if (date1.getTime() > date2.getTime()) {
					return 1;
				} else if (date1.getTime() < date2.getTime()) {
					return -1;
				} else {
					return 0;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return 0;
		});
		return dateList;
	}
}