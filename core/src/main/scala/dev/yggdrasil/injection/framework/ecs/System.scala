package dev.yggdrasil.injection.framework.ecs

import dev.yggdrasil.injection.framework.ecs.Component.entityToComponentMap


object System {

  case class GameState(entityStorage: EntityStorage, systems: Set[System]) {
    def unpack: (EntityStorage, Set[System]) = (entityStorage, systems)
    def clearChanged(): GameState = copy(entityStorage = entityStorage.clearChanged())
  }

  def stepState(delta: Float, gameState: GameState): GameState =
    gameState.systems.foldLeft(gameState)((state, system) => system(delta, state))


  trait System {
    val name: String

    def apply(delta: Float, gameState: GameState): GameState

    override def equals(obj: Any): Boolean = obj match {
      case s: System => s.name == name
      case _ => false
    }

    override def hashCode(): Int = name.hashCode
  }

  case class EntityStorage(m: Map[Class[_ <: Component], Set[Entity]],
                           all: Option[Map[Int, Entity]] = None,
                           changedEntities: Set[Entity] = Set.empty) {
    val allEntities: Map[Int, Entity] = all.getOrElse(m.values.fold(Set.empty[Entity])(_ ++ _).map(e => e.id -> e).toMap)

    def join(over: Class[_ <: Component]*): Set[Entity] =
      over.foldLeft(allEntities.values.toSet){
        (out, c) => m.get(c) match {
          case Some(entities) => entities.intersect(out)
          case None => Set.empty
        }
      }

    def clearChanged(): EntityStorage = copy(changedEntities = Set.empty)

    def updated(entity: Entity): EntityStorage = {
      def f(v: (Class[_ <: Component], Set[Entity])): (Class[_ <: Component], Set[Entity]) = {
        val (key, value) = v
        key -> (value - entity)
      }

      val _all = allEntities.updated(entity.id, entity)


      val removed = m.map[Class[_ <: Component], Set[Entity]](f)
      val newM = entity.foldLeft(removed){
        (out, cWc) => {
          val (c, component) = cWc
          out.updated(c, (out.getOrElse(c, Set.empty[Entity]) - entity) + entity)
        }
      }
      EntityStorage(newM, Some(_all), changedEntities + entity)
    }

    def apply(id: Int): Entity = allEntities(id)
    def contains(id: Int): Boolean = allEntities.contains(id)
    def get(id: Int): Option[Entity] = allEntities.get(id)
  }

  object EntityStorage {
    val empty: EntityStorage = EntityStorage(Map.empty)
  }
}
