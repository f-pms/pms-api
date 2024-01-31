package com.hbc.pms.core.model;

import com.hbc.pms.core.model.enums.AlarmSeverity;
import com.hbc.pms.core.model.enums.AlarmType;
import lombok.*;

import java.util.Set;

import static java.util.Objects.nonNull;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class AlarmCondition {
  private Long id;
  private String name;
  private String description;
  private boolean isEnabled;
  private AlarmSeverity severity;
  private SensorConfiguration sensorConfiguration;

  // TODO: type: PREDEFINED, CUSTOM
  private AlarmType type;

  // predefined condition
  private String cron;
  private int timeDelay;
  private Double min;
  private Double max;

  public boolean isMet(Double value) {
    // predefined condition
    if (type.equals(AlarmType.PREDEFINED)) {
      return (value < 1);
    }

    // custom condition
    if (nonNull(min) && nonNull(max)) {
      return (min <= value && value <= max);
    }
    if (nonNull(min)) {
      return (min <= value);
    }
    if (nonNull(max)) {
      return (value <= max);
    }
    return false;
  }
}