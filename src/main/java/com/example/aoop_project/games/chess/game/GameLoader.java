package com.example.aoop_project.games.chess.game;

import com.example.aoop_project.games.chess.figures.*;


import java.util.ArrayList;

public class GameLoader {
    public static ChessBoardModel loadGame(byte[] data,ChessBoardController controller) throws IllegalArgumentException {
        if(data == null) return null;
        ChessBoardModel loadedChessBoardModel = controller.getGame().getModel();
        loadedChessBoardModel.resetBoard();
        controller.getView().removeAllChessPieces();
        controller.setGame(new Game(loadedChessBoardModel));
        String[] allLines = new String(data).split("\n");
        String startColor = allLines[0];
        if(startColor.equals("BLACK"))
            controller.getGame().setCurrentPieceColor(ChessPiece.PieceColor.BLACK);
        else if (startColor.equals("WHITE"))
            controller.getGame().setCurrentPieceColor(ChessPiece.PieceColor.WHITE);
        else
            throw new IllegalArgumentException();
        for (int i =  0; i < allLines.length; i++) {
            String[] line = allLines[i].split(",");
            if(line.length <= 2 || line[0].equals("UNDEFINED")){
                continue;
            }
            ChessPiece.PieceColor color = line[0].equals("BLACK")?
                    ChessPiece.PieceColor.BLACK : ChessPiece.PieceColor.WHITE;
            String pieceType = line[1];
            ChessPiece currentPiece = null;
            switch(pieceType){
                case "Pawn":
                    currentPiece = new Pawn(color,Integer.parseInt(line[2]),
                            Integer.parseInt(line[3]));
                    break;
                case "Rook":
                    currentPiece = new Rook(color,Integer.parseInt(line[2]),
                            Integer.parseInt(line[3]));
                    if(line[4].equals("false")){
                        ((Rook) currentPiece).setCannotCastle();
                    }
                    break;
                case "Knight":
                    currentPiece = new Knight(color,Integer.parseInt(line[2]),
                            Integer.parseInt(line[3]));
                    break;
                case "Bishop":
                    currentPiece = new Bishop(color,Integer.parseInt(line[2]),
                            Integer.parseInt(line[3]));
                    break;
                case "Queen":
                    currentPiece = new Queen(color,Integer.parseInt(line[2]),
                            Integer.parseInt(line[3]));
                    break;
                case "King":
                    currentPiece = new King(color,Integer.parseInt(line[2]),
                            Integer.parseInt(line[3]));
                    if(line[4].equals("false")){((King) currentPiece).setCannotCastle();};
                    break;
            }
            if(currentPiece != null) {
                loadedChessBoardModel.addChessPiece(currentPiece);
                controller.getView().addChessPiece(currentPiece);
            }
        }
        return loadedChessBoardModel;
    }
    public static byte[] saveGame(ChessBoardModel gameBoard, Game game) {
        StringBuilder sb = new StringBuilder();
        sb.append(game.getColorInPlay()).append('\n');
        ArrayList<ChessPiece> pieces = gameBoard.getBoardAsList();
        for (int i = 0; i < pieces.size(); i++) {
            String color = pieces.get(i).getPieceColor().name();
            String type = pieces.get(i).getClass().getSimpleName();
            int row = pieces.get(i).getRow();
            int column = pieces.get(i).getColumn();
            sb.append(color).append(',');
            sb.append(type).append(',');
            sb.append(row).append(',');
            sb.append(column);
            if(type.equals("King") || type.equals("Rook")){
                sb.append(',');
                if(type.equals("King"))
                    sb.append(((King)pieces.get(i)).canCastle());
                else sb.append(((Rook)pieces.get(i)).canCastle());
            }
            sb.append("\n");
        }
        return sb.toString().getBytes();
    }

}
