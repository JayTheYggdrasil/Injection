package dev.yggdrasil.injection.framework.ui

import com.badlogic.gdx.{Gdx, Screen}
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.{Actor, Stage}
import com.badlogic.gdx.utils.viewport.Viewport
import com.badlogic.gdx.utils.viewport.StretchViewport
import dev.yggdrasil.injection.framework.ecs.System.{EntityStorage, GameState, stepState}
import dev.yggdrasil.injection.framework.ui.Components.Visual
import dev.yggdrasil.injection.framework.util.StateVisualDifferences

abstract class ECSScreen extends Screen {
  private var _batch: Option[SpriteBatch] = None
  private var _stage: Option[EntityStage] = None
  private var _viewport: Option[Viewport] = None

  protected val actorFactory: ECSActorFactory

  def batch: SpriteBatch = _batch.get
  def stage: EntityStage = _stage.get
  def viewport: Viewport = _viewport.get

  var gameState: GameState = initialState

  override def show() = {
    _batch = Some(new SpriteBatch())
    _stage = Some(new EntityStage())
    _viewport = Some(new StretchViewport(900, 900))

    // Make the initial visuals then add them to the screen's stage.
    actorFactory.makeActors(gameState).foreach(stage.addActor)

    Gdx.input.setInputProcessor(stage)
  }

  def initialState: GameState

  override def render(delta: Float): Unit = {
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

    // Update the game state
    val newState = stepState(delta, gameState.clearChanged())

    // Update Screen
    val (diffAdded, diffRemoved, diffChanged) = StateVisualDifferences(gameState, newState)

    diffAdded.map(actorFactory.makeOne(_, newState)).foreach(stage.addActor(_))
    diffChanged.foreach(e => stage.getActor(e.id).foreach(_.update(newState)))
    diffRemoved.foreach(e => stage.getActor(e.id).foreach(_.remove()))

    stage.act(delta)
    stage.draw()

    // Do the state transition
    gameState = newState
  }
}
