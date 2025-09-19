package com.example.aoop_project.games.chess.figures;

import java.util.ArrayList;
import com.example.aoop_project.games.chess.game.ChessBoardModel;

public class Knight extends ChessPiece {
    
    public Knight(PieceColor color,int row, int column) {
        super(color, row, column);
    }

    @Override
    public boolean isKnight(){
        return true;
    }

    @Override
    public boolean canMoveTo(int newRow, int newColumn) {
        boolean twoVerOneHor = posRemains(newColumn,column) == 2 &&
                posRemains(newRow,row) == 1;
        boolean twoHorOneVer = posRemains(newColumn,column) == 1 &&
                posRemains(newRow,row) == 2;
        return twoVerOneHor || twoHorOneVer;
    }

    @Override
    public ArrayList<Sequence> getAccessibleSequences(ChessBoardModel board) {
        clearAllowedSequences();
        boolean repeat = false;
        int i = -2;
        do{
            int columnVal = 1;
            if(repeat) {
                i--;
                columnVal = -1;
            }
            if(i==0) {
                repeat = !repeat;
                continue;
            }
            columnVal *= (Math.abs(i) == 2)?1:2;
            if(i<=2) {
                addPath(board, this,row + i,column + columnVal);
            }
            repeat = !repeat;
        } while(i++<3);
        return allowedSequences;
    }

    @Override
    public void addStraightPath(String direction, ChessBoardModel board) {}

    @Override
    public ChessPiece newInstance() {
        return new Knight(color, row, column);
    }
}