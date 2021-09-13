package dev.yggdrasil.injection.project

import dev.yggdrasil.injection.framework.ui.Components.Shape

package object ui {
  final object Global {
    val GRID_SIZE: Int = 64
    val SPACE_SHAPE: Shape = Shape(GRID_SIZE, GRID_SIZE)
    val STEP_INTERVAL: Float = 1
  }
}
