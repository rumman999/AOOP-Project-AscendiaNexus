package com.example.aoop_project.games.chess.figures;

import java.util.ArrayList;
import com.example.aoop_project.games.chess.game.ChessBoardModel;

public class Queen extends ChessPiece {

    public Queen(PieceColor color, int row, int column) {
        super(color, row, column);
    }

    @Override
    public boolean isQueen(){
        return true;
    }

    @Override
    public boolean canMoveTo(int newRow, int newColumn) {
        if(posRemains(newRow,this.row) == 0){
            return posRemains(newColumn, this.column) !=0 ;
        }
        else if (posRemains(newColumn,this.column) == 0){
            return posRemains(newRow,this.row) != 0;
        }
        else{
            return posRemains(newColumn, this.column) == posRemains(newRow,this.row);
        }
    }

    @Override
    public void addStraightPath(String direction,ChessBoardModel board){
        switch(direction){
            case "VERTICAL":
                addPathInOneDirection(board, this, this.row+1, this.column);
                addPathInOneDirection(board, this, this.row-1, this.column);
                break;

            case "HORIZONTAL":
                addPathInOneDirection(board, this, this.row, this.column + 1);
                addPathInOneDirection(board, this, this.row, this.column - 1);
                break;

            case "DIAGONAL":
                addPathInOneDirection(board, this, this.row + 1, this.column + 1);
                addPathInOneDirection(board, this, this.row - 1, this.column - 1);
                addPathInOneDirection(board, this, this.row + 1, this.column - 1);
                addPathInOneDirection(board, this, this.row - 1, this.column + 1);
                break;
        }
    }

    @Override
    public ArrayList<Sequence> getAccessibleSequences(ChessBoardModel board) {
        clearAllowedSequences();
        addStraightPath("VERTICAL", board);
        addStraightPath("HORIZONTAL", board);
        addStraightPath("DIAGONAL", board);
        return allowedSequences;
    }

    @Override
    public ChessPiece newInstance() {
        return new Queen(color, row, column);
    }
}