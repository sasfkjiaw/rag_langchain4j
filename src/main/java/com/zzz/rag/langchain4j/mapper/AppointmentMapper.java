package com.zzz.rag.langchain4j.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zzz.rag.langchain4j.entity.Appointment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AppointmentMapper extends BaseMapper<Appointment> {
}
