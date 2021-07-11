package dev.yggdrasil.injection.project.ecs.Systems
import dev.yggdrasil.injection.framework.ecs.System.System
import dev.yggdrasil.injection.project.ui.Global

trait TimedSystem extends System {
  val stepInterval: Float
  val accumulatedDelta: Float
  val shouldStep: Boolean = accumulatedDelta >= stepInterval
}
