package homeworkzen.rest.swagger

import java.util

import com.github.ghik.silencer.silent
import io.swagger.core.filter.SwaggerSpecFilter
import io.swagger.model._
import io.swagger.models.{Model, Operation}
import io.swagger.models.parameters.Parameter
import io.swagger.models.properties.Property

//noinspection ScalaDeprecation
@silent
class SwaggerConfigurationFilter extends SwaggerSpecFilter {

  override def isParamAllowed(parameter: Parameter,
                              operation: Operation,
                              api: ApiDescription,
                              params: util.Map[String, util.List[String]],
                              cookies: util.Map[String, String],
                              headers: util.Map[String, util.List[String]]
                             ): Boolean = false

  override def isPropertyAllowed(
                                  model: Model,
                                  property: Property,
                                  propertyName: String,
                                  params: util.Map[String, util.List[String]],
                                  cookies: util.Map[String, String],
                                  headers: util.Map[String, util.List[String]]
                                ): Boolean = false

  override def isOperationAllowed(
                                   operation: Operation,
                                   api: ApiDescription,
                                   params: util.Map[String, util.List[String]],
                                   cookies: util.Map[String, String],
                                   headers: util.Map[String, util.List[String]]
                                 ): Boolean = false
}