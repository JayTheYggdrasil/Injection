package dev.yggdrasil.injection.project.ecs.Systems

import dev.yggdrasil.injection.framework.ecs.Entity
import dev.yggdrasil.injection.framework.ecs.System.{EntityStorage, GameState, System}
import dev.yggdrasil.injection.framework.events.EventController
import dev.yggdrasil.injection.project.Events.Play
import dev.yggdrasil.injection.project.ecs.Components.Sequence
import dev.yggdrasil.injection.project.ui.Global
import dev.yggdrasil.injection.util.{Looped, LoopedList}

case class PlaySystem(name: String) extends System {
  override def apply(delta: Float, gameState: GameState): GameState = {
    // Triggered by Play event
    val playEvents = EventController(classOf[Play])
    EventController.remove(Play())

    if(playEvents.nonEmpty) {
      val systems = gameState.systems

      // Generate sequenced movement system from Sequence components.

      val compiledSequence = compileSequences(gameState)

      val playSystems: Set[System] = Set(
        MovementSystem("PlayMovement", Global.STEP_INTERVAL, compiledSequence),
//        StopSystem("Stop"),
//        PauseSystem("Pause")
      )
      gameState.copy(systems = playSystems)
    } else {
      gameState
    }
  }

  def compileSequences(gameState: GameState): Looped[Int] = {
    // Not super efficient, more book keeping would help, but it should work.
    val sequencedEntities = gameState.entityStorage.join(classOf[Sequence])
    val starts = sequencedEntities.filter(e => e(classOf[Sequence]) match {
      case Sequence(_, _, None, _) => true
      case _ => false
    })
    Looped.combine(starts.map(e => LoopedList(travel(e, gameState))).toList)
  }

  def travel(entity: Entity, gameState: GameState): List[Int] = {
    entity(classOf[Sequence]) match {
      case Sequence(_, _, _, Some(next)) => entity.id :: travel(gameState.entityStorage(next), gameState)
      case _ => entity.id :: Nil
    }
  }
}
