package dev.yggdrasil.injection.framework.ecs.Systems

import dev.yggdrasil.injection.framework.ecs.Systems.DispatcherSystem.EventGenerator
import dev.yggdrasil.injection.framework.ecs.{GameState, System, SystemStorage}

case class DispatcherSystem private(override val name: String, dispatchEvent: GameState => GameState, period: Float, count: Int = -1, elapsedTime: Float = 0) extends System(name) {
  require(period > 0)

  override final def apply(delta: Float, gameState: GameState): GameState = if(count < (elapsedTime/period).toInt){
    val gs = dispatchEvent(gameState)
    dispatched(gs, delta)
  } else failed(gameState, delta)

  def failed(gameState: GameState, delta: Float): GameState =
    gameState.storage -> gameState.systems.updatedByName(copy(elapsedTime = elapsedTime + delta))

  def dispatched(gameState: GameState, delta: Float): GameState = {
    val me: DispatcherSystem = gameState.systems.find(_.name == name) match {
      case Some(system) => system.asInstanceOf[DispatcherSystem]
      case None => return gameState
    }
    val newMe = me.copy(count = count + 1, elapsedTime = elapsedTime + delta)

    gameState.storage -> gameState.systems.updatedByName(newMe)
  }
}

object DispatcherSystem {
  abstract class EventGenerator(val name: String, val period: Float) {
    def dispatchEvent(gameState: GameState): GameState
    final def getDispatcher: DispatcherSystem = DispatcherSystem(name, dispatchEvent, period)
  }
}
