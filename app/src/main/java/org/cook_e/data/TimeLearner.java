package org.cook_e.data;

import org.joda.time.ReadableDuration;

/**
 * Created by Shan Yaang on 2/18/2016.
 * This according to the Step Given, this class will perform varies operations to give estimates
 */
public class TimeLearner {
    /**
     *
     * @param s The step we need to estimate the time for
     * @return the leanrned time for that specific Step
     */
    public static ReadableDuration learnTimeForStep(Step s) {
        // ToDo: Perform some machine learning algorithm to make return a better time estimate for that person.
        return s.getTime();
    }
}
