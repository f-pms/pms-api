package com.hbc.pms.plc.integration.plc4x;

import com.hbc.pms.plc.api.IoResponse;
import com.hbc.pms.plc.api.PlcConnector;
import com.hbc.pms.plc.api.exceptions.NotSupportedPlcResponseException;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.PlcDriverManager;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.value.PlcValue;
import org.apache.plc4x.java.s7.readwrite.connection.S7HDefaultNettyPlcConnection;
import org.apache.plc4x.java.s7.readwrite.connection.S7HMuxImpl;
import org.apache.plc4x.java.s7.readwrite.tag.S7Tag;
import org.apache.plc4x.java.spi.messages.DefaultPlcReadResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Slf4j
@Primary
public class Plc4xConnector implements PlcConnector {

  @Value("${hbc.plc.url}")
  private String plcUrl;

  private PlcConnection plcConnection;

  @SuppressWarnings("java:S1135")
  private static IoResponse getIoResponse(PlcReadResponse defaultPlcReadResponse, String entry) {
    IoResponse ioResponse = new IoResponse();
    PlcValue plcValue = defaultPlcReadResponse.getPlcValue(entry);
    if (Objects.isNull(plcValue)) {
      // temporarily ignore logs due to annoying std out
      // TODO: escalate to higher layer to resolve the invalid tags
      log.debug("Tag with address {} is null", entry);
    }
    ioResponse.setPlcValue(plcValue);
    ioResponse.setVariableName(entry);
    return ioResponse;
  }

  @PostConstruct
  private void init() throws PlcConnectionException {
    Assert.notNull(plcUrl, "PLC URL must be provided!s");
    plcConnection =
        PlcDriverManager.getDefault().getConnectionManager().getConnection("s7://" + plcUrl);
  }
  @SneakyThrows
  public boolean tryToConnect() {
    if (plcConnection != null && !isConnected()) {
      try {
        log.info("Try to reconnect to PLC");
        plcConnection.close();
        init();
      } catch (PlcConnectionException plcConnectionException) {
        log.error("Failed to connect to PLC", plcConnectionException);
        return false;
      }
    }
    return isConnected();
  }

  public boolean isConnected() {
    if (plcConnection instanceof S7HDefaultNettyPlcConnection s7HDefaultNettyPlcConnection) {
      return s7HDefaultNettyPlcConnection.getChannel().attr(S7HMuxImpl.IS_CONNECTED).get();
    }
    return false;
  }

  @Override
  @SneakyThrows
  public Map<String, IoResponse> executeBlockRequest(List<String> variableNames) {
    if (!tryToConnect()) {
      return Map.of();
    }
    Map<String, IoResponse> stringIoResponseMap = new HashMap<>();
    if (!plcConnection.getMetadata().canRead()) {
      log.error("This connection doesn't support reading.");
      return stringIoResponseMap;
    }
    PlcReadRequest.Builder builder = plcConnection.readRequestBuilder();
    for (var address : variableNames) {
      builder.addTag(address, S7Tag.of(address));
    }
    final PlcReadRequest rr = builder.build();
    PlcReadResponse result = rr.execute().get(4000, TimeUnit.MILLISECONDS);
    if (result instanceof DefaultPlcReadResponse defaultPlcReadResponse) {
      for (var entry : defaultPlcReadResponse.getValues().keySet()) {
        IoResponse ioResponse = getIoResponse(defaultPlcReadResponse, entry);
        stringIoResponseMap.put(entry, ioResponse);
      }
    } else {
      throw new NotSupportedPlcResponseException(
          "PlcReadResponse is not instance of DefaultPlcReadResponse, throwing an exception");
    }
    return stringIoResponseMap;
  }

  @Override
  @SneakyThrows
  public IoResponse validate(String address) {
    PlcReadRequest.Builder builder = plcConnection.readRequestBuilder();
    builder.addTag(address, S7Tag.of(address));
    PlcReadResponse readResponse = builder.build().execute().get();
    return getIoResponse(readResponse, address);
  }
}
