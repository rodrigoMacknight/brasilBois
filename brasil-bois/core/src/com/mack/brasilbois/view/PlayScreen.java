package com.mack.brasilbois.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.mack.brasilbois.BrBoisMain;
import com.mack.brasilbois.Tests;
import com.mack.brasilbois.Values;
import com.mack.brasilbois.model.BattleField;
import com.mack.brasilbois.model.Card;
import com.mack.brasilbois.model.CreatureCard;
import com.mack.brasilbois.model.Player;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;



public class PlayScreen implements Screen, InputProcessor {
    //in order to access the game.batch
    private BrBoisMain game;
    //loading comons textures
    private Texture backGround;
    //reference to the layout of every card
    public static Texture cardBg;
    public static Texture mana;
    public static Texture emptyMana;
    public static Texture cardBack;
    public static Texture manaCost;
    public static Texture atkHolder;
    public static Texture cocaine;
    public static Texture cristo;

    public static Vector2 playerHPPos;
    public static Vector2 enemyHPPos;


    private Socket socket;


    Player me;
    Player enemy;

    int turno;

    public static BitmapFont boardFont;
    public static BitmapFont cardFont;
    public static BitmapFont descFont;

    private static List<BattleField> creatureHolders;


    private static List<BattleField> enemyCreatureHolders;


    private Card current; //holds the card to be d'n'd


    private void configSocketForPlay() {
          connectPlaySocket();
          configSocketListeners();
    }

    private void configSocketListeners() {
        socket.on("enemyCardPlaced", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                String data =  (String) args[0];

                try {
                    JSONObject json = new JSONObject(data);
                    Gdx.app.log("SocketIO", "CardPlaced");
                    Gdx.app.log("cardName & pos: ", data);
                    printJson(json);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private void printJson(JSONObject json) {
        Iterator<String> keys = json.keys();

        while(keys.hasNext()) {
            String key = keys.next();
            Gdx.app.log("key: ", key);
            try {
                Gdx.app.log("value: ",  json.get(key).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void connectPlaySocket(){
        try {
            socket = IO.socket("http://localhost:8080");
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    //constructor
    public PlayScreen(BrBoisMain game) {
        this.game = game;
        configSocketForPlay();

        //loading comum textures for the game
        backGround = new Texture("Layout/NewLayout.png");
        cardBg = new Texture("Layout/newCard.png");
        mana = new Texture("Layout/mana.png");
        emptyMana = new Texture("Layout/emptyMana.png");
        cardBack = new Texture("Layout/cardback.png");
        manaCost = new Texture("Layout/manaCost.png");
        atkHolder = new Texture ("Layout/atkHolder.png");
        cocaine = new Texture("Layout/cocaine.png");
        cristo= new Texture("Layout/cristo.png");

        //creating the fonts
        boardFont = new BitmapFont(Gdx.files.internal("Fonts/teste.fnt"));
        cardFont = new BitmapFont(Gdx.files.internal("Fonts/cardFontHolder.fnt"));
        descFont = new BitmapFont(Gdx.files.internal("Fonts/description.fnt"));

        //setting that this class is listening for input
        Gdx.input.setInputProcessor(this);

        //generating placeholder deck
        me = new Player(Tests.getTestDeck(20, false));
        enemy = new Player(Tests.getTestDeck(20, true));

        //set the player that owns that card for all the cards
        enemy.setOwner();
        me.setOwner();
        //initilize player hand
        me.grabCard(5);
        enemy.grabCard(6);
        me.startTurn();

        //loads hpMaths
        playerHPPos = new Vector2(Values.PLAYER_HP_X, Values.PLAYER_HP_X);
        enemyHPPos = new Vector2(Values.ENEMY_HP_X, Values.ENEMY_HP_Y);

        //create the battlefields for the creatures
        creatureHolders = createCreatureHolders();
        enemyCreatureHolders = createEnemyCreatureHolders();

    }


    @Override
    public void show() {

        current = null;

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        game.batch.begin();
        game.batch.draw(backGround, 0, 0);

        me.drawGrimorio(game.batch);
        me.drawHand(game.batch);


        enemy.drawEnemyHand(game.batch);
        enemy.drawGrimorio(game.batch);

        //creatureHolders.printAssitent();
        //draw cards and creatures on the table
        drawBoardPlace();
        drawBattlefieldAssistant();
        drawMana();
        //drawing the hp
        boardFont.draw(game.batch, me.getHp(), Values.PLAYER_HP_X, Values.PLAYER_HP_Y);
        boardFont.draw(game.batch, enemy.getHp(), Values.ENEMY_HP_X, Values.ENEMY_HP_Y);
        //drawing the qtty of cards still in the deck
        boardFont.draw(game.batch, me.getDeck().size() + "", Values.FRIENDLY_CARD_COUNTER_X, Values.FRIENDLY_CARD_COUNTER_Y);
        boardFont.draw(game.batch, enemy.getDeck().size() + "", Values.ENEMY_CARD_COUNTER_X, Values.ENEMY_CARD_COUNTER_Y);

        if (current != null) {
        //   game.batch.draw(new Texture("coxinha.jpg"), creatureHolders.get(0).getXy().x, creatureHolders.get(0).getXy().y);
            current.drawWithMana(game.batch);


    }


        game.batch.end();

}

    private void drawMana() {

        int manaOffset = 20;

        for (int i = 0; i < me.getCurrentMana(); i++) {
            if (i < 5) {
                game.batch.draw(mana, Values.PLAYER_MANA_X + (manaOffset * i), Values.PLAYER_MANA_Y);
            } else {
                game.batch.draw(mana, Values.PLAYER_MANA_X + (manaOffset * (i - 5)), Values.PLAYER_MANA_Y - manaOffset);
            }
        }
        for (int i = me.getCurrentMana(); i < me.getTotalMana(); i++) {
            if (i < 5) {
                game.batch.draw(emptyMana, Values.PLAYER_MANA_X + manaOffset * i, Values.PLAYER_MANA_Y);
            } else {
                game.batch.draw(emptyMana, Values.PLAYER_MANA_X + (manaOffset * (i - 5)), Values.PLAYER_MANA_Y - manaOffset);
            }

        }

        for (int i = 0; i < enemy.getCurrentMana(); i++) {
            if (i < 5) {
                game.batch.draw(mana, Values.ENEMY_MANA_X + (manaOffset * i), Values.ENEMY_MANA_Y);
            } else {
                game.batch.draw(mana, Values.ENEMY_MANA_X + (manaOffset * (i - 5)), Values.ENEMY_MANA_Y - manaOffset);
            }
        }
        for (int i = enemy.getCurrentMana(); i < enemy.getTotalMana(); i++) {
            if (i < 5) {
                game.batch.draw(emptyMana, Values.ENEMY_MANA_X + manaOffset * i, Values.ENEMY_MANA_Y);
            } else {
                game.batch.draw(emptyMana, Values.ENEMY_MANA_X + (manaOffset * (i - 5)), Values.ENEMY_MANA_Y - manaOffset);
            }

        }


    }

    private void drawBattlefieldAssistant() {
        for (BattleField creatureHolder : creatureHolders) {
            Texture x = new Texture("Card_arts/coxinha.png");
            game.batch.draw(x, creatureHolder.getXy().x, creatureHolder.getXy().y, 5, 5);
            x.dispose();
        }
        for (BattleField creatureHolder : enemyCreatureHolders) {
            Texture x = new Texture("Card_arts/coxinha.png");
            game.batch.draw(x, creatureHolder.getXy().x, creatureHolder.getXy().y, 5, 5);
            x.dispose();
        }
    }

    private void drawBoardPlace() {
        for (BattleField battleField : creatureHolders) {
            Card c = battleField.getCard();
            if (c != null) {
                c.drawWithoutMana(game.batch);
               // game.batch.draw(PlayScreen.cardBg, c.getxPos(), c.getyPos(), Values.CARD_SIZE_X, Values.CARD_SIZE_Y);
                //game.batch.draw(c.getCardArt(), c.getxPos() + Values.THUMBNAIL_OFFSET_X, c.getyPos() + Values.THUMBNAIL_OFFSET_Y, Values.THUMBNAIL_SIZE_X, Values.THUMBNAIL_SIZE_Y);


        }
        }
        for (BattleField battleField : enemyCreatureHolders) {
            Card c = battleField.getCard();
            if (c != null) {
                //game.batch.draw(PlayScreen.cardBg, c.getxPos(), c.getyPos(), Values.CARD_SIZE_X, Values.CARD_SIZE_Y);
                //game.batch.draw(c.getCardArt(), c.getxPos() + Values.THUMBNAIL_OFFSET_X, c.getyPos() + Values.THUMBNAIL_OFFSET_Y, Values.THUMBNAIL_SIZE_X, Values.THUMBNAIL_SIZE_Y);
                c.drawWithoutMana(game.batch);
            }
        }
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        //checks the friendly grimorio for input
        //getGrimorioInput(screenX, screenY);
        //checks the friendly hand for input
        int ypsolon = (Gdx.graphics.getHeight() - screenY);

        if (me.isPlaying()) {

            checkHandInput(screenX, ypsolon);
            checkBattlefieldInput(screenX, ypsolon);
        } else {//enemy Playing
            checkEnemyHandInput(screenX, ypsolon);
        }
        checkPassTurn(screenX, ypsolon);
        return false;
    }

    private void checkBattlefieldInput(int screenX, int ypsolon) {

        for (BattleField creatureHolder : creatureHolders) {
            //if the field has a card
            if (creatureHolder.getCard() != null) {

                float x = creatureHolder.getXy().x;
                    float y = creatureHolder.getXy().y;
                    //if player clicks on the card on that battlefield
                    if (screenX > (x - (Values.CARD_SIZE_X / 2)) && screenX < x + (Values.CARD_SIZE_X / 2)) {
                        if (ypsolon > y - (Values.CARD_SIZE_Y / 2) && ypsolon < y + (Values.CARD_SIZE_Y / 2)) {
                            //player got the card on that field
                            current = creatureHolder.getCard();
                     //       System.out.println("card <" + current.getName() + "> removed from " + current.getCurrentPlace());
                            creatureHolder.setCard(null);
                            break;
                        }
                }

            }
        }
    }


    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {


        if (current != null) {
            current.setxPos(screenX - current.getCardSprite().getWidth() / 2);
            current.setyPos(Gdx.graphics.getHeight() - (screenY + current.getCardSprite().getHeight() / 2));
        }


        return false;
    }


    @Override
    public boolean
    touchUp(int screenX, int screenY, int pointer, int button) {

        int ipsolon = Gdx.graphics.getHeight() - screenY;
        //if the card was not dropped in any valid card position return it to the grimorio.
        System.out.println("x: " + screenX);
        System.out.println("y: " + ipsolon);

        if (me.isPlaying()) {
            checkUserInput(screenX, ipsolon);


        } else {//enemy Playing
            checkEnemyInput(screenX, ipsolon);
        }


        return false;
    }

    private void checkUserInput(int screenX, int ipsolon) {

        if (current != null) {

            boolean wasPlaced = false;
            //where the card was
        //    System.out.println("card " + current.getName() +  " is on " + current.getCurrentPlace());

            switch (current.getCurrentPlace()) {
                //if the card was in the hand and is a  creature type
                //the user can place her in any battlefield
                case HAND:

                    Vector2 mouse = new Vector2(screenX, ipsolon);
                    for (BattleField b : creatureHolders) {

                        //if the user tryed to place a card

                        if (mouse.dst(b.getXy()) < 65) {
                            //se nao tinha carta antes
                            if (b.getCard() == null){
                                wasPlaced = placeCard(b);
                                break;
                            }
                        }
                    }
                    if (!wasPlaced) {
                        current.returnToLastPosition();
                     //   System.out.println("card <" + current.getName() + ">" + "was returned to last position");
                    }
                    break;
                case FIELD_1:
                case FIELD_2:
                case FIELD_3:
                case FIELD_4:
                case FIELD_5:
                case FIELD_6:
                    //the card was taken from a friendly battlefield

                    boolean survived = checkInteraction(screenX, ipsolon);
                    if (survived) {
                        current.returnToLastPosition();
                    }
                    break;
            }

            current = null;
        }
    }

    private boolean placeCard( BattleField b) {
        if(current instanceof CreatureCard) {
            CreatureCard currentCreature = (CreatureCard) current;
            //check if card mana cost higher than my mana cost
            if (me.getCurrentMana() >= current.getManaCost()) {

                b.setCard(currentCreature);

                if(currentCreature.hasDeployAction()){
                        currentCreature.doDeployAction(creatureHolders);
                }
                current.setCurrentPlace(b.getBoardPlace());

                //places the texture on top of that battlefield
                current.setxPos(b.getXy().x - (Values.CARD_SIZE_X / 2));
                current.setyPos(b.getXy().y - (Values.CARD_SIZE_Y / 2));
                //subtract mana from the uesr
                me.useMana(current.getManaCost());
                //seta enjoo de criatura
                currentCreature.setSick(true);

                sendPlaceCardToServer(b, currentCreature);

                return true;

            } else {
                return false;
            }
        }else{
            return false;
        }
    }

    private void sendPlaceCardToServer(BattleField b, CreatureCard currentCreature) {
        String position = b.getBoardPlace().name();
        String cardId = currentCreature.getName();
        String cardAndPosition = "{ \" position:\"" +  position + "\""+ "," + "\"cardName\":"  + cardId + "\"}";

        try {
            String jsonString = new JSONObject()
                    .put("position", position)
                    .put("cardName", cardId)
                   .toString();
            socket.emit("placeCard", jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    private void checkEnemyInput(int screenX, int ipsolon) {

        if (current != null) {
            boolean wasPlaced = false;
            //where the card was
            CreatureCard creature = (CreatureCard) current;
            switch (current.getCurrentPlace()) {
                case HAND:
                    Vector2 mouse = new Vector2(screenX, ipsolon);
                    for (BattleField b : enemyCreatureHolders) {

                        //if the user tryed to place a card
                        if (mouse.dst(b.getXy()) < 40) {

                            if (b.getCard() == null) {
                                if(enemy.getCurrentMana()>=current.getManaCost()) {
                                    wasPlaced = true;
                                    b.setCard(creature);
                                    System.out.println("card <" + current.getName() + "> was placed on " + b.getBoardPlace());
                                    current.setCurrentPlace(b.getBoardPlace());
                                    current.setxPos(b.getXy().x - (Values.CARD_SIZE_X / 2));
                                    current.setyPos(b.getXy().y - (Values.CARD_SIZE_Y / 2));
                                    enemy.useMana(current.getManaCost());
                                }
                            }
                        }
                    }
                    if (!wasPlaced) {
                        current.returnToLastPosition();

                    }
                    break;
                case ENEMY_FIELD_1:
                case ENEMY_FIELD_2:
                case ENEMY_FIELD_3:
                case ENEMY_FIELD_4:
                case ENEMY_FIELD_5:
                case ENEMY_FIELD_6:
                    //the card was taken from a friendly battlefield
                    boolean survived = checkInteraction(screenX, ipsolon);
                    if (survived) {
                        current.returnToLastPosition();
                    }else{
                        //TODO:KILL ANIMATION
                        current = null;
                    }
                    break;
            }

            current = null;
        }
    }


    //the friendly creature was just removed from a battlefield
    //must see where the user wants to place her and returns a boolean telling
    //if the card should return to its original position
    private boolean checkInteraction(int screenX, int ipsolon) {

        CreatureCard creature = (CreatureCard) current;

        if(creature.isSick()){
            //the card cant attack just return her
            return true;
        }

        Vector2 mouse = new Vector2(screenX, ipsolon);

        if(mouse.dst(PlayScreen.enemyHPPos)<60){
            //attacked the player
            enemy.damage(creature.getAtkTotal());
            creature.setTargetable(true);
            creature.fighted = true;
            return  true;
        }

        for (BattleField b : enemyCreatureHolders) {

            //if the user tryed to place a card
            if (mouse.dst(b.getXy()) < 60) {

                //battle!
                if (b.getCard() != null && b.getCard().isTargetable()) {
                    creature.damage(b.getCard());
                    creature.fighted = true;
                    //the card attacked died, kill her
                    if(b.getCard().getHealth()<=0){
                        System.out.println("card "+ b.getCard().getName() +  " died");

                        b.setCard(null);
                    }

                    //morreu
                    if(creature.getHealth()<=0){
                        System.out.println("card "+ current.getName() +  " died");


                        return false;
                    }else{
                        //it survived the battle
                        return true;
                    }
                }
            }
        }
    //nothing happened, survived
    return true;


    }





    private void checkHandInput(int screenX, int screenY) {
        boolean handGrabbed = false;

        cardGrabbed:
        for (int i = me.getHand().size()-1; i>=0;i--){
            Card c = me.getHand().get(i);
            if (c.isClicked(screenX, screenY)) {
                if (current == null) {
                    current = c;

                    handGrabbed = true;

                }
                break cardGrabbed;
            }

        }
        if (handGrabbed) {
            //tira carta da mão
            me.getHand().remove(current);
        }
    }

    private void checkEnemyHandInput(int screenX, int screenY) {
        boolean handGrabbed = false;
        for (Card c : enemy.getHand()) {
            if (c.isClicked(screenX, screenY)) {
                if (current == null) {
                    current = c;

                    handGrabbed = true;

                }
                break;
            }

        }
        if (handGrabbed) {
            //tira carta da mão
            enemy.getHand().remove(current);
        }
    }


    private void checkPassTurn(int x, int y) {
        if (x > Values.PASS_TURN_LEFT_X && x < Values.PASS_TURN_RIGHT_X) {
            if (y > Values.PASS_TURN_BOTTON_Y && y < Values.PASS_TURN_UPPER_Y) {

                if (me.isPlaying()) {
                    me.setPlaying(false);
                    enemy.startTurn();
                    unsickEnemyBattleFields();
                } else {
                    enemy.setPlaying(false);
                    me.startTurn();
                    unsickBattleFields();
                }
            }
        }

    }

    private void unsickBattleFields() {
        for (BattleField creatureHolder : creatureHolders) {
            if(creatureHolder.getCard()!=null ){
                creatureHolder.getCard().setSick(false);
            }
        }
    }

    private void unsickEnemyBattleFields() {
        for (BattleField creatureHolder : enemyCreatureHolders) {
            if(creatureHolder.getCard()!=null ){
                creatureHolder.getCard().setSick(false);
            }
        }
    }


    public static List<BattleField> getCreatureHolders() {
        return creatureHolders;
    }


    private List<BattleField> createCreatureHolders() {
        List<BattleField> ret = new ArrayList<BattleField>();
        BattleField one = new BattleField(new Vector2(Values.FIELD_CREATURE_ONE_X, Values.FIELD_CREATURE_ONE_Y),
                Card.BoardPlace.FIELD_1);
        ret.add(one);
        BattleField two = new BattleField(new Vector2(Values.FIELD_CREATURE_TWO_X, Values.FIELD_CREATURE_TWO_Y),
                Card.BoardPlace.FIELD_2);
        ret.add(two);
        BattleField three = new BattleField(new Vector2(Values.FIELD_CREATURE_THREE_X, Values.FIELD_CREATURE_THREE_Y),
                Card.BoardPlace.FIELD_3);
        ret.add(three);
        BattleField four = new BattleField(new Vector2(Values.FIELD_CREATURE_FOUR_X, Values.FIELD_CREATURE_FOUR_Y),
                Card.BoardPlace.FIELD_4);
        ret.add(four);
        BattleField five = new BattleField(new Vector2(Values.FIELD_CREATURE_FIVE_X, Values.FIELD_CREATURE_FIVE_Y),
                Card.BoardPlace.FIELD_5);
        ret.add(five);
        BattleField six = new BattleField(new Vector2(Values.FIELD_CREATURE_SIX_X, Values.FIELD_CREATURE_SIX_Y),
                Card.BoardPlace.FIELD_6);
        ret.add(six);

        return ret;
    }

    private List<BattleField> createEnemyCreatureHolders() {
        List<BattleField> ret = new ArrayList<BattleField>();

        BattleField one = new BattleField(new Vector2(Values.FIELD_ENEMY_CREATURE_ONE_X, Values.FIELD_ENEMY_CREATURE_ONE_Y),
                Card.BoardPlace.ENEMY_FIELD_1);
        ret.add(one);

        BattleField two = new BattleField(new Vector2(Values.FIELD_ENEMY_CREATURE_TWO_X, Values.FIELD_ENEMY_CREATURE_TWO_Y),
                Card.BoardPlace.ENEMY_FIELD_2);
        ret.add(two);

        BattleField three = new BattleField(new Vector2(Values.FIELD_ENEMY_CREATURE_THREE_X, Values.FIELD_ENEMY_CREATURE_THREE_Y),
                Card.BoardPlace.ENEMY_FIELD_3);
        ret.add(three);

        BattleField four = new BattleField(new Vector2(Values.FIELD_ENEMY_CREATURE_FOUR_X, Values.FIELD_ENEMY_CREATURE_FOUR_Y),
                Card.BoardPlace.ENEMY_FIELD_4);
        ret.add(four);

        BattleField five = new BattleField(new Vector2(Values.FIELD_ENEMY_CREATURE_FIVE_X, Values.FIELD_ENEMY_CREATURE_FIVE_Y),
                Card.BoardPlace.ENEMY_FIELD_5);
        ret.add(five);


        BattleField six = new BattleField(new Vector2(Values.FIELD_ENEMY_CREATURE_SIX_X, Values.FIELD_ENEMY_CREATURE_SIX_Y),
                Card.BoardPlace.ENEMY_FIELD_6);
        ret.add(six);


        return ret;
    }




    //bellow NOTHING REALLY MATTERS


    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }


    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }


    @Override
    public boolean mouseMoved(int screenX, int screenY) {

        return false;
    }


    @Override
    public boolean scrolled(int amount) {
        return false;
    }


    @Override
    public void dispose() {
        backGround.dispose();
        cardBg.dispose();
        mana.dispose();
        emptyMana.dispose();
        cardBack.dispose();
        boardFont.dispose();
        manaCost.dispose();
        atkHolder.dispose();
        boardFont.dispose();
        cardFont.dispose();

    }
}
