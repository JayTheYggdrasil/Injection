package dev.yggdrasil.injection.project.ui

import dev.yggdrasil.injection.framework.ecs.Systems.MuteEvents
import dev.yggdrasil.injection.framework.ecs.{Entity, EntityStorage, GameState, System}
import dev.yggdrasil.injection.framework.ui.{ECSActorFactory, ECSScreen}
import dev.yggdrasil.injection.project.ecs.Entities.{arrow, emptyGrid, putGridEntity}
import dev.yggdrasil.injection.project.ecs.Systems.PlayEvent
import dev.yggdrasil.injection.project.ui.actors.ActorFactory

class LevelScreen extends ECSScreen {
  override def initialState: GameState = LevelScreen.initialState

  override def resize(width: Int, height: Int): Unit = ()

  override def pause(): Unit = ()

  override def resume(): Unit = ()

  override def hide(): Unit = ()

  override def dispose(): Unit = ()

  override protected val actorFactory: ECSActorFactory = ActorFactory
}

object LevelScreen {
  def initialState: GameState = {
    var storage = EntityStorage.empty
    val gridEntity = Entity.fromComponents()
    val gridID = gridEntity.id

    val (entities, lookup) = emptyGrid(10, 20, gridID)

    storage = entities.foldLeft(storage)((s, e) => s.updated(e))

//    // Create and add the arrows
//    val arrowEntity = arrow(Direction.UP)
//    val arrowEntity2 = arrow(Direction.UP)
//
//    // Put the arrows in the grid
//    storage = putGridEntity(arrowEntity, GridPosition(0, 0, gridID), storage)
//    storage = putGridEntity(arrowEntity2, GridPosition(0, 1, gridID), storage)

    // Create the movement sequence
//    val sequence: Looped[Int] = LoopedList(List(arrowEntity.id, arrowEntity2.id))
    val systems: List[System] = MuteEvents("MutePlayEvent", _.isInstanceOf[PlayEvent]) :: Nil

    GameState(storage, systems)
  }
}
