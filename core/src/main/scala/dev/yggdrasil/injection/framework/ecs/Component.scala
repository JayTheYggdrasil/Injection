package dev.yggdrasil.injection.framework.ecs

trait Component

object Component {

  case class Renderable() extends Component

  type ComponentMap = Map[Class[_ <: Component], Component]

  implicit def entityToComponentMap(entity: Entity): ComponentMap = entity.componentMap
}


