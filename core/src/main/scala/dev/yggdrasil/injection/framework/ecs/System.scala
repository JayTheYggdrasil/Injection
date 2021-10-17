package dev.yggdrasil.injection.framework.ecs

abstract class System(val name: String) {
  def apply(delta: Float, gameState: GameState): GameState

  override def hashCode(): Int = name.hashCode
}

object System {
  def stepState(delta: Float, gameState: GameState): GameState =
    gameState.systems.foldLeft(gameState)((state, system) => system(delta, state))
}
