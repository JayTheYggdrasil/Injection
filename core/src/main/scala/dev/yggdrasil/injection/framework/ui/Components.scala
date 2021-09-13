package dev.yggdrasil.injection.framework.ui

import dev.yggdrasil.injection.framework.ecs.Component

object Components {
  case class Visual() extends Component
  case class Shape(height: Int, width: Int) extends Component
  case class Clicked() extends Component
  case class Hovered() extends Component
}
