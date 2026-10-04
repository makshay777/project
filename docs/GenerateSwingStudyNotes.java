package docs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

/** Small, dependency-free Markdown-to-PDF renderer for the Swing study note. */
public final class GenerateSwingStudyNotes {
    private static final int PAGE_WIDTH = 612;
    private static final int PAGE_HEIGHT = 792;
    private static final int LEFT = 52;
    private static final int BOTTOM = 58;
    private final List<StringBuilder> pages = new ArrayList<>();
    private StringBuilder page;
    private float y;

    private GenerateSwingStudyNotes() {
        newPage();
    }

    public static void main(String[] args) throws IOException {
        Path markdown = args.length > 0 ? Path.of(args[0]) : Path.of("docs/Swing-Study-Notes.md");
        Path pdf = args.length > 1 ? Path.of(args[1]) : Path.of("docs/Swing-Study-Notes.pdf");
        GenerateSwingStudyNotes renderer = new GenerateSwingStudyNotes();
        renderer.render(Files.readAllLines(markdown, StandardCharsets.UTF_8));
        Files.createDirectories(pdf.toAbsolutePath().getParent());
        Files.write(pdf, renderer.createPdf());
        System.out.println("Created " + pdf.toAbsolutePath() + " (" + renderer.pages.size() + " pages)");
    }

    private void render(List<String> lines) {
        boolean code = false;
        boolean inTable = false;
        for (String raw : lines) {
            String line = raw.trim();
            if (line.startsWith("```")) {
                code = !code;
                if (code) y -= 3;
                else y -= 7;
                continue;
            }
            if (code) {
                codeLine(line);
                continue;
            }
            if (line.startsWith("|")) {
                if (line.matches("[| :\\-]+")) continue;
                tableLine(line);
                inTable = true;
                continue;
            } else if (inTable) {
                y -= 5;
                inTable = false;
            }
            if (line.isEmpty()) {
                y -= 5;
            } else if (line.startsWith("# ")) {
                ensureSpace(60);
                text(LEFT, y, 27, "F2", "0.10 0.17 0.31", clean(line.substring(2)));
                y -= 36;
            } else if (line.startsWith("## ")) {
                section(clean(line.substring(3)));
            } else if (line.startsWith("### ")) {
                ensureSpace(34);
                text(LEFT, y, 13, "F2", "0.12 0.31 0.68", clean(line.substring(4)));
                y -= 19;
            } else if (line.equals("$$")) {
                y -= 1;
            } else if (line.startsWith("\\text{average}")) {
                paragraph("Average = sum of all marks divided by the number of students.");
            } else if (line.startsWith("- ")) {
                bullet(clean(line.substring(2)));
            } else if (line.matches("\\d+\\. .*")) {
                bullet(clean(line.replaceFirst("^\\d+\\. ", "")), true);
            } else {
                paragraph(clean(line));
            }
        }
    }

    private void section(String heading) {
        ensureSpace(48);
        y -= 4;
        text(LEFT, y, 16, "F2", "0.12 0.31 0.68", heading);
        y -= 8;
        rectangle(LEFT, y, 508, 0.7f, "0.87 0.90 0.95");
        y -= 17;
    }

    private void paragraph(String value) {
        String normalized = ascii(value);
        for (String line : wrap(normalized, 91)) {
            ensureSpace(17);
            text(LEFT, y, 10, "F1", "0.20 0.24 0.31", line);
            y -= 14;
        }
        y -= 2;
    }

    private void bullet(String value) {
        bullet(value, false);
    }

    private void bullet(String value, boolean numbered) {
        String normalized = ascii(value);
        List<String> wrapped = wrap(normalized, 86);
        for (int i = 0; i < wrapped.size(); i++) {
            ensureSpace(16);
            String prefix = i == 0 ? (numbered ? "- " : "  * ") : "     ";
            text(LEFT, y, 10, "F1", "0.20 0.24 0.31", prefix + wrapped.get(i));
            y -= 14;
        }
        y -= 1;
    }

    private void tableLine(String markdownLine) {
        String[] columns = markdownLine.split("\\|", -1);
        List<String> values = new ArrayList<>();
        for (int i = 1; i < columns.length - 1; i++) {
            values.add(ascii(clean(columns[i].trim())));
        }
        ensureSpace(18);
        if (values.size() == 2) {
            List<String> description = wrap(values.get(1), 61);
            for (int row = 0; row < description.size(); row++) {
                ensureSpace(16);
                String firstCell = row == 0 ? String.format("%-20s", values.get(0)) : "                    ";
                text(LEFT, y, 8.5f, "F1", "0.20 0.24 0.31", firstCell + "  " + description.get(row));
                y -= 13;
            }
            return;
        }
        String display;
        if (values.size() >= 3) {
            display = String.format("%-22s  %-7s  %s", values.get(0), values.get(1), values.get(2));
        } else {
            display = String.join("  |  ", values);
        }
        if (display.length() > 94) display = display.substring(0, 94);
        text(LEFT, y, 8.5f, "F1", "0.20 0.24 0.31", display);
        y -= 13;
    }

    private void codeLine(String source) {
        ensureSpace(16);
        String line = ascii(source);
        if (line.length() > 88) line = line.substring(0, 88);
        rectangle(LEFT - 3, y - 4, 514, 14, "0.95 0.96 0.98");
        text(LEFT + 5, y, 8.5f, "F3", "0.16 0.22 0.32", line);
        y -= 14;
    }

    private void ensureSpace(float amount) {
        if (y - amount < BOTTOM) newPage();
    }

    private void newPage() {
        page = new StringBuilder();
        pages.add(page);
        page.append("0.12 0.21 0.39 rg 0 778 612 14 re f\n");
        page.append("0.12 0.31 0.68 rg 0 778 8 14 re f\n");
        y = 744;
    }

    private void finishPage(int pageNumber) {
        rectangle(LEFT, 43, 508, 0.7f, "0.87 0.90 0.95");
        text(LEFT, 28, 8, "F1", "0.42 0.47 0.55", "Java Swing Study Notes  |  Student Gradebook Project");
        String pageNumberText = "Page " + pageNumber + " of " + pages.size();
        text(492, 28, 8, "F1", "0.42 0.47 0.55", pageNumberText);
    }

    private byte[] createPdf() throws IOException {
        List<byte[]> objects = new ArrayList<>();
        for (int i = 0; i < 5; i++) objects.add(null);
        List<Integer> pageObjectIds = new ArrayList<>();
        for (int i = 0; i < pages.size(); i++) {
            finishPage(i + 1);
            int pageObjectId = objects.size() + 1;
            int streamObjectId = pageObjectId + 1;
            pageObjectIds.add(pageObjectId);
            String pageDictionary = "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 " + PAGE_WIDTH + " " + PAGE_HEIGHT
                    + "] /Resources << /Font << /F1 3 0 R /F2 4 0 R /F3 5 0 R >> >> /Contents "
                    + streamObjectId + " 0 R >>";
            objects.add(asciiBytes(pageDictionary));
            byte[] stream = asciiBytes(pages.get(i).toString());
            ByteArrayOutputStream streamObject = new ByteArrayOutputStream();
            streamObject.write(asciiBytes("<< /Length " + stream.length + " >>\nstream\n"));
            streamObject.write(stream);
            streamObject.write(asciiBytes("\nendstream"));
            objects.add(streamObject.toByteArray());
        }
        StringBuilder kids = new StringBuilder();
        for (int id : pageObjectIds) kids.append(id).append(" 0 R ");
        objects.set(0, asciiBytes("<< /Type /Catalog /Pages 2 0 R >>"));
        objects.set(1, asciiBytes("<< /Type /Pages /Kids [" + kids + "] /Count " + pages.size() + " >>"));
        objects.set(2, asciiBytes("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>"));
        objects.set(3, asciiBytes("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold >>"));
        objects.set(4, asciiBytes("<< /Type /Font /Subtype /Type1 /BaseFont /Courier >>"));

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        output.write(asciiBytes("%PDF-1.4\n%StudyNotes\n"));
        List<Integer> offsets = new ArrayList<>();
        offsets.add(0);
        for (int i = 0; i < objects.size(); i++) {
            offsets.add(output.size());
            output.write(asciiBytes((i + 1) + " 0 obj\n"));
            output.write(objects.get(i));
            output.write(asciiBytes("\nendobj\n"));
        }
        int xrefOffset = output.size();
        output.write(asciiBytes("xref\n0 " + (objects.size() + 1) + "\n"));
        output.write(asciiBytes("0000000000 65535 f \n"));
        for (int i = 1; i < offsets.size(); i++) {
            output.write(asciiBytes(String.format("%010d 00000 n \n", offsets.get(i))));
        }
        output.write(asciiBytes("trailer\n<< /Size " + (objects.size() + 1) + " /Root 1 0 R >>\nstartxref\n"
                + xrefOffset + "\n%%EOF\n"));
        return output.toByteArray();
    }

    private void text(float x, float baseline, float fontSize, String font, String rgb, String value) {
        page.append("BT /" + font + " " + fontSize + " Tf " + rgb + " rg 1 0 0 1 " + x + " " + baseline
                + " Tm (" + escape(ascii(value)) + ") Tj ET\n");
    }

    private void rectangle(float x, float bottom, float width, float height, String rgb) {
        page.append(rgb).append(" rg ").append(x).append(' ').append(bottom).append(' ').append(width).append(' ')
                .append(height).append(" re f\n");
    }

    private static List<String> wrap(String text, int limit) {
        List<String> result = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        for (String word : text.split("\\s+")) {
            if (word.isEmpty()) continue;
            if (line.length() > 0 && line.length() + 1 + word.length() > limit) {
                result.add(line.toString());
                line.setLength(0);
            }
            if (line.length() > 0) line.append(' ');
            line.append(word);
        }
        if (line.length() > 0) result.add(line.toString());
        return result;
    }

    private static String clean(String text) {
        return text.replace("**", "").replace("`", "").replace("\\", "");
    }

    private static String ascii(String text) {
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFKD).replaceAll("\\p{M}+", "");
        return normalized.replace("≥", ">=").replace("≤", "<=").replace("—", "-")
                .replace("–", "-").replace("’", "'").replace("“", "\"").replace("”", "\"")
                .replaceAll("[^\\x20-\\x7E]", "?");
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }

    private static byte[] asciiBytes(String value) {
        return value.getBytes(StandardCharsets.ISO_8859_1);
    }
}
