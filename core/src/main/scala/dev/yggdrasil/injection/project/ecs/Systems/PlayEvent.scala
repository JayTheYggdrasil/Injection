package dev.yggdrasil.injection.project.ecs.Systems

import dev.yggdrasil.injection.framework.ecs.Systems.EventSystem
import dev.yggdrasil.injection.framework.ecs.{Entity, GameState, SystemStorage}
import dev.yggdrasil.injection.project.ecs.Components.Sequence
import dev.yggdrasil.injection.project.ui.Global
import dev.yggdrasil.injection.util.{Looped, LoopedList}

case class PlayEvent(override val name: String) extends EventSystem(name) {

  override def handleEvent(gameState: GameState): GameState = {
    // Generate sequenced movement system from Sequence components.
    val compiledSequence = compileSequences(gameState)

    val playSystems: SystemStorage = SystemStorage.fromList(
      MovementGenerator(Global.STEP_INTERVAL, compiledSequence).getDispatcher :: Nil
    )

    gameState.copy(systems = playSystems)
  }

  def compileSequences(gameState: GameState): Looped[Int] = {
    // Not super efficient, more book keeping would help, but it should work.
    val sequencedEntities = gameState.storage.join(classOf[Sequence])
    val starts = sequencedEntities.filter(e => e(classOf[Sequence]) match {
      case Sequence(_, _, None, _) => true
      case _ => false
    })
    Looped.combine(starts.map(e => LoopedList(travel(e, gameState))).toList)
  }

  def travel(entity: Entity, gameState: GameState): List[Int] = {
    entity(classOf[Sequence]) match {
      case Sequence(_, _, _, Some(next)) => entity.id :: travel(gameState.storage(next), gameState)
      case _ => entity.id :: Nil
    }
  }
}
