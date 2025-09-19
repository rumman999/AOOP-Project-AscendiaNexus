package com.example.aoop_project.games.chess.figures;

import java.util.ArrayList;
import com.example.aoop_project.games.chess.game.ChessBoardModel;

import static com.example.aoop_project.games.chess.figures.ChessPiece.PieceColor.BLACK;
import static com.example.aoop_project.games.chess.figures.ChessPiece.PieceColor.UNDEFINED;

public class Pawn extends ChessPiece {
    private final int BLACK_STARTING_COLUMN = 1;
    private final int WHITE_STARTING_COLUMN = 6;
    public Pawn(PieceColor color, int row, int column ) {
        super(color, row, column);
    }

    @Override
    public boolean isPawn(){
        return true;
    }

    @Override
    public boolean canMoveTo(int newRow, int newColumn) {
        return singleAdvance(newRow,newColumn) ||
                initialDoubleAdvance(newRow,newColumn) ||
                diagonalAdvance(newRow,newColumn)?
                true:false;
    }
    @Override
    public ArrayList<Sequence> getAccessibleSequences(ChessBoardModel board) {
        clearAllowedSequences();
        int forwardOffset = advanceRow() - row;
        if (canAdvance(advanceRow(), column, board)){
            addToAllowedSequences("UP_ONE");
            if (canAdvance(row + 2 *forwardOffset, column, board)) addToAllowedSequences("UP_TWO");
        }
        if (canDiagonalAdvance(advanceRow(), column + forwardOffset, board)) addToAllowedSequences("UP_LEFT");
        if (canDiagonalAdvance(advanceRow(), column - forwardOffset, board)) addToAllowedSequences("UP_RIGHT");
        return allowedSequences;
    }

    @Override
    public void addStraightPath(String direction, ChessBoardModel board) {}


    private boolean canAdvance(int row, int column, ChessBoardModel board) {
        return board.squareExists(row,column) &&
                board.vacantSequence(row,column) &&
                this.canMoveTo(row,column);
    }

    private boolean canDiagonalAdvance(int row, int column, ChessBoardModel board) {
        return board.findPiece(row,column) != null &&
                board.findPiece(row,column).getPieceColor() != color &&
                board.findPiece(row,column).getPieceColor() != UNDEFINED &&
                canMoveTo(row, column);
    }
    private boolean singleAdvance(int nextRow, int nextColumn){
        return (color == BLACK)? nextRow - row == 1 && nextColumn == this.column:
                nextRow - row == -1 && nextColumn == this.column;
    }

    private boolean initialDoubleAdvance(int nextRow, int nextColumn){
        return (color == BLACK)? row == BLACK_STARTING_COLUMN && nextRow - row == 2 && nextColumn == this.column:
                row == WHITE_STARTING_COLUMN && nextRow - row == -2 && nextColumn == this.column;
    }


    private boolean diagonalAdvance(int newRow, int newColumn) {
        return newRow == advanceRow() &&
                (newColumn == column+1 || newColumn == column-1);
    }

    private void addToAllowedSequences(String move){
        int forwardOffset = advanceRow() - row;
        Sequence s = null;
        switch(move){
            case "UP_ONE":
                s = new Sequence(advanceRow(), column, this);
                allowedSequences.add(s);
                break;

            case "UP_TWO":
                s = new Sequence(row + forwardOffset*2, column, this);
                break;

            case "UP_LEFT":
                s = new Sequence(row + forwardOffset, column + forwardOffset, this);
                break;

            case "UP_RIGHT":
                s = new Sequence(row + forwardOffset, column - forwardOffset, this);
                break;

        }
        allowedSequences.add(s);
    }


    private int advanceRow(){return color.equals(BLACK)?row+1:row-1;}


    @Override
    public ChessPiece newInstance() {
        return new Pawn(color, row, column);
    }
}