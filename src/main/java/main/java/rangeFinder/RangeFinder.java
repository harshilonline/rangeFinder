package main.java.rangeFinder;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.List;
import redis.clients.jedis.Jedis;

public class RangeFinder {
    public static String REDISKEY = "ranges";
    // Generic function to convert Set of
    // String to Set of Integer
    public static Set<Integer>
    convertStringSetToIntSet(Set<String> setOfString)
    {
        List<Integer> ret = new ArrayList<>();
        // Creating an iterator
        Iterator value = setOfString.iterator();

        while (value.hasNext()) {
            try {
                ret.add(Integer.parseInt((String) value.next()));
            } catch(NumberFormatException ex){
                continue;
            }
        }
        // Sorting HashSet using List
        Collections.sort(ret);
        return new HashSet<Integer>(ret);
    }

    // Function merging two sets using DoubleBrace Initialisation
    public static Integer[] mergeSet(Set<Integer> a, Set<Integer> b)
    {
        Set<Integer> merge = new HashSet<>();
        merge.addAll(a);
        merge.addAll(b);
        //Creating an empty integer array
        Integer[] array = new Integer[merge.size()];
        //Converting Set object to integer array
        merge.toArray(array);
        return array;
    }

    // Generic function to convert list to set
    public static Set<Integer> convertListToSet(Integer[] nums)
    {
        // create a set from the List
        return new HashSet<Integer>(Arrays.asList(nums));
    }

    // Generic function to convert set to list
    public static <T> List<T> convertSetToList(Set<T> set)
    {
        // create a set from the List
        return new ArrayList<>(set);
    }

    // Dump all values into redis
    public static void insertToRedis(Jedis redis, String key, Integer[] nums) {

        for (Integer num: nums) {
            redis.sadd(key, num.toString());
        }
    }

    public static List<String> getRangesUnbounded(Integer[] nums) {
        // TODO: Move to constructor
        Jedis jedis;

        jedis = new Jedis();
        Set<Integer> intRanges = new HashSet<>();
        Set<String> ranges = jedis.smembers(REDISKEY);
        if (ranges.size() > 0) {
            // Convert Set of String to set of Integer
            intRanges = convertStringSetToIntSet(ranges);
        }

        Set<Integer> retSet = convertListToSet(nums);
        // combine all elements
        nums = mergeSet(retSet, intRanges);
        // insert new range into redis set
        insertToRedis(jedis, REDISKEY, nums);
        // get ranges info
        return getRanges(nums);
    }

    public static List<String> getRanges(Integer[] nums) {
        if (nums == null || nums.length == 0 ) {
            return null;
        }

        List<String> retList = new ArrayList<String>();
        // case if there is only one element in list
        if (nums.length == 1) {
            retList.add(nums[0]+"");
            return retList;
        }

        for (int i = 0; i < nums.length; i++) {
            int a = nums[i];
            // increment index until next element is equal to one plus
            // current element
            while (i+1 < nums.length && (nums[i+1] - nums[i]) == 1) {
                i++;
            }

            if (a != nums[i]) {
                retList.add("(" + a + "," + nums[i] + ")");
            } else {
                retList.add("(" + a + ")");
            }
        }

        return retList;
    }

    public static void main(String[] args) {
        // bounded ranges
        List<String> result = RangeFinder.getRanges(new Integer[] {2,4,5,6,7,9,10,12});
        System.out.println("Averages of subarrays of size K: " + result.toString());
        // unbounded ranges
        result = RangeFinder.getRangesUnbounded(new Integer[] {1, 2, 4, 5, 7, 8, 9, 11, 12, 14});
        System.out.println("Averages of subarrays of size K: " + result.toString());
        result = RangeFinder.getRangesUnbounded(new Integer[] {1, 2, 3, 4, 6, 8, 9, 10});
        System.out.println("Averages of subarrays of size K: " + result.toString());
        // invalid ranges
    }
}