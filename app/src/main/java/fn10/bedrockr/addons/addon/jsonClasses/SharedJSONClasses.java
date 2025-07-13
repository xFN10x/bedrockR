package fn10.bedrockr.addons.addon.jsonClasses;

import java.util.Arrays;
import java.util.Vector;

public class SharedJSONClasses {

    public static class VersionVector {

        public static Vector<Integer> fromString(String fromThis) {
            java.util.List<String> array = Arrays.asList(fromThis.split("\\."));

            // expand if not big enough
            //if (array.size() < 3) {
            //    while (array.size() <= 3) {
            //        array.addLast(fromThis);
            //    }
            //}

            //make vector
            var vec = new Vector<Integer>(3, 1);
            vec.add(0);
            vec.add(0);
            vec.add(0);

            //try adding to it
            try {
                vec.setElementAt(Integer.valueOf(array.get(0)), 0); //1
            } catch (Exception e) {
            }
            try {
                vec.setElementAt(Integer.valueOf(array.get(1)), 1); //2
            } catch (Exception e) {
            }
            try {
                vec.setElementAt(Integer.valueOf(array.get(2)), 2); //3
            } catch (Exception e) {
            }
            return vec;
        }

        // spam /
        // public VersionVector(String fromThis) {
        // java.util.List<String> array = Arrays.asList(fromThis.split("\\."));
        //
        // // expand if not big enough
        /// // if (array.size() < 3) {
        // // while (array.size() <= 3) {
        // array.addLast(fromThis);;
        // // }
        // }
        ///////
        // try {
        /// this.a = Integer.valueOf(array.get(0));
        // this.b = Integer.valueOf(array.get(1));
        /// this.c = Integer.valueOf(array.get(2));
        /// } catch (Exception e) {
        // }
    }
}
