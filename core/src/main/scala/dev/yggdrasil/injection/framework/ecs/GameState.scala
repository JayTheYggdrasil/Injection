package dev.yggdrasil.injection.framework.ecs

case class GameState(storage: EntityStorage, systems: SystemStorage) {
  def unpack: (EntityStorage, SystemStorage) = (storage, systems)

  def clearChanged(): GameState = copy(storage.clearChanged())
}

object GameState {
  implicit def fromTuple(tuple: (EntityStorage, SystemStorage)): GameState = GameState(tuple._1, tuple._2)
}
