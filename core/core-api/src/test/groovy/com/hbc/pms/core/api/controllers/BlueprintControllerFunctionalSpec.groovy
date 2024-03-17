package com.hbc.pms.core.api.controllers

import com.hbc.pms.core.api.TestDataFixture
import com.hbc.pms.core.api.controller.v1.request.SensorConfigurationRequest
import com.hbc.pms.core.api.controller.v1.response.BlueprintResponse
import com.hbc.pms.core.api.support.error.ErrorCode
import com.hbc.pms.core.api.support.response.ApiResponse
import com.hbc.pms.core.api.test.setup.FunctionalTestSpec
import com.hbc.pms.integration.db.repository.BlueprintRepository
import com.hbc.pms.integration.db.repository.SensorConfigurationRepository
import com.hbc.pms.support.spock.test.RestClient
import java.util.concurrent.ThreadLocalRandom
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.PendingFeature

class BlueprintControllerFunctionalSpec extends FunctionalTestSpec {
  @Autowired
  RestClient restClient

  @Autowired
  BlueprintRepository blueprintRepository

  @Autowired
  SensorConfigurationRepository configurationRepository

  def "Get all blueprints - OK"() {
    given:
    def blueprintEntities = blueprintRepository.findAll()
    def blueprintEntityCount = blueprintEntities.size()

    when:
    def response
            = restClient.get("/blueprints", ApiResponse<List<BlueprintResponse>>)

    then:
    response.statusCode.is2xxSuccessful()
    def blueprints = response.body.data as List<BlueprintResponse>
    blueprints.size() == blueprintEntityCount
    blueprints.every {
      it["id"] as Long in blueprintEntities.asList().stream().map(be -> be.getId()).toList()
    }
  }

  def "Get all blueprints by blueprintName - OK"() {
    given:
    def blueprintEntities = blueprintRepository
            .findAllByTypeAndName(null, "PREDEFINED")
    def blueprintEntityCount = blueprintEntities.size()

    when:
    def response
            = restClient.get("/blueprints?blueprintName=PREDEFINED", ApiResponse<List<BlueprintResponse>>)

    then:
    response.statusCode.is2xxSuccessful()
    def blueprints = response.body.data as List<BlueprintResponse>
    blueprints.size() == blueprintEntityCount
    blueprints.every {
      it["id"] as Long in blueprintEntities.asList().stream().map(be -> be.getId()).toList()
    }
  }

  def "Get all blueprints by blueprintType - OK"() {

  }

  def "Get all blueprints by blueprintType and blueprintName - OK"() {

  }

  def "Get all blueprints by blueprintType - Not existed type - OK with empty list"() {

  }

  def "Get all blueprints by blueprintName - Not existed name - OK with empty list"() {

  }

  def "Get blueprint by Id - OK"() {
    when:
    def response
            = restClient.get("/blueprints/$TestDataFixture.MONITORING_BLUEPRINT_ID", ApiResponse<BlueprintResponse>)
    def blueprintEntity = blueprintRepository.findById(TestDataFixture.MONITORING_BLUEPRINT_ID).get()
    configurationRepository.save(
            TestDataFixture.createSensorConfiguration(blueprintEntity, TestDataFixture.PLC_ADDRESS_BOOL_01))

    then:
    response.statusCode.is2xxSuccessful()
    verifyAll(response.body.data as BlueprintResponse) {
      it.getId() == TestDataFixture.MONITORING_BLUEPRINT_ID
      it.getName() == blueprintEntity.name
      it.getDescription() == blueprintEntity.description
      it.getSensorConfigurations().size() == blueprintEntity.sensorConfigurations.size()
      it.sensorConfigurations.every {
        it.getId() in blueprintEntity
                .sensorConfigurations.stream().map(sc -> sc.getId()).toList()
      }
    }
  }

  def "Get blueprint by Id - Not found and Bad request"() {
    when:
    def response
            = restClient.get("/blueprints/123", ApiResponse<RuntimeException>)

    then:
    response.statusCode.is4xxClientError()
    response.body.error["code"] == ErrorCode.E404.toString()
  }

  def "Get blueprint by Id - Invalid format input - Bad request"() {
    when:
    def response
            = restClient.get("/blueprints/abc", ApiResponse<RuntimeException>)

    then:
    response.statusCode.is4xxClientError()
    response.body.error["code"] == ErrorCode.E400.toString()
  }

  @PendingFeature
  //No feature is using this endpoint yet
  def "Create blueprint - OK"() {}

  @PendingFeature
  //No feature is using this endpoint yet
  def "Create blueprint - Null name and description - Bad Request"() {}

  @PendingFeature
  //No feature is using this endpoint yet
  def "Update blueprint - OK"() {}

  @PendingFeature
  //No feature is using this endpoint yet
  def "Update blueprint - Null name and description - Bad Request"() {}

  @PendingFeature
  //No feature is using this endpoint yet
  def "Update blueprint - Not existed blueprint - Not found and Bad request"() {}

  @PendingFeature
  // can't create sensor configuration
  def "Create sensor config - OK"() {
    given:
    def configRequest = createSensorConfigurationRequest()
    def configCountBefore = configurationRepository.findAll().size()

    when:
    def response = restClient
            .post(
                    "/blueprints/$TestDataFixture.MONITORING_BLUEPRINT_ID/sensor-configurations", configRequest,
                    ApiResponse<Boolean>)

    then:
    response.statusCode.is2xxSuccessful()
    response.body.data == true
    def configCountAfter = configurationRepository.findAll().size()
    configCountBefore + 1 == configCountAfter
  }

  @PendingFeature
  def "Create sensor config - Null address - Bad request"() {}

  @PendingFeature
  def "Create sensor config - Not existed blueprint - Not found and Bad request"() {}

  def "Update sensor config - Using address field - OK"() {

  }

  def "Update sensor config - Using 3 fields aggregating - OK"() {}

  def "Update sensor config - Using 3 fields aggregating with 1 null - Exception thrown"() {}

  def "Update sensor config - Not existed blueprint - Not found and Bad request"() {}

  def "Update sensor config - Not existed sensor config - Not found and Bad request"() {}

  def createSensorConfigurationRequest() {
    return SensorConfigurationRequest.builder()
            .address(TestDataFixture.PLC_ADDRESS_REAL_01)
            .x(ThreadLocalRandom.current().nextDouble(1, 500))
            .y(ThreadLocalRandom.current().nextDouble(1, 500))
            .build()
  }
}