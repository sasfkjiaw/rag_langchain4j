package com.zzz.rag.langchain4j.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzz.rag.langchain4j.entity.Appointment;

public interface AppointmentService extends IService<Appointment> {
    Appointment getOne(Appointment appointment);
}