package com.example.aoop_project.games.chess.figures;

import static com.example.aoop_project.games.chess.figures.ChessPiece.PieceColor.UNDEFINED;

public class Sequence {

    private int row;
    private int column;
    private ChessPiece chessPiece;

    public Sequence(int squareRow, int squareColumn, ChessPiece figure) {
        row = squareRow;
        column = squareColumn;
        chessPiece = figure;
    }


    @Override
    public String toString() {
        return chessPiece.toString() + " in square (" + row + ", " + column + ")";
    }

    @Override
    public int hashCode(){
        int result = 17;
        result = 31 * result + row;
        result = 31 * result + column;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o==this) {
            return true;
        }
        if (!(o instanceof Sequence)) {
            return false;
        }
        Sequence objectSequence = (Sequence) o;
        return row == objectSequence.row && column == objectSequence.column &&
                chessPiece == objectSequence.chessPiece? true: false;
    }

    public boolean isAvailable(){
        return chessPiece.getPieceColor() == UNDEFINED;
    }

    public ChessPiece getChessPiece() {
        return chessPiece;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}
