import io.gatling.core.scenario.Simulation
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._

class ControllerTest extends Simulation{
  private val httpConf = http
    .acceptHeader(" application/json")
    .baseUrl("http://localhost:8080")

  private val scn: ScenarioBuilder = scenario("Initial Scenario")
    .exec(http("Get all posts")
      .get("/venues"))

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpConf)
}
