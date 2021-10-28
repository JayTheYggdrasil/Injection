package dev.yggdrasil.injection.framework.ecs

abstract class System(val name: String) {
  def apply(delta: Float, gameState: GameState): GameState

  override def hashCode(): Int = name.hashCode
}

object System {
  def stepState(delta: Float, gameState: GameState, used: Set[System] = Set.empty): GameState = {
    val system = gameState.systems.find(s => !used.contains(s))
    val newState = system.map(s =>
      stepState(delta, s(delta, gameState), used + s)
    )
    newState.getOrElse(gameState)
  }
}
