package com.mack.brasilbois.model;

import com.badlogic.gdx.math.Vector2;

public class BattleField {
    Vector2 xy;
    Card.BoardPlace where;
    CreatureCard c;


    public BattleField(Vector2 vector2, Card.BoardPlace firld1) {
        this.xy = vector2;
        where = firld1;
    }


    public Vector2 getXy()   {
        return this.xy;
    }

    public void setCard(CreatureCard card){
        if(card!=null) {
            card.setCurrentPlace(this.getBoardPlace());
            //check if the card has fighted or just returned to owner's hand
            if(card.fighted){
                card.setSick(true);
                card.fighted = false;
            }
        }
        this.c = card;

    }

    public Card.BoardPlace getBoardPlace(){
        return this.where;
    }

    public CreatureCard getCard() {
        return c;
    }
}
