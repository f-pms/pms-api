package com.hbc.pms.integration.db.entity;

import com.hbc.pms.core.model.enums.AlarmSeverity;
import com.hbc.pms.core.model.enums.AlarmType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "alarm_condition")
@Builder
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class AlarmConditionEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String name;

  @Column
  private String description;

  @Column
  private boolean isEnabled; // TODO: will implement

  @Column
  @Enumerated(EnumType.STRING)
  private AlarmSeverity severity;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  private SensorConfigurationEntity sensorConfiguration;

  @Column
  @Enumerated(EnumType.STRING)
  private AlarmType type;

  @Column
  private String cron;

  @Column
  private int timeDelay;

  @Column
  private Double min;

  @Column
  private Double max;

  @ManyToMany
  @JoinTable(
      name = "alarm_condition_alarm_action",
      joinColumns = @JoinColumn(name = "alarm_condition_id"),
      inverseJoinColumns = @JoinColumn(name = "alarm_action_id"))
  private List<AlarmActionEntity> actions;
}