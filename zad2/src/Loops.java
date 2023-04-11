import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

class Loops implements GeneralLoops {
    List<Integer> lowerLimit;
    List<Integer> upperLimit;
    List<Integer> variables;
    List<List<Integer>> result;

    public void setLowerLimits(List<Integer> limits) {
        lowerLimit = new ArrayList<Integer>();
        lowerLimit.addAll(limits);
    }

    @Override
    public void setUpperLimits(List<Integer> limits) {
        upperLimit = new ArrayList<Integer>();
        upperLimit.addAll(limits);
    }

    @Override
    public List<List<Integer>> getResult() {
        checkLimits();
        Loop(variables, 0);
        return result;
    }


    private void checkLimits() {
        if (lowerLimit == null && upperLimit == null) {
            setLowerLimits(List.of(0));
            setUpperLimits(List.of(0));
        }

        int x;
        if (lowerLimit == null) {
            x = upperLimit.size() - 1;
        } else {
            x = lowerLimit.size() - 1;
        }

        if (lowerLimit == null && upperLimit != null) {
            lowerLimit = new ArrayList<Integer>(upperLimit);

            for (int i = 0; i <= x; i++) {
                lowerLimit.set(i, 0);
            }
        } else if (upperLimit == null && lowerLimit != null) {
            upperLimit = new ArrayList<Integer>(lowerLimit);

            for (int i = 0; i <= x; i++) {
                upperLimit.set(i, 0);
            }
        }

        result = new ArrayList<List<Integer>>();
        variables = new ArrayList<Integer>(lowerLimit);
        for (int i = 0; i <= x; i++) {
            variables.set(i, 0);
        }
    }

    private void Loop(List<Integer> list, int level) {
        if (level != lowerLimit.size()) {
            variables.set(level, lowerLimit.get(level));
            while (variables.get(level) <= upperLimit.get(level)) {
                Loop(variables, level + 1);
                variables.set(level, variables.get(level) + 1);
            }
        } else {
            ArrayList<Integer> temp = new ArrayList<Integer>(variables);
            result.addAll(Collections.singleton(temp));
        }
    }
}