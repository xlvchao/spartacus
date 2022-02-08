package com.xlc.spartacus.monitor.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * mapper
 *
 * @author xlc, since 2021
 */
@Mapper
@Component("taskMapper")
public interface ScheduleTaskMapper {

    List<Map<String, Object>> getRecentTaskExeRecords();
}