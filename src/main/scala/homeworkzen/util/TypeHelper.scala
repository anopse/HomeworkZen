package homeworkzen.util

import scala.reflect.runtime.universe._

object TypeHelper {
  def getType[A](a: A)(implicit tag: TypeTag[A]): Type = tag.tpe

}
