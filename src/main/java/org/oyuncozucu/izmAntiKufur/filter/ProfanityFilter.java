package org.oyuncozucu.izmAntiKufur.filter;

import java.text.Normalizer;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import org.oyuncozucu.izmAntiKufur.config.Settings;

public final class ProfanityFilter {

    private static final Pattern COMBINING_MARKS = Pattern.compile("\\p{M}+");
    private static final Pattern NON_ALNUM = Pattern.compile("[^a-z0-9çğıöşü]+");

    private final Settings settings;
    private final List<String> badWords;
    private final List<String> whitelist;
    private final Set<String> normalizedBadWords;
    private final Set<String> normalizedWhitelist;

    public ProfanityFilter(Settings settings) {
        this.settings = settings;
        this.badWords = settings.badWords();
        this.whitelist = settings.whitelist();
        this.normalizedBadWords = normalizeWords(badWords);
        this.normalizedWhitelist = normalizeWords(whitelist);
    }

    public FilterResult check(String message) {
        if (!settings.enabled() || message == null || message.isBlank()) {
            return new FilterResult(false, message, Set.of());
        }

        String normalized = normalize(message, false);
        String compact = normalize(message, true);
        Set<String> matches = new LinkedHashSet<>();

        for (String allowed : normalizedWhitelist) {
            normalized = normalized.replace(allowed, " ");
            compact = compact.replace(allowed, "");
        }

        for (String badWord : normalizedBadWords) {
            if (badWord.length() < 2) {
                continue;
            }
            if (containsWord(normalized, compact, badWord)) {
                matches.add(badWord);
            }
        }

        if (matches.isEmpty()) {
            return new FilterResult(false, message, Set.of());
        }
        return new FilterResult(true, maskMessage(message, matches), matches);
    }

    public String normalize(String input, boolean compact) {
        String lower = input.toLowerCase(Locale.ROOT);
        lower = replaceLeet(lower);
        lower = Normalizer.normalize(lower, Normalizer.Form.NFD);
        lower = COMBINING_MARKS.matcher(lower).replaceAll("");
        lower = replaceTurkish(lower);
        lower = reduceRepeated(lower, settings.maxRepeatedCharacters());
        String cleaned = NON_ALNUM.matcher(lower).replaceAll(compact ? "" : " ").trim();
        return compact ? cleaned : cleaned.replaceAll("\\s+", " ");
    }

    public List<String> badWords() {
        return badWords;
    }

    public List<String> whitelist() {
        return whitelist;
    }

    private boolean containsWord(String normalizedMessage, String compactMessage, String badWord) {
        if (Pattern.compile("(^|\\s)" + Pattern.quote(badWord) + "($|\\s)").matcher(normalizedMessage).find()) {
            return true;
        }
        return settings.checkSpacedLetters() && compactMessage.contains(badWord);
    }

    private String maskMessage(String message, Set<String> matches) {
        String sanitized = message;
        for (String match : matches) {
            StringBuilder pattern = new StringBuilder();
            for (char c : match.toCharArray()) {
                pattern.append(Pattern.quote(String.valueOf(c))).append("[^a-zA-Z0-9çğıöşüÇĞİÖŞÜ]*");
            }
            sanitized = sanitized.replaceAll("(?iu)" + pattern, settings.replacement());
        }
        return sanitized;
    }

    private Set<String> normalizeWords(List<String> words) {
        Set<String> result = new LinkedHashSet<>();
        for (String word : words) {
            String normalized = normalize(word, true);
            if (!normalized.isBlank()) {
                result.add(normalized);
            }
        }
        return result;
    }

    private String reduceRepeated(String input, int max) {
        StringBuilder builder = new StringBuilder(input.length());
        char last = 0;
        int count = 0;
        for (char current : input.toCharArray()) {
            if (current == last) {
                count++;
            } else {
                last = current;
                count = 1;
            }
            if (count <= max) {
                builder.append(current);
            }
        }
        return builder.toString();
    }

    private String replaceTurkish(String input) {
        return input
            .replace('ç', 'c')
            .replace('ğ', 'g')
            .replace('ı', 'i')
            .replace('ö', 'o')
            .replace('ş', 's')
            .replace('ü', 'u');
    }

    private String replaceLeet(String input) {
        return input
            .replace('0', 'o')
            .replace('1', 'i')
            .replace('!', 'i')
            .replace('3', 'e')
            .replace('4', 'a')
            .replace('@', 'a')
            .replace('5', 's')
            .replace('$', 's')
            .replace('7', 't')
            .replace('+', 't')
            .replace('8', 'b')
            .replace('9', 'g');
    }
}
