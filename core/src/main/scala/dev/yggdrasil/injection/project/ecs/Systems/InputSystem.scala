package dev.yggdrasil.injection.project.ecs.Systems

import dev.yggdrasil.injection.framework.ecs.Entity
import dev.yggdrasil.injection.framework.ecs.System.{EntityStorage, GameState, System}
import dev.yggdrasil.injection.framework.ui.Components.Clicked

case class InputSystem(name: String) extends System {
  override def apply(delta: Float, gameState: GameState): GameState = {
    val entityStorage = gameState.entityStorage
    val clickedEntities = entityStorage.join(classOf[Clicked])

    // Default escape
    gameState.copy(entityStorage = entityStorage)
  }
}
