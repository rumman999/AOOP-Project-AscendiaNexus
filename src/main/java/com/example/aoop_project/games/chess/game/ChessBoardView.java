package com.example.aoop_project.games.chess.game;

import com.example.aoop_project.games.chess.Interface;
import com.example.aoop_project.games.chess.figures.*;
import com.example.aoop_project.games.chess.figures.*;
import com.example.aoop_project.games.chess.figures.ChessPiece.PieceColor;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;

import javafx.scene.transform.Rotate;

import java.util.ArrayList;

import static com.example.aoop_project.games.chess.figures.ChessPiece.PieceColor.UNDEFINED;
import static com.example.aoop_project.games.chess.game.CastleHelper.CastleType.TYPE_LONG;
import static com.example.aoop_project.games.chess.game.CastleHelper.CastleType.TYPE_SHORT;


public class ChessBoardView extends GridPane {

    public static final int SQUARE_PANEL_SIZE = 80;

    private final ArrayList<Integer> ghostChessList;

    private static String lightColor = "#eeeed2";
    private static String darkColor = "#769656";

    private static String highLightSqColor = "#e0b64a";
    private static String highLightCheckColor = "#e96717";
    private static String highLightCheckMateColor = "#6717e9";

    public ChessBoardView() {
        initializeSequences(new ColumnConstraints(80), new RowConstraints(80));
        initPieces();
        ghostChessList = new ArrayList<Integer>();
    }

    public ChessBoardView(String darkSquareColor, String lightSquareColor ) {
        darkColor = darkSquareColor;
        lightColor = lightSquareColor;
        initializeSequences(new ColumnConstraints(80), new RowConstraints(80));
        initPieces();
        ghostChessList = new ArrayList<Integer>();
    }

    public void initializeSequences(ColumnConstraints colCon, RowConstraints rowCon){
        StackPane square;
        for (int i = 0; i < 8; i++) {
            this.getColumnConstraints().add(colCon);
            this.getRowConstraints().add(rowCon);
            for (int j = 0; j < 8; j++) {
                square = new StackPane();
                setSequenceBackgroundColor(square,i,j);
                this.add(square, i, j);
            }
        }
    }
    public void changeSquareColors(String darkSquareColor, String lightSqareColor){
        lightColor = lightSqareColor;
        darkColor = darkSquareColor;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                setSequenceBackgroundColor((StackPane)getChildren().get(8 * i + j),i,j);
            }
        }
    }

    public void removeAllChessPieces(){
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                deleteChessPiece(i,j);
            }
        }
    }

    public void colorizeSequence(int row, int column) {
        StackPane square = (StackPane) getChildren().get(8 * column + row);
        square.setStyle("-fx-background-insets: 0;\n" +
                "-fx-background-radius: 0;\n" +
                "-fx-background-color: " + highLightSqColor +  "\n" +
                "linear-gradient(from 0% 86% to 0% 95%, #a34313 0%, #903b12 60%),\n" +
                highLightSqColor + ",\n" +
                "#d86e3a,\n" +
                "radial-gradient(center 50% 50%, radius 100%, #eeeed2," + highLightSqColor + ");");
    }

    public void colorizeSequence(int row, int column, String color) {
        StackPane square = (StackPane) getChildren().get(8 * column + row);
        square.setStyle("-fx-background-insets: 0;\n" +
                "-fx-background-radius: 0;\n" +
                "-fx-background-color: " + color +  "\n" +
                "linear-gradient(from 0% 86% to 0% 95%, #a34313 0%, #903b12 60%),\n" +
                color + ",\n" +
                "#d86e3a,\n" +
                "radial-gradient(center 50% 50%, radius 100%, #eeeed2," + color + ");");
    }

    public void colorizeCheckedSequence(int row, int column) {
        StackPane square = (StackPane) getChildren().get(8 * column + row);
        square.setStyle("-fx-background-insets: 0;\n" +
                "-fx-background-radius: 0;\n" +
                "-fx-background-color: " + highLightCheckColor + "\n" +
                "linear-gradient(from 0% 86% to 0% 95%, #a34313 0%, #903b12 60%),\n" +
                highLightCheckColor + ",\n" +
                "#d86e3a,\n" +
                "radial-gradient(center 50% 50%, radius 100%, #eeeed2," + highLightCheckColor + ");");
    }

    public void colorizeCheckMatedSequence(int row, int column) {
        StackPane square = (StackPane) getChildren().get(8 * column + row);
        square.setStyle("-fx-background-insets: 0;\n" +
                "-fx-background-radius: 0;\n" +
                "-fx-background-color: " + highLightCheckMateColor + "\n" +
                "linear-gradient(from 0% 86% to 0% 95%, #a34313 0%, #903b12 60%),\n" +
                highLightCheckMateColor + ",\n" +
                "#d86e3a,\n" +
                "radial-gradient(center 50% 50%, radius 100%, #eeeed2," + highLightCheckMateColor + ");");
    }

    public void colorizeSequences(ChessBoardModel board, ChessPiece piece) {
        for (Sequence seq : piece.getAccessibleSequences(board)) {
            if (!board.sequenceIsCheckInducing(seq) && !board.findPiece(seq.getRow(), seq.getColumn()).isKing()) {
                colorizeSequence(seq.getRow(), seq.getColumn());
            }
        }
    }

    private void setSequenceBackgroundColor(Pane square, int i, int j){
        if (((i + j) % 2) == 0) {
            square.setStyle("-fx-background-insets: 0;\n" +
                    "-fx-background-radius: 0;\n" +
                    "-fx-background-color: " + lightColor + ";");
        }
        else {
            square.setStyle("-fx-background-insets: 0;\n" +
                    "-fx-background-radius: 0;\n" +
                    "-fx-background-color: " + darkColor + ";");
        }
    }


    public void addChessPiece(ChessPiece piece) {
        ((StackPane) getChildren().get(8*piece.getColumn()+piece.getRow()))
                .getChildren().add(getChessPieceImageView(piece));
    }

    public void deleteChessPiece(int row, int column) {
        for (int i=0; i< getChildren().size(); i++) {
            Node child = getChildren().get(i);
            boolean found = this.getColumnIndex(child) == column
                    && this.getRowIndex(child) == row;
            if (found) {
                StackPane removePane = ((StackPane) getChildren().get(8*column+row));
                if (removePane.getChildren() != null && removePane.getChildren().size() == 1)
                    removePane.getChildren().remove(0);
            }
        }
    }

    public void colorizeCastleSequences(ChessBoardModel board, ChessPiece figure, int startColumn) {
        if ((!board.isCurrentlyChecked(figure.getPieceColor()) && figure.isKing())) {

            int shortCastleColumn = figure.getColumn() + 2;
            int longCastleColumn = figure.getColumn() - 2;

            if (CastleHelper.sequenceIsShortCastle(board, (King)figure, figure.getRow(), shortCastleColumn) &&
                    !board.sequenceIsCheckInducing(
                            new Sequence(figure.getRow(),shortCastleColumn,figure)) &&
                    !board.sequenceIsCheckInducing(
                            new Sequence(figure.getRow(),CastleHelper.getKingsAdjacentCastlePathSquareColumn(
                                    TYPE_SHORT,
                                    figure.getColumn()),
                                   figure))) {

                colorizeSequence(figure.getRow(), shortCastleColumn);
            }
            if (CastleHelper.sequenceIsLongCastle(board, (King)figure, figure.getRow(), longCastleColumn)  &&
                    !board.sequenceIsCheckInducing(
                            new Sequence(figure.getRow(),longCastleColumn,figure)) &&
                    !board.sequenceIsCheckInducing(
                            new Sequence(figure.getRow(),CastleHelper.getKingsAdjacentCastlePathSquareColumn(
                                    TYPE_LONG,
                                    figure.getColumn()),
                                    figure))) {
                colorizeSequence(figure.getRow(), longCastleColumn);
            }
        }
    }



    public void unColorizeSequence(int row, int column) {
        setSequenceBackgroundColor((StackPane)getChildren().get(8 * column + row),row,column);
    }

    public void unColorizeAll() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                unColorizeSequence(i, j);
            }
        }
    }
    public void colorizeKingIfChecked(PieceColor pieceColor, ChessBoardModel board) {
        King kingPiece = board.getKing(pieceColor);
        if (board.isCurrentlyChecked(pieceColor)) colorizeCheckedSequence(kingPiece.getRow(), kingPiece.getColumn());
        if (board.isMate(pieceColor)) colorizeCheckMatedSequence(kingPiece.getRow(), kingPiece.getColumn());
    }
    

    public void displayGhostChessPiece(ChessPiece piece, int newRow, int newColumn) {
        ChessPiece replacementPiece = piece.newInstance();
        replacementPiece.setRow(newRow);
        replacementPiece.setColumn(newColumn);
        addGhostChessPiece(replacementPiece);
    }

    public void addGhostChessPiece(ChessPiece piece) {
        ImageView pieceImage = getGhostPieceImage(piece);
        int index = 8 * piece.getColumn() + piece.getRow();
        if(!ghostChessList.contains(index)){
            ((Pane) getChildren().get(index)).getChildren().add(pieceImage);
            ghostChessList.add(index);
        }

    }

    public void removeAllGhostPieces(){
        for(int index: ghostChessList){
            int lastIndex = ((Pane) getChildren().get(index)).getChildren().size()-1;
            ((Pane)getChildren().get(index)).getChildren().remove(lastIndex);
        }
        ghostChessList.clear();
    }

    public void moveChessPiece(ChessPiece piece, int newRow, int newColumn) {
        deleteChessPiece(piece.getRow(), piece.getColumn());
        deleteChessPiece(newRow, newColumn);

        ChessPiece replacementPiece = piece.newInstance();
        replacementPiece.setRow(newRow);
        replacementPiece.setColumn(newColumn);
        if (pawnIsPromoting(piece,newRow)) {
            addChessPiece(new Queen(piece.getPieceColor(), newRow, newColumn));
        }
        else{
            addChessPiece(replacementPiece);
        }
    }


    public void initPieces() {

        initPawns();
        initStrongPieces();
    }

    private void initStrongPieces() {
        PieceColor color = PieceColor.BLACK;
        addChessPiece(new Rook(color, 0, 0));
        addChessPiece(new Knight(color, 0, 1));
        addChessPiece(new Bishop(color, 0, 2));
        addChessPiece(new Queen(color, 0, 3));
        addChessPiece(new King(color, 0, 4));
        addChessPiece(new Bishop(color, 0, 5));
        addChessPiece(new Knight(color, 0, 6));
        addChessPiece(new Rook(color, 0, 7));

        color = PieceColor.WHITE;
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
            addChessPiece(new Pawn(PieceColor.BLACK, 1, column));
            addChessPiece(new Pawn(PieceColor.WHITE, 6, column));
        }
    }
    
    private static ImageView getChessPieceImageView(ChessPiece piece) {
        if (piece.getPieceColor() != UNDEFINED) {
            String colorName = "";
            if(piece.getPieceColor() == PieceColor.WHITE){
                colorName += "white_";
            }
            else if (piece.getPieceColor() == PieceColor.BLACK){
                colorName += "black_";
            }
            String pieceName = piece.getClass().getSimpleName().toLowerCase();
            Image image = new Image(Interface.class.getResourceAsStream(
                    String.format("images/%s%s.png", colorName,pieceName)));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(80);
            imageView.setFitHeight(80);
            return imageView;
        }
        return null;

    }

    private static ImageView getGhostPieceImage(ChessPiece piece)  {
        if (piece.getPieceColor() != UNDEFINED) {
            String colorName = "";
            if(piece.getPieceColor() == PieceColor.WHITE){
                colorName += "white_";
            }
            else if (piece.getPieceColor() == PieceColor.BLACK){
                colorName += "black_";
            }
            String pieceName = piece.getClass().getSimpleName().toLowerCase();
            String imageURI = "images/" +  colorName + pieceName + ".png";
            Image image = new Image(Interface.class.getResourceAsStream(
                    String.format("images/%s%s.png", colorName,pieceName)));
            ImageView imageView = new ImageView(image);
            imageView.setOpacity(0.3);
            imageView.setFitHeight(60);
            imageView.setFitHeight(60);
            imageView.setSmooth(true);
            imageView.setPreserveRatio(true);

            return imageView;
        }

        return null;
    }

    private boolean pawnIsPromoting(ChessPiece p, int row){
        return p.isPawn() &&
                ((p.getPieceColor() == PieceColor.WHITE && row == 0)
                        || (p.getPieceColor() == PieceColor.BLACK && row == 7));
    }

    public void flipBoardImages(){
        for(Node n: this.getChildren()){
            Rotate rt = new Rotate(180,SQUARE_PANEL_SIZE/2,SQUARE_PANEL_SIZE/2);
            n.getTransforms().addAll(rt);
        }
    }
}