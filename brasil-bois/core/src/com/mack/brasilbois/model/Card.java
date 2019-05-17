package com.mack.brasilbois.model;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mack.brasilbois.Values;
import com.mack.brasilbois.view.PlayScreen;

import static com.mack.brasilbois.model.Card.BoardPlace.GRIMORIO;
import static com.mack.brasilbois.model.Card.BoardPlace.HAND;

public abstract class Card {


    public abstract Card getCopy();
    public enum Faction {
        VERMELHA, //esquerdistas
        AZUL, //direitas
        MACONARIA, //macon
        BANDIDAGEM //bandidagem
    }

    public enum CardType {
        CRIATURA,
        ENCANTAMENTO,
        ARTEFEATO,
        MAGIA;


    }

    public enum Tribo {

        ESTUDANTE,

        GENTE_DE_BEM,

        ILLUMINATI,

        POLITICO,

        MILITAR


    }

    public enum BoardPlace {
        HAND,
        FIELD_1,
        FIELD_2,
        FIELD_3,
        FIELD_4,
        FIELD_5,
        FIELD_6,
        ENEMY_FIELD_1,
        ENEMY_FIELD_2,
        ENEMY_FIELD_3,
        ENEMY_FIELD_4,
        ENEMY_FIELD_5,
        ENEMY_FIELD_6,
        GRIMORIO


    }

    Player owner;


    protected int photo;
    protected Faction faction;
    protected CardType cardType;
    protected Tribo org;
    protected String name;
    protected Texture cardArt;
    private Sprite cardSprite;

    private BoardPlace currentPlace;
    protected int manaCost;


    public BoardPlace getCurrentPlace() {
        return this.currentPlace;
    }

    public void setCurrentPlace(BoardPlace bp) {
        this.currentPlace = bp;
    }


    float xPos;
    float yPos;



    public Sprite getCardSprite() {
        return this.cardSprite;
    }


    public Texture getCardArt() {
        return cardArt;
    }

    public void setCardArt(Texture cardArt) {
        this.cardArt = cardArt;
    }

    public float getxPos() {
        return xPos;
    }

    public void setxPos(float xPos) {
        this.xPos = xPos;
    }

    public float getyPos() {
        return yPos;
    }

    public void setyPos(float yPos) {
        this.yPos = yPos;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getPhoto() {
        return photo;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }


    public Faction getFaction() {
        return faction;
    }

    public void setFaction(Faction faction) {
        this.faction = faction;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public Tribo getOrg() {
        return org;
    }

    public void setOrg(Tribo org) {
        this.org = org;
    }

    public Card(String name, int photo, Faction faction, CardType cardType, Tribo org, Texture cardArt, int manaCost) {



        this.photo = photo;
        this.faction = faction;
        this.cardType = cardType;
        this.org = org;
        this.name = name;
        this.cardArt = cardArt;
        this.xPos = 0;
        this.yPos = 0;
        this.cardSprite = new Sprite(cardArt, 200, 300, Values.CARD_SIZE_X, Values.CARD_SIZE_Y);
        this.currentPlace = GRIMORIO;
        this.manaCost = manaCost;

    }

    //checa se a carta foi clicada pelo mouse, ou dedo
    public boolean isClicked(int screenX, int screenY) {


        //System.out.println("mouse.screenX = " + screenX);
        //System.out.println("c.getxPos() = " + this.getxPos());
        //System.out.println("c.cardEnd = " + (this.getxPos() + cardSprite.getWidth()));
        //System.out.println("mouse.screenY = " + ipsolon);
        // System.out.println("c.getyPos() = " +  this.getyPos() );
        // System.out.println("c.cardtop = " +  this.getyPos()+cardSprite.getHeight());


        //checks if mouse pressed this card.
        if (screenX > this.getxPos() &&
                screenX < this.getxPos() + cardSprite.getWidth()) {


            //esta ok no eixo x
            if (screenY > this.getyPos() && screenY < this.getyPos() + cardSprite.getHeight()) {
                // System.out.println("cardPicked: " + this.getName() );
                return true;
            }
        }
        return false;
    }


    public int getManaCost() {
        return manaCost;
    }

    public void drawWithMana(SpriteBatch batch) {
        drawWithoutMana(batch);
        //draw mana cost
        batch.draw(PlayScreen.manaCost, this.getxPos() + Values.CARD_MANACOST_X, this.getyPos() + Values.CARD_MANACOST_Y);
        PlayScreen.cardFont.draw(batch, this.getManaCost() + "", this.getxPos() + Values.CARD_MANACOST_X + 5, this.getyPos() + Values.CARD_MANACOST_Y + 25);

    }



    public void drawWithoutMana(SpriteBatch batch) {
        batch.draw(PlayScreen.cardBg, this.getxPos(), this.getyPos(), Values.CARD_SIZE_X, Values.CARD_SIZE_Y);
        batch.draw(this.getCardArt(), this.getxPos() + Values.THUMBNAIL_OFFSET_X, this.getyPos() + Values.THUMBNAIL_OFFSET_Y, Values.THUMBNAIL_SIZE_X, Values.THUMBNAIL_SIZE_Y);
        //draw card description
        PlayScreen.descFont.draw(batch, this.name, this.getxPos() + Values.CARD_DESCRIPTION_X, this.getyPos() + Values.CARD_DESCRIPTION_Y);

    }


    public void returnToLastPosition() {
        switch (currentPlace) {
            case FIELD_1:
                PlayScreen.getCreatureHolders().get(0).setCard((CreatureCard) this);
                this.setxPos(Values.FIELD_CREATURE_ONE_X - Values.CARD_SIZE_X / 2);
                this.setyPos(Values.FIELD_CREATURE_ONE_Y - Values.CARD_SIZE_Y / 2);
                break;
            case FIELD_2:
                this.setxPos(Values.FIELD_CREATURE_TWO_X - Values.CARD_SIZE_X / 2);
                this.setyPos(Values.FIELD_CREATURE_TWO_Y - Values.CARD_SIZE_Y / 2);
                PlayScreen.getCreatureHolders().get(1).setCard((CreatureCard)this);
                break;
            case FIELD_3:
                this.setxPos(Values.FIELD_CREATURE_THREE_X - Values.CARD_SIZE_X / 2);
                this.setyPos(Values.FIELD_CREATURE_THREE_Y - Values.CARD_SIZE_Y / 2);
                PlayScreen.getCreatureHolders().get(2).setCard((CreatureCard)this);
                break;
            case FIELD_4:
                this.setxPos(Values.FIELD_CREATURE_FOUR_X - Values.CARD_SIZE_X / 2);
                this.setyPos(Values.FIELD_CREATURE_FOUR_Y - Values.CARD_SIZE_Y / 2);
                PlayScreen.getCreatureHolders().get(3).setCard((CreatureCard)this);
                break;
            case FIELD_5:
                this.setxPos(Values.FIELD_CREATURE_FIVE_X - Values.CARD_SIZE_X / 2);
                this.setyPos(Values.FIELD_CREATURE_FIVE_Y - Values.CARD_SIZE_Y / 2);
                PlayScreen.getCreatureHolders().get(4).setCard((CreatureCard)this);
                break;
            case FIELD_6:
                this.setxPos(Values.FIELD_CREATURE_SIX_X - Values.CARD_SIZE_X / 2);
                this.setyPos(Values.FIELD_CREATURE_SIX_Y - Values.CARD_SIZE_Y / 2);
                PlayScreen.getCreatureHolders().get(5).setCard((CreatureCard)this);
                break;
            case HAND:
                owner.getHand().add(this);
                this.setCurrentPlace(HAND);
                break;

        }
    }

    public void setOwner(Player player) {
        this.owner = player;
    }


}
