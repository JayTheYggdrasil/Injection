package dev.yggdrasil.injection.framework.ecs.Systems

import dev.yggdrasil.injection.framework.ecs.GameState
import dev.yggdrasil.injection.framework.ecs.System

abstract class EventSystem(override val name: String) extends System(name) {
  override def apply(delta: Float, gameState: GameState): GameState = {
    val gs = handleEvent(gameState)
    gs.storage -> gs.systems.removed(this)
  }

  def handleEvent(gameState: GameState): GameState
}
