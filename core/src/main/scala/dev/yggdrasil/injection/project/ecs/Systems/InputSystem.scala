package dev.yggdrasil.injection.project.ecs.Systems

import dev.yggdrasil.injection.framework.ecs.Entity
import dev.yggdrasil.injection.framework.ecs.System.{EntityStorage, GameState, System}
import dev.yggdrasil.injection.framework.ui.Components.Clicked
import dev.yggdrasil.injection.project.ecs.Components.{Direction, GridEntity, GridPosition, Space}
import dev.yggdrasil.injection.project.ecs.Entities.{arrow, parentOf, putGridEntity}

case class InputSystem(name: String) extends System {
  override def apply(delta: Float, gameState: GameState): GameState = {
    val (storage, systems) = gameState.unpack
    val clickedEntities = storage.join(classOf[Clicked])
    // Add arrows for each empty space clicked

    // Get all empty spaces
    val clickedSpaces: Set[Entity] = clickedEntities.filter(e =>
      e.getInstance(classOf[Space]).nonEmpty && e(classOf[Space]).entityRef.isEmpty
    )

    // Create new arrows for each space clicked
    val newStorage = clickedSpaces.foldLeft(storage)((s, space) => {
      val newArrow = arrow(Direction.UP)
      println(newArrow.componentMap)
      putGridEntity(newArrow, space(classOf[GridPosition]), s.updated(space.removed(classOf[Clicked])))
    })

    gameState.copy(entityStorage = newStorage)
  }
}
