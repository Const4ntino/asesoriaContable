package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.model.dto.ObligacionNrusRequest;
import com.asesoria.contable.app_ac.model.dto.ObligacionNrusResponse;

import java.util.List;

public interface ObligacionNrusService {

    List<ObligacionNrusResponse> getAll();

    ObligacionNrusResponse getOne(Long id);

    ObligacionNrusResponse save(ObligacionNrusRequest obligacionNrusRequest);

    ObligacionNrusResponse update(Long id, ObligacionNrusRequest obligacionNrusRequest);

    void delete(Long id);
}
