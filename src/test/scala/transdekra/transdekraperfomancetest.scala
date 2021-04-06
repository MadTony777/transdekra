package transdekra

import java.util.concurrent.TimeUnit

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._


class transdekraperfomancetest extends Simulation {

  val arg = System.getProperty("arg", "test")

  var url = ""
  arg match {
    case "stage" =>
      url = "http://esb-stage:8501/cxf/rest/vehicle/vin/info"
    case "test" =>
      url = "http://esb-test01:8181/cxf/rest/vehicle/vin/info"
  }



  val csvFeeder= csv("vin.csv").circular

  val scn = scenario("VinInfo").repeat(1) {
    feed(csvFeeder).
        exec(http("transdekraperfomancetestRequest")
          .post(url)
          .header("Content-Type", "application/json")
          .header("Accept", "application/json")
            .header("X-VSK-CorrelationId", "${id}")
          .body(StringBody(
            """{ "vin" : """" + "${vin}" + """"}"""))
          .check(status.is(200))
        ).pause(Duration.apply(20, TimeUnit.MILLISECONDS))
      }

  setUp(scn.inject(constantUsersPerSec(10) during (2 minute)))
    .maxDuration(FiniteDuration.apply(2, TimeUnit.MINUTES))

}
