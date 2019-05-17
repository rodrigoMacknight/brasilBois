package com.mack.brasilbois;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mack.brasilbois.model.Card;
import com.mack.brasilbois.model.CreatureCard;
import com.mack.brasilbois.view.MenuScreen;
import com.mack.brasilbois.view.PlayScreen;


import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

//classe que controla o game inteiro.
//aqui se controla a transicao de telas do game e chama o render
//e update das screen que estao ativas
public class BrBoisMain extends Game {

    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;

    public static final String TITLE = "Brasil Bois";
    //NICE TUTS
    //https://libgdx.info/buttons-scene2d/


    public static List<Card> allCards;

    public SpriteBatch batch;

    private Socket socket;
    @Override
        public void create() {

        connectSocket();
        configSocketEvents();
        batch = new SpriteBatch();
        //initialize game cards on memory
        allCards = initializeCards();
        //create the gameStateManager
        setScreen(new PlayScreen(this));


    }
    //config listeners
    private void configSocketEvents() {
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener(){

            @Override
            public void call(Object... args) {
                Gdx.app.log("SocketIO", "Connected");
            }
        }).on("socketId", new Emitter.Listener(){
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];

                String id = null;
                try {
                    id = data.getString("id");
                    Gdx.app.log("socketIO", "my id: " + id );
                } catch (JSONException e) {
                    Gdx.app.log("socketIO", "error getting ID" );
                }

            }
        }).on("newPlayer", new Emitter.Listener(){

            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                Gdx.app.log("SocketIO", "received event socketId");
                String id = null;
                try {
                    id = data.getString("id");
                    Gdx.app.log("socketIO", "new player id: " + id );
                } catch (JSONException e) {
                    Gdx.app.log("socketIO", "error getting ID" );
                }

            }
        });
    }
    //connect to server
    private void connectSocket() {

        try {
            socket = IO.socket("http://localhost:8000");
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private List<Card> initializeCards() {
        List<Card> ret = new ArrayList<Card>();

        Texture coxinhaArt = new Texture("Card_arts/coxinha.png");
        Card coxinha = new CreatureCard("coxinha",2,3,1,
                Card.Faction.AZUL,Card.CardType.CRIATURA, Card.Tribo.GENTE_DE_BEM, coxinhaArt,
        1);
        ret.add(coxinha);

        Texture acreBoyArt = new Texture("Card_arts/meninoDoAcre.png");
        CreatureCard acreBoy =   new CreatureCard("Menino do acre ",4,2,1,
                Card.Faction.MACONARIA,Card.CardType.CRIATURA, Card.Tribo.ILLUMINATI, acreBoyArt,
                1);

        acreBoy.setAbilities(CreatureCard.Ability.STEALTH);
        ret.add(acreBoy);

        Texture veteranoFederal = new Texture("Card_arts/veteranoFederal.png");
        CreatureCard veterano  = new CreatureCard("Veterano da federal ",3,5,1,
                Card.Faction.VERMELHA,Card.CardType.CRIATURA, Card.Tribo.ESTUDANTE, veteranoFederal,
                1);
        ret.add(veterano);
        Texture calouroFederal = new Texture("Card_arts/calouro.png");
        CreatureCard calouro=  new CreatureCard("Calouro da federal ",3,5,1,
                Card.Faction.VERMELHA,Card.CardType.CRIATURA, Card.Tribo.ESTUDANTE, calouroFederal,
                1);

        ret.add(calouro);
        Texture aecio = new Texture("Card_arts/aecio.png");
        CreatureCard aecioCard =  new CreatureCard("Aecio Neves ",10,5,1,
                Card.Faction.AZUL,Card.CardType.CRIATURA, Card.Tribo.ESTUDANTE, aecio,
                1);

        aecioCard.setAbilities(CreatureCard.Ability.BUFF_COCAINE);
        ret.add(aecioCard);


        Texture caveirao = new Texture("Card_arts/caveirao.png");
        CreatureCard caveiraoCard  = new CreatureCard("Caveirao",1,6,1,
                Card.Faction.AZUL,Card.CardType.CRIATURA, Card.Tribo.ESTUDANTE, caveirao,
                1);

        caveiraoCard.setAbilities(CreatureCard.Ability.BUFF_GENTE_DE_BEM);
        ret.add(caveiraoCard);

        return ret;
    }


    @Override
    public void render() {
        super.render();
    }


}
