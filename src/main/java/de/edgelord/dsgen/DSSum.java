package de.edgelord.dsgen;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static de.edgelord.dsgen.DSGen.toMandS;

public class DSSum {

    public static void main(String[] args) throws IOException {
        final File in = new File(args[0]);
        List<String> lines = Files.readAllLines(in.toPath());

        final long startTime = System.currentTimeMillis();
        long sum = 0;
        long i = 0;
        long max = lines.size();
        for (final String s : lines) {
            progress(i++, max, startTime);
            sum += Long.parseLong(s);
        }

        System.out.println();
        System.out.println(sum);
    }

    private static void progress(final long i, final long max, final long startTime) {
        final double progress = (float) i / max;
        final double progressPercent = progress * 100;
        final long remainingMillis = Math.round((System.currentTimeMillis() - startTime) / progress);
        System.out.printf("%.2f%% (%s / %s)\r", progressPercent, toMandS(System.currentTimeMillis() - startTime), toMandS(remainingMillis));
    }
}
