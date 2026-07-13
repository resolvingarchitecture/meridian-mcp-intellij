package meridian.intellij.findings;

public record MeridianFinding(
        String severity,
        String type,
        String file,
        int line,
        String title,
        String explanation,
        String consequence,
        String suggestion,
        String adrReference,
        double confidence
) {
    public MeridianFinding normalized() {
        String normalizedSeverity = severity == null || severity.isBlank()
                ? "INFO"
                : severity.trim().toUpperCase();

        double normalizedConfidence = Math.max(0.0, Math.min(1.0, confidence));

        return new MeridianFinding(
                normalizedSeverity,
                valueOrEmpty(type),
                valueOrEmpty(file),
                Math.max(1, line),
                valueOrEmpty(title),
                valueOrEmpty(explanation),
                valueOrEmpty(consequence),
                valueOrEmpty(suggestion),
                valueOrEmpty(adrReference),
                normalizedConfidence
        );
    }

    public boolean isRenderable() {
        return !valueOrEmpty(title).isBlank()
                && !valueOrEmpty(file).isBlank()
                && line > 0;
    }

    private static String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}