package dev.yggdrasil.injection.project

import com.badlogic.gdx.graphics.Texture
import dev.yggdrasil.injection.framework.ui.Components.{Shape, Visual}

package object ui {
  object Global {
    val GRID_SIZE: Int = 64
    val SPACE_SHAPE: Shape = Shape(GRID_SIZE, GRID_SIZE)
    val STEP_INTERVAL: Float = 1
    object textures {
      val arrow: Visual = Visual(new Texture("Arrow.png"), Shape(128, 128), directed = true)
      val space: Visual = Visual(new Texture("Space.png"), Shape(128, 128), directed = false)
    }
  }
}