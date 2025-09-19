package com.example.aoop_project.games.chess.game;

import com.example.aoop_project.games.chess.figures.ChessPiece;
import com.example.aoop_project.games.chess.figures.King;
import com.example.aoop_project.games.chess.figures.Rook;

import static com.example.aoop_project.games.chess.figures.ChessPiece.PieceColor.*;
import static com.example.aoop_project.games.chess.game.CastleHelper.CastleType.TYPE_LONG;
import static com.example.aoop_project.games.chess.game.CastleHelper.CastleType.TYPE_SHORT;

public class CastleHelper {
    public enum CastleType{ TYPE_LONG, TYPE_SHORT}
    private static final int WHITE_CASTLE_ROW = 7;
    private static final int BLACK_CASTLE_ROW = 0;

    private static final int ROOK_SHORT_CASTLE_COLUMN = 7;
    private static final int ROOK_LONG_CASTLE_COLUMN = 0;

    private static int getKingCastleColumn(King kingPiece, String castleType){
        switch(castleType){
            case "SHORT":
                return kingPiece.getColumn() + 2;
            case "LONG":
                return kingPiece.getColumn() - 2;
        }
        return -1;
    }
    private static boolean kingIsInCastleRow(King kingPiece){
        return kingPiece.getRow() == getCastleRow(kingPiece);
    }

    private static boolean destRowIsInKingsRow(King kingPiece, int destRow){
        return destRow == getCastleRow(kingPiece);
    }
    
    public static boolean sequenceIsCastle(ChessBoardModel board, ChessPiece piece, int destRow, int destColumn) {
        return (piece.isKing())?
                (sequenceIsLongCastle(board, (King)piece, destRow, destColumn) ||
                        sequenceIsShortCastle(board, (King)piece, destRow, destColumn)): 
                false;
    }

    public static boolean sequenceIsShortCastle(ChessBoardModel board, King kingPiece, int destRow, int destColumn) {
        return (kingIsInCastleRow(kingPiece) 
                && destRowIsInKingsRow(kingPiece, destRow)
                && kingCanShortCastle(board, kingPiece) 
                && destColumn == getKingCastleColumn(kingPiece,"SHORT"));
    }

    public static boolean sequenceIsLongCastle(ChessBoardModel board, King kingPiece, int destRow, int destColumn) {
        return (kingIsInCastleRow(kingPiece)
                && destRowIsInKingsRow(kingPiece, destRow)
                && kingCanLongCastle(board, kingPiece)
                && destColumn == getKingCastleColumn(kingPiece,"LONG"));
    }
    
    private static int getCastleRow(King kingPiece){
        if(kingPiece.getPieceColor() == WHITE){
            return WHITE_CASTLE_ROW;
        }
        else if (kingPiece.getPieceColor() == BLACK){
            return BLACK_CASTLE_ROW;
        }
        else return -1;
    }
/*
    public boolean colorCanCastle(ChessPiece.PieceColor color) {
        King king = getKing(color);
        return kingCanShortCastle(king) || kingCanLongCastle(king);
    }*/
    private static int getRookCastleColumn(CastleType castleType){
        if(castleType == TYPE_SHORT) 
            return ROOK_SHORT_CASTLE_COLUMN;
        else
            return ROOK_LONG_CASTLE_COLUMN;
    }

    public static boolean kingCanShortCastle(ChessBoardModel board, King kingPiece) {
        if (!kingPiece.canCastle()) return false;
        ChessPiece rookPiece = board.findPiece(getCastleRow(kingPiece), getRookCastleColumn(TYPE_SHORT));
        return rookCanCastle(rookPiece) &&
                rookPiece.getPieceColor() == kingPiece.getPieceColor() &&
                kingPiece.getRow() == getCastleRow(kingPiece) &&
                kingPiece.getColumn() == 4 &&
                castlePathIsUnoccupied(board, TYPE_SHORT,  kingPiece.getRow(), kingPiece.getColumn());
    }

    public static boolean kingCanLongCastle(ChessBoardModel board, King kingPiece) {
        if (!kingPiece.canCastle()) return false;
        ChessPiece rookPiece = board.findPiece(getCastleRow(kingPiece), getRookCastleColumn(TYPE_LONG));
        return rookCanCastle(rookPiece) &&
                rookPiece.getPieceColor() == kingPiece.getPieceColor() &&
                kingPiece.getRow() == getCastleRow(kingPiece) &&
                kingPiece.getColumn() == 4 &&
                castlePathIsUnoccupied(board, TYPE_LONG,  kingPiece.getRow(), kingPiece.getColumn());
    }

    private static boolean rookCanCastle(ChessPiece rookPiece){
        return  rookPiece.isRook() && ((Rook)rookPiece).canCastle();
    }

    public static int getKingsAdjacentCastlePathSquareColumn(CastleType castleType, int kingColumn){
        if(castleType == TYPE_LONG)
            return kingColumn -1;
        else return kingColumn + 1;
    }

    public static int getKingsFarCastlePathSquareColumn(CastleType castleType, int kingColumn){
        if(castleType == TYPE_LONG)
            return kingColumn - 2;
        else return kingColumn + 2;
    }




    private static boolean castlePathIsUnoccupied(ChessBoardModel board, CastleType castleType, int kingRow,
                                                     int kingColumn) {
        if (castleType == TYPE_LONG) {
            if(kingColumn-3 >= 0 && board.findPiece(kingRow, kingColumn - 3).getPieceColor() != UNDEFINED){
                return false;
            }
        }
        return board.findPiece(kingRow, getKingsAdjacentCastlePathSquareColumn(castleType, kingColumn)).getPieceColor()
                == UNDEFINED
                && board.findPiece(kingRow, getKingsFarCastlePathSquareColumn(castleType, kingColumn)).getPieceColor()
                == UNDEFINED;
    }


}
