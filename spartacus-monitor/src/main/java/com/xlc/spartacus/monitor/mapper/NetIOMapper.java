package com.xlc.spartacus.monitor.mapper;

import com.xlc.spartacus.monitor.pojo.NetIO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * mapper
 *
 * @author xlc, since 2021
 */
@Mapper
public interface NetIOMapper {

    List<NetIO> getTodayNetIoDetails(String ip);


    List<NetIO> getMonthNetIoDetails(String ip);


    List<NetIO> getYearNetIoDetails(String ip);
}