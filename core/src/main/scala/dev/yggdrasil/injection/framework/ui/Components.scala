package dev.yggdrasil.injection.framework.ui

import com.badlogic.gdx.graphics.Texture
import dev.yggdrasil.injection.framework.ecs.Component

object Components {
  case class Visual(texture: Texture, shape: Shape, zIndex: Int, directed: Boolean) extends Component
  case class Shape(height: Int, width: Int) extends Component
  case class Clicked() extends Component
  case class Hovered() extends Component
}
