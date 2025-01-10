import java.util.Arrays;

public class TextJustifier {
    public static void main(String[] args) {
        String[] words = {"The", "quick", "brown", "fox", "jumps", "over", "the", "lazy", "dog."};
        String[] res = justifyText(words, 5);
        for(String row: res) {
            System.out.println(row);
        }

    }

    public static String[] justifyText(String[] words, int maxWidth) {
        if(words.length == 0) {
            return words;
        }

        TextData  currWordsTextData = extractTextData(words, maxWidth);

        return buildRows(words, currWordsTextData);

    }

    private static TextData extractTextData(String[] words, int maxWidth) {
        int currWidth = 0;
        int currRow = 0;

        int[] spacesOnRow = new int[words.length];
        Arrays.fill(spacesOnRow, maxWidth);
        int[] wordsOnRow = new int[words.length];

        for(String word : words) {
            if (currWidth + word.length() <= maxWidth) {
                currWidth += word.length() + 1;// +1 for space
            }
            else {
                currRow++;
                currWidth = word.length() + 1;
            }
            spacesOnRow[currRow] -= word.length();
            wordsOnRow[currRow]++;
        }

        TextData currWordsTextData = new TextData();

        currWordsTextData.rowsCount = currRow + 1;
        currWordsTextData.spacesOnRow = Arrays.copyOfRange(spacesOnRow, 0, currRow + 1);
        currWordsTextData.wordsOnRow = Arrays.copyOfRange(wordsOnRow, 0, currRow + 1);

        return currWordsTextData;
    }

    private static String[] buildRows(String[] words, TextData currWordsTextData) {
        int currWord = 0;
        String[] result = new String[currWordsTextData.rowsCount];

        for(int i = 0; i < currWordsTextData.rowsCount - 1; i++) { //-1 because last row follows different logic

            StringBuilder row = new StringBuilder();

            if(currWordsTextData.wordsOnRow[i] > 1) {

                int spacesBetweenWords = currWordsTextData.spacesOnRow[i] / (currWordsTextData.wordsOnRow[i] - 1);
                int largerSpacesCount = currWordsTextData.spacesOnRow[i] % (currWordsTextData.wordsOnRow[i] - 1); // holds how many of the spaces between will be with 1 larger than the rest(counting from left)

                for(int j = 0; j < currWordsTextData.wordsOnRow[i]; j++) {
                    row.append(words[currWord]);
                    currWord++;

                    if(j == currWordsTextData.wordsOnRow[i] - 1) {
                        break;
                    }

                    if(largerSpacesCount > 0) {
                        row.append(" ".repeat(spacesBetweenWords + 1));
                        largerSpacesCount--;
                    }
                    else {
                        row.append(" ".repeat(spacesBetweenWords));
                    }
                }
           }
            else {
                row.append(words[currWord]);
                currWord++;
                row.append(" ".repeat(currWordsTextData.spacesOnRow[i]));
            }

           result[i] = row.toString();
        }

        StringBuilder lastRow = new StringBuilder();

        for(int i = currWord; i < words.length; i++) {
            lastRow.append(words[currWord]);
            if(currWordsTextData.spacesOnRow[currWordsTextData.rowsCount - 1] > 0) {
                lastRow.append(" ");
                currWordsTextData.spacesOnRow[currWordsTextData.rowsCount - 1]--;
            }
            currWord++;
        }
        if(currWordsTextData.spacesOnRow[currWordsTextData.rowsCount - 1] > 0) {
            lastRow.append(" ".repeat(currWordsTextData.spacesOnRow[currWordsTextData.rowsCount - 1]));
            currWordsTextData.spacesOnRow[currWordsTextData.rowsCount - 1]--;
        }

        result[currWordsTextData.rowsCount - 1] = lastRow.toString();

        return result;
    }
}

class TextData {
    public int rowsCount;
    public int[] spacesOnRow;
    public int[] wordsOnRow;
}