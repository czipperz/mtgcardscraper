package edu.rosehulman.csse333.magicthemetabase.cardscraper;

import io.magicthegathering.javasdk.api.SetAPI;
import io.magicthegathering.javasdk.resource.Card;
import io.magicthegathering.javasdk.resource.MtgSet;

public class Main {
    public static void main(String[] args) {
        MtgSet m19 = SetAPI.getSet("M19");
        for (Card card : m19.getCards()) {
            createCard(card);
            createCardColor(card);
            createCardSet(card);
            createCardType(card);
            createCardSubtype(card);
        }
    }

    private static void createCard(Card card) {
        System.out.print("INSERT INTO Card (Name, ManaCost, Description, Power, Toughness) VALUES (");
        printEscapedString(card.getName());
        System.out.print(", ");
        printEscapedString(card.getManaCost());
        System.out.print(", ");
        printEscapedString(card.getText());
        System.out.print(", ");
        printEscapedString(card.getPower());
        System.out.print(", ");
        printEscapedString(card.getToughness());
        System.out.println(");");
    }

    private static void createCardColor(Card card) {
        for (String color : card.getColors()) {
            System.out.print("SELECT @id = ID FROM Card WHERE Card.Name = ");
            printEscapedString(card.getName());
            System.out.print("; ");
            System.out.print("INSERT INTO CardColor (Card, Color) VALUES (");
            System.out.print("@id");
            System.out.print(", ");
            System.out.print(stringifyColor(color));
            System.out.println(");");
        }
    }

    private static void createCardSet(Card card) {
        System.out.print("SELECT @id = ID FROM Card WHERE Card.Name = ");
        printEscapedString(card.getName());
        System.out.print("; ");
        System.out.print("INSERT INTO CardSet (Card, MultiverseID, SetID, ImageURL) VALUES (");
        System.out.print("@id");
        System.out.print(", ");
        System.out.print(card.getMultiverseid());
        System.out.print(", ");
        printEscapedString(card.getSet());
        System.out.print(", ");
        printEscapedString(card.getImageUrl());
        System.out.println(");");
    }

    private static void createCardType(Card card) {
        for (String type : card.getTypes()) {
            createCardType(card, type);
        }
        for (String type : card.getSupertypes()) {
            createCardType(card, type);
        }
    }

    private static void createCardType(Card card, String type) {
        System.out.print("SELECT @id = ID FROM Card WHERE Card.Name = ");
        printEscapedString(card.getName());
        System.out.print("; ");
        System.out.print("INSERT INTO CardType (Card, Type) VALUES (");
        System.out.print("@id");
        System.out.print(", ");
        printEscapedString(type);
        System.out.println(");");
    }

    private static void createCardSubtype(Card card) {
        System.out.println();
        System.out.print("--");
        System.out.print(card.getName());
        System.out.print(": ");
        System.out.println(card.getType());
        for (String type : card.getSubtypes()) {
            System.out.print("SELECT @id = ID FROM Card WHERE Card.Name = ");
            printEscapedString(card.getName());
            System.out.print("; ");
            System.out.print("INSERT INTO CardSubtype (Card, Subtype) VALUES (");
            System.out.print("@id");
            System.out.print(", ");
            printEscapedString(type);
            System.out.println(");");
        }
    }

    private static String stringifyColor(String color) {
        switch (color) {
            case "Black":
                return "'B'";
            case "Blue":
                return "'U'";
            case "Red":
                return "'R'";
            case "Green":
                return "'G'";
            case "White":
                return "'W'";
            default:
                throw new IllegalArgumentException(color);
        }
    }

    private static void printEscapedString(String s) {
        if (s == null) {
            System.out.print("null");
        } else {
            System.out.print("'");
            s.chars().forEachOrdered(Main::printEscapedChar);
            System.out.print("'");
        }
    }

    private static void printEscapedChar(int c) {
        if (c == '\'') {
            System.out.print("''");
        } else {
            System.out.printf("%c", c);
        }
    }
}
