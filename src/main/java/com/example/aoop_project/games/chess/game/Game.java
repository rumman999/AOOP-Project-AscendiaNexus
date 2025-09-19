package com.example.aoop_project.games.chess.game;

import static com.example.aoop_project.games.chess.game.Game.Status.*;
import static com.example.aoop_project.games.chess.figures.ChessPiece.PieceColor.BLACK;
import static com.example.aoop_project.games.chess.figures.ChessPiece.PieceColor.WHITE;
import com.example.aoop_project.games.chess.figures.*;
import com.example.aoop_project.games.chess.figures.ChessPiece;
import com.example.aoop_project.games.chess.figures.ChessPiece.PieceColor;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Game {

    public enum Status {IN_PLAY, OVER_W, OVER_B, OVER_DRAW}
    private Status status;

    public static SimpleBooleanProperty isRunning = new SimpleBooleanProperty();
    private PieceColor colorInPlay;
    private ChessBoardModel board;

    private SimpleIntegerProperty moveCounterProperty = new SimpleIntegerProperty(0);


    public Game(ChessBoardModel board) {
        isRunning.setValue(true);
        colorInPlay = WHITE;
        this.board = board;
        status = IN_PLAY;
    }

    public static SimpleBooleanProperty isRunningProperty() {
        return isRunning;
    }

    public void initiateMove(ChessPiece figure, int row, int column) {
        if (figure.getPieceColor() == colorInPlay) {
            moveCounterProperty.set(moveCounterProperty.get()+1);
            board.initiateMove(figure, row, column);
            status = getBoardStatus();
            if(status != IN_PLAY) isRunning.setValue(false);
            colorInPlay = (colorInPlay != WHITE)? WHITE: BLACK;
        }
    }

    public Status getBoardStatus(){
        boolean whiteWin = board.isMate(BLACK);
        boolean blackWin = board.isMate(WHITE);
        boolean draw = board.isStaleMate();
        return whiteWin?OVER_W: blackWin?OVER_B:draw?OVER_DRAW:IN_PLAY;
    }


    public PieceColor getColorInPlay() {
        return colorInPlay;
    }
    public void setCurrentPieceColor(PieceColor color) {
        colorInPlay = color;
    }

    public Status getStatus() {
        return status;
    }

    public ChessBoardModel getModel() {
        return board;
    }

    public SimpleIntegerProperty getMoveCounterProperty(){
        return moveCounterProperty;
    }
}