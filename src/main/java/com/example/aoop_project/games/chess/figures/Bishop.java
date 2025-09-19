package com.example.aoop_project.games.chess.figures;

import java.util.ArrayList;
import com.example.aoop_project.games.chess.game.ChessBoardModel;

public class Bishop extends ChessPiece {
    
    public Bishop(PieceColor color, int row, int column) {
        super(color, row, column);
    }

    @Override
    public boolean isBishop(){
        return true;
    }

    @Override
    public boolean canMoveTo(int newRow, int newColumn) {
        return posRemains(newRow,this.row) == posRemains(newColumn,this.column)
                && newRow != this.row && newColumn != this.column;
    }

    @Override
    public ArrayList<Sequence> getAccessibleSequences(ChessBoardModel board) {
        clearAllowedSequences();
        addStraightPath("DIAGONAL", board);
        return allowedSequences;
    }

    @Override
    public void addStraightPath(String direction,ChessBoardModel board) {
        switch (direction) {
            case "DIAGONAL":
                addPathInOneDirection(board, this, this.row + 1, this.column + 1);
                addPathInOneDirection(board, this, this.row - 1, this.column - 1);
                addPathInOneDirection(board, this, this.row + 1, this.column - 1);
                addPathInOneDirection(board, this, this.row - 1, this.column + 1);
                break;
        }
    }


    @Override
    public ChessPiece newInstance() {
        return new Bishop(color, row, column);
    }
}