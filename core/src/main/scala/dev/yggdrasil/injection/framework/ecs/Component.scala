package dev.yggdrasil.injection.framework.ecs

import scala.reflect.ClassTag

trait Component

object Component {

  case class Renderable() extends Component

  type ComponentMap = Map[ClassTag[_ <: Component], Component]

  implicit def entityToComponentMap(entity: Entity): ComponentMap = entity.componentMap
}


