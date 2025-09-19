package com.example.aoop_project.games.chess.game;

import com.example.aoop_project.games.chess.figures.*;
import com.example.aoop_project.games.chess.figures.*;

import static com.example.aoop_project.games.chess.figures.ChessPiece.PieceColor.*;

import java.util.ArrayList;

public class ChessBoardModel {
    
    public static final int NUM_ROWS = 8;
    public static final int NUM_COLUMNS = 8;
    private ChessPiece[][] board = new ChessPiece[NUM_ROWS][NUM_COLUMNS];

    public ChessBoardModel() {init();}

    public void init(){
        board = new ChessPiece[NUM_ROWS][NUM_COLUMNS];
        reset();
        initPawns();
        initStrongPieces();
    }

    public ChessPiece[][] getBoard(){
        return board;
    }

    public void resetBoard(){
        board = new ChessPiece[NUM_ROWS][NUM_COLUMNS];
        reset();
    }

    public void reset() {
        for(int i=0; i< NUM_ROWS; i++){
            deleteRow(i);
        }
    }

    public ArrayList<ChessPiece> getBoardAsList(){
        ArrayList boardList = new ArrayList<>();
        for (ChessPiece[] row : board) {
            for (ChessPiece piece : row) {
                boardList.add(piece);
            }
        }
        return boardList;
    }

    public void initiateMove(ChessPiece piece, int newRow, int newColumn) {
        if (canMoveTo(piece, newRow, newColumn)) moveChessPiece(piece, newRow, newColumn);
    }

    public boolean canMoveTo(ChessPiece piece, int newRow, int newColumn) {
        boolean valid = true;
        if(findPiece(newRow, newColumn).getPieceColor() == piece.getPieceColor() ||
                !piece.canMoveTo(newRow, newColumn) ||
                !piece.getAccessibleSequences(this).contains(
                        new Sequence(newRow, newColumn, piece))){
            if (findPiece(newRow, newColumn).getPieceColor() != piece.getPieceColor() &&
                    CastleHelper.sequenceIsCastle(this, piece, newRow, newColumn)) {
                return true;
            }
            else{
                valid =  false;
            }
        }
        return valid;
    }


    public void moveChessPiece(ChessPiece piece, int newRow, int newColumn) {
        int oldRow = piece.getRow();
        int oldColumn = piece.getColumn();

        piece.setRow(newRow);
        piece.setColumn(newColumn);
        deleteChessPiece(oldRow, oldColumn);
        addChessPiece(piece);

        if (piece.isPawn()) promotePawnIfEndReached((Pawn)piece);
    }

    private void promotePawnIfEndReached(Pawn pawn){
        int column = pawn.getColumn();
        int row = pawn.getRow();
        switch (pawn.getPieceColor()){
            case WHITE:
                if (pawn.getRow() == 0){
                    deleteChessPiece(row, column);
                    addChessPiece(new Queen(pawn.getPieceColor(), row, column));
                }
                break;
            case BLACK:
                if (pawn.getRow() == 7){
                    deleteChessPiece(row, column);
                    addChessPiece(new Queen(pawn.getPieceColor(), row, column));
                }
                break;
        }
    }

    public boolean vacantSequence(int row, int column){
        return squareExists(row, column)? board[row][column].getPieceColor() == UNDEFINED: false;
    }


    public boolean squareExists(int row, int column) {
        return row >= 0 && row < NUM_ROWS && column >= 0 && column < NUM_COLUMNS;
    }

    public ChessPiece findPiece(int row, int column) {
        if(squareExists(row, column)){
            return board[row][column];
        }
        else return null;
    }


    public void addChessPiece(ChessPiece piece) {
        board[piece.getRow()][piece.getColumn()] = piece;
    }

    public void deleteChessPiece(int row, int column) {
        board[row][column] = new ChessPiece(UNDEFINED, row, column) {
            @Override
            public boolean canMoveTo(int newRow, int newColumn) {
                return false;
            }
            @Override
            public ArrayList<Sequence> getAccessibleSequences(ChessBoardModel board) {
                return new ArrayList<>();
            }
            @Override
            public void addStraightPath(String direction, ChessBoardModel board) {}
            @Override
            public ChessPiece newInstance() {
                return this;
            }
        };
    }

    private void initStrongPieces() {
        ChessPiece.PieceColor color = ChessPiece.PieceColor.BLACK;
        addChessPiece(new Rook(color, 0, 0));
        addChessPiece(new Knight(color, 0, 1));
        addChessPiece(new Bishop(color, 0, 2));
        addChessPiece(new Queen(color, 0, 3));
        addChessPiece(new King(color, 0, 4));
        addChessPiece(new Bishop(color, 0, 5));
        addChessPiece(new Knight(color, 0, 6));
        addChessPiece(new Rook(color, 0, 7));

        color = ChessPiece.PieceColor.WHITE;
        addChessPiece(new Rook(color, 7, 0));
        addChessPiece(new Knight(color, 7, 1));
        addChessPiece(new Bishop(color, 7, 2));
        addChessPiece(new Queen(color, 7, 3));
        addChessPiece(new King(color, 7, 4));
        addChessPiece(new Bishop(color, 7, 5));
        addChessPiece(new Knight(color, 7, 6));
        addChessPiece(new Rook(color, 7, 7));
    }

    private void initPawns() {
        for (int column = 0; column < 8; column++) {
            addChessPiece(new Pawn(ChessPiece.PieceColor.BLACK, 1, column));
            addChessPiece(new Pawn(ChessPiece.PieceColor.WHITE, 6, column));
        }
    }

    public int getNumLegalMovesAvailable(ChessPiece.PieceColor color) {
        int numLegalMoves = 0;
        for(int i=0; i<NUM_ROWS; i++){
            for(int j=0; j<NUM_COLUMNS; j++){
                ChessPiece figure = board[i][j];
                ArrayList<Sequence> allowedMoves = figure.getAccessibleSequences(this);
                if(figure.getPieceColor() != color) continue;
                for (int k=0; k<allowedMoves.size(); k++) {
                    if (!sequenceIsCheckInducing(allowedMoves.get(k))) {
                        numLegalMoves++;
                    }
                }
            }
        }
        return numLegalMoves;
    }
    private void deleteRow(int row){
        for (int column = 0; column < NUM_COLUMNS; column++) {
            deleteChessPiece(row, column);
        }
    }

    public King getWhiteKing(){
        for(int i=0; i<NUM_ROWS; i++){
            for(int j=0; j<NUM_COLUMNS; j++){
                if(board[i][j].isKing() && board[i][j].getPieceColor() == WHITE)
                    return (King) board[i][j];
            }
        }
        return null;
    }
    public King getBlackKing(){
        for(int i=0; i<NUM_ROWS; i++){
            for(int j=0; j<NUM_COLUMNS; j++){
                if(board[i][j].isKing() && board[i][j].getPieceColor() == BLACK)
                    return (King) board[i][j];
            }
        }
        return null;
    }
    
    public King getKing(ChessPiece.PieceColor colorToCheck) {
        if(colorToCheck == WHITE)
            return getWhiteKing();
        else if(colorToCheck == BLACK)
            return getBlackKing();
        else return null;
    }

    public boolean sequenceIsCheckInducing(Sequence sequence) {
        ChessBoardModel simulationBoard = newInstance();
        ChessPiece figure = initiateSimulationMove(simulationBoard,sequence);
        if (simulationBoard.isCurrentlyChecked(figure.getPieceColor())) {
            figure.getAccessibleSequences(this).remove(sequence);
            return true;
        }
        return false;
    }

    public boolean isMate(ChessPiece.PieceColor color) {
        if(getNumLegalMovesAvailable(color) < 1)
            return isCurrentlyChecked(color);
        else return false;
    }

    public boolean isStaleMate() {
        for(ChessPiece.PieceColor color: ChessPiece.PieceColor.values()){
            if(color == UNDEFINED) continue;
            if(getNumLegalMovesAvailable(color) < 1){
                if(!isCurrentlyChecked(color))
                    return true;
            }
        }
        return false;
    }


    public boolean isCurrentlyChecked(ChessPiece.PieceColor color) {
        King kingPiece = getKing(color);
        int kingRow = kingPiece.getRow();
        int kingColumn = kingPiece.getColumn();
        ChessBoardModel model = this;

        ArrayList<Sequence> sequenceList = new Queen(color, kingRow, kingColumn).getAccessibleSequences(model);
        sequenceList.addAll(new Knight(color, kingRow, kingColumn).getAccessibleSequences(model));

        for (int i=0; i< sequenceList.size(); i++) {
            ChessPiece opponentPiece = findPiece(sequenceList.get(i).getRow(), sequenceList.get(i).getColumn());
            ArrayList<Sequence> opponentSequences = opponentPiece.getAccessibleSequences(model);
            for (int j=0; j<opponentSequences.size(); j++) {
                Sequence sequence = opponentSequences.get(j);
                if (kingRow == sequence.getRow() && kingColumn == sequence.getColumn())
                    return true;
            }
        }
        return false;
    }

    public ChessBoardModel newInstance() {
        ChessBoardModel newBoard = new ChessBoardModel();
        newBoard.reset();
        for(int i=0; i<NUM_ROWS; i++){
            for(int j=0; j<NUM_COLUMNS; j++){
                newBoard.addChessPiece(board[i][j].newInstance());
            }
        }
        return newBoard;
    }

    private ChessPiece initiateSimulationMove(ChessBoardModel simulationBoard, Sequence sequence){
        int currentRow= sequence.getChessPiece().getRow();
        int currentColumn = sequence.getChessPiece().getColumn();
        int nextRow = sequence.getRow();
        int nextColumn = sequence.getColumn();

        ChessPiece figure = simulationBoard.findPiece(currentRow, currentColumn);
        simulationBoard.initiateMove(figure, nextRow, nextColumn);
        return figure;
    }


}