package dev.yggdrasil.injection.project.ecs.Systems

import dev.yggdrasil.injection.framework.ecs.Entity
import dev.yggdrasil.injection.framework.ecs.System.{EntityStorage, GameState, System}
import dev.yggdrasil.injection.framework.events.EventController
import dev.yggdrasil.injection.project.Events.{MakeArrow, TurnEntityLeft, TurnEntityRight}
import dev.yggdrasil.injection.project.ecs.Components.{Direction, GridEntity, GridPosition, Space}
import dev.yggdrasil.injection.project.ecs.Entities.{arrow, childOf, parentOf, putGridEntity}

case class EditorSystem(name: String) extends System {
  override def apply(delta: Float, gameState: GameState): GameState = {
    val a = makeArrows(gameState)
    val b = rotateEntitiesLeft(a)
    rotateEntitiesRight(b)
  }

  def makeArrows(gameState: GameState): GameState = {
    val (storage, _) = gameState.unpack
    val makeEvents = EventController(classOf[MakeArrow])
    val clickedEntities = makeEvents.map(e => storage(e.spaceID))

    // Get all empty spaces + remove events for the valid ones
    val clickedSpaces: Set[Entity] = clickedEntities.filter(e =>
      e.getInstance(classOf[Space]).nonEmpty && e(classOf[Space]).entityRef.isEmpty && {
        EventController.remove(MakeArrow(e.id))
        true
      }
    )

    // Create new arrows for each space clicked.
    val newStorage = clickedSpaces.foldLeft(storage)((s, space) => {
      val newArrow = arrow(Direction.UP)
      putGridEntity(newArrow, space(classOf[GridPosition]), s)
    })


    gameState.copy(entityStorage = newStorage)
  }

  def rotateEntitiesLeft(gameState: GameState): GameState = {
    val (storage, _) = gameState.unpack

    val turnLeftEvents = EventController(classOf[TurnEntityLeft])
    turnLeftEvents.foreach(EventController.remove(_))
    val entitiesToTurnLeft = turnLeftEvents.map(e => storage(e.entityID))
    val newStorage = entitiesToTurnLeft.foldLeft(storage)((s, entity) => entity(classOf[Direction]) match {
      case Direction.DOWN => storage.updated(entity.updated(Direction.RIGHT))
      case Direction.RIGHT => storage.updated(entity.updated(Direction.UP))
      case Direction.UP => storage.updated(entity.updated(Direction.LEFT))
      case Direction.LEFT => storage.updated(entity.updated(Direction.DOWN))
    })

    gameState.copy(entityStorage = newStorage)
  }

  def rotateEntitiesRight(gameState: GameState): GameState = {
    val (storage, _) = gameState.unpack

    val turnRightEvents = EventController(classOf[TurnEntityRight])
    turnRightEvents.foreach(EventController.remove(_))
    val entitiesToTurnRight = turnRightEvents.map(e => storage(e.entityID))
    val newStorage = entitiesToTurnRight.foldLeft(storage)((s, entity) => entity(classOf[Direction]) match {
      case Direction.DOWN => storage.updated(entity.updated(Direction.LEFT))
      case Direction.RIGHT => storage.updated(entity.updated(Direction.DOWN))
      case Direction.UP => storage.updated(entity.updated(Direction.RIGHT))
      case Direction.LEFT => storage.updated(entity.updated(Direction.UP))
    })

    gameState.copy(entityStorage = newStorage)
  }
}
