import java.util.Arrays;

public class CourseScheduler {
    public static void main(String[] args) {

    }

    public static int maxNonOverlappingCourses(int[][] courses) {
            mergeSort(courses);

            int counter = 0;
            int currEndHour = 0;
            for(int[] course : courses) {
                if(course[0] >= currEndHour) {
                    counter++;
                    currEndHour = course[1];
                }
            }

            return counter;
    }

    private static int[][] mergeSort(int[][] courses) {
        if(courses.length < 2) {
            return courses;
        }

        int[][] left = Arrays.copyOfRange(courses, 0, courses.length/2);
        int[][] right = Arrays.copyOfRange(courses, courses.length / 2, courses.length);

        mergeSort(left);
        mergeSort(right);

        return merge(courses, left, right);

    }

    private static int[][] merge(int[][] origin, int[][] left, int[][] right) {
        int indexOfLeft = 0, indexOfRight = 0;

        while(indexOfLeft < left.length && indexOfRight < right.length) {
            if(left[indexOfLeft][1] < right[indexOfRight][1]) {
                origin[indexOfLeft + indexOfRight] = left[indexOfLeft];
                indexOfLeft++;
            }
            else {
                origin[indexOfLeft + indexOfRight] = right[indexOfRight];
                indexOfRight++;
            }
        }

        while(indexOfLeft < left.length) {
            origin[indexOfLeft + indexOfRight] = left[indexOfLeft];
            indexOfLeft++;
        }

        while(indexOfRight < right.length) {
            origin[indexOfRight + indexOfLeft] = right[indexOfRight];
            indexOfRight++;
        }

        return origin;
    }

    private static void swap(int[][] courses, int i, int j) {
        int[] temp = courses[i];
        courses[i] = courses[j];
        courses[j] = temp;

    }

}
