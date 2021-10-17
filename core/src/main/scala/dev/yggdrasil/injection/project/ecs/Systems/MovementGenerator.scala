package dev.yggdrasil.injection.project.ecs.Systems

import dev.yggdrasil.injection.framework.ecs.Systems.DispatcherSystem.EventGenerator
import dev.yggdrasil.injection.framework.ecs.{Entity, EntityStorage, GameState, System}
import dev.yggdrasil.injection.framework.ecs.Systems.{DispatcherSystem, EventSystem}
import dev.yggdrasil.injection.project.ecs.Components.{Arrow, Direction, GridEntity, GridPosition, Pushable, Space}
import dev.yggdrasil.injection.project.ecs.Entities
import dev.yggdrasil.injection.project.ecs.Entities.{childOf, neighborOf, parentOf, putGridEntity}
import dev.yggdrasil.injection.util.{Looped, LoopedList}


// In order to play sequences they are aggregated into a single sequence and this system is added to the active systems
// When none of the entities in the sequence are able to move it removes itself from the active systems

case class MovementGenerator(
                              override val period: Float,
                              sequence: Looped[Int],
                              lastSuccessfulState: Option[Looped[Int]] = None
) extends EventGenerator("MovementGenerator", period) {
  override def dispatchEvent(gameState: GameState): GameState = makeEvent(gameState) match {
    // Event is created without issue
    case Some(event) => GameState(gameState.storage, gameState.systems.updatedByName(successNext.getDispatcher).appended[System](event))

    // Event fails, no sequence to compare to yet, claim a success, but try the next item in the sequence.
    case None if lastSuccessfulState.isEmpty => successNext.dispatchEvent(gameState)

    // Event fails, try on the next item in the sequence
    case None if !lastSuccessfulState.contains(sequence) => failNext.dispatchEvent(gameState)

    // None of the items in the sequence can generate events, so it removes itself
    case None => gameState.storage -> gameState.systems.removedByName(this.getDispatcher)
  }

  def successNext: MovementGenerator = copy(sequence = sequence.next, lastSuccessfulState = Some(sequence))
  def failNext: MovementGenerator = copy(sequence = sequence.next)

  def makeEvent(gameState: GameState): Option[System] = gameState.storage(sequence.get) match {
    case e if e.contains[Arrow] && movable(e, gameState.storage) => Some(MoveArrow("MoveArrow", e.id))
    case _ => None
  }

  case class MoveArrow(override val name: String, entityID: Int) extends EventSystem(name) {
    override def handleEvent(gameState: GameState): GameState =
      move(gameState.storage(entityID), gameState.storage) -> gameState.systems
  }

  def resolveDirection(entity: Entity, direction: Option[Direction] = None): Direction =
    direction.getOrElse(entity.getInstance[Direction].getOrElse(Direction.UP))

  def movable(entity: Entity, entityStorage: EntityStorage, direction: Option[Direction] = None): Boolean = {
    val dir = resolveDirection(entity, direction)

    neighborOf(entity, dir, entityStorage) match {
      case Some(neighbor) => childOf(neighbor, entityStorage) match {
        case Some(ent) => // There's an entity
            ent.getInstance[Pushable].nonEmpty && // Is it pushable?
              movable(ent, entityStorage, Some(dir)) // can it be pushed?
        case None => true // There's no entity
      }
      case None => false // There's no space to move into
    }
  }

  def move(entity: Entity, entityStorage: EntityStorage, direction: Option[Direction] = None): EntityStorage = {
    val dir = resolveDirection(entity, direction)

    val neighbor = neighborOf(entity, dir, entityStorage).get
    val newStorage = childOf(neighbor, entityStorage).map(move(_, entityStorage, Some(dir))).getOrElse(entityStorage)

    val parent = parentOf(entity, newStorage).get
    val neighborPos = neighbor[GridPosition]
    putGridEntity(entity, neighborPos, newStorage) // Move the entity
      .updated(Entities.clear(parent)) // Clear the space it was in
  }
}
