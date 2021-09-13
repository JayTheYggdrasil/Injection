package dev.yggdrasil.injection.project.ui

import dev.yggdrasil.injection.framework.ecs.Entity
import dev.yggdrasil.injection.framework.ecs.System.{EntityStorage, GameState, System}
import dev.yggdrasil.injection.framework.ui.{ECSActorFactory, ECSScreen}
import dev.yggdrasil.injection.project.ecs.Components.{Direction, GridPosition}
import dev.yggdrasil.injection.project.ecs.Entities.{arrow, emptyGrid, putGridEntity}
import dev.yggdrasil.injection.project.ecs.Systems.{EditorSystem, MovementSystem, PlaySystem}
import dev.yggdrasil.injection.project.ui.actors.ActorFactory
import dev.yggdrasil.injection.util.{Looped, LoopedList}

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
    val systems: Set[System] = Set(
//      MovementSystem("sequence", Global.STEP_INTERVAL, sequence),
      EditorSystem("input"),
      PlaySystem("play")
    )

    GameState(storage, systems)
  }
}
