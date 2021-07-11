package dev.yggdrasil.injection.project.ecs.Systems

import dev.yggdrasil.injection.framework.ecs.Entity
import dev.yggdrasil.injection.framework.ecs.System.{EntityStorage, GameState, System}
import dev.yggdrasil.injection.project.ecs.Components.{Arrow, Direction, Grid, GridPosition, Pushable, Space}
import dev.yggdrasil.injection.project.ecs.Entities.updateStorageWithGrid
import dev.yggdrasil.injection.project.ui.Global
import dev.yggdrasil.injection.util.{InfiniteGrid, LoopedVector}


// In order to play sequences they are aggregated into a single sequence and this system is added to the active systems
// When none of the entities in the sequence are able to move it removes itself from the active systems

case class SequencedMovement(name: String, stepInterval: Float, current: Int, sequence: LoopedVector[Int], lastSuccessfulIndex: Int = 0, accumulatedDelta: Float = 0) extends TimedSystem {
  val seqLength: Int = sequence.length

  override def apply(delta: Float, gameState: GameState): GameState = {
    val entityStorage = gameState.entityStorage
    val active: Set[System] = gameState.systems

    val systemsWithoutMe: Set[System] = active - this

    if (!shouldStep) {
      val sys: System = SequencedMovement(name, stepInterval, current, sequence, lastSuccessfulIndex, accumulatedDelta + delta)
      return gameState.copy(entityStorage, systemsWithoutMe + sys)
    }

    // Try do the current entity's action. If that fails, try to do it for the next entity in the sequence
    val entityId: Int = sequence(current)

    val entity: Entity = entityStorage(entityId)

    // Deactivate if nothing is able to move
    if (lastSuccessfulIndex % seqLength == current % seqLength && !movable(entity, entityStorage))
      return gameState.copy(entityStorage, systemsWithoutMe)


    val (newMe, updatedStorage) = tryMove(entity, entityStorage) match {
        // Move is a success
      case Some(es) => SequencedMovement(name, stepInterval, current + 1, sequence, lastSuccessfulIndex = current) -> es
        // Move is a failure
      case None =>
        return SequencedMovement(name, stepInterval, current + 1, sequence, lastSuccessfulIndex, accumulatedDelta)(delta, gameState)
    }


    val _active: Set[System] = systemsWithoutMe + newMe

    // If the entity has a grid, we update it
    val  gridUpdatedStorage = entity.getInstance(classOf[GridPosition]).map(
      gridPos => updateStorageWithGrid(updatedStorage, updatedStorage(gridPos.gridId))
    ).getOrElse(updatedStorage)
    gameState.copy(gridUpdatedStorage, _active)
  }

  def tryMove(entity: Entity, entityStorage: EntityStorage): Option[EntityStorage] =
    if(movable(entity, entityStorage)) Some(move(entity, entityStorage)) else None

  def resolveDirection(direction: Option[Direction], entity: Entity): Direction =
    direction.getOrElse(entity.getInstance(classOf[Direction]).getOrElse(Direction.UP))

  def movable(entity: Entity, entityStorage: EntityStorage, direction: Option[Direction] = None): Boolean = {
    val _gridPosition = entity.getInstance(classOf[GridPosition])
    val _pushable = entity.getInstance(classOf[Pushable])
    val _grid = _gridPosition.map(p => entityStorage(p.gridId)).flatMap(_.getInstance(classOf[Grid]))

    // An entity needs all of these components to be considered movable
    _gridPosition zip _grid zip _pushable match {
      case Some(gridPosition -> grid -> _) => {
        val dir = resolveDirection(direction, entity)

        // Check to see if the current entity is an arrow facing against the direction of motion
        val cantPushArrow = entity.getInstance(classOf[Arrow]) zip entity.getInstance(classOf[Direction]) match {
          case Some((_, direction)) => direction.x + dir.x == 0 && direction.y + dir.y == 0
          case None => false
        }


        // If it is then we can't push
        if(cantPushArrow) return false


        // See if we can move into the neighboring square
        val startingLocation = grid.origin.toLocation(gridPosition.x, gridPosition.y)

        val entityToBePushed = startingLocation.toDirection(dir).value
        entityToBePushed.getInstance(classOf[Space]) match {
          case  Some(_) => true
          case None => movable(entityToBePushed, entityStorage, Some(dir))
        }
      }
      case None => false
    }
  }


  def getStartingLocation(entity: Entity, entityStorage: EntityStorage): InfiniteGrid[Entity] = {
    val gridPosition = entity.getInstance(classOf[GridPosition]).get
    val gridEntity = entityStorage(gridPosition.gridId)
    val grid = gridEntity.getInstance(classOf[Grid]).get
    grid.origin.toLocation(gridPosition.x, gridPosition.y)
  }

  def move(entity: Entity, entityStorage: EntityStorage, direction: Option[Direction] = None): EntityStorage = {

    val startingLocation = getStartingLocation(entity, entityStorage)

    val dir = resolveDirection(direction, entity)

    val movingTo = startingLocation.toDirection(dir)
    val movingToEntity = movingTo.value
    movingToEntity.getInstance(classOf[Space]) match {
      case Some(_) => forceMove(entity, entityStorage, dir)
      case None => {
        val newStorage = forceMove(movingToEntity, entityStorage, dir)
        forceMove(entity, newStorage, dir)
      }
    }
  }

  def forceMove(entity: Entity, entityStorage: EntityStorage, direction: Direction): EntityStorage = {
    // Forces a move in the direction, removing anything that was there
    val gridPosition: GridPosition = entity.getInstance(classOf[GridPosition]).get
    val gridEntity: Entity = entityStorage(gridPosition.gridId)
    val startingLocation: InfiniteGrid[Entity] = getStartingLocation(entity, entityStorage)

    val updatedEntity = entity.updated(GridPosition(gridPosition.x + direction.x, gridPosition.y + direction.y, gridPosition.gridId))
    val updatedInfGrid = startingLocation.clear().toDirection(direction).updated(updatedEntity)
    println(entity.id + " moving from: (" + gridPosition.x + ", " + gridPosition.y + ") to: (" + updatedInfGrid._x + ", " + updatedInfGrid._y + ")")
    entityStorage
      .updated(gridEntity.updated(Grid(updatedInfGrid)))
      .updated(updatedEntity)
  }
}
