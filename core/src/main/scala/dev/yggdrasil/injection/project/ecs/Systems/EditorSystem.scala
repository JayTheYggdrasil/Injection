package dev.yggdrasil.injection.project.ecs.Systems

import dev.yggdrasil.injection.framework.ecs.Entity
import dev.yggdrasil.injection.framework.ecs.System.{EntityStorage, GameState, System}
import dev.yggdrasil.injection.framework.events.EventController
import dev.yggdrasil.injection.project.Events.{Clicked, MakeArrow}
import dev.yggdrasil.injection.project.ecs.Components.{Direction, GridEntity, GridPosition, Space}
import dev.yggdrasil.injection.project.ecs.Entities.{arrow, childOf, parentOf, putGridEntity}

case class EditorSystem(name: String) extends System {
  override def apply(delta: Float, gameState: GameState): GameState = {
    val (storage, systems) = gameState.unpack
    val makeEvents = EventController(classOf[MakeArrow])
    val clickedEntities = makeEvents.map(e => storage(e.spaceID))

    // Get all empty spaces
    val clickedSpaces: Set[Entity] = clickedEntities.filter(e =>
      e.getInstance(classOf[Space]).nonEmpty && e(classOf[Space]).entityRef.isEmpty && {
        EventController.remove(MakeArrow(e.id))
        true
      }
    )

    // Create new arrows for each space clicked
    val newStorage = clickedSpaces.foldLeft(storage)((s, space) => {
      val newArrow = arrow(Direction.UP)
      putGridEntity(newArrow, space(classOf[GridPosition]), s)
    })

    gameState.copy(entityStorage = newStorage)
  }
}
