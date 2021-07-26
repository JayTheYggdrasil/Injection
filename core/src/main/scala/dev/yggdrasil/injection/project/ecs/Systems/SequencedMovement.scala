package dev.yggdrasil.injection.project.ecs.Systems

import dev.yggdrasil.injection.framework.ecs.Entity
import dev.yggdrasil.injection.framework.ecs.System.{EntityStorage, GameState, System}
import dev.yggdrasil.injection.project.ecs.Components.{Arrow, Direction, GridEntity, GridPosition, Pushable, Space}
import dev.yggdrasil.injection.project.ecs.Entities
import dev.yggdrasil.injection.project.ecs.Entities.{childOf, neighborOf, parentOf, putGridEntity}
import dev.yggdrasil.injection.util.Looped


// In order to play sequences they are aggregated into a single sequence and this system is added to the active systems
// When none of the entities in the sequence are able to move it removes itself from the active systems

case class SequencedMovement(name: String, stepInterval: Float, sequence: Looped[Int], lastSuccessfulState: Option[Looped[Int]] = None, accumulatedDelta: Float = 0) extends TimedSystem {

  override def apply(delta: Float, gameState: GameState): GameState = {
    val entityStorage = gameState.entityStorage
    val active: Set[System] = gameState.systems

    val systemsWithoutMe: Set[System] = active - this

    // Prepare the fail state if necessary
    lazy val sys: System = copy(accumulatedDelta = accumulatedDelta + delta)
    lazy val failState = gameState.copy(entityStorage, systemsWithoutMe + sys)

    if (!shouldStep) return failState

    val entityId: Int = sequence.get

    // Deactivate if nothing in the sequence is able to move
    if (lastSuccessfulState.nonEmpty && lastSuccessfulState.get == sequence) {
      println(name + ": DEACTIVATING")
      return gameState.copy(entityStorage, systemsWithoutMe)
    }

    // -- Skip conditions
    lazy val skipResult = copy(sequence = sequence.next)(delta, gameState)

    // If the entity doesn't exist skip it
    val entity: Entity = entityStorage.get(entityId)
      .getOrElse(return skipResult)

    // Skip if the entity isn't on a grid, skip it
    entity.getInstance(classOf[GridEntity]).getOrElse(return skipResult)

    // Try to move, skip on failure
    val (newMe, updatedStorage) = tryMove(entity, entityStorage) match {
      case Some(es) =>
        copy(sequence = sequence.next, lastSuccessfulState = Some(sequence), accumulatedDelta = 0) -> es
      case None =>
        return skipResult
    }


    val _active: Set[System] = systemsWithoutMe + newMe


    gameState.copy(updatedStorage, _active)
  }

  def tryMove(entity: Entity, entityStorage: EntityStorage): Option[EntityStorage] =
    if(movable(entity, entityStorage)) Some(move(entity, entityStorage)) else None

  def resolveDirection(direction: Option[Direction], entity: Entity): Direction =
    direction.getOrElse(entity.getInstance(classOf[Direction]).getOrElse(Direction.UP))

  def movable(entity: Entity, entityStorage: EntityStorage, direction: Option[Direction] = None): Boolean = {
    val dir = resolveDirection(direction, entity)

    neighborOf(entity, dir, entityStorage) match {
      case Some(neighbor) => childOf(neighbor, entityStorage) match {
        case Some(ent) => // There's an entity
            ent.getInstance(classOf[Pushable]).nonEmpty && // Is it pushable?
              movable(ent, entityStorage, Some(dir)) // can it be pushed?
        case None => true // There's no entity
      }
      case None => false // There's no space to move into
    }
  }

  def move(entity: Entity, entityStorage: EntityStorage, direction: Option[Direction] = None): EntityStorage = {
    val dir = resolveDirection(direction, entity)

    val neighbor = neighborOf(entity, dir, entityStorage).get
    val newStorage = childOf(neighbor, entityStorage).map(move(_, entityStorage, Some(dir))).getOrElse(entityStorage)

    val parent = parentOf(entity, newStorage).get
    val neighborPos = neighbor(classOf[GridPosition])
    putGridEntity(entity, neighborPos, newStorage) // Move the entity
      .updated(Entities.clear(parent)) // Clear the space it was in
  }
}
