package com.example.aoop_project.games.chess.figures;

import java.util.ArrayList;
import com.example.aoop_project.games.chess.game.ChessBoardModel;

public class King extends ChessPiece {

    private boolean canCastle;

    public King(PieceColor color, int row, int column) {
        super(color, row, column);
        canCastle = true;
    }

    @Override
    public boolean isKing(){
        return true;
    }


    @Override
    public boolean canMoveTo(int newRow, int newColumn) {return posRemains(newRow,row) < 2
            && posRemains(newColumn,column) < 2;}

    @Override
    public void setRow(int newRow) {
        if (row < 0 || row >= ChessBoardModel.NUM_ROWS) {
            throw new IndexOutOfBoundsException("Invalid row: " + row);
        }
        this.row = newRow;
        setCannotCastle();
    }

    public boolean canCastle() {
        return canCastle;
    }

    public void setCannotCastle(){
        canCastle = false;
    }

    @Override
    public ArrayList<Sequence> getAccessibleSequences(ChessBoardModel board) {
        allowedSequences = new ArrayList<Sequence>();
        int i=-2, j=-1, counter = 0;
        do{
            if(counter%3 == 0){
                i++;
                j=-1;
            }
            if(i != 0 || j != 0){
                addPath(board, this,row + i,column + j);
            }
            j++;
            counter++;
        } while(counter<9);
        return allowedSequences;
    }

    @Override
    public void addStraightPath(String direction, ChessBoardModel board) {}

    @Override
    public ChessPiece newInstance() {
        return new King(color, row, column);
    }
    
}