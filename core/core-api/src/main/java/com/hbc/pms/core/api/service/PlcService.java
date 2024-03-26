package com.hbc.pms.core.api.service;

import com.hbc.pms.plc.api.PlcConnector;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class PlcService {

  private final PlcConnector s7Connector;

  public boolean isTagExisted(String address) {
    return true;
  }
}
