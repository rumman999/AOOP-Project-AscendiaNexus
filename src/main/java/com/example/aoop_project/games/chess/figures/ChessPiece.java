package com.example.aoop_project.games.chess.figures;
import java.util.ArrayList;
import com.example.aoop_project.games.chess.game.ChessBoardModel;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import static com.example.aoop_project.games.chess.figures.ChessPiece.PieceColor.UNDEFINED;

public abstract class ChessPiece {

    public enum PieceColor {
        WHITE, BLACK, UNDEFINED;

    }
    protected PieceColor color;
    protected int row;
    protected int column;
    protected static SimpleDoubleProperty x;
    protected static SimpleDoubleProperty y;

    protected ArrayList<Sequence> allowedSequences;

    public ChessPiece(PieceColor color, int row, int column) {
        setPieceColor(color);
        setRow(row);
        setColumn(column);
        allowedSequences = new ArrayList<Sequence>();
        x = new SimpleDoubleProperty();
        y = new SimpleDoubleProperty();
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public String getSequencePosition(){
        String position = "";
        int rank = 8-row;
        switch(column){
            case 0:
                position = "a" + rank;
                break;
            case 1:
                position = "b"+ rank;
                break;
            case 2:
                position = "c"+ rank;
                break;
            case 3:
                position = "d"+ rank;
                break;
            case 4:
                position = "e"+ rank;
                break;
            case 5:
                position = "f"+ rank;
                break;
            case 6:
                position = "g"+ rank;
                break;
            case 7:
                position = "h"+ rank;
                break;
        }
        return position;
    }

    public PieceColor getPieceColor() {
        return color;
    }

    private boolean validEntry(int val){
        return val >= 0 && val < ChessBoardModel.NUM_COLUMNS;
    }

    public double getX(){
        return this.x == null ? 0.0 : this.x.get();
    }
    public double getY(){
        return this.y == null ? 0.0 : this.y.get();
    }

    public static DoubleProperty xProperty(){
        return x;
    }

    public static DoubleProperty yProperty(){
        return y;
    }
    public void setX(double xVal){
        x.set(xVal);
    }
    public void setY(double yVal){
        y.set(yVal);
    }

	public void setRow(int newRow) {
        if (validEntry(newRow)) this.row = newRow;
    }

	public void setColumn(int newColumn) {
        if (validEntry(newColumn)) this.column = newColumn;
    }

    private void setPieceColor(PieceColor color) {
        if (isPawn() || isBishop() || isKnight() || isRook() || isQueen() || isKing()) {
            if(color == UNDEFINED)
                throw new IllegalArgumentException();
        }
        this.color = color;
    }

    public void addPath(ChessBoardModel board, ChessPiece chessPiece, int row, int column) {
        if (board.squareExists(row, column) && board.findPiece(row, column).color != chessPiece.color)
            allowedSequences.add(new Sequence(row, column, chessPiece));
    }

    void addPathInOneDirection(ChessBoardModel board, ChessPiece piece, int nextRow, int nextColumn) {
        int rowIncrement = nextRow - piece.row;
        int colIncrement = nextColumn - piece.column;
        while (board.squareExists(nextRow, nextColumn)) {
            PieceColor nextColor = board.findPiece(nextRow, nextColumn).color;
            if (nextColor != piece.color)
                addPath(board, piece, nextRow, nextColumn);
            else break;
            if (nextColor == UNDEFINED) {
                nextRow += rowIncrement;
                nextColumn += colIncrement;
            }
            else break;
        }
    }

    public int posRemains(int val1, int val2){
        int d = val1 - val2;
        return d < 0 ? d*-1: d;
    }

    @Override
    public String toString() {
        return String.format("<<%s %s (row: %d, col: %d) square: [%s]>>",
                color.toString(), getClass().getSimpleName(), row, column, getSequencePosition());
    }

    public abstract boolean canMoveTo(int newRow, int newColumn);
    public abstract ArrayList<Sequence> getAccessibleSequences(ChessBoardModel board);

    public abstract void addStraightPath(String direction,ChessBoardModel board);

    public boolean isPawn() {
        return false;
    }

    public boolean isKnight() {
        return false;
    }

    public boolean isBishop() {
        return false;
    }

    public boolean isRook() {
        return false;
    }

    public boolean isQueen() {
        return false;
    }

    public boolean isKing() {
        return false;
    }

    public void clearAllowedSequences(){
        allowedSequences = new ArrayList<>();
    }

    public abstract ChessPiece newInstance();
}