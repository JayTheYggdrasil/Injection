package dev.yggdrasil.injection.framework

import dev.yggdrasil.injection.framework.ecs.{Entity, EntityStorage, GameState}
import dev.yggdrasil.injection.framework.ui.Components.Visual
import dev.yggdrasil.injection.project.ecs.Components.Direction

import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
import scala.reflect.classTag

package object util {

  val dir2deg: Map[Direction, Float] = Map(
    Direction.UP -> 0,
    Direction.DOWN -> 180,
    Direction.LEFT -> 90,
    Direction.RIGHT -> 270
  )

  def StateVisualDifferences[G](state1: GameState, state2: GameState): (Set[Entity], Set[Entity], Set[Entity]) = {
    val s1: EntityStorage = state1.storage
    val s2: EntityStorage = state2.storage

    val a1 = state1.systems
    val a2 = state2.systems

    // Added
    val newEntities = s2.join(classTag[Visual]) -- s1.join(classTag[Visual])

    // Removed
    val removedEntities = s1.join(classTag[Visual]) -- s2.join(classTag[Visual])

    // Changed
    val changedEntities: Set[Entity] = s2.changedEntities intersect s2.join(classTag[Visual])

    (newEntities, removedEntities, changedEntities)
  }

  def saveState(name: String, state: GameState): Unit = {
    val saveLoc: String = System.getProperty("user.home").concat("\\Documents\\My Games\\injection\\" + name)

    val oos = new ObjectOutputStream(new FileOutputStream(saveLoc))
    oos.writeObject(state)
    oos.close()
  }

  def loadState(name: String): GameState = {
    val saveLoc: String = System.getProperty("user.home").concat("\\Documents\\My Games\\injection\\" + name)
    val ois = new ObjectInputStream(new FileInputStream(saveLoc))
    val output = ois.readObject().asInstanceOf[GameState]

    ois.close()

    output
  }
}
