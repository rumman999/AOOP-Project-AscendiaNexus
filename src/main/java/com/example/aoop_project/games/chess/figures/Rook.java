package com.example.aoop_project.games.chess.figures;

import java.util.ArrayList;
import com.example.aoop_project.games.chess.game.ChessBoardModel;

public class Rook extends ChessPiece {
    private boolean canCastle = true;

    public Rook(PieceColor color,int row, int column) {
        super(color, row, column);
    }

    @Override
    public boolean isRook(){
        return true;
    }

    @Override
    public void setRow(int nextRow) {
        if (row < 0 || row >= ChessBoardModel.NUM_ROWS) {
            throw new IndexOutOfBoundsException("Invalid row: " + row);
        }
        this.row = nextRow;
        canCastle = false;
    }

    public boolean canCastle() {
        return canCastle;
    }

    public void setCannotCastle(){
        canCastle = false;
    }


    @Override
    public boolean canMoveTo(int nextRow, int nextColumn) {
        return (posRemains(nextColumn,this.column) == 0)?
                posRemains(nextRow,this.row) != 0: posRemains(nextRow, this.row) == 0;
    }

    @Override
    public ArrayList<Sequence> getAccessibleSequences(ChessBoardModel board) {
        clearAllowedSequences();
        addStraightPath("HORIZONTAL", board);
        addStraightPath("VERTICAL", board);
        return allowedSequences;
    }

    @Override
    public void addStraightPath(String direction,ChessBoardModel board) {
        switch (direction) {
            case "VERTICAL":
                addPathInOneDirection(board, this, this.row + 1, this.column);
                addPathInOneDirection(board, this, this.row - 1, this.column);
                break;

            case "HORIZONTAL":
                addPathInOneDirection(board, this, this.row, this.column + 1);
                addPathInOneDirection(board, this, this.row, this.column - 1);
                break;
        }
    }

    @Override
    public ChessPiece newInstance() {
        return new Rook(color, row, column);
    }
}