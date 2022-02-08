package com.xlc.spartacus.monitor.mapper;

import com.xlc.spartacus.monitor.pojo.Access;
import com.xlc.spartacus.monitor.pojo.HighFrequencyAccess;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * mapper
 *
 * @author xlc, since 2021
 */
@Mapper
public interface AccessMapper {

    /**
     * 根据IP获取访问次数
     */
    Integer getTotalAccessCountByIp(String ip);
    Integer getTodayAccessCountByIp(String ip);
    Integer getMonthAccessCountByIp(String ip);
    Integer getYearAccessCountByIp(String ip);


    /**
     * 高频访问IP
     */
    List<HighFrequencyAccess> getTotalHighFrequencyAccessess(Integer totalAccessThreshold);
    List<Date> getTotalHighFrequencyAccessessDateList(String ip);
    List<HighFrequencyAccess> getTodayHighFrequencyAccessess(Integer todayAccessThreshold);
    List<Date> getTodayHighFrequencyAccessessDateList(String ip);
    List<HighFrequencyAccess> getMonthHighFrequencyAccessess(Integer monthAccessThreshold);
    List<Date> getMonthHighFrequencyAccessessDateList(String ip);
    List<HighFrequencyAccess> getYearHighFrequencyAccessess(Integer yearAccessThreshold);
    List<Date> getYearHighFrequencyAccessessDateList(String ip);


    /**
     * 浏览记录
     */
    List<Access> getTotalScan();
    List<Access> getTodayScan();
    List<Access> getMonthScan();
    List<Access> getYearScan();


    /**
     * 浏览-详情
     */
    Integer getTotalScanCount();
    Integer getTodayScanCount();
    List<HashMap<Object, Object>> getTodayScanDetails();
    Integer getYestodayScanCount();
    List<HashMap<Object, Object>> getYestodayScanDetails();
    Integer getMonthScanCount();
    List<HashMap<Object, Object>> getMonthScanDetails();
    Integer getLastMonthScanCount();
    List<HashMap<Object, Object>> getLastMonthScanDetails();
    Integer getYearScanCount();
    List<HashMap<Object, Object>> getYearScanDetails();
    Integer getLastYearScanCount();
    List<HashMap<Object, Object>> getLastYearScanDetails();


    /**
     * 访客-详情
     */
    Integer getTotalVisitorCount();
    Integer getTodayVisitorCount();
    List<HashMap<Object, Object>> getTodayVisitorDetails();
    Integer getYestodayVisitorCount();
    List<HashMap<Object, Object>> getYestodayVisitorDetails();
    Integer getMonthVisitorCount();
    List<HashMap<Object, Object>> getMonthVisitorDetails();
    Integer getLastMonthVisitorCount();
    List<HashMap<Object, Object>> getLastMonthVisitorDetails();
    Integer getYearVisitorCount();
    List<HashMap<Object, Object>> getYearVisitorDetails();
    Integer getLastYearVisitorCount();
    List<HashMap<Object, Object>> getLastYearVisitorDetails();


    /**
     * IP-详情
     */
    Integer getTotalIPCount();
    Integer getTodayIPCount();
    List<HashMap<Object, Object>> getTodayIPDetails();
    Integer getYestodayIPCount();
    List<HashMap<Object, Object>> getYestodayIPDetails();
    Integer getMonthIPCount();
    List<HashMap<Object, Object>> getMonthIPDetails();
    Integer getLastMonthIPCount();
    List<HashMap<Object, Object>> getLastMonthIPDetails();
    Integer getYearIPCount();
    List<HashMap<Object, Object>> getYearIPDetails();
    Integer getLastYearIPCount();
    List<HashMap<Object, Object>> getLastYearIPDetails();


    /**
     * 评论-详情
     */
    Integer getTotalCommentCount();
    Integer getTodayCommentCount();
    List<HashMap<Object, Object>> getTodayCommentDetails();
    Integer getYestodayCommentCount();
    List<HashMap<Object, Object>> getYestodayCommentDetails();
    Integer getMonthCommentCount();
    List<HashMap<Object, Object>> getMonthCommentDetails();
    Integer getLastMonthCommentCount();
    List<HashMap<Object, Object>> getLastMonthCommentDetails();
    Integer getYearCommentCount();
    List<HashMap<Object, Object>> getYearCommentDetails();
    Integer getLastYearCommentCount();
    List<HashMap<Object, Object>> getLastYearCommentDetails();
}