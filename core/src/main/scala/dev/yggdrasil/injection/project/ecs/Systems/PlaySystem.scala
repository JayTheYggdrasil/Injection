package dev.yggdrasil.injection.project.ecs.Systems

import dev.yggdrasil.injection.framework.ecs.System.{EntityStorage, GameState, System}
import dev.yggdrasil.injection.framework.events.EventController
import dev.yggdrasil.injection.project.Events.Play
import dev.yggdrasil.injection.project.ecs.Components.Sequence
import dev.yggdrasil.injection.project.ui.Global
import dev.yggdrasil.injection.util.Looped

case class PlaySystem(name: String) extends System {
  override def apply(delta: Float, gameState: GameState): GameState = {
    // Triggered by Play event
    val playEvents = EventController(classOf[Play])
    EventController.remove(Play())

    if(playEvents.nonEmpty) {
      val systems = gameState.systems

      // Generate sequenced movement system from Sequence components.
      val sequences = gameState.entityStorage.join(classOf[Sequence]).map(e =>
        e(classOf[Sequence])
      )

      val sorted = sequences.toList.sortBy(_.loopID).map(_.loop)

      val compiledSequence = Looped.combine(sorted)

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
}
