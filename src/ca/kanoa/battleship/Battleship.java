package ca.kanoa.battleship;

import ca.kanoa.battleship.network.BaseClient;
import ca.kanoa.battleship.ui.state.*;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class Battleship extends StateBasedGame {

    protected final GameState gameState;
    protected final LobbyState lobbyState;
    protected final MenuState menuState;
    protected final SplashState splashState;
    private BaseClient networkClient;

    public Battleship() {
        super("BATTLESHIP");

        gameState = new GameState(this);
        lobbyState = new LobbyState(this);
        menuState = new MenuState(this);
        splashState = new SplashState(this);
    }

    @Override
    public void initStatesList(GameContainer gc) throws SlickException {
        this.addState(splashState);
        this.addState(gameState);
        this.addState(lobbyState);
        this.addState(menuState);
    }

    public BaseClient getNetwork() {
        return this.networkClient;
    }

    public void setNetwork(BaseClient client) {
        this.networkClient = client;
    }

}