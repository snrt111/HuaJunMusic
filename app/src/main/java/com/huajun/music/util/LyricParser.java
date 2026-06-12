package com.huajun.music.util;

import com.huajun.music.model.LyricLine;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LyricParser {

    private static final Pattern LINE_PATTERN = Pattern.compile("\\[(\\d{1,2}):(\\d{1,2})(?:\\.(\\d{1,3}))?\\](.*)");

    public static List<LyricLine> parse(String lyricText) {
        List<LyricLine> lines = new ArrayList<>();
        if (lyricText == null || lyricText.isEmpty()) {
            lines.add(new LyricLine(0, "暂无歌词"));
            return lines;
        }
        String[] rawLines = lyricText.split("\n");
        for (String raw : rawLines) {
            String line = raw.trim();
            if (line.isEmpty()) continue;
            Matcher matcher = LINE_PATTERN.matcher(line);
            if (matcher.find()) {
                try {
                    int min = Integer.parseInt(matcher.group(1));
                    int sec = Integer.parseInt(matcher.group(2));
                    int ms = 0;
                    String msGroup = matcher.group(3);
                    if (msGroup != null && !msGroup.isEmpty()) {
                        ms = Integer.parseInt(msGroup);
                        if (msGroup.length() == 2) ms *= 10;
                    }
                    long time = (long) min * 60 * 1000 + (long) sec * 1000 + ms;
                    String text = matcher.group(4).trim();
                    if (!text.isEmpty()) {
                        lines.add(new LyricLine(time, text));
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        if (lines.isEmpty()) {
            lines.add(new LyricLine(0, "暂无歌词"));
        }
        return lines;
    }
}
