package com.hbc.pms.core.api.service;

import com.hbc.pms.core.api.support.notification.EmailChannel;
import com.hbc.pms.core.api.support.notification.PopupChannel;
import com.hbc.pms.core.model.AlarmHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
  private final PopupChannel popupChannel;
  private final EmailChannel emailChannel;

  public void notify(List<AlarmHistory> histories) {
    // TODO: add async
    histories.forEach(history -> {
      var condition = history.getAlarmCondition();
      var actions = condition.getActions();
      actions.forEach(action -> {
        popupChannel.notify(action, condition);
        emailChannel.notify(action, condition);
      });
    });
  }
}
