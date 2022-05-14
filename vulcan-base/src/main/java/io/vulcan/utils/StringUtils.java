package io.vulcan.utils;

import java.util.Collection;
import java.util.Iterator;

public final class StringUtils {

    public static boolean hasLength(final String text) {
        return null != text && !text.isEmpty();
    }

    public static boolean isNullOrEmpty(final String text) {
        return null == text || text.isEmpty();
    }

    public static String join(final String... phrases) {
        return joinOn(",").join(phrases);
    }

    public static String join(final Collection<String> phrases) {
        return joinOn(",").join(phrases);
    }

    public static String join(final String delimiter, final Collection<String> phrases) {
        return joinOn(delimiter).join(phrases);
    }

    public static Joiner joinOn(final String delimiter) {
        return new Joiner(delimiter);
    }

    public static class Joiner {

        private final String delimiter;

        private Joiner(final String delimiter) {
            this.delimiter = delimiter;
        }

        public String join(final String... phrases) {
            if (null == phrases || 0 == phrases.length) {
                return "";
            }

            final int len = phrases.length;

            if (1 == len) {
                return phrases[0];
            }

            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < len - 1; i++) {
                sb.append(phrases[i]).append(delimiter);
            }
            sb.append(phrases[len - 1]);

            return sb.toString();
        }

        public String join(final Collection<String> phrases) {
            if (null == phrases || 0 == phrases.size()) {
                return "";
            }

            final int len = phrases.size();
            final Iterator<String> itr = phrases.iterator();

            if (1 == len) {
                return itr.next();
            }

            final StringBuilder sb = new StringBuilder();
            for (int i = 0; itr.hasNext() && i < len - 1; i++) {
                sb.append(itr.next()).append(delimiter);
            }
            if (itr.hasNext()) {
                sb.append(itr.next());
            }

            return sb.toString();
        }
    }
}
