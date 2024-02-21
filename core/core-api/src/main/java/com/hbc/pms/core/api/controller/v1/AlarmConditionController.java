package com.hbc.pms.core.api.controller.v1;

import com.hbc.pms.core.api.controller.v1.request.CreateAlarmConditionCommand;
import com.hbc.pms.core.api.controller.v1.request.UpdateAlarmConditionCommand;
import com.hbc.pms.core.api.service.AlarmConditionPersistenceService;
import com.hbc.pms.core.api.service.AlarmConditionService;
import com.hbc.pms.core.api.support.response.ApiResponse;
import com.hbc.pms.core.model.AlarmCondition;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("alarm-conditions")
@RequiredArgsConstructor
public class AlarmConditionController {
  private final ModelMapper mapper;
  private final AlarmConditionService alarmConditionService;
  private final AlarmConditionPersistenceService alarmConditionPersistenceService;

  @PostMapping
  public ApiResponse<AlarmCondition> create(@Valid @RequestBody CreateAlarmConditionCommand body) {
    AlarmCondition result = alarmConditionService.createAlarmCondition(body);
    return ApiResponse.success(result);
  }

  @PutMapping("/{id}")
  ApiResponse<AlarmCondition> update(
      @PathVariable Long id, @Valid @RequestBody UpdateAlarmConditionCommand body) {
    AlarmCondition result = alarmConditionService.updateAlarmCondition(id, body);

    return ApiResponse.success(result);
  }

  @GetMapping
  public ApiResponse<List<AlarmCondition>> getAll() {
    List<AlarmCondition> alarmConditions =
        alarmConditionPersistenceService.getAll().stream()
            .map(ac -> mapper.map(ac, AlarmCondition.class))
            .toList();

    return ApiResponse.success(alarmConditions);
  }

  @GetMapping("/{alarmConditionId}")
  public ApiResponse<AlarmCondition> getById(@PathVariable long alarmConditionId) {
    AlarmCondition condition = alarmConditionPersistenceService.getById(alarmConditionId);

    return ApiResponse.success(condition);
  }
}
