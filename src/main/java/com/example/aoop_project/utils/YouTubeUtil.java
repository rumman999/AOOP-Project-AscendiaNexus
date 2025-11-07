package com.example.aoop_project.utils;

public class YouTubeUtil {
    /** Extracts the “list” parameter from a YouTube playlist URL. */
    public static String extractPlaylistId(String playlistUrl) {
        var params = playlistUrl.split("\\?");
        if (params.length < 2) return "";
        for (var part : params[1].split("&")) {
            if (part.startsWith("list=")) return part.substring(5);
        }
        return "";
    }

    /** Returns the iframe embed URL for a playlist. */
    public static String toEmbedUrl(String playlistId) {
        return "https://www.youtube.com/embed/videoseries?list=" + playlistId
                + "&enablejsapi=1&rel=0";
    }
}
