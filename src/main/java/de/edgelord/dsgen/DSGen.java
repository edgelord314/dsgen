package de.edgelord.dsgen;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class DSGen implements Callable<Integer> {

    private final File out;
    private final int digits;

    public DSGen(final File out, final int digits) {
        this.out = out;
        this.digits = digits;
    }

    @Override
    public Integer call() throws IOException {

        out.createNewFile();

        List<String> dsNums = new ArrayList<>();
        double maxNum = highestPossibleNumber(digits);
        System.out.println("Starting listing of all DS-Numbers with " + digits + " didgits or less (highest possible: " + maxNum +")");
        int counter = 0;
        final long startTime = System.currentTimeMillis();
        for (long i = 1; i <= maxNum; i++) {
            if (counter > 1000) {
                progress(i, maxNum, startTime);
                counter = 0;
            } else counter++;
            boolean[] result = isDSNum(i);
            if (result[0]) {
                final String s = Long.toString(i);
                if (result[1]) {
                    i += Math.pow(10, s.length() - 2);
                } else {
                    i++;
                }
                dsNums.add(s);
            }
        }
        System.out.println();
        System.out.println("Done collecting the " + dsNums.size() + " DS-Numbers (ratio numbers to DS-Numbers "
                + dsNums.size() / maxNum
                + ") with " + digits + " or less digits!");

        Files.write(out.toPath(), dsNums);
        return 0;
    }

    private void progress(final long i, final double max, final long startTime) {
        final double progress = i / max;
        final double progressPercent = progress * 100;
        final long remainingMillis = Math.round((System.currentTimeMillis() - startTime) / progress);
        System.out.printf("%.2f%% (%s / %s)\r", progressPercent, toMandS(System.currentTimeMillis() - startTime), toMandS(remainingMillis));
    }

    public static String toMandS(final long millis) {
        return TimeUnit.MILLISECONDS.toMinutes(millis) + "m" +
                (TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))) + "s";
    }

    private double highestPossibleNumber(final int d) {
        return Math.pow(10, d) - 1;
    }

    /*private boolean isDSNum(final long digitSum, final long maxDigit) {
        // get highest digit and check if the sum of digits is twice
        // that number
        return maxDigit * 2 == digitSum;
    }

    private long getHighestDigit(final long num) {
        long n = num;
        long max = 0;
        while (n > 0) {
            long digit = n % 10;
            max = Math.max(max, digit);
            n /= 10;
        }
        return max;
    }*/

    /**
     * Returns two boolean:
     * 1. true if the given number is a ds num
     * 2. true if the number start with 99; important for skipping
     *
     * @param num a number
     * @return -1 if the number can not be a DS-Number and the sum of its digits
     * if it could be
     */
    private boolean[] isDSNum(final long num) {
        long n = num;
        long max = 0;
        int sumOfDigits = 0;
        boolean lastDSNumInPow = false;
        while (n > 0) {
            long d = n % 10;
            sumOfDigits += d;
            if (sumOfDigits > 18) {
                return new boolean[]{false};
            }
            max = Math.max(max, d);
            n /= 10;
            lastDSNumInPow = lastDSNumInPow || n == 99;
        }
        return new boolean[]{max * 2 == sumOfDigits, lastDSNumInPow};
    }

    public static void main(String[] args) throws IOException {
        new DSGen(new File(args[0]), Integer.parseInt(args[1])).call();
    }
}
