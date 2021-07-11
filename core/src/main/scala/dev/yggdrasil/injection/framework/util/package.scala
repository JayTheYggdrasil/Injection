package dev.yggdrasil.injection.framework

import dev.yggdrasil.injection.framework.ecs.System.{EntityStorage, GameState}
import dev.yggdrasil.injection.project.ecs.Components.Direction

package object util {
//  def loadLevel[G](SomeLevelRepresentation: _): GameState = {
//    ???
//  }

  val dir2deg: Map[Direction, Float] = Map(
    Direction.UP -> 0,
    Direction.DOWN -> 180,
    Direction.LEFT -> 90,
    Direction.RIGHT -> 270
  )

  def StateDifferences[G](state1: GameState, state2: GameState): (EntityStorage, EntityStorage, EntityStorage) = {
    val s1 = state1.entityStorage
    val s2 = state2.entityStorage

    val a1 = state1.systems
    val a2 = state2.systems

    // Added
    val newEntities = s2.allEntities.values.toSet -- s1.allEntities.values.toSet
    val addedStorage: EntityStorage = newEntities.foldLeft(EntityStorage.empty)((storage, e) => storage.updated(e))

    // Removed
    val removedEntities = s1.allEntities.values.toSet -- s2.allEntities.values.toSet
    val removedStorage: EntityStorage = removedEntities.foldLeft(EntityStorage.empty)((storage, e) => storage.updated(e))

    // Changed
    val changedStorage: EntityStorage = s2.changedEntities.foldLeft(EntityStorage.empty)((storage, e) => storage.updated(e))

    (addedStorage, removedStorage, changedStorage)
  }
}
