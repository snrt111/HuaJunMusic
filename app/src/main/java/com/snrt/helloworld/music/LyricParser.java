package com.snrt.helloworld.music;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LyricParser {

    public static class LyricLine {
        private long timeInMillis;
        private String content;

        public LyricLine(long timeInMillis, String content) {
            this.timeInMillis = timeInMillis;
            this.content = content;
        }

        public long getTimeInMillis() {
            return timeInMillis;
        }

        public String getContent() {
            return content;
        }
    }

    public static List<LyricLine> parseLyrics(String lyricsText) {
        List<LyricLine> lyricLines = new ArrayList<>();

        // Define a pattern for matching time tags and lyrics
        Pattern pattern = Pattern.compile("\\[(\\d{2}):(\\d{2}).(\\d{3})\\](.*)");

        // Split the text into lines
        String[] lines = lyricsText.split("\n");

        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                int minutes = Integer.parseInt(matcher.group(1));
                int seconds = Integer.parseInt(matcher.group(2));
                int milliseconds = Integer.parseInt(matcher.group(3));
                String content = matcher.group(4);

                long timeInMillis = minutes * 60 * 1000 + seconds * 1000 + milliseconds;
                lyricLines.add(new LyricLine(timeInMillis, content));
            }
        }

        return lyricLines;
    }
}

