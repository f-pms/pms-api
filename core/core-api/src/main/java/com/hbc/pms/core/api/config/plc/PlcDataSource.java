package com.hbc.pms.core.api.config.plc;

import com.hbc.pms.core.api.service.BlueprintPersistenceService;
import com.hbc.pms.plc.api.scraper.HbcScrapeJobDataSource;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class PlcDataSource implements HbcScrapeJobDataSource {
  private final BlueprintPersistenceService blueprintService;

  @Override
  public Map<String, String> getTags() {
    return blueprintService
            .getAllAddresses()
            .stream()
            .collect(Collectors.toMap(Function.identity(), Function.identity(), (a1, a2) -> a1));
  }
}
