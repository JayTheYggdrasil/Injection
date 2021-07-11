package dev.yggdrasil.injection

import project.ui.LevelScreen
import com.badlogic.gdx.Game

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
class MainGame extends Game {
  override def create() = setScreen(new LevelScreen)
}