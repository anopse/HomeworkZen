package homeworkzen.rest

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.github.swagger.akka._
import com.github.swagger.akka.model.Info
import homeworkzen.Config
import io.swagger.models.auth.BasicAuthDefinition

import scala.reflect.runtime.universe

class SwaggerDocService(implicit context: RestContext) extends SwaggerHttpService with HasActorSystem {
  override val apiTypes: Seq[universe.Type] = Routes.routes.map(_.thisType)
  override val host = s"${Config.Api.interface}:${Config.Api.port}"
  override val info = Info(version = "1.0")
  override val securitySchemeDefinitions = Map("basicAuth" -> new BasicAuthDefinition())
  // url of swagger endpoint with default config is http://localhost:8080/api-docs/swagger.json

  override implicit val actorSystem: ActorSystem = context.system
  override implicit val materializer: ActorMaterializer = context.materializer
}